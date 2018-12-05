package com.rest.model;
import com.rest.util.InstrumentManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Orders implements Serializable {

    @Id
    @Column(name = "order_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;

    private int version;
    private int inst_id;
    private double price;
    private double quantity;
    private String notes;

    public Orders() {

    }

    public Orders(int version, int inst_id, double price, double quantity, String notes) {
        this.version = version;
        this.inst_id = inst_id;
        this.price = price;
        this.quantity = quantity;
        this.notes = notes;
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

    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("OrderID", getOrderID());
            res.put("Quantity",getQuantity());
            res.put("Price",   getPrice());
            res.put("InstrID", getInst_id());
            res.put("Notes",   getNotes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

}
