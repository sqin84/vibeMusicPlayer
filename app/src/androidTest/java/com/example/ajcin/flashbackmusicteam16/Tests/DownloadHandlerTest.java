package com.example.ajcin.flashbackmusicteam16.Tests;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.ajcin.flashbackmusicteam16.DownloadHandler;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by ajcin on 3/14/2018.
 */

public class DownloadHandlerTest {
    private Context context;
    private DownloadHandler handler;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        handler = new DownloadHandler(context);
    }

    @Test
    public void test_download_file() {

    }

    @Test
    public void test_unpack_zip() {

    }

    @Test
    public void test_check_status() {

    }

    @Test
    public void test_download_status() {

    }

    @Test
    public void test_check_available_space() {

    }
}
