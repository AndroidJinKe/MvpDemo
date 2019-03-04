package com.orhanobut.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.orhanobut.logger.Utils.checkNotNull;

/**
 * LogCat implementation for {@link LogStrategy}
 * <p>
 * This simply prints out all logs to Logcat by using standard {@link Log} class.
 */
public class LogcatLogStrategy implements LogStrategy {

    static final String DEFAULT_TAG = "NO_TAG";

    private int last;

    private String randomKey() {
        int random = (int) (10 * Math.random());
        if (random == last) {
            random = (random + 1) % 10;
        }
        last = random;
        return String.valueOf(random);
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        checkNotNull(message);

        if (tag == null) {
            tag = DEFAULT_TAG;
        }

        Log.println(priority, randomKey() + tag, message);
    }
}
