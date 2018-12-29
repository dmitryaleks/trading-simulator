package com.rest.util;

import com.rest.model.Instrument;
import com.rest.model.Orders;
import com.rest.model.Trade;
import com.rest.session.SessionManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Date;
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
        Orders order = (Orders)session.get(Orders.class, Long.valueOf(orderID));
        session.close();

        if(order == null) {
            throw new OrderLookupException(String.format("Order %s not found", orderID));
        }

        return order;
    }

    public static List<Orders> getAllOrders() {

        Session session = SessionManager.getSessionFactory().openSession();
        final String getInstrHQL = String.format("SELECT O, I FROM Orders O, Instrument I WHERE I.instrument_id = O.inst_id ORDER BY O.order_id");
        Query query = session.createQuery(getInstrHQL);
        List<Object[]> res = query.list();
        session.close();

        List<Orders> orders = res.stream().map(elm -> (Orders)elm[0]).collect(Collectors.toList());
        return orders;
    }

    public static List<JSONObject> getAllOrdersJSON(int limit) {

        Session session = SessionManager.getSessionFactory().openSession();
        final String getInstrHQL = String.format("SELECT O, I FROM Orders O, Instrument I WHERE I.instrument_id = O.inst_id ORDER BY O.order_id DESC");
        Query query = session.createQuery(getInstrHQL);
        query.setMaxResults(limit);
        List<Object[]> res = query.list();
        session.close();

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

    public static void commitOrdersWithTrade(final Orders resting_order, final Orders incoming_order, final Trade trade) {
        Session session = SessionManager.getSessionFactory().openSession();
        session.beginTransaction();

        trade.setResting_order_id(resting_order.getOrderID());
        session.update(resting_order);

        Long id = incoming_order.getOrderID();
        // incoming order may or may not have been persisted already
        if (id == null) {
            id = (Long) session.save(incoming_order);
        } else {
            session.update(incoming_order);
        }

        trade.setIncoming_order_id(id);
        session.save(trade);

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
