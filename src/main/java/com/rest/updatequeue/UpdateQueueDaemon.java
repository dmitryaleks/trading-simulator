package com.rest.updatequeue;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import com.rest.util.log.Logger;

import java.sql.Statement;
import java.util.*;

public class UpdateQueueDaemon {

    public interface UpdateQueueListener {
        void update();
    }

    private Map<String, Set<UpdateQueueListener>> listeners = new HashMap<>();

    private PGConnection connection;

    private PGNotificationListener listener = new PGNotificationListener() {

        @Override
        public void notification(int processId, String channelName, String subject) {
            Logger.log("UpdateQueueDaemon", String.format("Update on subject [%s] on channel [%s]", subject, channelName));
            pushNotification(subject);
        }
    };

    public UpdateQueueDaemon() {

        Logger.log("UpdateQueueDaemon", "Watching the Update Queue");

        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost("localhost");
        dataSource.setPort(5432);
        dataSource.setDatabase("db");
        dataSource.setUser("app");

        try {
            connection = (PGConnection) dataSource.getConnection();
            connection.addNotificationListener(listener);
            Statement statement = connection.createStatement();
            statement.execute("LISTEN \"update_queue\";");
            statement.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void addListener(final String subject, final UpdateQueueListener listener) {
        if(listeners.containsKey(subject)) {
            listeners.get(subject).add(listener);
        } else {
            listeners.put(subject, new HashSet<>(Arrays.asList(listener)));
        }
    }

    public void removeListener(final String subject, final UpdateQueueListener listener) {
        if(!listeners.containsKey(subject) ||
           !listeners.get(subject).contains(listener)) {
            return;
        }
        listeners.get(subject).remove(listener);
    }

    private void pushNotification(final String subject) {
        if(listeners.containsKey(subject)) {
            listeners.get(subject).forEach(lsn -> lsn.update());
        }
    }
}