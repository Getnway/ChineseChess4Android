package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-26.
 * ��ʷ�߷���Ϣ
 */
public class MoveStruct {
    int mv, ucpcCaptured;   //�߷������Ե����ӣ�����еĻ���
    int key;   //�����ֵ
    boolean ucbCheck;   //�Ƿ񱻽���

    public void set(int mv, int ucpcCaptured, boolean ucbCheck, int key) {
        this.mv = mv;
        this.ucpcCaptured = ucpcCaptured;
        this.ucbCheck = ucbCheck;
        this.key = key;
    }

}
