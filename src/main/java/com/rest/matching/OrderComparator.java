package com.rest.matching;

import com.rest.model.Orders;
import java.util.Comparator;

public class OrderComparator implements Comparator<Orders> {


    public int compare(final Orders left, final Orders right) {
       int priceComparison = PriceComparison.priceCompare(left, right);
       if (priceComparison != 0) {
           return priceComparison;
       } else {
           if (left.getTimestamp().after(right.getTimestamp())) {
               return 1;
           } else {
               return -1;
           }
       }
    }
}
