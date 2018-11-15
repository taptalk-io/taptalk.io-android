package io.taptalk.TapTalk.Helper;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.taptalk.Taptalk.R;

/**
 * reference: https://stackoverflow.com/a/23215152/817837
 * Created by Fadhlan on 6/16/17.
 */

public class TAPTimeFormatter {

    private static TAPTimeFormatter instance;

    public static TAPTimeFormatter getInstance() {
        return null == instance ? instance = new TAPTimeFormatter() : instance;
    }

    private static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));

    public String durationString(long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        Calendar past = Calendar.getInstance();
        past.setTime(new Date(timestamp));
        timeGap = timeNow - past.getTimeInMillis();

        long midnightTimeGap;
        Calendar midnightToday = Calendar.getInstance();
        midnightToday.setTime(new Date(timeNow));
        midnightToday.set(Calendar.HOUR, -12);
        midnightToday.set(Calendar.MINUTE, 0);
        midnightToday.set(Calendar.SECOND, 0);
        midnightTimeGap = timeNow - midnightToday.getTimeInMillis();


        if (timestamp == 0) {
            return "";
        } else if (timeGap <= midnightTimeGap) {
            return formatClock(timestamp);
        } else if (timeGap <= TAPTimeFormatter.times.get(3) + midnightTimeGap) {
            return TapTalk.appContext.getString(R.string.yesterday);
        } else if (timeGap <= TAPTimeFormatter.times.get(3) * 6 + midnightTimeGap) {
            return formatDay(timestamp);
        } else {
            return formatDate(timestamp);
        }
    }

    public String durationChatString(Context context, long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        Calendar past = Calendar.getInstance();
        past.setTime(new Date(timestamp));
        timeGap = timeNow - past.getTimeInMillis();

        long midnightTimeGap;
        Calendar midnightToday = Calendar.getInstance();
        midnightToday.setTime(new Date(timeNow));
        midnightToday.set(Calendar.HOUR, -12);
        midnightToday.set(Calendar.MINUTE, 0);
        midnightToday.set(Calendar.SECOND, 0);
        midnightTimeGap = timeNow - midnightToday.getTimeInMillis();
        String sentAt = context.getString(R.string.sent_at);

        if (timestamp == 0) {
            return "";
        } else if (timeGap <= midnightTimeGap) {
            return String.format("%s %s", sentAt, formatClock(timestamp));
        } else if (timeGap <= TAPTimeFormatter.times.get(3) + midnightTimeGap) {
            return String.format(context.getString(R.string.sent_yesterday_at), formatClock(timestamp));
        } else {
            return String.format("%s %s %s", sentAt, formatDate(timestamp), formatClock(timestamp));
        }
    }

    public String getLastActivityString(Context context, long timestamp) {
        long timeGap;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        Calendar past = Calendar.getInstance();
        past.setTime(new Date(timestamp));
        timeGap = timeNow - past.getTimeInMillis();

        long midnightTimeGap;
        Calendar midnightToday = Calendar.getInstance();
        midnightToday.setTime(new Date(timeNow));
        midnightToday.set(Calendar.HOUR, -12);
        midnightToday.set(Calendar.MINUTE, 0);
        midnightToday.set(Calendar.SECOND, 0);
        midnightTimeGap = timeNow - midnightToday.getTimeInMillis();

        if (timestamp == 0) {
            return "";
        } else if (timeGap <= midnightTimeGap) {
            if (timeGap < times.get(5)) {
                return context.getString(R.string.last_seen_recently);
            } else if (timeGap < times.get(4)) {
                long numberOfMinutes = timeGap / times.get(5);
                if (timeGap < TimeUnit.MINUTES.toMillis(2))
                    return String.format(Locale.getDefault(), context.getString(R.string.minute_ago), numberOfMinutes);
                else
                    return String.format(Locale.getDefault(), context.getString(R.string.minutes_ago), numberOfMinutes);
            } else {
                long numberOfHour = timeGap / times.get(4);
                if (timeGap < TimeUnit.HOURS.toMillis(2))
                    return String.format(Locale.getDefault(), context.getString(R.string.hour_ago), numberOfHour);
                else
                    return String.format(Locale.getDefault(), context.getString(R.string.hours_ago), numberOfHour);
            }
        } else if (timeGap <= times.get(3) + midnightTimeGap) {
            Date yesterdayTime = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = sdf.format(yesterdayTime);
            return String.format(context.getString(R.string.last_seen_yesterday_at), time);
        } else if (timeGap <= times.get(3) * 6 + midnightTimeGap) {
            long numberOfDays = timeGap / times.get(3);
            if (timeGap < TimeUnit.DAYS.toMillis(2))
                return String.format(Locale.getDefault(), context.getString(R.string.day_ago), numberOfDays);
            else
                return String.format(Locale.getDefault(), context.getString(R.string.days_ago), numberOfDays);
        } else if (timeGap <= times.get(2)) {
            return context.getString(R.string.last_seen_a_week_ago);
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String dateString = sdf.format(date);
            return String.format(context.getString(R.string.last_seen), dateString);
        }
    }

    public String formatTime(long timestamp, String pattern) {
        SimpleDateFormat timeSdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public String formatClock(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public String formatDate(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public String formatDay(long timestamp) {
        SimpleDateFormat timeSdf = new SimpleDateFormat("EEE", Locale.getDefault());
        return timeSdf.format(timestamp);
    }

    public boolean checkOverOneWeekOrNot(long timestamp) {
        return timestamp >= times.get(2);
    }

    public boolean checkOverOneMonthOrNot(long timestamp) {
        return timestamp >= times.get(1);
    }
}
