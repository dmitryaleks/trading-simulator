package com.rest;

import com.rest.matching.MatchingEngine;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupListener implements ServletContextListener
{

    public void contextInitialized(ServletContextEvent arg0)
    {
        new com.rest.socket.Server(7888).start();
        MatchingEngine.getInstance();
    }


    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
