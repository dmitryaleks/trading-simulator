package com.rest.util;

import com.rest.model.Instrument;
import com.rest.model.Orders;
import com.rest.session.SessionManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManager {

    static public class OrderLookupException extends Exception {

        public OrderLookupException(final String msg) {
            super(msg);
        }

    }

    public static Orders getOrder(final String orderID) throws OrderLookupException {

        Session session = SessionManager.getSessionFactory().openSession();
        final String getInstrHQL = String.format("SELECT O FROM Orders O WHERE O.order_id = '%s'", orderID);
        Query query = session.createQuery(getInstrHQL);
        List<Orders> orders = query.list();
        session.close();

        if(orders.size() == 0) {
            throw new OrderLookupException(String.format("Order %s not found", orderID));
        }

        if(orders.size() > 1) {
            throw new OrderLookupException(String.format("Ambiguous order ID: %s", orderID));
        }

        return orders.get(0);
    }

    public static List<Orders> getAllOrders() throws OrderLookupException {

        Session session = SessionManager.getSessionFactory().openSession();
        final String getInstrHQL = String.format("SELECT O, I FROM Orders O, Instrument I WHERE I.instrument_id = O.inst_id ORDER BY O.order_id");
        Query query = session.createQuery(getInstrHQL);
        List<Object[]> res = query.list();
        session.close();
        if(res.size() == 0) {
            throw new OrderLookupException(String.format("No orders found"));
        }

        List<Orders> orders = res.stream().map(elm -> (Orders)elm[0]).collect(Collectors.toList());
        return orders;
    }

    public static List<JSONObject> getAllOrdersJSON() throws OrderLookupException {

        Session session = SessionManager.getSessionFactory().openSession();
        final String getInstrHQL = String.format("SELECT O, I FROM Orders O, Instrument I WHERE I.instrument_id = O.inst_id ORDER BY O.order_id");
        Query query = session.createQuery(getInstrHQL);
        List<Object[]> res = query.list();
        session.close();
        if(res.size() == 0) {
            throw new OrderLookupException(String.format("No orders found"));
        }

        List<JSONObject> orders = new LinkedList<>();
        for(Object[] elem: res) {
            Orders ord = (Orders) elem[0];
            Instrument instr = (Instrument) elem[1];
            try {
                orders.add(ord.getJSON().put("InstrCode", instr.getName()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return orders;
    }

    public static void commitOrder(final Orders ord) {
        Session session = SessionManager.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(ord);
        session.getTransaction().commit();
        session.close();
    }

    public static void updateOrder(final Orders ord) {
        Session session = SessionManager.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(ord);
        session.getTransaction().commit();
        session.close();
    }
}
