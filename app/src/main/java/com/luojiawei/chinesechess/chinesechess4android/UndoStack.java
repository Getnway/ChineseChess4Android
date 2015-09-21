package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-09-21.
 */
public class UndoStack {
    int mv;
    int pcCaptured;
    public UndoStack(int mv, int pcCaptured){
        this.mv = mv;
        this.pcCaptured = pcCaptured;
    }
    /*
    int stackSize = 700;
    int[] mvs;
    int[] pcCaptureds; //被吃子（如果有的话）
    int top;
    public UndoStack(){
        mvs = new int[stackSize];
        pcCaptureds = new int[stackSize];
        top = 0;
    }

    public int size(){
        return top;
    }

    public void push(int mv, int pcCaptured){
        mvs[top] = mv;
        pcCaptureds[top] = pcCaptured;
        ++top;
        if(top == stackSize){   //栈的大小达到极限，则悔棋栈大小增加一倍
            stackSize *= 2;
            int[] tmpMvs = new int[stackSize];
            int[] tmpPcCaptureds = new int[stackSize];
            for(int i = 0; i < top; ++i){
                tmpMvs[i] = mvs[i];
                tmpPcCaptureds[i] = pcCaptureds[i];
            }
            mvs = tmpMvs;
            pcCaptureds = tmpPcCaptureds;
        }
    }

    public int pop(){
        if(top == 0){
            return 0;
        }
        return mvs[--top];
    }
    */
}
