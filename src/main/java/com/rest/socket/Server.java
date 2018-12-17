package com.rest.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.socket.message.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends WebSocketServer {

    private static Server instance;

    public static Server getInstance() {
        if(instance == null) {
            instance = new Server(7888);
        }
        return instance;
    }

    private Set<WebSocket> connections;
    // maps subjects to subscribers
    private Map<String, Set<WebSocket>> subscribers;
    private Map<String, String> subjectData;

    public Server(int port) {
        super(new InetSocketAddress(port));
        connections = new HashSet<>();
        subscribers = new HashMap<>();
        subjectData = new HashMap<>();
    }

    @Override
    public void start() {
        log(String.format("Starting a server at port: %d", super.getPort()));
        super.start();
    }

    public void updateSubject(final String subject, final String data) {
        subjectData.put(subject, data);
        pushUpdate(subject);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        connections.add(webSocket);
        log(String.format("New connection from %s:%d",
                webSocket.getRemoteSocketAddress().getHostName(),
                webSocket.getRemoteSocketAddress().getPort()));

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connections.remove(webSocket);

    }

    @Override
    public void onMessage(WebSocket webSocket, String msg) {
        log("New message: " + msg);

        ObjectMapper mapper = new ObjectMapper();

        try {
            Message message = mapper.readValue(msg, Message.class);

            switch(message.getType()) {
                case SUBSCRIBE:
                    log("A new user has subscribed");
                    subscribe(message.getSubject(), webSocket);
                break;
                case UNSUBSCRIBE:
                    log("A user has unsubscribed");
                    unsubscribe(message.getSubject(), webSocket);
                break;
            }
        } catch (IOException e) {
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("[ERROR] " + e.getMessage());
    }


    private void pushUpdate(final String subject) {
        if(!subscribers.containsKey(subject)) {
            return;
        }

        List<WebSocket> subscribersToBeDiscontinued = new LinkedList<>();

        subscribers.get(subject).forEach(subscriber -> {
            try {
                subscriber.send(subjectData.get(subject));
            } catch (Throwable e) {
                log("Could not send an update to the client");
                // unsubscribe implicitly
                subscribersToBeDiscontinued.add(subscriber);
            }
        });
        subscribersToBeDiscontinued.forEach(s -> unsubscribe(subject, s));
    }

    private void unsubscribe(final String subject, final WebSocket subscriber) {
        if(!subscribers.containsKey(subject) ||
           !subscribers.get(subject).contains(subscriber)) {
            return;
        }
        subscribers.get(subject).remove(subscriber);
    }

    private void subscribe(final String subject, final WebSocket subscriber) {
        if(subscribers.containsKey(subject)) {
            subscribers.get(subject).add(subscriber);
        } else {
            subscribers.put(subject, new HashSet<>(Arrays.asList(subscriber)));
        }
    }

    static void log(final String msg) {
        final String title = "[SERVER] ";
        System.out.println(title + msg);
    }

    static String getCurrentTimestamp() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
}
