package com.rest.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Instrument implements Serializable {

    @Id
    private int instrument_id;

    private Date timestamp;
    private String name;
    private String description;

    public Instrument() {
    }

    public Instrument(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public int getInstrument_id() {
        return instrument_id;
    }

    public void setInstrument_id(int instrument_id) {
        this.instrument_id = instrument_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("InstrID",   getInstrument_id());
            res.put("InstrCode", getName());
            res.put("Desciption",getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

}
