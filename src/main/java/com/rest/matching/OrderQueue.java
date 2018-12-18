package com.rest.matching;

import com.rest.model.Orders;

import java.util.PriorityQueue;

public class OrderQueue {

    private PriorityQueue<Orders> queue = new PriorityQueue<>(new OrderComparator());

    public void addOrder(final Orders ord) {
        queue.add(ord);
    }

    public void deleteOrder(final Orders ord) {
        if(queue.contains(ord)) {
            queue.remove(ord);
        }
    }

    public Orders getHighestPriorityOrder() {
        return queue.peek();
    }

    public int size() {
        return queue.size();
    }

}
