package com.moselo.HomingPigeon.Helper;

import android.content.Context;

import com.moselo.HomingPigeon.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * reference: https://stackoverflow.com/a/23215152/817837
 * Created by Fadhlan on 6/16/17.
 */

public class HpTimeFormatter {
    public static final List<Long> times = Arrays.asList(
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
        } else if (timeGap <= HpTimeFormatter.times.get(3) + midnightTimeGap) {
            return "Yesterday";
        } else if (timeGap <= HpTimeFormatter.times.get(3) * 6 + midnightTimeGap) {
            return formatDay(timestamp);
        } else {
            return formatDate(timestamp);
        }
    }

    public static String durationChatString(Context context, long timestamp) {
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
            return sentAt+" "+formatClock(timestamp);
        } else if (timeGap <= HpTimeFormatter.times.get(3) + midnightTimeGap) {
            return "Sent Yesterday at "+formatClock(timestamp);
        } else {
            return sentAt+" "+formatDate(timestamp)+" "+formatClock(timestamp);
        }
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
}
