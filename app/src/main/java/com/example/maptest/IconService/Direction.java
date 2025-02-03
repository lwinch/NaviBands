package com.example.maptest.IconService;

public enum Direction {
    ARRIVED(Directions.ARRIVED, "Arrived", "\uD83D\uDCCD", "\uD83D\uDCCD",
            ""),
    STRAIGHT(Directions.STRAIGHT, "Straight", "️⬆️", "️⬆",
            "⬜\uD83D\uDD3C⬜\n\uD83D\uDD3C⚫\uD83D\uDD3C\n⬜⚫⬜"),  //"□▲□\n◢■◣\n□■□"
    SHARP_RIGHT(Directions.SHARP_RIGHT, "Sharp right", "↘️", "↘",
            ""),
    RIGHT(Directions.RIGHT, "Right", "➡️", "➡",
            "⬜⬜▶️\n⬛⬛⬛▶️\n⬜⬜▶️\n"), //"□□◣\n■■■▶\n□□◤\n"
    SLIGHT_RIGHT(
            Directions.SLIGHT_RIGHT, "Slight right", "↗️", "↗",
            ""),
    U_RIGHT(Directions.U_RIGHT, "U-turn right", "↩️", "↩",
            ""),
    SLIGHT_LEFT(
            Directions.SLIGHT_LEFT, "Slight Left", "↖️", "↖",
            ""),
    LEFT(Directions.LEFT, "Left", "⬅️", "⬅",
            "⬜◀️⬜⬜\n◀️⬛⬛⬛\n⬜◀️⬜⬜\n"), //"□◢□□\n◀■■■\n□◥□□\n"
    SHARP_LEFT(Directions.SHARP_LEFT, "Sharp left", "↙️", "↙",
            ""),
    U_LEFT(Directions.U_LEFT, "U-turn left", "↪️", "↪",
            ""),
    ALTERNATE(Directions.ALTERNATE, "Alternate route", "❗", "❗",
            ""),
    UNKNOWN(Directions.UNKNOWN, "Unknown", "❓", "❓",
            ""),
    ;

    private final String shortName;
    private final String longName;
    private final String emoji;
    private final String symbol;
    private final String bigSymbol;

    Direction(String shortName, String longName, String emoji, String symbol, String bigSymbol) {
        this.shortName = shortName;
        this.longName = longName;
        this.emoji = emoji;
        this.symbol = symbol;
        this.bigSymbol = bigSymbol;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getBigSymbol() {
        return bigSymbol;
    }
}
