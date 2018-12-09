package com.rest;

import com.rest.matching.MatchingEngine;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupListener implements ServletContextListener
{

    public void contextInitialized(ServletContextEvent arg0)
    {
        MatchingEngine.getInstance();
    }


    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
