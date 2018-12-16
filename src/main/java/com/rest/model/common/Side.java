package com.rest.model.common;

public enum Side {

    S("Sell"),
    B("Buy");

    private final String name;

    Side(final String name) {
        this.name = name;
    }

    public String getCode() {
        return name;
    }

    public static Side sideFromName(final String name) {
        for(final Side side: Side.values()) {
            if (side.name.equals(name)) {
                return side;
            }
        }
        throw new RuntimeException("Unknown side: " + name);
    }
}
