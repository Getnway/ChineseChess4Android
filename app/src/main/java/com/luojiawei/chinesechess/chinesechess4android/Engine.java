package com.luojiawei.chinesechess.chinesechess4android;

import android.sax.EndElementListener;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by L1 on 15-08-26.
 * ��������
 */
public class Engine {
    static String TAG = "Engine";
    static int phase;
    static int[] tmpMap = new int[256];
    final static int MATE_VALUE = 10000;  // ��߷�ֵ���������ķ�ֵ
    final static int BAN_VALUE = MATE_VALUE - 100; // �����и��ķ�ֵ�����ڸ�ֵ����д���û���
    final static int WIN_VALUE = MATE_VALUE - 200; // ������ʤ���ķ�ֵ���ޣ�������ֵ��˵���Ѿ�������ɱ����
    static int LIMIT_DEPTH = 64;    // �����������
    static long CLOCKS_PER_SEC = 1000;   //��λ��ms��
    static int mvResult;             // ����������������߷�
    static int[] nHistoryTable = new int[65536]; // ��ʷ��
    static int nDistance;                  // ������ڵ�Ĳ���
    static CompareHistory compareHistory = new CompareHistory();
    final static boolean NO_NULL = true;   // "SearchFull"�Ĳ������Ƿ�ղ��ü�
    static boolean GEN_CAPTURE = true;  // "GenerateMoves"�������Ƿ�ֻ���ɳ����߷�
    final static int NULL_DEPTH = 2;      // �ղ��ü��Ĳü���� 2��3
    final static int HASH_SIZE = 1 << 18; // �û����С
    final static int HASH_ALPHA = 1;      // ALPHA�ڵ���û�����
    final static int HASH_BETA = 2;       // BETA�ڵ���û�����
    final static int HASH_PV = 3;         // PV�ڵ���û�����
    static int[][] mvKillers = new int[LIMIT_DEPTH][2]; // ɱ���߷���
    static HashItem[] hashTable = new HashItem[HASH_SIZE]; // �û���
    // �߷�����ṹ
    final static int PHASE_HASH = 0;
    final static int PHASE_KILLER_1 = 1;
    final static int PHASE_KILLER_2 = 2;
    final static int PHASE_GEN_MOVES = 3;
    final static int PHASE_REST = 4;
    static int mvHash, mvKiller1, mvKiller2; // �û����߷�������ɱ���߷�
    static int nPhase, nIndex, nGenMoves;    // ��ǰ�׶Σ���ǰ���õڼ����߷����ܹ��м����߷�
    static Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];           // ���е��߷�

    static {
        LogUtil.i(TAG, "new hashTable");
        for (int i = 0; i < HASH_SIZE; i++){
            hashTable[i] = new HashItem();
        }
        LogUtil.i(TAG, "end new hashTable");
    }

    /**     -----------------------------��ʼ������-----------------------------     */
    //��ʼ����ʷ��
    public static void initHistorytable() {
        for (int i = 0; i < nHistoryTable.length; i++) {
            nHistoryTable[i] = 0;
        }
    }

    //��ʼ��ɱ���߷���
    public static void initKillers(){
        for (int i = 0; i < LIMIT_DEPTH; i++){
            mvKillers[i][0] = mvKillers[i][1] = 0;
        }
    }

    //��ʼ����ϣ��
    public static void initHashTable(){
        for (int i = 0; i < HASH_SIZE; i++){
            hashTable[i].init();
        }
    }
    /**     -----------------------------end ��ʼ������-----------------------------     */


    /**     -----------------------------�߷�����ṹ-----------------------------     */
    // ��ʼ�����趨�û����߷�������ɱ���߷�
    static void initMove(int mvHash_) {
        mvHash = mvHash_;
        mvKiller1 = mvKillers[nDistance][0];
        mvKiller2 = mvKillers[nDistance][1];
        nPhase = PHASE_HASH;
    }

    // �õ���һ���߷�
    static int nextMove() {
        int mv;
        switch (nPhase) {
            // "nPhase"��ʾ�ŷ����������ɽ׶Σ�����Ϊ��

            // 0. �û����ŷ���������ɺ�����������һ�׶Σ�
            case PHASE_HASH:
                nPhase = PHASE_KILLER_1;
                if (mvHash != 0) {
                    phase = 0;
//                    LogUtil.i(TAG, "from PHASE_HASH" +
////                            "\t mv:" + String.valueOf(mvHash) +
////                            "\t From " + String.valueOf(ChessboardUtil.getMoveSrc(mvHash)) +
////                            " To " + String.valueOf(ChessboardUtil.getMoveDst(mvHash)) +
//                            ChessboardUtil.getMoveString(mvHash));
                    return mvHash;
                }
                // ���ɣ�����û��"break"����ʾ"switch"����һ��"case"ִ��������������һ��"case"����ͬ

                // 1. ɱ���ŷ�����(��һ��ɱ���ŷ�)����ɺ�����������һ�׶Σ�
            case PHASE_KILLER_1:
                nPhase = PHASE_KILLER_2;
                if (mvKiller1 != mvHash && mvKiller1 != 0 && Rule.isLegalMove(mvKiller1)) {
                    phase = 1;
//                    LogUtil.i(TAG, "from PHASE_KILLER_1" +
////                            "\t mv:" + String.valueOf(mvKiller1) +
////                            "\t From " + String.valueOf(ChessboardUtil.getMoveSrc(mvKiller1)) +
////                            " To " + String.valueOf(ChessboardUtil.getMoveDst(mvKiller1)) +
//                            ChessboardUtil.getMoveString(mvKiller1));
                    return mvKiller1;
                }

                // 2. ɱ���ŷ�����(�ڶ���ɱ���ŷ�)����ɺ�����������һ�׶Σ�
            case PHASE_KILLER_2:
                nPhase = PHASE_GEN_MOVES;
                if (mvKiller2 != mvHash && mvKiller2 != 0 && Rule.isLegalMove(mvKiller2)) {
                    phase = 2;
//                    LogUtil.i(TAG, "from PHASE_KILLER_2" +
////                            "\t mv:" + String.valueOf(mvKiller2) +
////                            "\t From " + String.valueOf(ChessboardUtil.getMoveSrc(mvKiller2)) +
////                            " To " + String.valueOf(ChessboardUtil.getMoveDst(mvKiller2)) +
//                            ChessboardUtil.getMoveString(mvKiller2));
                    return mvKiller2;
                }

                // 3. ���������ŷ�����ɺ�����������һ�׶Σ�
            case PHASE_GEN_MOVES:
                nPhase = PHASE_REST;
//                ChessboardUtil.printBoard(Engine.nDistance);
                nGenMoves = Rule.generateMoves(mvs, false);
                Arrays.sort(mvs,0,nGenMoves,new CompareHistory());
                for (int i = 0; i < nGenMoves; i++) {
                    if(!Rule.isLegalMove(mvs[i])){
                        LogUtil.i("LegalMove", ChessboardUtil.getMoveString(mvs[i]));
                    }else{
//                        LogUtil.i("LegalMove", "isLegalMove: " +String.valueOf(i));
                    }
                }
                nIndex = 0;

                // 4. ��ʣ���ŷ�����ʷ��������
            case PHASE_REST:
                while (nIndex < nGenMoves) {
                    mv = mvs[nIndex];
                    nIndex ++;
                    if (mv != mvHash && mv != mvKiller1 && mv != mvKiller2) {
                        phase = 3;
//                        LogUtil.i(TAG, "from PHASE_REST" +
////                                "\t mv:" + String.valueOf(mv) +
////                                "\t From " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) +
////                                " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)) +
//                                ChessboardUtil.getMoveString(mv));
                        return mv;
                    }
                }

                // 5. û���ŷ��ˣ������㡣
            default:
                return 0;
        }
    }
    /**     -----------------------------end �߷�����ṹ-----------------------------     */

    /**     -----------------------------��ȡ�ͼ�¼Hash-----------------------------     */
    // ��ȡ�û�����
    static int[] probeHash(int vlAlpha, int vlBeta, int nDepth, ZobristStruct zobr) {
        int pos = zobr.key & (HASH_SIZE - 1);
        boolean bMate; // ɱ���־�������ɱ�壬��ô����Ҫ�����������
        HashItem hsh = hashTable[pos];
        int[] mvAndValue = new int[2];  //�߷��ͷ�ֵ

//        LogUtil.i("ZobristStruct"," get: " + String.valueOf(ChessboardUtil.zobr.lock0));
        if (hsh.lock0 != zobr.lock0 || hsh.lock1 != zobr.lock1) {
//            LogUtil.i("Hash"," diff in pos: " + String.valueOf(pos) +
//                    "\tkey:" + String.valueOf(zobr.key) +
//                    "\tlock0: " + String.valueOf(zobr.lock0) +
//                    "\tlock1: " + String.valueOf(zobr.lock1) +
//                    "\thsh.lock0: " + String.valueOf(hsh.lock0) +
//                    "\thsh.lock1: " + String.valueOf(hsh.lock1));
            mvAndValue[0] = 0;
            mvAndValue[1] = -MATE_VALUE;
            return mvAndValue;
        }
        LogUtil.i("Hash"," -------get : " +
                String.valueOf(pos) +
//                "\tkey:" + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1) +
//                "\t hsh.lock0: " + String.valueOf(hsh.lock0) +
//                "\t hsh.lock1: " + String.valueOf(hsh.lock1) +
                ChessboardUtil.getMoveString(hsh.mv));
        mvAndValue[0] = hsh.mv;
        bMate = false;
        if (hsh.vl > WIN_VALUE) {
//            if (hsh.vl < BAN_VALUE) {
//                mvAndValue[1] = -MATE_VALUE;
//                return mvAndValue; // ���ܵ��������Ĳ��ȶ��ԣ������˳���������ŷ������õ�
//            }
            hsh.vl -= nDistance;
            bMate = true;
        } else if (hsh.vl < -WIN_VALUE) {
//            if (hsh.vl < -BAN_VALUE) {
//                mvAndValue[1] = -MATE_VALUE;
//                return mvAndValue; // ���ܵ��������Ĳ��ȶ��ԣ������˳���������ŷ������õ�
//            }
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

    // �����û�����
    static void recordHash(int nFlag, int vl, int nDepth, int mv, ZobristStruct zobr) {
        int pos = zobr.key & (HASH_SIZE - 1);
        HashItem hsh = hashTable[pos];
        if (hsh.ucDepth > nDepth) {
            return;
        }
        hsh.ucFlag = nFlag;
        hsh.ucDepth = nDepth;
        if (vl > WIN_VALUE) {
//            if (mv == 0 && vl <= BAN_VALUE) {
//                return; // ���ܵ��������Ĳ��ȶ��ԣ�����û������ŷ��������˳�
//            }
            hsh.vl = vl + nDistance;
        } else if (vl < -WIN_VALUE) {
//            if (mv == 0 && vl >= -BAN_VALUE) {
//                return; // ���ܵ��������Ĳ��ȶ��ԣ�����û������ŷ��������˳�
//            }
            hsh.vl = vl - nDistance;
        } else {
            hsh.vl = vl;
        }
        hsh.mv = mv;
        hsh.lock0 = zobr.lock0;
        hsh.lock1 = zobr.lock1;
//        LogUtil.i("Hash","recordHash before:" + hashTable[pos].lock0 + "\t" + String.valueOf(hsh.lock0));
        hashTable[pos] = hsh;
//        LogUtil.i("Hash","recordHash after:" + hashTable[pos].lock0);
        LogUtil.i("Hash","recordHash : " + String.valueOf(pos) +
//                "\tkey:" + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1) +
//                "\t hsh.lock0: " + String.valueOf(hashTable[pos].lock0) +
//                "\t hsh.lock1: " + String.valueOf(hashTable[pos].lock1) +
                "\t vl: " + String.valueOf(hashTable[pos].vl) +
//                "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) +
//                " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)) +
                ChessboardUtil.getMoveString(hsh.mv));
//        for (int i = 0; i < 10; i++) {
//            LogUtil.i("Hash", String.valueOf(hashTable[i].mv) + " " + String.valueOf(hashTable[i].lock0) + " " + String.valueOf(hashTable[i].lock1)+
//                    " " + String.valueOf(hashTable[i].ucDepth)+ " " + String.valueOf(hashTable[i].ucFlag));
//        }
    }
    /**     -----------------------------end ��ȡ�ͼ�¼Hash-----------------------------     */

    /**     -----------------------------MVV/LVA-----------------------------     */
    // MVV/LVAÿ�������ļ�ֵ
    // �����м�ֵ���ܺ���/��û��ֵ�Ĺ����ߡ�(Most Valuable Victim/Least Valuable Attacker)
    static int[] cucMvvLva = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 1, 1, 3, 4, 3, 2, 0, //��ʿ����
            5, 1, 1, 3, 4, 3, 2, 0
    };

    // ��MVV/LVAֵ(�������Ӽ�ֵ-�����Ӽ�ֵ��)
    static int MvvLva(int mv) {
        return (cucMvvLva[ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)]] << 3)
                - cucMvvLva[ChessboardUtil.currentMap[ChessboardUtil.getMoveSrc(mv)]];
    }
    /**     -----------------------------end MVV/LVA-----------------------------     */


    /**     -----------------------------��������-----------------------------     */
    /**
     * ����������������
     */
    static void searchMain() {
        LogUtil.i(TAG, "searchMain zobr.lock0=" + String.valueOf(ChessboardUtil.zobr.lock0));
        int i, vl;
        long startTime, spendTime;

        // ��ʼ��
        initHistorytable(); //��ʼ����ʷ��
        initKillers(); //��ʼ��ɱ���߷�
        initHashTable(); //��ʼ���û���
        startTime = System.currentTimeMillis();       // ��ʼ����ʱ��
        Engine.nDistance = 0; // ��ʼ����

        // �����������
        for (i = 3; i <= LIMIT_DEPTH; i++) {
            vl = SearchFull(-MATE_VALUE, MATE_VALUE, i, false);
            spendTime = System.currentTimeMillis() - startTime;
//            if(i>=5) {
                LogUtil.i(TAG, "Best: vl=" + String.valueOf(vl) +
                        "\t From " + Integer.toHexString(ChessboardUtil.getMoveSrc(mvResult)) +
                        " To " + Integer.toHexString(ChessboardUtil.getMoveDst(mvResult)) +
                        "\tDepth=" + String.valueOf(i) +
                        "\tTime=" + String.valueOf(spendTime));
//            }

            // ������ɱ�壬����ֹ����
            if (vl > WIN_VALUE || vl < -WIN_VALUE) {
                break;
            }
            // ����һ�룬����ֹ����
            if (spendTime > CLOCKS_PER_SEC) {
                break;
            }
        }
        LogUtil.i(TAG, "end search zobr.lock0=" + String.valueOf(ChessboardUtil.zobr.lock0));
    }

    /**
     * �����߽�(Fail-Soft)��Alpha-Beta��������
     * @param vlAlpha �±߽�
     * @param vlBeta �ϱ߽�
     * @param nDepth ���
     * @param isNoNull �Ƿ�ղ��ü�
     * @return ������ѷ�ֵ
     */
    static int SearchFull(int vlAlpha, int vlBeta, int nDepth, boolean isNoNull) {
//        int i, nGenMoves;
        int vl, vlBest, mvBest;
        int nHashFlag = 0, mvHash, mv;
        int[] mvAndValue;
//        Integer[] mvs = new Integer[Rule.MAX_GEN_MOVES];
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

            // 1-3. �����û���ü������õ��û����߷�
//            LogUtil.i("ZobristStruct"," in: " + String.valueOf(ChessboardUtil.zobr.lock0));
            mvAndValue = probeHash(vlAlpha, vlBeta, nDepth, ChessboardUtil.zobr);
//            LogUtil.i("Hash","------getHash: " + String.valueOf(ChessboardUtil.zobr.key & (HASH_SIZE - 1)) +
//                    "\tvl: " + String.valueOf(mvAndValue[1]) +
//                    "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvAndValue[0])) +
//                    " To " + String.valueOf(ChessboardUtil.getMoveDst(mvAndValue[0])));
            mvHash = mvAndValue[0];
            vl = mvAndValue[1];
            if (vl > -MATE_VALUE) {
//                LogUtil.i("Hash","********getHash: " + String.valueOf(ChessboardUtil.zobr.key & (HASH_SIZE - 1)) +
//                        "\tvl: " + String.valueOf(mvAndValue[1]) +
//                        "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvAndValue[0])) +
//                        " To " + String.valueOf(ChessboardUtil.getMoveDst(mvAndValue[0])));
                return vl;
            }

            // 1-4. ���Կղ��ü�(���ڵ��Betaֵ��"MATE_VALUE"�����Բ����ܷ����ղ��ü�)
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
        }else{
            mvHash = 0;
        }

