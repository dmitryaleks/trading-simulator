package com.rest.matching;

import static org.junit.Assert.*;

import org.junit.Test;
import com.rest.model.Orders;
import java.util.LinkedList;
import java.util.List;

public class OrderQueueTest {

    @Test
    public void orderQueueTest() {
        OrderQueue oq = new OrderQueue();

        Orders second = new Orders(1, 1, "S", 10, 1000, "Second order");
        Orders first = new Orders(1, 1, "S", 8, 1000, "First order");
        Orders third = new Orders(1, 1, "S", 11, 1000, "Third order");

        oq.addOrder(second);
        oq.addOrder(first);
        oq.addOrder(third);

        List<Orders> expectedOrder = new LinkedList<>();
        expectedOrder.add(first);
        expectedOrder.add(second);
        expectedOrder.add(third);

        int i = 0;
        while(oq.size() > 0) {
            Orders hpOrder = oq.getHighestPriorityOrder();
            System.out.println(hpOrder.getNotes());
            assertEquals(hpOrder, expectedOrder.get(i++));
        }
    }
}
