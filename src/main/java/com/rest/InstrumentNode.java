package com.rest;


import com.rest.model.Instrument;
import com.rest.util.InstrumentManager;
import org.codehaus.jettison.json.JSONArray;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/instruments")
public class InstrumentNode {


    public InstrumentNode() {
        System.out.println("InstrumentNode has been instantiated now");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@DefaultValue("") @QueryParam("stockCode") String stockCode) {

        // NOTE: sample request: http://localhost:8080/orders?stockCode=6753.T

        if (stockCode.length() == 0) {
            // find all available instruments
            try {
                List<Instrument> instruments = InstrumentManager.getAllInstruments();
                JSONArray res = new JSONArray();
                instruments.stream().forEach(inst -> res.put(inst.getJSON()));
                return Response.status(200).entity(res.toString()).build();
            } catch (final InstrumentManager.InstrumentLookupException ex) {
                ex.printStackTrace();
            }
        } else {
            // lookup a particular instrument
            try {
                Instrument inst = InstrumentManager.getInstrument(stockCode);
                return Response.status(200).entity(inst.getJSON().toString()).build();
            } catch (final InstrumentManager.InstrumentLookupException ex) {
                ex.printStackTrace();
            }
        }

        return Response.status(210).entity("Instrument not found").build();
    }

}
