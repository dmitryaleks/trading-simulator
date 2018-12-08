package com.rest.matching;

import com.rest.model.Orders;
import com.rest.util.OrderManager;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.util.HashMap;
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

    public void addOrder(Orders ord) {

        // try to match with resting orders
        // TODO repeat this process until there are no matching orders found
        final String matchingKey = ord.getMatchingKey();
        if (queues.containsKey(matchingKey) && queues.get(matchingKey).size() > 0) {

            Orders hpOrder = queues.get(matchingKey).getHighestPriorityOrder();

            if(PriceComparison.areInMatch(hpOrder, ord)) {

                // TODO publish a trade
                double tradeQty = Math.min(
                        hpOrder.getQuantity() - hpOrder.getQuantity_filled(),
                        ord.getQuantity() - ord.getQuantity_filled());
                hpOrder.setQuantity_filled(hpOrder.getQuantity_filled() + tradeQty);
                ord.setQuantity_filled(ord.getQuantity_filled() + tradeQty);

                System.out.println(String.format("Order %s has matched with resting order %s", ord, hpOrder));

                if(hpOrder.getQuantity_filled() == hpOrder.getQuantity()) {
                    hpOrder.setStatus("C");
                    queues.get(matchingKey).deleteOrder(hpOrder);
                }

                OrderManager.updateOrder(hpOrder);
            }
        }

        if(ord.getQuantity() == ord.getQuantity_filled()) {
            ord.setStatus("C");
        }

        // TODO place remainder of the incoming order on a queue
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
    }

    public void cancelOrder(Orders ord) {
        // TODO
    }
}
