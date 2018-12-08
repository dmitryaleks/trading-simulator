package com.rest.model;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.persistence.*;

@Entity
public class Orders implements Serializable {

    @Id
    @Column(name = "order_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;

    @Column(name="timestamp", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date timestamp;

    private int version;
    private int inst_id;
    private double price;
    private double quantity;
    private String notes;
    private String side;
    private double quantity_filled;
    private String status;

    public Orders() {
    }

    public Orders(int orderID) {
        this.order_id = orderID;
    }

    public Orders(int version, int inst_id, String side, double price, double quantity, String notes) {
        this.version = version;
        this.inst_id = inst_id;
        this.price = price;
        this.quantity = quantity;
        this.notes = notes;
        this.side = side;
        this.status = "A";
        this.quantity_filled = 0;
        this.timestamp = new Date(new java.util.Date().getTime());
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getOrderID() {
        return order_id;
    }

    public void setOrderID(int orderID) {
        this.order_id = orderID;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getInst_id() {
        return inst_id;
    }

    public void setInst_id(int inst_id) {
        this.inst_id = inst_id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getQuantity_filled() {
        return quantity_filled;
    }

    public void setQuantity_filled(double quantity_filled) {
        this.quantity_filled = quantity_filled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @PrePersist
    protected void onCreate() {
        timestamp = new Date(new java.util.Date().getTime());
    }

    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("OrderID", getOrderID());
            res.put("Quantity",getQuantity());
            res.put("Price",   getPrice());
            res.put("InstrID", getInst_id());
            res.put("Notes",   getNotes());
            res.put("Side",    getSide());
            res.put("QuantityFilled", getQuantity_filled());
            res.put("Status",  getStatus());
            res.put("Timestamp", getTimestampString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getMatchingKey() {
        return String.format("%d-%s", getInst_id(), getSide().equals("B")?"S":"B");
    }

    public String getSelfKey() {
        return String.format("%d-%s", getInst_id(), getSide());
    }

    public String toString() {
        return String.format("#%d: [%d] %s %.2f@%.2f", getOrderID(), getInst_id(), getSide(), getQuantity(), getPrice());
    }
}
