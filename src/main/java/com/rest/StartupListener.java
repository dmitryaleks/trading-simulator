package com.rest;

import com.rest.matching.MatchingEngine;
import com.rest.socket.Server;
import com.rest.updatequeue.UpdateQueueDaemon;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Arrays;
import java.util.List;

public class StartupListener implements ServletContextListener
{

    UpdateQueueDaemon upqDaemon;

    public void contextInitialized(ServletContextEvent event)
    {
        Server.getInstance().start();
        MatchingEngine.getInstance();

        upqDaemon = new UpdateQueueDaemon();
        List<String> tablesToWatch = Arrays.asList(new String[] {"ORDERS", "TRADE"});

        tablesToWatch.stream().forEach(table -> {
            upqDaemon.addListener(table.toLowerCase(), () -> {
                Server.getInstance().updateSubject(table, table);
            });
        });
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