//        LogUtil.i("Hash", "mvHash:" + String.valueOf(mvHash) +
//                "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvHash)) +
//                " To " + String.valueOf(ChessboardUtil.getMoveDst(mvHash)));

        // 2. ��ʼ�����ֵ������߷�
        nHashFlag = HASH_ALPHA;
        vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)
        mvBest = 0;           // ��������֪�����Ƿ���������Beta�߷���PV�߷����Ա㱣�浽��ʷ��


//        // 3. ����ȫ���߷�����������ʷ������
//        nGenMoves = Rule.generateMoves(mvs, false);
//        Arrays.sort(mvs, 0, nGenMoves, compareHistory);

        // 3. ��ʼ���߷�����ṹ
        initMove(mvHash);


        // 4. ��һ����Щ�߷��������еݹ�
//        for (i = 0; i < nGenMoves; i++) {
//            if (ChessboardUtil.makeMove(mvs[i])) {
        while ((mv = nextMove()) !=0 ){
            LogUtil.d("LegalMove", ChessboardUtil.printBoard(Engine.nDistance, ChessboardUtil.currentMap));
            LogUtil.d("LegalMove", "tmp\n" + ChessboardUtil.printBoard(Engine.nDistance, tmpMap));
//            LogUtil.i("Hash", "nextMove:" + String.valueOf(mv) +
//                    "\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) +
//                    " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
//            if(!Rule.isLegalMove(mv)){
//                LogUtil.i("LegalMove", "sdPlayer: " + String.valueOf(ChessboardUtil.sdPlayer));
//                LogUtil.i("LegalMove",
//                    "\tFrom " + Integer.toHexString(ChessboardUtil.getMoveSrc(mv)) +
//                    " To " + Integer.toHexString(ChessboardUtil.getMoveDst(mv)));
//                ChessboardUtil.printBoard(Engine.nDistance, "LegalMove");
//                continue;
//            }
            try {
                if (ChessboardUtil.makeMove(mv)) {
                    // �������죨������ʱ������һ�㣩
                    vl = -SearchFull(-vlBeta, -vlAlpha, ChessboardUtil.inCheck() ? nDepth : nDepth - 1, false);
                    ChessboardUtil.undoMakeMove();

                    // 5. ����Alpha-Beta��С�жϺͽض�
                    if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
                        vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
                        if (vl >= vlBeta) { // �ҵ�һ��Beta�߷�
//                        mvBest = mvs[i];  // Beta�߷�Ҫ���浽��ʷ��
                            nHashFlag = HASH_BETA;
                            mvBest = mv;
                            break;            // Beta�ض�
                        }
                        if (vl > vlAlpha) { // �ҵ�һ��PV�߷�
//                        mvBest = mvs[i];  // PV�߷�Ҫ���浽��ʷ��
                            nHashFlag = HASH_PV;
                            mvBest = mv;
                            vlAlpha = vl;     // ��СAlpha-Beta�߽�
                        }
                    }
                }
            }catch (Exception e){
                LogUtil.e(TAG, e.toString());
                LogUtil.e("LegalMove", "PHASE: " + String.valueOf(phase));
                LogUtil.e("LegalMove", "sdPlayer: " + String.valueOf(ChessboardUtil.sdPlayer));
                LogUtil.e("LegalMove",
                        "\tFrom " + Integer.toHexString(ChessboardUtil.getMoveSrc(mv)) +
                                " To " + Integer.toHexString(ChessboardUtil.getMoveDst(mv)));
//                LogUtil.e("LegalMove", ChessboardUtil.printBoard(Engine.nDistance, ChessboardUtil.currentMap));
//                LogUtil.e("LegalMove", ChessboardUtil.printBoard(Engine.nDistance, tmpMap));
            }
