package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-09-04.
 */
public class HashItem {
    int ucDepth, ucFlag;
    int mv, reserved;
    int vl;
    long lock0, lock1;

    public void init() {
        ucDepth = ucFlag = 0;
        mv = reserved = vl =0;
        lock0 = lock1 = 0;
    }
}
