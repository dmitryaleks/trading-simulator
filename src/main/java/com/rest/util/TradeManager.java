package com.rest.util;

import com.rest.model.Orders;
import com.rest.model.Trade;
import com.rest.session.SessionManager;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeManager {

    static public class TradeLookupException extends Exception {

        public TradeLookupException(final String msg) {
            super(msg);
        }
    }

    public static List<JSONObject> getAllTrades() throws TradeLookupException {

        Session session = SessionManager.getSessionFactory().openSession();
        final String getInstrHQL = String.format("SELECT T FROM Trade T ORDER BY T.trade_id");
        Query query = session.createQuery(getInstrHQL);
        List<Trade> trades = query.list();
        session.close();
        if(trades.size() == 0) {
            throw new TradeLookupException(String.format("No trades found"));
        }

        return trades.stream().map(t -> t.getJSON()).collect(Collectors.toList());
    }

    public static List<JSONObject> getTrades(final String instrCode) throws TradeLookupException {

        try {
            final int instrID = InstrumentManager.getInstrument(instrCode).getInstrument_id();
            Session session = SessionManager.getSessionFactory().openSession();
            final String getInstrHQL = String.format("SELECT T, O FROM Trade T, Orders O WHERE O.order_id = T.resting_order_id ORDER BY T.trade_id");
            Query query = session.createQuery(getInstrHQL);
            List<Object[]> res = query.list();
            session.close();

            List<Trade> trades = res.stream()
                    .filter(elm -> ((Orders)elm[1]).getInst_id() == instrID)
                    .map(elm -> (Trade)elm[0]).collect(Collectors.toList());

            if(trades.size() == 0) {
                throw new TradeLookupException(String.format("No trades found"));
            }

            return trades.stream().map(t -> t.getJSON()).collect(Collectors.toList());

        } catch (InstrumentManager.InstrumentLookupException e) {
            e.printStackTrace();
            throw new TradeLookupException(String.format("Unknown instrument"));
        }
    }

    public static void commitTrade(final Trade trade) {
       List<Trade> trades = new LinkedList<>();
       trades.add(trade);
       commitTrades(trades);
    }

    public static void commitTrades(final List<Trade> trades) {
        Session session = SessionManager.getSessionFactory().openSession();
        session.beginTransaction();
        for(Trade trade: trades) {
            session.save(trade);
        }
        session.getTransaction().commit();
        session.close();
    }
}