//            if(!Rule.isLegalMove(mvBest)){
//                LogUtil.i("LegalMove", "sdPlayer: " + String.valueOf(ChessboardUtil.sdPlayer));
//                LogUtil.i("LegalMove",
//                    "\tFrom " + Integer.toHexString(ChessboardUtil.getMoveSrc(mv)) +
//                    " To " + Integer.toHexString(ChessboardUtil.getMoveDst(mv)));
//                ChessboardUtil.printBoard(Engine.nDistance, "LegalMove");
//                continue;
//            }
        }


        // 5. �����߷����������ˣ�������߷�(������Alpha�߷�)���浽��ʷ���������ֵ
        if (vlBest == -MATE_VALUE) {
            // �����ɱ�壬�͸���ɱ�岽����������
            return Engine.nDistance - MATE_VALUE;
        }

        //��¼���û���
        recordHash(nHashFlag, vlBest, nDepth, mvBest, ChessboardUtil.zobr);
        if (mvBest != 0) {
//            LogUtil.i(TAG, "***AI*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mvBest)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mvBest)));
            // �������Alpha�߷����ͽ�����߷����浽��ʷ��
//            Engine.nHistoryTable[mvBest] += nDepth * nDepth;
//            LogUtil.i(TAG,"nDistance:"+String.valueOf(nDistance));
            //��������߷�
            setBestMove(mvBest, nDepth);
            if (Engine.nDistance == 0) {
                // �������ڵ�ʱ��������һ������߷�(��Ϊȫ�����������ᳬ���߽�)��������߷���������
                Engine.mvResult = mvBest;
//                LogUtil.i(TAG, "Best:" + String.valueOf(mvBest));
            }
        }
        return vlBest;
    }


    /**
     * ��̬(Quiescence)��������
     * @param vlAlpha �±߽�
     * @param vlBeta �ϱ߽�
     * @return �������ֵ
     */
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

    // ��������߷�
    static void setBestMove(int mv, int nDepth) {
        LogUtil.i("setBestMove","BestMove:" + ChessboardUtil.getMoveString(mv) +
                "\t Depth=" + String.valueOf(nDepth));
        int[] lpmvKillers;
        nHistoryTable[mv] += nDepth * nDepth;
        lpmvKillers = mvKillers[nDistance];
        if (lpmvKillers[0] != mv) {
            lpmvKillers[1] = lpmvKillers[0];
            lpmvKillers[0] = mv;
        }
    }
    /**     -----------------------------end ��������-----------------------------     */

    /**     -----------------------------�ȽϺ���-----------------------------     */
    private static class CompareHistory implements Comparator<Integer> {
        //����ʷ������ıȽϺ���
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return Engine.nHistoryTable[lpmv2] - Engine.nHistoryTable[lpmv1];
        }
    }

    private static class CompareMvvLva implements Comparator<Integer> {
        //��MVV/LVA����ıȽϺ���
        @Override
        public int compare(Integer lpmv1, Integer lpmv2) {
            return MvvLva(lpmv2) - MvvLva(lpmv1);
        }
    }
    /**     -----------------------------end �ȽϺ���-----------------------------     */

}
