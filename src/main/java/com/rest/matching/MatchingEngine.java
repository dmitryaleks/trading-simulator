package com.rest.matching;

import com.rest.model.Orders;
import com.rest.model.Trade;
import com.rest.session.SessionManager;
import com.rest.util.OrderManager;
import com.rest.util.TradeManager;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MatchingEngine {

    // key format: "<instr>-<side>", E.g.: "6758.T-S".
    private Map<String, OrderQueue> queues = new HashMap<>();

    private static MatchingEngine me;

    public static MatchingEngine getInstance() {
        if (me == null) {
            me = new MatchingEngine();
        }

        return me;
    }

    public MatchingEngine() {
        // TODO load active orders from DB into "queues"
    }

    public void addOrder(Orders ord) {

        // try to match incoming order with resting orders
        final String matchingKey = ord.getMatchingKey();
        boolean matchingCompleted = false;

        List<Trade> incomingTrades = new LinkedList<>();

        // repeat matching until there are no matching orders found or incoming order got filled
        while (queues.containsKey(matchingKey) &&
               queues.get(matchingKey).size() > 0 &&
               !matchingCompleted) {

            Orders hpOrder = queues.get(matchingKey).getHighestPriorityOrder();

            if (PriceComparison.areInMatch(hpOrder, ord)) {

                double tradeQty = Math.min(
                        hpOrder.getQuantity() - hpOrder.getQuantity_filled(),
                        ord.getQuantity() - ord.getQuantity_filled());

                double tradePrice = PriceComparison.getMatchPrice(ord, hpOrder);

                hpOrder.addTrade(tradeQty, tradePrice);
                ord.addTrade(tradeQty, tradePrice);

                // accumulate trades on incoming order
                incomingTrades.add(new Trade(ord.getOrderID(), tradeQty, tradePrice));

                System.out.println(String.format("Order %s has matched with resting order %s", ord, hpOrder));

                if(hpOrder.getQuantity_filled() == hpOrder.getQuantity()) {
                    hpOrder.setStatus("C");
                    queues.get(matchingKey).deleteOrder(hpOrder);
                }

                // publish trade
                TradeManager.commitTrade(new Trade(hpOrder.getOrderID(), tradeQty, tradePrice));
                OrderManager.updateOrder(hpOrder);

                if (ord.getQuantity_filled() == ord.getQuantity()) {
                    matchingCompleted = true;
                    ord.setStatus("C");
                }
            } else {
                matchingCompleted = true;
            }
        }

        // place the remainder of the incoming order on a queue
        if(ord.getStatus() == "A") {
            if(queues.containsKey(ord.getSelfKey())) {
                queues.get(ord.getSelfKey()).addOrder(ord);
            } else {
                OrderQueue oq = new OrderQueue();
                oq.addOrder(ord);
                queues.put(ord.getSelfKey(), oq);
            }
        }

        OrderManager.commitOrder(ord);
        for(Trade t: incomingTrades) {
            t.setOrder_id(ord.getOrderID());
        }
        TradeManager.commitTrades(incomingTrades);
    }

    public void cancelOrder(Orders ord) {
        ord.setStatus("C");
        queues.get(ord.getSelfKey()).deleteOrder(ord);
        OrderManager.updateOrder(ord);
    }
}
