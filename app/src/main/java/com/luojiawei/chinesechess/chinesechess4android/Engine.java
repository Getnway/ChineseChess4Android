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
    static int LIMIT_DEPTH = 64;    // �����������
    static long CLOCKS_PER_SEC = 1000;   //��λ��ms��
    static int mvResult;             // ����������������߷�
    static int[] nHistoryTable = new int[65536]; // ��ʷ��
    static int nDistance;                  // ������ڵ�Ĳ���
    static CompareHistory compareHistory = new CompareHistory();
    final static boolean NO_NULL = true;   // "SearchFull"�Ĳ������Ƿ�ղ��ü�
    final static boolean GEN_CAPTURE = true;  // "GenerateMoves"�������Ƿ�ֻ����ɱ���߷�
    final static int NULL_DEPTH = 2;      // �ղ��ü��Ĳü���� 2��3


    // MVV/LVAÿ�������ļ�ֵ
    // �����м�ֵ���ܺ���/��û��ֵ�Ĺ����ߡ�(Most Valuable Victim/Least Valuable Attacker)
    static int[] cucMvvLva = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 1, 1, 3, 4, 3, 2, 0, //��ʿ����
            5, 1, 1, 3, 4, 3, 2, 0
    };

    // ��MVV/LVAֵ(�������Ӽ�ֵ-�����Ӽ�ֵ��)
    static int MvvLva(int mv) {
        return (cucMvvLva[ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)]] << 3) - cucMvvLva[ChessboardUtil.currentMap[ChessboardUtil.getMoveSrc(mv)]];
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
        for (i = 3; i <= LIMIT_DEPTH; i++) {
            vl = SearchFull(-MATE_VALUE, MATE_VALUE, i, false);
            long time = System.currentTimeMillis() - t;
//            if(i>=5) {
                LogUtil.i(TAG, "Best: vl=" + String.valueOf(vl) +
                        "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvResult)) +
                        " To " + String.valueOf(ChessboardUtil.getMoveDst(mvResult)) +
                        "\tDepth=" + String.valueOf(i) +
                        "\tTime=" + String.valueOf(time));
//            }

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

    // �����߽�(Fail-Soft)��Alpha-Beta��������
    static int SearchFull(int vlAlpha, int vlBeta, int nDepth, boolean isNoNull) {
        int i, nGenMoves, pcCaptured;
        int vl, vlBest, mvBest;
        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
        // һ��Alpha-Beta��ȫ������Ϊ���¼����׶�

        if (Engine.nDistance > 0) {
            // 1. ����ˮƽ�ߣ�����þ�̬����(ע�⣺���ڿղ��ü�����ȿ���С����)
            if (nDepth <= 0) {
                return SearchQuiesc(vlAlpha, vlBeta);
            }

            // 1-1. ����ظ�����(ע�⣺��Ҫ�ڸ��ڵ��飬�����û���߷���)
            vl = ChessboardUtil.repStatus(1);
            if (vl != 0) {
                return ChessboardUtil.repValue(vl);
            }

            // 1-2. ���Ｋ����Ⱦͷ��ؾ�������
            if (Engine.nDistance == LIMIT_DEPTH) {
                return Value.Evaluate();
            }

            // 1-3. ���Կղ��ü�(���ڵ��Betaֵ��"MATE_VALUE"�����Բ����ܷ����ղ��ü�)
            // ���������������ʲô����������������ʲô����������һ�������Ļ��ᣩ
            if (!isNoNull && !ChessboardUtil.inCheck() && ChessboardUtil.nullOkay()) {
                ChessboardUtil.nullMove();
                //��һ��������ȵ�����
                vl = -SearchFull(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1, NO_NULL);
                ChessboardUtil.undoNullMove();
                if (vl >= vlBeta) { //���������Ȼ�õ��򳬹�Beta�ĳ̶ȣ��ͼ�����������������е��ŷ�Ҳ�ᳬ��Beta��
                    return vl;
                }
            }
        }

        // 2. ��ʼ�����ֵ������߷�
        vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)
        mvBest = 0;           // ��������֪�����Ƿ���������Beta�߷���PV�߷����Ա㱣�浽��ʷ��

        // 3. ����ȫ���߷�����������ʷ������
        nGenMoves = Rule.generateMoves(mvs, false);
        Arrays.sort(mvs, 0, nGenMoves, compareHistory);

        // 4. ��һ����Щ�߷��������еݹ�
        for (i = 0; i < nGenMoves; i++) {
            pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mvs[i])];
            if (ChessboardUtil.makeMove(mvs[i])) {
                // �������죨������ʱ������һ�㣩
                vl = -SearchFull(-vlBeta, -vlAlpha, ChessboardUtil.inCheck() ? nDepth : nDepth - 1, false);
                ChessboardUtil.undoMakeMove();

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

    // ��̬(Quiescence)��������
    static int SearchQuiesc(int vlAlpha, int vlBeta) {
        int i, nGenMoves;
        int vl, vlBest;
        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
        // һ����̬������Ϊ���¼����׶�

        // 1. ����ظ�����
        vl = ChessboardUtil.repStatus(1);
        if (vl != 0) {
            return ChessboardUtil.repValue(vl);
        }

        // 2. ���Ｋ����Ⱦͷ��ؾ�������
        if (Engine.nDistance == LIMIT_DEPTH) {
            return Value.Evaluate();
        }

        // 3. ��ʼ�����ֵ
        vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)

        if (ChessboardUtil.inCheck()) {
            // 4. �����������������ȫ���߷�
            nGenMoves = Rule.generateMoves(mvs, false);
            Arrays.sort(mvs, 0, nGenMoves, compareHistory);
        } else {

            // 5. �������������������������
            vl = Value.Evaluate();
            if (vl > vlBest) {
                vlBest = vl;
                if (vl >= vlBeta) {
                    return vl;
                }
                if (vl > vlAlpha) {
                    vlAlpha = vl;
                }
            }

            // 6. �����������û�нضϣ������ɳ����߷�
            nGenMoves = Rule.generateMoves(mvs, GEN_CAPTURE);
            Arrays.sort(mvs, 0, nGenMoves, new CompareMvvLva());
        }

        // 7. ��һ����Щ�߷��������еݹ�
        for (i = 0; i < nGenMoves; i++) {
            if (ChessboardUtil.makeMove(mvs[i])) {
                vl = -SearchQuiesc(-vlBeta, -vlAlpha);
                ChessboardUtil.undoMakeMove();

                // 8. ����Alpha-Beta��С�жϺͽض�
                if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
                    vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
                    if (vl >= vlBeta) { // �ҵ�һ��Beta�߷�
                        return vl;        // Beta�ض�
                    }
                    if (vl > vlAlpha) { // �ҵ�һ��PV�߷�
                        vlAlpha = vl;     // ��СAlpha-Beta�߽�
                    }
                }
            }
        }

        // 9. �����߷����������ˣ��������ֵ
        return vlBest == -MATE_VALUE ? Engine.nDistance - MATE_VALUE : vlBest;
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

    private static class CompareMvvLva implements Comparator<Integer> {
        //����ʷ������ıȽϺ���
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return MvvLva(lpmv2) - MvvLva(lpmv1);
        }
    }
}
