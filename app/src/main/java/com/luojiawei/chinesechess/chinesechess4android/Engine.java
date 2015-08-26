package com.luojiawei.chinesechess.chinesechess4android;


import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by L1 on 15-08-26.
 * ��������
 */
public class Engine {
    static String TAG = "Engine";
    final static int MATE_VALUE = 10000;  // ��߷�ֵ���������ķ�ֵ
    final static int WIN_VALUE = MATE_VALUE - 100; // ������ʤ���ķ�ֵ���ޣ�������ֵ��˵���Ѿ�������ɱ����
    static int LIMIT_DEPTH = 32;    // �����������
    static long CLOCKS_PER_SEC = 1000;   //��λ��ms��
    static int mvResult;             // ����������������߷�
    static int[] nHistoryTable = new int[65536]; // ��ʷ��
    static int nDistance;                  // ������ڵ�Ĳ���
    static CompareHistory compareHistory = new CompareHistory();

    // �����߽�(Fail-Soft)��Alpha-Beta��������
    static int SearchFull(int vlAlpha, int vlBeta, int nDepth) {
        int i, nGenMoves, pcCaptured;
        int vl, vlBest, mvBest;
        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
        // һ��Alpha-Beta��ȫ������Ϊ���¼����׶�

        // 1. ����ˮƽ�ߣ��򷵻ؾ�������ֵ
        if (nDepth == 0) {
            return Value.Evaluate();
        }

        // 2. ��ʼ�����ֵ������߷�
        vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)
        mvBest = 0;           // ��������֪�����Ƿ���������Beta�߷���PV�߷����Ա㱣�浽��ʷ��

        // 3. ����ȫ���߷�����������ʷ������
        nGenMoves = Rule.generateMoves(mvs);
        Arrays.sort(mvs, 0, nGenMoves, compareHistory);

        // 4. ��һ����Щ�߷��������еݹ�
        for (i = 0; i < nGenMoves; i++) {
            pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mvs[i])];
            if (ChessboardUtil.makeMove(mvs[i])) {
                vl = -SearchFull(-vlBeta, -vlAlpha, nDepth - 1);
                ChessboardUtil.undoMakeMove(mvs[i], pcCaptured);

                // 5. ����Alpha-Beta��С�жϺͽض�
                if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
                    vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
                    if (vl >= vlBeta) { // �ҵ�һ��Beta�߷�
                        mvBest = mvs[i];  // Beta�߷�Ҫ���浽��ʷ��
                        break;            // Beta�ض�
                    }
                    if (vl > vlAlpha) { // �ҵ�һ��PV�߷�
                        mvBest = mvs[i];  // PV�߷�Ҫ���浽��ʷ��
                        vlAlpha = vl;     // ��СAlpha-Beta�߽�
                    }
                }
            }
        }

        // 5. �����߷����������ˣ�������߷�(������Alpha�߷�)���浽��ʷ���������ֵ
        if (vlBest == -MATE_VALUE) {
            // �����ɱ�壬�͸���ɱ�岽����������
            return Engine.nDistance - MATE_VALUE;
        }
        if (mvBest != 0) {
//            LogUtil.i(TAG, "***AI*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvBest)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mvBest)));
            // �������Alpha�߷����ͽ�����߷����浽��ʷ��
            Engine.nHistoryTable[mvBest] += nDepth * nDepth;
//            LogUtil.i(TAG,"nDistance:"+String.valueOf(nDistance));
            if (Engine.nDistance == 0) {
                // �������ڵ�ʱ��������һ������߷�(��Ϊȫ�����������ᳬ���߽�)��������߷���������
                Engine.mvResult = mvBest;
//                LogUtil.i(TAG, "Best:" + String.valueOf(mvBest));
            }
        }
        return vlBest;
    }

    // ����������������
    static void searchMain() {
        LogUtil.i(TAG, "searchMain");
        int i, vl;
        long t;

        // ��ʼ��
        initHistorytable(); //�����ʷ��
        t = System.currentTimeMillis();       // ��ʼ����ʱ��
        Engine.nDistance = 0; // ��ʼ����

        // �����������
        for (i = 1; i <= LIMIT_DEPTH; i++) {
            vl = SearchFull(-MATE_VALUE, MATE_VALUE, i);
            long time = System.currentTimeMillis() - t;
            LogUtil.i(TAG, "Best: vl=" + String.valueOf(vl) +
                    "\tDepth=" + String.valueOf(i) +
                    "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvResult)) +
                    " To " + String.valueOf(ChessboardUtil.getMoveDst(mvResult)) +
                    "\tTime=" + String.valueOf(time));

            // ������ɱ�壬����ֹ����
            if (vl > WIN_VALUE || vl < -WIN_VALUE) {
                break;
            }
            // ����һ�룬����ֹ����
            if (time > CLOCKS_PER_SEC) {
                break;
            }
        }
        LogUtil.i(TAG, "end search");
    }

    //��ʼ����ʷ��
    public static void initHistorytable() {
        for (int i = 0; i < nHistoryTable.length; i++) {
            nHistoryTable[i] = 0;
        }
    }

    private static class CompareHistory implements Comparator<Integer> {
        //����ʷ������ıȽϺ���
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return Engine.nHistoryTable[lpmv2] - Engine.nHistoryTable[lpmv1];
        }
    }
}
