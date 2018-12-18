package com.rest;

import com.rest.matching.MatchingEngine;
import com.rest.socket.Server;
import com.rest.updatequeue.UpdateQueueDaemon;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupListener implements ServletContextListener
{

    UpdateQueueDaemon upqDaemon;

    public void contextInitialized(ServletContextEvent event)
    {
        Server.getInstance().start();
        MatchingEngine.getInstance();
        upqDaemon = new UpdateQueueDaemon();

        upqDaemon.addListener("orders", () -> {
            Server.getInstance().updateSubject("ORDERS", "Update on orders table");
        });
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
