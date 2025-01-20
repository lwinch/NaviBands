package com.example.maptest.IconService;

import java.util.HashSet;

/**
 * Created by <a href="https://github.com/jashgopani/">jashgopani</a> on 03-07-2020
 */
public class Directions{
    /**
     * Left directions
     */
    public static final String SLIGHT_LEFT = "L0";
    public static final String LEFT = "L1";
    public static final String SHARP_LEFT = "L3";
    public static final String U_LEFT = "LU";

    /**
     * Right Directions
     */
    public static final String SLIGHT_RIGHT = "R0";
    public static final String RIGHT = "R1";
    public static final String SHARP_RIGHT = "R2";
    public static final String U_RIGHT = "RU";

    /**
     * Others
     */
    public static final String ARRIVED = "A";
    public static final String STRAIGHT = "S";
    public static final String UNKNOWN = "X";
    public static final String ALTERNATE = "LSR";

    private static final HashSet<String> leftDirections = new HashSet<>();
    private static final HashSet<String> rightDirections = new HashSet<>();

    static {
        leftDirections.add(SLIGHT_LEFT);
        leftDirections.add(LEFT);
        leftDirections.add(SHARP_LEFT);
        leftDirections.add(U_LEFT);

        rightDirections.add(SLIGHT_RIGHT);
        rightDirections.add(RIGHT);
        rightDirections.add(SHARP_RIGHT);
        rightDirections.add(U_RIGHT);

    }

    public static boolean isLeft(String d){
        return leftDirections.contains(d);
    }

    public static boolean isRight(String d){
        return rightDirections.contains(d);
    }

    public static boolean isUTurn(String d){
        return d.endsWith("U");
    }

}
