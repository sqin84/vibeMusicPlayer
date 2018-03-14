package com.example.ajcin.flashbackmusicteam16;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** TimeMachine class to get the current time in user's timezone.
 * Author: CSE 110 - Team 16, Winter 2018
 * Date: February 17, 2018
 */
public class TimeMachine {
    private static Clock clock = Clock.systemDefaultZone();
    private static ZoneId zoneId = ZoneId.systemDefault();

    /** now
      * Get the local dateTime.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(getClock());
    }

    /** useFixedClockAt
      *  Use fixed clock at local dateTime.
      * @param date local dateTime
     */
    public static void useFixedClockAt(LocalDateTime date){
        clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
    }

    public static void useSystemDefaultZoneClock(){
        clock = Clock.systemDefaultZone();
    }

    private static Clock getClock() {
        return clock ;
    }


}
