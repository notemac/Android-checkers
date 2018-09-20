package ru.bstu.checkers;

/** Item представляет шашку или пустую клетку */
public class Item {
    /** Черная шашка, белая, пустая клетка */
    enum ITEM_TYPE {black, white, square};
    /** Количество диагоналей */
    public static final int WAYS_COUNT = 13;
    int id;
    ITEM_TYPE type;
    /**Дамка?*/
    boolean isKing;
    /** Подсвечена ли клетка?*/
    boolean isHighlight;
    /**Диагонали, на которых лежит данный Item*/
    boolean ways[];
    /*boolean doubleWayG1A7, doubleWayH2B8, doubleWayG1H2, doubleWayA7B8, tripleWayC1A3, tripleWayC1H6, tripleWayH6F8,
            tripleWayA3F8, ultraWayA5D8, ultraWayH4D8, ultraWayE1A5, ultraWayE1H4, goldWay;*/
    public Item() {}
    public Item(int id, ITEM_TYPE type, boolean doubleWayG1A7, boolean doubleWayH2B8, boolean doubleWayG1H2, boolean doubleWayA7B8,
                boolean tripleWayC1A3, boolean tripleWayC1H6, boolean tripleWayH6F8, boolean tripleWayA3F8,
                boolean ultraWayA5D8, boolean ultraWayH4D8, boolean ultraWayE1A5, boolean ultraWayE1H4,
                boolean goldWay)
    {
        this.id = id; this.type = type;
        this.isKing = this.isHighlight = false;
        ways = new boolean[WAYS_COUNT];
        ways[0] = doubleWayG1A7; ways[1] = doubleWayH2B8; ways[2] = doubleWayG1H2;
        ways[3] = doubleWayA7B8; ways[4] = tripleWayC1A3; ways[5] = tripleWayC1H6;
        ways[6] = tripleWayH6F8; ways[7] = tripleWayA3F8; ways[8] = ultraWayA5D8;
        ways[9] = ultraWayH4D8; ways[10] = ultraWayE1A5; ways[11] = ultraWayE1H4;
        ways[12] = goldWay;
        /*this.doubleWayG1A7 = doubleWayG1A7; this.doubleWayH2B8 = doubleWayH2B8; this.doubleWayG1H2 = doubleWayG1H2;
        this.doubleWayA7B8 = doubleWayA7B8; this.tripleWayC1A3 = tripleWayC1A3; this.tripleWayC1H6 = tripleWayC1H6;
        this.tripleWayH6F8 = tripleWayH6F8; this.tripleWayA3F8 = tripleWayA3F8; this.ultraWayA5D8 = ultraWayA5D8;
        this.ultraWayH4D8 = ultraWayH4D8; this.ultraWayE1A5 = ultraWayE1A5; this.ultraWayE1H4 = ultraWayE1H4;
        this.goldWay = goldWay;*/
    }
}
