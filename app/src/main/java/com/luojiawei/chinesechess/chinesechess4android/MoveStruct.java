package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-26.
 * 历史走法信息
 */
public class MoveStruct {
    int mv, ucpcCaptured;   //走法，被吃的棋子（如果有的话）
    boolean ucbCheck;   //是否被将军
    long key;   //局面键值

    public void set(int mv, int ucpcCaptured, boolean ucbCheck, long key) {
        this.mv = mv;
        this.ucpcCaptured = ucpcCaptured;
        this.ucbCheck = ucbCheck;
        this.key = key;
    }

}
