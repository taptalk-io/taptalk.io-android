package io.taptalk.TapTalk.Helper;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.R;

public class TAPTimeFormatter {

    private static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));

    public static String durationString(long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        timeGap = timeNow - timestamp;

        long midnightTimeGap;
        Calendar midnightFromSendTime = Calendar.getInstance();
        midnightFromSendTime.setTime(new Date(timestamp));
        midnightFromSendTime.add(Calendar.DATE, 1);
        midnightFromSendTime.set(Calendar.HOUR_OF_DAY, 0);
        midnightFromSendTime.set(Calendar.MINUTE, 0);
        midnightFromSendTime.set(Calendar.SECOND, 0);
        midnightFromSendTime.set(Calendar.MILLISECOND, 0);
        midnightTimeGap = midnightFromSendTime.getTimeInMillis() - timestamp;

        if (timestamp == 0) {
            return "";
        } else if (midnightTimeGap > timeGap) {
            return formatClock(timestamp);
        } else if ((TAPTimeFormatter.times.get(3)) + midnightTimeGap > timeGap) {
            return TapTalk.appContext.getString(R.string.tap_yesterday);
        } else if ((TAPTimeFormatter.times.get(3) * 6) + midnightTimeGap >= timeGap) {
            return formatDay(timestamp);
        } else {
            return formatDate(timestamp);
        }
    }

    public static String durationChatString(Context context, long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        timeGap = timeNow - timestamp;

        long midnightTimeGap;
        Calendar midnightFromSendTime = Calendar.getInstance();
        midnightFromSendTime.setTime(new Date(timestamp));
        midnightFromSendTime.add(Calendar.DATE, 1);
        midnightFromSendTime.set(Calendar.HOUR_OF_DAY, 0);
        midnightFromSendTime.set(Calendar.MINUTE, 0);
        midnightFromSendTime.set(Calendar.SECOND, 0);
        midnightFromSendTime.set(Calendar.MILLISECOND, 0);
        midnightTimeGap = midnightFromSendTime.getTimeInMillis() - timestamp;

        if (timestamp == 0) {
            return "";
        } else if (midnightTimeGap > timeGap && context != null) {
            return String.format("%s • %s", context.getString(R.string.tap_today), formatClock(timestamp));
        } else if ((TAPTimeFormatter.times.get(3)) + midnightTimeGap > timeGap && context != null) {
            return String.format("%s • %s", context.getString(R.string.tap_yesterday), formatClock(timestamp));
        } else {
            return String.format("%s • %s", formatTime(timestamp, "dd/MM/yy"), formatClock(timestamp));
        }
    }

    public static String dateStampString(Context context, long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        timeGap = timeNow - timestamp;

        long midnightTimeGap;
        Calendar midnightFromSendTime = Calendar.getInstance();
        midnightFromSendTime.setTime(new Date(timestamp));
        midnightFromSendTime.add(Calendar.DATE, 1);
        midnightFromSendTime.set(Calendar.HOUR_OF_DAY, 0);
        midnightFromSendTime.set(Calendar.MINUTE, 0);
        midnightFromSendTime.set(Calendar.SECOND, 0);
        midnightFromSendTime.set(Calendar.MILLISECOND, 0);
        midnightTimeGap = midnightFromSendTime.getTimeInMillis() - timestamp;

        if (timestamp == 0) {
            return "";
        } else if (midnightTimeGap > timeGap) {
            return context.getString(R.string.tap_today);
        } else if ((TAPTimeFormatter.times.get(3)) + midnightTimeGap > timeGap) {
            return context.getString(R.string.tap_yesterday);
        } else {
            return formatDate(timestamp);
        }
    }

    public static String forwardDateStampString(Context context, long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        timeGap = timestamp - timeNow;

        long midnightTimeGap;
        Calendar midnightFromSendTime = Calendar.getInstance();
        midnightFromSendTime.setTime(new Date(timestamp));
        midnightFromSendTime.add(Calendar.DATE, 1);
        midnightFromSendTime.set(Calendar.HOUR_OF_DAY, 0);
        midnightFromSendTime.set(Calendar.MINUTE, 0);
        midnightFromSendTime.set(Calendar.SECOND, 0);
        midnightFromSendTime.set(Calendar.MILLISECOND, 0);
        midnightTimeGap = midnightFromSendTime.getTimeInMillis() - timeNow;

        if (timestamp == 0) {
            return "";
        } else if ((TAPTimeFormatter.times.get(3)) + midnightTimeGap <= timeGap) {
            return formatDate(timestamp);
        } else if (midnightTimeGap <= timeGap) {
            return String.format("%s %s", context.getString(R.string.tap_tomorrow), formatClock(timestamp));
        } else {
            return formatClock(timestamp);
        }
    }

    public static String getLastActivityString(Context context, long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        timeGap = timeNow - timestamp;

        long midnightTimeGap;
        Calendar midnightFromSendTime = Calendar.getInstance();
        midnightFromSendTime.setTime(new Date(timestamp));
        midnightFromSendTime.add(Calendar.DATE, 1);
        midnightFromSendTime.set(Calendar.HOUR_OF_DAY, 0);
        midnightFromSendTime.set(Calendar.MINUTE, 0);
        midnightFromSendTime.set(Calendar.SECOND, 0);
        midnightFromSendTime.set(Calendar.MILLISECOND, 0);
        midnightTimeGap = midnightFromSendTime.getTimeInMillis() - timestamp;

        if (timestamp == 0) {
            return "";
        } else if (midnightTimeGap > timeGap && timeGap < times.get(5)) {
            return context.getString(R.string.tap_active_recently);
        } else if (midnightTimeGap > timeGap && timeGap < times.get(4) && timeGap < TimeUnit.MINUTES.toMillis(2)) {
            long numberOfMinutes = timeGap / times.get(5);
            return String.format(Locale.getDefault(), context.getString(R.string.tap_format_d_minute_ago), numberOfMinutes);
        } else if (midnightTimeGap > timeGap && timeGap < times.get(4)) {
            long numberOfMinutes = timeGap / times.get(5);
            return String.format(Locale.getDefault(), context.getString(R.string.tap_format_d_minutes_ago), numberOfMinutes);
        } else if (midnightTimeGap > timeGap && timeGap < TimeUnit.HOURS.toMillis(2)) {
            long numberOfHour = timeGap / times.get(4);
            return String.format(Locale.getDefault(), context.getString(R.string.tap_format_d_hour_ago), numberOfHour);
        } else if (midnightTimeGap > timeGap) {
            long numberOfHour = timeGap / times.get(4);
            return String.format(Locale.getDefault(), context.getString(R.string.tap_format_d_hours_ago), numberOfHour);
        } else if ((times.get(3)) + midnightTimeGap > timeGap) {
//            Date yesterdayTime = new Date(timestamp);
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            String time = sdf.format(yesterdayTime);
            return context.getString(R.string.tap_active_yesterday);
        } else if ((TAPTimeFormatter.times.get(3) * 6) + midnightTimeGap >= timeGap && timeGap < TimeUnit.DAYS.toMillis(2)) {
            long numberOfDays = timeGap / times.get(3);
            return String.format(Locale.getDefault(), context.getString(R.string.tap_format_d_day_ago), numberOfDays);
        } else if ((TAPTimeFormatter.times.get(3) * 6) + midnightTimeGap >= timeGap) {
            long numberOfDays = timeGap / times.get(3);
            return String.format(Locale.getDefault(), context.getString(R.string.tap_format_d_days_ago), numberOfDays);
        } else if (timeGap <= times.get(2)) {
            return context.getString(R.string.tap_active_a_week_ago);
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String dateString = sdf.format(date);
            return String.format(context.getString(R.string.tap_format_s_last_active), dateString);
        }
    }

    public static String formatTime(long timestamp, String pattern) {
        SimpleDateFormat timeSdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public static String formatClock(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public static String formatDate(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public static String formatDay(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("EEE", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public static String formatMonth(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public static boolean isOverOneWeek(long timestamp) {
        return (System.currentTimeMillis() - timestamp) >= (times.get(2));
    }

    public static long oneMonthAgoTimeStamp(long currentTimestamp) {
        return currentTimestamp - times.get(1);
    }
}
