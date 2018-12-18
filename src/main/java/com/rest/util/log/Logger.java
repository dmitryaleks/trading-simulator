package com.rest.util.log;

public class Logger {

    public static void log(final String header, final String msg) {
        System.out.println(String.format("[%s] %s", header, msg));
    }
}
