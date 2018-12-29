package com.rest;

import com.rest.matching.MatchingEngine;
import com.rest.model.Orders;
import com.rest.model.common.Side;
import com.rest.util.InstrumentManager;
import com.rest.util.OrderManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/order")
public class OrderNode {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@DefaultValue("1000000000") @QueryParam("limit") Integer limit) {

        List<JSONObject> orders = OrderManager.getAllOrdersJSON(limit);
        JSONArray res = new JSONArray();
        orders.stream().forEach(ord -> res.put(ord));
        return Response.status(200).entity(res.toString()).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(String request) {

        try {

            JSONObject req;
            try {
                req = new JSONObject(request);
            } catch (JSONException e) {
                throw new RuntimeException("Malformed request");
            }

            Double price;
            try {
                price = Double.valueOf(req.optString("Price"));
            } catch (Throwable e) {
                throw new RuntimeException("Price is missing");
            }

            Double quantity;
            try {
                quantity = Double.valueOf(req.optString("Quantity"));
            } catch (Throwable e) {
                throw new RuntimeException("Quantity is missing");
            }

            String instCode = req.optString("InstrCode");
            if (instCode == null) {
                throw new RuntimeException("Instrument Code is missing");
            }

            Side side;
            try {
                side = Side.valueOf(req.optString("Side"));
            } catch (Throwable e) {
                throw new RuntimeException("Side (Buy/Sell) is missing");
            }

            String notes = req.optString("Notes");

            int instID;
            try {
                instID = InstrumentManager.getInstrument(instCode).getInstrument_id();
            } catch (InstrumentManager.InstrumentLookupException e) {
                throw new RuntimeException("Unknown instrument code");
            }

            Orders ord = new Orders(1, instID, side, price, quantity, notes);
            MatchingEngine.getInstance().addOrder(ord);

            return Response.status(200).entity(ord.getJSON().toString()).build();

        } catch(Throwable e) {

            return Response.status(400).entity(getErrorResponse(e.getMessage()).toString()).build();
        }
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrder(String request) {

        try {
            JSONObject req = null;
            try {
                req = new JSONObject(request);
            } catch (JSONException e) {
                throw new RuntimeException("Malformed request");
            }

            String orderID = req.optString("OrderID");

            Orders ord = null;
            try {
                ord = OrderManager.getOrder(orderID);
                MatchingEngine.getInstance().cancelOrder(ord);
            } catch (OrderManager.OrderLookupException e) {
                e.printStackTrace();
            }

            return Response.status(200).entity(ord.getJSON().toString()).build();

        } catch(Throwable e) {

            return Response.status(400).entity(getErrorResponse(e.getMessage()).toString()).build();
        }

    }

    private JSONObject getErrorResponse(final String message) {

        JSONObject response = new JSONObject();
        try {
            response.put("message", message);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

}
