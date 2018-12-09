package com.rest.matching;

import com.rest.model.Orders;

public class PriceComparison {

    static boolean areInMatch(final Orders left, final Orders right) {

        if (left.getSide().equals(right.getSide())) {
            return false;
        }

        if (left.getSide().equals("S")) {
            if(left.getPrice() <= right.getPrice()) {
                return true;
            }
        } else {
            if(left.getPrice() >= right.getPrice()) {
                return true;
            }
        }

        return false;
    }

    static Double getMatchPrice(final Orders incoming, final Orders resting) {

        if(!areInMatch(incoming, resting)) {
            throw new RuntimeException("Orders are not in match");
        }

        return resting.getPrice();
    }

    static int priceCompare(final Orders left, final Orders right) {
        if (left.getPrice() == right.getPrice()) {
            return 0;
        } else {
            if (left.getSide() == "S") {
                if (left.getPrice() > right.getPrice()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if (left.getPrice() > right.getPrice()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }
}
