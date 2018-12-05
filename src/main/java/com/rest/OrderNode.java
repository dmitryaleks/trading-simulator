package com.rest;

import com.rest.model.Orders;
import com.rest.session.SessionManager;
import com.rest.util.InstrumentManager;
import com.rest.util.OrderManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Path("/order")
public class OrderNode {

    public OrderNode() {
        System.out.println("OrderNode has been instantiated now");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@DefaultValue("6758.T") @QueryParam("instCode") String instCode) {

        try {
            List<JSONObject> orders = OrderManager.getAllOrders();
            JSONArray res = new JSONArray();
            orders.stream().forEach(ord -> res.put(ord));
            return Response.status(200).entity(res.toString()).build();
        } catch (final OrderManager.OrderLookupException ex) {
            ex.printStackTrace();
        }
        return Response.status(210).entity("No orders found").build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(String request) {

        System.out.println("Got a POST request for ORDER: " + request);

        JSONObject req = null;
        try {
            req = new JSONObject(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Double price = Double.valueOf(req.optString("price"));
        System.out.println(req.optString("price"));
        Double quantity = Double.valueOf(req.optString("quantity"));
        System.out.println(req.optString("quantity"));

        String instCode = req.optString("instCode");
        String notes = req.optString("notes");

        int instID = 0;
        try {
            instID = InstrumentManager.getInstrument(instCode).getInstrument_id();
        } catch (InstrumentManager.InstrumentLookupException e) {
            e.printStackTrace();
            return Response.status(400).entity("Unknown instrument code").build();
        }

        Session session = SessionManager.getSessionFactory().openSession();
        session.beginTransaction();

        Orders ord = new Orders(1, instID, price, quantity, notes);
        session.save(ord);
        session.getTransaction().commit();
        session.close();

        return Response.status(200).entity(ord.getJSON().toString()).build();
    }

}
