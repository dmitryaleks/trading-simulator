package com.rest;


import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/data")
public class Resource {

    ServletContainer a;

    public Resource() {
        System.out.println("Resource has been instantiated now");
    }

    static Counter counter = new Counter();
    static DateFormat df = new SimpleDateFormat("HH:mm:ss");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return String.format("%s: %d", getTimeNow(), counter.getNext());
    }

    private String getTimeNow() {
        return df.format(new Date());
    }
}
