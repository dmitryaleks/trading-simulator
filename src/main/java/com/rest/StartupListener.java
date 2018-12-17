package com.rest;

import com.rest.matching.MatchingEngine;
import com.rest.socket.Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupListener implements ServletContextListener
{

    public void contextInitialized(ServletContextEvent arg0)
    {
        Server.getInstance().start();
        MatchingEngine.getInstance();
    }


    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
