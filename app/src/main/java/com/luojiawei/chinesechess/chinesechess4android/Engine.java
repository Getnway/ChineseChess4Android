package com.luojiawei.chinesechess.chinesechess4android;

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
    static int LIMIT_DEPTH = 64;    // 最大的搜索深度
    static long CLOCKS_PER_SEC = 1000;   //单位（ms）
    static int mvResult;             // 电脑搜索到的最好走法
    static int[] nHistoryTable = new int[65536]; // 历史表
    static int nDistance;                  // 距离根节点的步数
    static CompareHistory compareHistory = new CompareHistory();
    final static boolean NO_NULL = true;   // "SearchFull"的参数
    static boolean GEN_CAPTURE = true;  // "GenerateMoves"参数
    final static int NULL_DEPTH = 2;      // 空步裁剪的裁剪深度 2或3
    final static int HASH_SIZE = 1 << 20; // 置换表大小
    final static int HASH_ALPHA = 1;      // ALPHA节点的置换表项
    final static int HASH_BETA = 2;       // BETA节点的置换表项
    final static int HASH_PV = 3;         // PV节点的置换表项
    static int[][] mvKillers = new int[LIMIT_DEPTH][2]; // 杀手走法表
    static HashItem[] hashTable = new HashItem[HASH_SIZE]; // 置换表
    // 走法排序结构
    final static int PHASE_HASH = 0;
    final static int PHASE_KILLER_1 = 1;
    final static int PHASE_KILLER_2 = 2;
    final static int PHASE_GEN_MOVES = 3;
    final static int PHASE_REST = 4;
    static int mvHash, mvKiller1, mvKiller2; // 置换表走法和两个杀手走法
    static int nPhase, nIndex, nGenMoves;    // 当前阶段，当前采用第几个走法，总共有几个走法
    static Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];           // 所有的走法

    static {
        for (int i = 0; i < HASH_SIZE; i++){
            hashTable[i] = new HashItem();
        }
    }

    /**     -----------------------------初始化操作-----------------------------     */
    //初始化历史表
    public static void initHistorytable() {
        for (int i = 0; i < nHistoryTable.length; i++) {
            nHistoryTable[i] = 0;
        }
    }

    //初始化杀手走法表
    public static void initKillers(){
        for (int i = 0; i < LIMIT_DEPTH; i++){
            mvKillers[i][0] = mvKillers[i][1] = 0;
        }
    }

    //初始化哈希表
    public static void initHashTable(){
        for (int i = 0; i < HASH_SIZE; i++){
            hashTable[i].init();
        }
    }
    /**     -----------------------------end 初始化操作-----------------------------     */


    /**     -----------------------------走法排序结构-----------------------------     */
    // 初始化，设定置换表走法和两个杀手走法
    static void initMove(int mvHash_) {
        mvHash = mvHash_;
        mvKiller1 = mvKillers[nDistance][0];
        mvKiller2 = mvKillers[nDistance][1];
        nPhase = PHASE_HASH;
    }

    // 得到下一个走法
    static int nextMove() {
        int mv;
        switch (nPhase) {
            // "nPhase"表示着法启发的若干阶段，依次为：

            // 0. 置换表着法启发，完成后立即进入下一阶段；
            case PHASE_HASH:
                nPhase = PHASE_KILLER_1;
                if (mvHash != 0) {
                    return mvHash;
                }
                // 技巧：这里没有"break"，表示"switch"的上一个"case"执行完后紧接着做下一个"case"，下同

                // 1. 杀手着法启发(第一个杀手着法)，完成后立即进入下一阶段；
            case PHASE_KILLER_1:
                nPhase = PHASE_KILLER_2;
                if (mvKiller1 != mvHash && mvKiller1 != 0 && Rule.isLegalMove(mvKiller1)) {
                    return mvKiller1;
                }

                // 2. 杀手着法启发(第二个杀手着法)，完成后立即进入下一阶段；
            case PHASE_KILLER_2:
                nPhase = PHASE_GEN_MOVES;
                if (mvKiller2 != mvHash && mvKiller2 != 0 && Rule.isLegalMove(mvKiller2)) {
                    return mvKiller2;
                }

                // 3. 生成所有着法，完成后立即进入下一阶段；
            case PHASE_GEN_MOVES:
                nPhase = PHASE_REST;
                nGenMoves = Rule.generateMoves(mvs,false);
                Arrays.sort(mvs,0,nGenMoves,new CompareHistory());
                nIndex = 0;

                // 4. 对剩余着法做历史表启发；
            case PHASE_REST:
                while (nIndex < nGenMoves) {
                    mv = mvs[nIndex];
                    nIndex ++;
                    if (mv != mvHash && mv != mvKiller1 && mv != mvKiller2) {
                        return mv;
                    }
                }

                // 5. 没有着法了，返回零。
            default:
                return 0;
        }
    }
    /**     -----------------------------end 走法排序结构-----------------------------     */

    /**     -----------------------------提取和记录Hash-----------------------------     */
    // 提取置换表项
    static int[] probeHash(int vlAlpha, int vlBeta, int nDepth, ZobristStruct zobr) {
        boolean bMate; // 杀棋标志：如果是杀棋，那么不需要满足深度条件
        HashItem hsh;
        int[] mvAndValue = new int[2];  //走法和分值

        hsh = hashTable[zobr.key & (HASH_SIZE - 1)];
        if (hsh.lock0 != zobr.lock0 || hsh.lock1 != zobr.lock1) {
            mvAndValue[0] = 0;
            mvAndValue[1] = -MATE_VALUE;
            return mvAndValue;
        }
        mvAndValue[0] = hsh.mv;
        bMate = false;
        if (hsh.vl > WIN_VALUE) {
            hsh.vl -= nDistance;
            bMate = true;
        } else if (hsh.vl < -WIN_VALUE) {
            hsh.vl += nDistance;
            bMate = true;
        }
        if (hsh.ucDepth >= nDepth || bMate) {
            if (hsh.ucFlag == HASH_BETA) {
                mvAndValue[1] = hsh.vl >= vlBeta ? hsh.vl : -MATE_VALUE;
                return mvAndValue;
            } else if (hsh.ucFlag == HASH_ALPHA) {
                mvAndValue[1] = hsh.vl <= vlAlpha ? hsh.vl : -MATE_VALUE;
                return mvAndValue;
            }
            mvAndValue[1] = hsh.vl;
            return mvAndValue;
        }
        mvAndValue[1] = -MATE_VALUE;
        return mvAndValue;
    }

    // 保存置换表项
    static void recordHash(int nFlag, int vl, int nDepth, int mv, ZobristStruct zobr) {
        HashItem hsh;
        hsh = hashTable[zobr.key & (HASH_SIZE - 1)];
        if (hsh.ucDepth > nDepth) {
            return;
        }
        hsh.ucFlag = nFlag;
        hsh.ucDepth = nDepth;
        if (vl > WIN_VALUE) {
            hsh.vl = vl + nDistance;
        } else if (vl < -WIN_VALUE) {
            hsh.vl = vl - nDistance;
        } else {
            hsh.vl = vl;
        }
        hsh.mv = mv;
        hsh.lock0 = zobr.lock0;
        hsh.lock1 = zobr.lock1;
        hashTable[zobr.key & (HASH_SIZE - 1)] = hsh;
    }
    /**     -----------------------------end 提取和记录Hash-----------------------------     */

    /**     -----------------------------MVV/LVA-----------------------------     */
    // MVV/LVA每种子力的价值
    // “最有价值的受害者/最没价值的攻击者”(Most Valuable Victim/Least Valuable Attacker)
    static int[] cucMvvLva = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 1, 1, 3, 4, 3, 2, 0, //将士象马车
            5, 1, 1, 3, 4, 3, 2, 0
    };

    // 求MVV/LVA值(“被吃子价值-攻击子价值”)
    static int MvvLva(int mv) {
        return (cucMvvLva[ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)]] << 3) - cucMvvLva[ChessboardUtil.currentMap[ChessboardUtil.getMoveSrc(mv)]];
    }
    /**     -----------------------------end MVV/LVA-----------------------------     */


    /**     -----------------------------搜索函数-----------------------------     */
    /**
     * 迭代加深搜索过程
     */
    static void searchMain() {
        LogUtil.i(TAG, "searchMain");
        int i, vl;
        long t;

        // 初始化
        initHistorytable(); //初始化历史表
        initKillers(); //初始化杀棋走法
        initHashTable(); //初始化置换表
        t = System.currentTimeMillis();       // 初始化定时器
        Engine.nDistance = 0; // 初始步数

        // 迭代加深过程
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

    /**
     * 超出边界(Fail-Soft)的Alpha-Beta搜索过程
     * @param vlAlpha 下边界
     * @param vlBeta 上边界
     * @param nDepth 深度
     * @param isNoNull 是否空步裁剪
     * @return 返回最佳分值
     */
    static int SearchFull(int vlAlpha, int vlBeta, int nDepth, boolean isNoNull) {
        int i, nGenMoves;
        int vl, vlBest, mvBest;
        int nHashFlag = 0, mvHash, mv;
        int[] mvAndValue;
        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
        // 一个Alpha-Beta完全搜索分为以下几个阶段

        if (Engine.nDistance > 0) {
            // 1. 到达水平线，则调用静态搜索(注意：由于空步裁剪，深度可能小于零)
            if (nDepth <= 0) {
                return SearchQuiesc(vlAlpha, vlBeta);
            }

            // 1-1. 检查重复局面(注意：不要在根节点检查，否则就没有走法了)
            vl = ChessboardUtil.repStatus(1);
            if (vl != 0) {
                return ChessboardUtil.repValue(vl);
            }

            // 1-2. 到达极限深度就返回局面评价
            if (Engine.nDistance == LIMIT_DEPTH) {
                return Value.Evaluate();
            }

            // 1-3. 尝试置换表裁剪，并得到置换表走法
            mvAndValue = probeHash(vlAlpha, vlBeta, nDepth, ChessboardUtil.zobr);
            mvHash = mvAndValue[0];
            vl = mvAndValue[1];
            if (vl > -MATE_VALUE) {
                return vl;
            }

            // 1-4. 尝试空步裁剪(根节点的Beta值是"MATE_VALUE"，所以不可能发生空步裁剪)
            // （“如果我在这里什么都不做，对手能做什么？”给对手一个出击的机会）
            if (!isNoNull && !ChessboardUtil.inCheck() && ChessboardUtil.nullOkay()) {
                ChessboardUtil.nullMove();
                //做一个减少深度的搜索
                vl = -SearchFull(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1, NO_NULL);
                ChessboardUtil.undoNullMove();
                if (vl >= vlBeta) { //如果局面仍然好到或超过Beta的程度，就假设如果你搜索了所有的着法也会超过Beta。
                    return vl;
                }
            }
        }else{
            mvHash = 0;
        }

        // 2. 初始化最佳值和最佳走法
        vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)
        mvBest = 0;           // 这样可以知道，是否搜索到了Beta走法或PV走法，以便保存到历史表


//        // 3. 生成全部走法，并根据历史表排序
//        nGenMoves = Rule.generateMoves(mvs, false);
//        Arrays.sort(mvs, 0, nGenMoves, compareHistory);

        // 3. 初始化走法排序结构
        initMove(mvHash);


        // 4. 逐一走这些走法，并进行递归
//        for (i = 0; i < nGenMoves; i++) {
//            if (ChessboardUtil.makeMove(mvs[i])) {
        while ((mv = nextMove()) !=0 ){
            if(ChessboardUtil.makeMove(mv)){
                // 将军延伸（被将军时多搜索一层）
                vl = -SearchFull(-vlBeta, -vlAlpha, ChessboardUtil.inCheck() ? nDepth : nDepth - 1, false);
                ChessboardUtil.undoMakeMove();

                // 5. 进行Alpha-Beta大小判断和截断
                if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
                    vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
                    if (vl >= vlBeta) { // 找到一个Beta走法
//                        mvBest = mvs[i];  // Beta走法要保存到历史表
                        nHashFlag = HASH_BETA;
                        mvBest = mv;
                        break;            // Beta截断
                    }
                    if (vl > vlAlpha) { // 找到一个PV走法
//                        mvBest = mvs[i];  // PV走法要保存到历史表
                        nHashFlag = HASH_PV;
                        mvBest = mv;
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

        //记录到置换表
        recordHash(nHashFlag, vlBest, nDepth, mvBest, ChessboardUtil.zobr);
        if (mvBest != 0) {
//            LogUtil.i(TAG, "***AI*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvBest)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mvBest)));
            // 如果不是Alpha走法，就将最佳走法保存到历史表
//            Engine.nHistoryTable[mvBest] += nDepth * nDepth;
//            LogUtil.i(TAG,"nDistance:"+String.valueOf(nDistance));
            //保存最佳走法
            setBestMove(mvBest, nDepth);
            if (Engine.nDistance == 0) {
                // 搜索根节点时，总是有一个最佳走法(因为全窗口搜索不会超出边界)，将这个走法保存下来
                Engine.mvResult = mvBest;
//                LogUtil.i(TAG, "Best:" + String.valueOf(mvBest));
            }
        }
        return vlBest;
    }


    /**
     * 静态(Quiescence)搜索过程
     * @param vlAlpha 下边界
     * @param vlBeta 上边界
     * @return 返回最佳值
     */
    static int SearchQuiesc(int vlAlpha, int vlBeta) {
        int i, nGenMoves;
        int vl, vlBest;
        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
        // 一个静态搜索分为以下几个阶段

        // 1. 检查重复局面
        vl = ChessboardUtil.repStatus(1);
        if (vl != 0) {
            return ChessboardUtil.repValue(vl);
        }

        // 2. 到达极限深度就返回局面评价
        if (Engine.nDistance == LIMIT_DEPTH) {
            return Value.Evaluate();
        }

        // 3. 初始化最佳值
        vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)

        if (ChessboardUtil.inCheck()) {
            // 4. 如果被将军，则生成全部走法
            nGenMoves = Rule.generateMoves(mvs, false);
            Arrays.sort(mvs, 0, nGenMoves, compareHistory);
        } else {

            // 5. 如果不被将军，先做局面评价
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

            // 6. 如果局面评价没有截断，再生成吃子走法
            nGenMoves = Rule.generateMoves(mvs, GEN_CAPTURE);
            Arrays.sort(mvs, 0, nGenMoves, new CompareMvvLva());
        }

        // 7. 逐一走这些走法，并进行递归
        for (i = 0; i < nGenMoves; i++) {
            if (ChessboardUtil.makeMove(mvs[i])) {
                vl = -SearchQuiesc(-vlBeta, -vlAlpha);
                ChessboardUtil.undoMakeMove();

                // 8. 进行Alpha-Beta大小判断和截断
                if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
                    vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
                    if (vl >= vlBeta) { // 找到一个Beta走法
                        return vl;        // Beta截断
                    }
                    if (vl > vlAlpha) { // 找到一个PV走法
                        vlAlpha = vl;     // 缩小Alpha-Beta边界
                    }
                }
            }
        }

        // 9. 所有走法都搜索完了，返回最佳值
        return vlBest == -MATE_VALUE ? Engine.nDistance - MATE_VALUE : vlBest;
    }

    // 保存最佳走法
    static void setBestMove(int mv, int nDepth) {
        LogUtil.i("setBestMove","setBestMove: From " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) +
                " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)) +
                "\tDepth=" + String.valueOf(nDepth));
        int[] lpmvKillers;
        nHistoryTable[mv] += nDepth * nDepth;
        lpmvKillers = mvKillers[nDistance];
        if (lpmvKillers[0] != mv) {
            lpmvKillers[1] = lpmvKillers[0];
            lpmvKillers[0] = mv;
        }
    }
    /**     -----------------------------end 搜索函数-----------------------------     */

    /**     -----------------------------比较函数-----------------------------     */
    private static class CompareHistory implements Comparator<Integer> {
        //按历史表排序的比较函数
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return Engine.nHistoryTable[lpmv2] - Engine.nHistoryTable[lpmv1];
        }
    }

    private static class CompareMvvLva implements Comparator<Integer> {
        //按MVV/LVA排序的比较函数
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return MvvLva(lpmv2) - MvvLva(lpmv1);
        }
    }
    /**     -----------------------------end 比较函数-----------------------------     */

}
