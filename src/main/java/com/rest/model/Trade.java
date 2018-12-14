package com.rest.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
public class Trade implements Serializable {

    @Id
    @Column(name = "trade_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trade_id;

    @Column(name="timestamp", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date timestamp;

    private int resting_order_id;
    private int incoming_order_id;
    private double price;
    private double quantity;

    public Trade() {
    }

    public Trade(int resting_order_id, int incoming_order_id, double quantity, double price) {
        this.resting_order_id = resting_order_id;
        this.incoming_order_id = incoming_order_id;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Date(new Date().getTime());
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return sdf.format(getTimestamp());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(int trade_id) {
        this.trade_id = trade_id;
    }

    public int getResting_order_id() {
        return resting_order_id;
    }

    public void setResting_order_id(int resting_order_id) {
        this.resting_order_id = resting_order_id;
    }

    public int getIncoming_order_id() {
        return incoming_order_id;
    }

    public void setIncoming_order_id(int incoming_order_id) {
        this.incoming_order_id = incoming_order_id;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = new Date(new Date().getTime());
    }

    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("TradeID", getTrade_id());
            res.put("RestingOrderID", getResting_order_id());
            res.put("IncomingOrderID", getIncoming_order_id());
            res.put("Quantity",getQuantity());
            res.put("Price",   getPrice());
            res.put("Timestamp", getTimestampString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String toString() {
        return String.format("Trade #%d: [%d<->%d] %.2f@%.2f", getTrade_id(), getResting_order_id(), getIncoming_order_id(), getQuantity(), getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade orders = (Trade) o;
        return trade_id == orders.trade_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trade_id);
    }
}
