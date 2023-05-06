package com.mcdead.busycoder.socialcipher.utility;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class TimeUtility {
    public static long getLocalMillisecondsFromUTCMilliseconds(
            final long utcTimeMilliseconds)
    {
        return utcTimeMilliseconds + getZoneOffset();
    }

    public static LocalTime getLocalTimeFromUTCMilliseconds(
            final long utcTimeMilliseconds)
    {
        return LocalDateTime.
                ofEpochSecond(utcTimeMilliseconds, 0, ZoneOffset.ofTotalSeconds(getZoneOffset())).
                toLocalTime();
    }

    public static int getZoneOffset() {
        return TimeZone.getDefault().getRawOffset() / 1000;
    }
}
