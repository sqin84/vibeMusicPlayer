package com.example.ajcin.flashbackmusicteam16;

import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Created by shuo on 2/18/2018.
 */
public class TimeMachineTest {
    @Test
    public void now() throws Exception {
        TimeMachine timeMachine = new TimeMachine();
        LocalDateTime test = timeMachine.now();
        LocalDateTime expect = LocalDateTime.now(Clock.systemDefaultZone());
        assertEquals(expect,test);
    }

    @Test
    public void useFixedClockAt() throws Exception {

    }

    @Test
    public void useSystemDefaultZoneClock() throws Exception {

    }

}