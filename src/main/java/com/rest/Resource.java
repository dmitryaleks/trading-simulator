package com.rest;


import com.rest.model.Instrument;
import com.rest.model.Orders;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Path("/orders")
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

        // NOTE: sample request: http://localhost:8080/orders?stockCode=6753.T

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();

        final String getInstrHQL = String.format("SELECT I FROM Instrument I WHERE I.name = '%s'", stockCode);
        Query query = session.createQuery(getInstrHQL);
        List<Instrument> instr = query.list();
        session.close();

        if(instr.size() == 0) {
            final String data = String.format("Instrument %s not found", stockCode);
            return Response.status(210).entity(data).build();
        }

        if(instr.size() > 1) {
            final String data = String.format("Ambiguous instrument code: %s", stockCode);
            return Response.status(210).entity(data).build();
        }

        Instrument inst = instr.get(0);
        System.out.println(String.format("Inst %d: %s", inst.getInstrument_id(), inst.getDescription()));
        JSONObject res = new JSONObject();
        try {
            res.put("InstrID", inst.getInstrument_id());
            res.put("InstrCode", inst.getName());
            res.put("Desciption", inst.getDescription());
            return Response.status(200).entity(res.toString()).build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.status(210).entity("Request unprocessed").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@DefaultValue("6758.T") @QueryParam("stockCode") String stockCode) {

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
