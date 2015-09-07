package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-09-04.
 */
public class HashItem {
    static int ucDepth, ucFlag;
    static int mv, reserved;
    static int vl;
    static long lock0, lock1;

    public static void init() {
        ucDepth = ucFlag = 0;
        mv = reserved = vl =0;
        lock0 = lock1 = 0;
    }
}
