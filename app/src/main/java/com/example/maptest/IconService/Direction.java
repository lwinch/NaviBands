package com.example.maptest.IconService;

public class Direction {
    String shortName;
    String longName;
    String emoji;
//    ArrayList<Integer> bitmapData;

    public Direction(String shortName, String longName, String emoji) {
        this.shortName = shortName;
        this.longName = longName;
        this.emoji = emoji;
//        Bitmap bitmap = PixelProcessingUtilities.getComparableBitmap(context, this.iconResId);
//        this.bitmapData = PixelProcessingUtilities.getAlphaPixels(bitmap);
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
