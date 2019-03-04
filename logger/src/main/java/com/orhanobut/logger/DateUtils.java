package com.orhanobut.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jkzhang
 * DATE : 2018/11/3
 * Description ：日期工具类
 */
public class DateUtils {

    // 日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM = "yyyy-MM";
    public static final String FORMAT_YYYY = "yyyy";
    public static final String FORMAT_HH_MM = "HH:mm";
    public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String FORMAT_MM_SS = "mm:ss";
    public static final String FORMAT_MM_DD_HH_MM = "MM-dd HH:mm";
    public static final String FORMAT_MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYY2MM2DD = "yyyy.MM.dd";
    public static final String FORMAT_YYYY2MM2DD_HH_MM = "yyyy.MM.dd HH:mm";
    public static final String FORMAT_MMCDD_HH_MM = "MM月dd日 HH:mm";
    public static final String FORMAT_MMCDD = "MM月dd日";
    public static final String FORMAT_YYYYCMMCDD = "yyyy年MM月dd日";

    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     *
     * @param dateL
     *            日期
     * @return
     */
    public static String formatDateTime(long dateL, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        return sdf.format(new Date(dateL));
    }



}
