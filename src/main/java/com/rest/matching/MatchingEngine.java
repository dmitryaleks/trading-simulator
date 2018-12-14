package com.rest.matching;

import com.rest.model.Orders;
import com.rest.model.Trade;
import com.rest.util.OrderManager;
import com.rest.util.TradeManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        System.out.println("MatchingEngine constructed");
        // load active orders from DB into "queues"
        try {
            List<Orders> existingOrders = OrderManager.getAllOrders();
            List<Orders> activeOrders = existingOrders.stream().
                    filter(ord -> ord.getStatus().equals("A")).collect(Collectors.toList());
            activeOrders.forEach(ord -> enqueueOrder(ord));
        } catch (OrderManager.OrderLookupException e) {
            e.printStackTrace();
        }
    }

    public void addOrder(Orders ord) {

        // try to match incoming order with resting orders
        final String matchingKey = ord.getMatchingKey();
        boolean matchingCompleted = false;
        boolean matchFound = false;

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

                // accumulate trades triggered by the incoming order
                Trade trade = new Trade(hpOrder.getOrderID(), ord.getOrderID(), tradeQty, tradePrice);

                System.out.println(String.format("Order %s has matched with resting order %s", ord, hpOrder));

                if(hpOrder.getQuantity_filled() == hpOrder.getQuantity()) {
                    hpOrder.setStatus("C");
                    queues.get(matchingKey).deleteOrder(hpOrder);
                }

                if (ord.getQuantity_filled() == ord.getQuantity()) {
                    matchingCompleted = true;
                    ord.setStatus("C");
                }

                matchFound = true;
                OrderManager.commitOrdersWithTrade(hpOrder, ord, trade);
            } else {
                matchingCompleted = true;
            }
        }

        if (!matchFound) {
            // simply commit incoming order to become a resting order
            OrderManager.commitOrder(ord);
        }

        // place the remainder of the incoming order on a queue
        if(ord.getStatus() == "A") {
            enqueueOrder(ord);
        }
    }

    public void cancelOrder(Orders ord) {
        ord.setStatus("C");
        queues.get(ord.getSelfKey()).deleteOrder(ord);
        OrderManager.updateOrder(ord);
    }

    private void enqueueOrder(final Orders ord) {
        if(queues.containsKey(ord.getSelfKey())) {
            queues.get(ord.getSelfKey()).addOrder(ord);
        } else {
            OrderQueue oq = new OrderQueue();
            oq.addOrder(ord);
            queues.put(ord.getSelfKey(), oq);
        }
    }

}
