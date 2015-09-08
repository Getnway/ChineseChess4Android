package com.luojiawei.chinesechess.chinesechess4android;

import android.util.Log;

/**
 * Created by L1 on 15-08-23.
 */
public class ChessboardUtil {
    static String TAG = "ChessboardUtil";
    public static int sdPlayer;                   // �ֵ�˭�ߣ�0=�췽��1=�ڷ�
    public static int[] currentMap = new int[256];          // �����ϵ�����

    final static int MAX_MOVES = 256;     // ������ʷ�߷���
    final static int DRAW_VALUE = 20;     // ����ʱ���صķ���(ȡ��ֵ)
    final static int NULL_MARGIN = 400;   // �ղ��ü��������߽�

    static int nHistoryMoveNum;    //��ʷ�߷���

    // ���̷�Χ
    public final static int BOARD_TOP = 3;
    public final static int BOARD_BOTTOM = 12;
    public final static int BOARD_LEFT = 3;
    public final static int BOARD_RIGHT = 11;

    static ZobristStruct zobrPlayer = new ZobristStruct();
    static ZobristStruct[][] zobrTable = new ZobristStruct[14][256];
    static ZobristStruct zobr = new ZobristStruct();
    static MoveStruct[] mvsList = new MoveStruct[MAX_MOVES];

//    static String[] chessName = {"˧", "��", "��", "��", "��", "�h", "��", "15", "��", "ʿ", "��", "��", "��", "��", "��"};
    static String[] chessName = {" r_shuai ", " r_shi ", " r_xiang ", " r_ma ", " r_ju ", " r_pao ", " r_bing ", " 15 ",
                                 " b_jiang ", " b_shi ", " b_xiang ", " b_ma ", " b_ju ", " b_pao ", " b_zu "};
//    static String[] coor = { "��", "��", "��", "��", "��", "��", "��", "��", "һ", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    static String[] coor = { "9", "8", "7", "6", "5", "4", "3", "2", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
//    static String[] orientation = {"��", "ƽ", "��"};
    static String[] orientation = {" jin ", " ping ", " tui "};

    static {
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 256; j++) {
                zobrTable[i][j] = new ZobristStruct();
            }
        }
        for(int i=0;i<MAX_MOVES;i++){
            mvsList[i] = new MoveStruct();
        }
        LogUtil.i("ZobristStruct", zobrTable.toString());
        ZobristStruct.initZobrist(zobrPlayer, zobrTable);

