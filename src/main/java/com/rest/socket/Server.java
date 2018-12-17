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

    private Set<WebSocket> connections;
    private Set<WebSocket> subscribers;

    public Server(int port) {
        super(new InetSocketAddress(port));
        connections = new HashSet<>();
        subscribers = new HashSet<>();
        log(String.format("Starting a server at port: %d", port));

        Timer timer = new Timer();
        List<WebSocket> subscribersToBeDiscontinued = new LinkedList<>();
        timer.scheduleAtFixedRate(new TimerTask() { public void run() {
                subscribers.forEach(subscriber -> {
                    try {
                        subscriber.send("[UPDATE] " + getCurrentTimestamp());
                    } catch (Throwable e) {
                        log("Could not send an update to client: " +
                                subscriber.getRemoteSocketAddress().getHostName());
                        // unsubscribe implicitly
                        subscribersToBeDiscontinued.add(subscriber);
                    }
                });
                subscribersToBeDiscontinued.forEach(s -> subscribers.remove(s));;
        }}, 0,3000);
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
                    log("User subscribed");
                    subscribers.add(webSocket);
                break;
                case UNSUBSCRIBE:
                    log("User unsubscribed");
                    subscribers.remove(webSocket);
                break;
            }
        } catch (IOException e) {
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("[ERROR] " + e.getMessage());
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
