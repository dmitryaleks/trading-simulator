package com.rest;

import com.rest.matching.MatchingEngine;
import com.rest.model.Orders;
import com.rest.model.Trade;
import com.rest.util.InstrumentManager;
import com.rest.util.OrderManager;
import com.rest.util.TradeManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/trade")
public class TradeNode {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTrades(@DefaultValue("1000000000") @QueryParam("limit") Integer limit) {

        try {
            List<JSONObject> orders = TradeManager.getAllTrades(limit);
            JSONArray res = new JSONArray();
            orders.stream().forEach(ord -> res.put(ord));
            return Response.status(200).entity(res.toString()).build();
        } catch (final TradeManager.TradeLookupException ex) {
        }
        return Response.status(210).entity("No orders found").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/byInstr")
    public Response getTrades(@DefaultValue("6758.T") @QueryParam("instCode") String instCode,
                              @DefaultValue("1000000000") @QueryParam("limit") Integer limit) {
        // http://localhost:8080/trade/byInstr?instCode=6758.T&limit=10

        try {
            List<JSONObject> orders = TradeManager.getTrades(instCode, limit);
            JSONArray res = new JSONArray();
            orders.stream().forEach(ord -> res.put(ord));
            return Response.status(200).entity(res.toString()).build();
        } catch (final TradeManager.TradeLookupException ex) {
        }
        return Response.status(210).build();
    }

}