//        LogUtil.i("Engine", "new hashTable");
//        for (int i = 0; i < Engine.HASH_SIZE; i++){
//            Engine.hashTable[i] = new HashItem();
//        }
//        LogUtil.i("Engine", "end new hashTable");

    }


    public static void startup() {            // ��ʼ������
        LogUtil.i(TAG, "startup()");
        LogUtil.i("Engine", "----------------------newGame()---------------------- ");
        int sq, pc;
        sdPlayer = 0;
        Engine.nDistance = 0;
        Value.vlBlack = Value.vlWhite = 0;
        zobr.initZero();
        LogUtil.i(TAG, "initZobr()");
        for (sq = 0; sq < 256; sq++) {
            pc = startupMap[sq];
            if (pc != 0) {
                addPiece(sq, pc);
            } else {
                currentMap[sq] = 0;
            }
        }
        setIrrev();
    }

    public static void setIrrev() {           // ���(��ʼ��)��ʷ�߷���Ϣ
        LogUtil.i(TAG, "setIrrev()");
        mvsList[0].set(0, 0, Rule.isChecked(), zobr.key);
        nHistoryMoveNum = 1;
    }

    public static String getMoveString(int mv){
        if(!Rule.isLegalMove(mv)){
            return "\t unLegalMove";
        }
        int chess, from, to;
        int fromX, fromY, toX, toY;
        from = getMoveSrc(mv);
        to = getMoveDst(mv);
        chess = currentMap[from];
        fromX = from % 16 - 3;
        fromY = from / 16 - 3;
        toX = to % 16 - 3;
        toY = to / 16 -3;
        StringBuffer sb = new StringBuffer();
        sb.append("\t ");
        sb.append(chessName[chess - 8]);
        if(chess > 15){
            sb.append(coor[fromX]);
            if(fromX == toX){
                if (fromY < toY) {//��
                    sb.append(orientation[0]);
                    sb.append(toY - fromY);
                } else {//��
                    sb.append(orientation[2]);
                    sb.append(fromY - toY);
                }
            }else {
                if (fromY < toY) {//��
                    sb.append(orientation[0]);
                } else if (fromY == toY) {//ƽ
                    sb.append(orientation[1]);
                } else {//��
                    sb.append(orientation[2]);
                }
                sb.append(coor[toX]);
            }
        }else{  //chess <= 15
            sb.append(coor[fromX + 9]);
            if(fromX == toX){
                if (fromY > toY) {//��
                    sb.append(orientation[0]);
                    sb.append(fromY - toY);
                } else {//��
                    sb.append(orientation[2]);
                    sb.append(toY - fromY);
                }
            }else {
                if (fromY > toY) {//��
                    sb.append(orientation[0]);
                } else if (fromY == toY) {//ƽ
                    sb.append(orientation[1]);
                } else {//��
                    sb.append(orientation[2]);
                }
                sb.append(coor[toX + 9]);
            }
        }
        return sb.toString();
    }

    public static void changeSide() {         // �������ӷ�
        sdPlayer = 1 - sdPlayer;
//        LogUtil.i(TAG, "changeSide key: " + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1));
        zobr.xor(zobrPlayer);
//        LogUtil.i(TAG, "after changeSide key: " + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1));
    }

    public static void addPiece(int sq, int pc) { // �������Ϸ�һö����
        currentMap[sq] = pc;
//        LogUtil.i(TAG, "addPiece key: " + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1));
        // �췽�ӷ֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)���֣���Ϊ������ֻ��һ�������λ������
        if (pc < 16) {
            Value.vlWhite += Value.cucvlPiecePos[pc - 8][sq];
            zobr.xor(zobrTable[pc - 8][sq]);
        } else {
            Value.vlBlack += Value.cucvlPiecePos[pc - 16][centreFlip(sq)];
            zobr.xor(zobrTable[pc - 9][sq]);
        }
//        LogUtil.i(TAG, "after addPiece key: " + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1));
    }

    public static void delPiece(int sq, int pc) {         // ������������һö����
        currentMap[sq] = 0;
//        LogUtil.i(TAG, "after delPiece key: " + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1));
//        LogUtil.i("TAG", "sq: " + String.valueOf(sq) + " pc:" + String.valueOf(pc));
        // �췽���֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)�ӷ֣���Ϊ������ֻ��һ�������λ������
        if (pc < 16) {
            Value.vlWhite -= Value.cucvlPiecePos[pc - 8][sq];
            zobr.xor(zobrTable[pc - 8][sq]);
        } else {
            Value.vlBlack -= Value.cucvlPiecePos[pc - 16][centreFlip(sq)];
            zobr.xor(zobrTable[pc - 9][sq]);
        }
//        LogUtil.i(TAG, "after delPiece key: " + String.valueOf(zobr.key) +
//                "\tlock0: " + String.valueOf(zobr.lock0) +
//                "\tlock1: " + String.valueOf(zobr.lock1));
    }

    public static int movePiece(int mv) {         // ��һ���������
        int sqSrc, sqDst, pc, pcCaptured;
        sqSrc = getMoveSrc(mv);
        sqDst = getMoveDst(mv);
        pcCaptured = currentMap[sqDst];
        if (pcCaptured != 0) {
            delPiece(sqDst, pcCaptured);
        }
        pc = currentMap[sqSrc];
        delPiece(sqSrc, pc);
        addPiece(sqDst, pc);
        return pcCaptured;
    }

    public static void undoMovePiece(int mv, int pcCaptured) {      // ������һ���������
        int sqSrc, sqDst, pc;
        sqSrc = getMoveSrc(mv);
        sqDst = getMoveDst(mv);
        pc = currentMap[sqDst];
        delPiece(sqDst, pc);
        addPiece(sqSrc, pc);
        if (pcCaptured != 0) {
            addPiece(sqDst, pcCaptured);
        }
    }

    public static boolean makeMove(int mv) {         // ��һ����
        int key = zobr.key;
        int pcCaptured = movePiece(mv);
        if (Rule.isChecked()) {
            undoMovePiece(mv, pcCaptured);
            return false;
        }
        changeSide();
        mvsList[nHistoryMoveNum].set(mv, pcCaptured, Rule.isChecked(), key);
        ++Engine.nDistance;
        ++nHistoryMoveNum;
        return true;
    }

    public static void undoMakeMove() { // ������һ����
        --Engine.nDistance;
        nHistoryMoveNum--;
        changeSide();
        undoMovePiece(mvsList[nHistoryMoveNum].mv, mvsList[nHistoryMoveNum].ucpcCaptured);
    }

    static boolean inCheck() {      // ��һ���Ƿ񱻽���
        return mvsList[nHistoryMoveNum - 1].ucbCheck;
    }

    static boolean captured() {     // ��һ���Ƿ����
        return mvsList[nHistoryMoveNum - 1].ucpcCaptured != 0;
    }

    static void nullMove() {                       // ��һ���ղ�
        mvsList[nHistoryMoveNum].set(0, 0, false, zobr.key);
        Engine.nDistance++;
        nHistoryMoveNum++;
        changeSide();
    }

    static void undoNullMove() {                   // ������һ���ղ�
        Engine.nDistance--;
        nHistoryMoveNum--;
        changeSide();
    }

    // ����ظ�����
    static int repStatus(int nRecur) {
        boolean bSelfSide, bPerpCheck, bOppPerpCheck;

        bSelfSide = false;
        bPerpCheck = bOppPerpCheck = true;
        int i = 1;
        MoveStruct lpmvs = mvsList[nHistoryMoveNum - i];
        while (lpmvs.mv != 0 && lpmvs.ucpcCaptured == 0) {  //�߷����ڣ���û�г���
            if (bSelfSide) {
                bPerpCheck = bPerpCheck && lpmvs.ucbCheck;
                if (lpmvs.key == zobr.key) {
                    nRecur--;
                    if (nRecur == 0) {
                        return 1 + (bPerpCheck ? 2 : 0) + (bOppPerpCheck ? 4 : 0);
                    }
                }
            } else {
                bOppPerpCheck = bOppPerpCheck && lpmvs.ucbCheck;
            }
            bSelfSide = !bSelfSide;
            i++;
            lpmvs = mvsList[nHistoryMoveNum - i];
        }
        return 0;
    }

    // �ظ������ֵ
    static int repValue(int nRepStatus) {
        int vlReturn;
        vlReturn = ((nRepStatus & 2) == 0 ? 0 : Engine.nDistance - Engine.MATE_VALUE) + //�ҷ���û����or����
                ((nRepStatus & 4) == 0 ? 0 : Engine.MATE_VALUE - Engine.nDistance); //�Է���û����or����
        return vlReturn == 0 ? drawValue() : vlReturn;  //˫������û����or�н���
    }

    // �����ֵ
    static int drawValue() {
        return (Engine.nDistance & 1) == 0 ? -DRAW_VALUE : DRAW_VALUE;
    }

    // �ж��Ƿ�����ղ��ü�
    static boolean nullOkay() {
        return (sdPlayer == 0 ? Value.vlWhite : Value.vlBlack) > NULL_MARGIN;
    }

    //��ʼ���
    public static final short[] startupMap = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 20, 19, 18, 17, 16, 17, 18, 19, 20, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0,
            0, 0, 0, 22, 0, 22, 0, 22, 0, 22, 0, 22, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 14, 0, 14, 0, 14, 0, 14, 0, 14, 0, 0, 0, 0,
            0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 12, 11, 10, 9, 8, 9, 10, 11, 12, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    // ��ø��ӵ�������
    public static int getCoordY(int sq) {
        return sq >> 4;
    }

    // ��ø��ӵĺ�����
    public static int getCoordX(int sq) {
        return sq & 15;
    }

    // ���ݺ��������ø���
    public static int getCoordPoint(int x, int y) {
        return x + (y << 4);
    }

    // ���ķ�ת���̣�180�㣩
    public static int centreFlip(int sq) {
        return 254 - sq;
    }

    // ������ˮƽ����
    public static int horizontalFlip(int x) {
        return 14 - x;
    }

    // �����괹ֱ����
    public static int verticlFlip(int y) {
        return 15 - y;
    }

    // ��ú�ڱ��(������8��������16)
    public static int getSideTag(int sd) {
        return 8 + (sd << 3);
    }

    // ��öԷ���ڱ��
    public static int getOppositeSideTag(int sd) {
        return 16 - (sd << 3);
    }

    // ����߷������
    public static int getMoveSrc(int mv) {
        return mv & 255;
    }

    // ����߷����յ�
    public static int getMoveDst(int mv) {
        return mv >> 8;
    }

    // ���������յ����߷�
    public static int getMove(int sqSrc, int sqDst) {
        return sqSrc + sqDst * 256;
    }

    // �߷�ˮƽ����
    public static int getFlipMove(int mv) {
        return getMove(centreFlip(getMoveSrc(mv)), centreFlip(getMoveDst(mv)));
    }

}
