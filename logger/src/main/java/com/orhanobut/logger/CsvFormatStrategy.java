package com.orhanobut.logger;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.orhanobut.logger.Utils.checkNotNull;

/**
 * CSV formatted file logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */
public class CsvFormatStrategy implements FormatStrategy {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    //    private static final String SEPARATOR = ",";
    private static final String SPACE = " ";
    private static final String SLASH = "/";
    private static final String COLON = ":";
    public final static String APP_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.yipin.storesort";
    public final static String LOGGER_DIR = File.separator + "logger" + File.separator;


//    时间+空格 +日志级别+”/“+tag+":"+"msg"

    @NonNull
    private final Date date;
    @NonNull
    private final SimpleDateFormat dateFormat;
    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;
    @Nullable
    private LoggerListener loggerListener;

    private CsvFormatStrategy(@NonNull Builder builder) {
        checkNotNull(builder);

        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
        loggerListener = builder.onLoggerListener;
    }

    @NonNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
        checkNotNull(message);

        //    时间+空格 +日志级别+”/“+tag+":"+"msg"
        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
//        builder.append(Long.toString(date.getTime()));

        // human-readable date/time
//        builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // level
        builder.append(SPACE);//添加空格
        builder.append(Utils.logLevel(priority));

        // tag
        builder.append(SLASH);//添加斜杠
        builder.append(tag);
        builder.append(COLON);//添加冒号
        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(SPACE);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);
        String logMsg = builder.toString();
        onLoggerListener(logMsg);
        logStrategy.log(priority, tag, logMsg);
    }


    private void onLoggerListener(String logMsg) {
        if (loggerListener != null) {
            loggerListener.onLogger(logMsg);
        }
    }

    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

        Date date;
        SimpleDateFormat dateFormat;
        LoggerListener onLoggerListener;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

        @NonNull
        public Builder date(@Nullable Date val) {
            date = val;
            return this;
        }

        @NonNull
        public Builder dateFormat(@Nullable SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        @NonNull
        public Builder loggerListener(@Nullable LoggerListener val) {
            onLoggerListener = val;
            return this;
        }

        @NonNull
        public Builder logStrategy(@Nullable LogStrategy val) {
            logStrategy = val;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull
        public CsvFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("MM:dd HH:mm:ss.SSS", Locale.UK);
            }
            if (logStrategy == null) {
                String diskPath = APP_ROOT_PATH + LOGGER_DIR + DateUtils.formatDateTime(System.currentTimeMillis(), DateUtils.DATE_FORMAT);
                HandlerThread ht = new HandlerThread("AndroidFileLogger." + diskPath);
                ht.start();
                Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(), diskPath, MAX_BYTES);
                logStrategy = new DiskLogStrategy(handler);
            }
            return new CsvFormatStrategy(this);
        }
    }
}
