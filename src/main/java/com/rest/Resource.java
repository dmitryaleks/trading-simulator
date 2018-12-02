package com.rest;


import com.rest.model.Orders;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Path("/data")
public class Resource {

    ServletContainer a;

    public Resource() {
        System.out.println("Resource has been instantiated now");
    }

    static Counter counter = new Counter();
    static DateFormat df = new SimpleDateFormat("HH:mm:ss");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@DefaultValue("6758.T") @QueryParam("stockCode") String stockCode) {

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Orders ord = new Orders(1, 2, 8800., 10000., "Test order");
        session.save(ord);
        session.getTransaction().commit();
        session.close();

        final double price = 7800.;
        final String data = String.format("Stock %s: %f", stockCode, price);
        return Response.status(200).entity(data).build();
    }

}
