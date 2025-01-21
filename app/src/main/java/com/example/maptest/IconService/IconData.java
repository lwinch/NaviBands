package com.example.maptest.IconService;

import java.util.ArrayList;

public class IconData {
    int resId;
    ArrayList<Integer> bitmapData;
    Direction direction;

    public IconData(int resId, ArrayList<Integer> bitmapData) {
        this.resId = resId;
        this.bitmapData = bitmapData;
        direction = new Direction(Directions.UNKNOWN, "Unknown", "❓");
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public ArrayList<Integer> getBitmapData() {
        return bitmapData;
    }

    public void setBitmapData(ArrayList<Integer> bitmapData) {
        this.bitmapData = bitmapData;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
