package com.luojiawei.chinesechess.chinesechess4android;


import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by L1 on 15-08-26.
 * 搜索引擎
 */
public class Engine {
    static String TAG = "Engine";
    final static int MATE_VALUE = 10000;  // 最高分值，即将死的分值
    final static int WIN_VALUE = MATE_VALUE - 100; // 搜索出胜负的分值界限，超出此值就说明已经搜索出杀棋了
    static int LIMIT_DEPTH = 32;    // 最大的搜索深度
    static long CLOCKS_PER_SEC = 1000;   //单位（ms）
    static int mvResult;             // 电脑搜索到的最好走法
    static int[] nHistoryTable = new int[65536]; // 历史表
    static int nDistance;                  // 距离根节点的步数
    static CompareHistory compareHistory = new CompareHistory();

    // 超出边界(Fail-Soft)的Alpha-Beta搜索过程
    static int SearchFull(int vlAlpha, int vlBeta, int nDepth) {
        int i, nGenMoves, pcCaptured;
        int vl, vlBest, mvBest;
        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
        // 一个Alpha-Beta完全搜索分为以下几个阶段

        // 1. 到达水平线，则返回局面评价值
        if (nDepth == 0) {
            return Value.Evaluate();
        }

        // 2. 初始化最佳值和最佳走法
        vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)
        mvBest = 0;           // 这样可以知道，是否搜索到了Beta走法或PV走法，以便保存到历史表

        // 3. 生成全部走法，并根据历史表排序
        nGenMoves = Rule.generateMoves(mvs);
        Arrays.sort(mvs, 0, nGenMoves, compareHistory);

        // 4. 逐一走这些走法，并进行递归
        for (i = 0; i < nGenMoves; i++) {
            pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mvs[i])];
            if (ChessboardUtil.makeMove(mvs[i])) {
                vl = -SearchFull(-vlBeta, -vlAlpha, nDepth - 1);
                ChessboardUtil.undoMakeMove(mvs[i], pcCaptured);

                // 5. 进行Alpha-Beta大小判断和截断
                if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
                    vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
                    if (vl >= vlBeta) { // 找到一个Beta走法
                        mvBest = mvs[i];  // Beta走法要保存到历史表
                        break;            // Beta截断
                    }
                    if (vl > vlAlpha) { // 找到一个PV走法
                        mvBest = mvs[i];  // PV走法要保存到历史表
                        vlAlpha = vl;     // 缩小Alpha-Beta边界
                    }
                }
            }
        }

        // 5. 所有走法都搜索完了，把最佳走法(不能是Alpha走法)保存到历史表，返回最佳值
        if (vlBest == -MATE_VALUE) {
            // 如果是杀棋，就根据杀棋步数给出评价
            return Engine.nDistance - MATE_VALUE;
        }
        if (mvBest != 0) {
//            LogUtil.i(TAG, "***AI*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvBest)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mvBest)));
            // 如果不是Alpha走法，就将最佳走法保存到历史表
            Engine.nHistoryTable[mvBest] += nDepth * nDepth;
//            LogUtil.i(TAG,"nDistance:"+String.valueOf(nDistance));
            if (Engine.nDistance == 0) {
                // 搜索根节点时，总是有一个最佳走法(因为全窗口搜索不会超出边界)，将这个走法保存下来
                Engine.mvResult = mvBest;
//                LogUtil.i(TAG, "Best:" + String.valueOf(mvBest));
            }
        }
        return vlBest;
    }

    // 迭代加深搜索过程
    static void searchMain() {
        LogUtil.i(TAG, "searchMain");
        int i, vl;
        long t;

        // 初始化
        initHistorytable(); //清空历史表
        t = System.currentTimeMillis();       // 初始化定时器
        Engine.nDistance = 0; // 初始步数

        // 迭代加深过程
        for (i = 1; i <= LIMIT_DEPTH; i++) {
            vl = SearchFull(-MATE_VALUE, MATE_VALUE, i);
            long time = System.currentTimeMillis() - t;
            LogUtil.i(TAG, "Best: vl=" + String.valueOf(vl) +
                    "\tDepth=" + String.valueOf(i) +
                    "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvResult)) +
                    " To " + String.valueOf(ChessboardUtil.getMoveDst(mvResult)) +
                    "\tTime=" + String.valueOf(time));

            // 搜索到杀棋，就终止搜索
            if (vl > WIN_VALUE || vl < -WIN_VALUE) {
                break;
            }
            // 超过一秒，就终止搜索
            if (time > CLOCKS_PER_SEC) {
                break;
            }
        }
        LogUtil.i(TAG, "end search");
    }

    //初始化历史表
    public static void initHistorytable() {
        for (int i = 0; i < nHistoryTable.length; i++) {
            nHistoryTable[i] = 0;
        }
    }

    private static class CompareHistory implements Comparator<Integer> {
        //按历史表排序的比较函数
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return Engine.nHistoryTable[lpmv2] - Engine.nHistoryTable[lpmv1];
        }
    }
}
