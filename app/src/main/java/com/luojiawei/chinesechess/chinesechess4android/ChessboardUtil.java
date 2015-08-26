package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-23.
 */
public class ChessboardUtil {
    public static int sdPlayer;                   // �ֵ�˭�ߣ�0=�췽��1=�ڷ�
    public static int[] currentMap = new int[256];          // �����ϵ�����

    // ���̷�Χ
    public final static int BOARD_TOP = 3;
    public final static int BOARD_BOTTOM = 12;
    public final static int BOARD_LEFT = 3;
    public final static int BOARD_RIGHT = 11;


    public static void startup() {            // ��ʼ������
        int sq, pc;
        sdPlayer = 0;
        Engine.nDistance = 0;
        Value.vlBlack = Value.vlWhite = 0;
        for (sq = 0; sq < 256; sq++) {
            pc = startupMap[sq];
            if (pc != 0) {
                addPiece(sq, pc);
            } else {
                currentMap[sq] = 0;
            }
        }
    }

    public static void changeSide() {         // �������ӷ�
        sdPlayer = 1 - sdPlayer;
    }

    public static void addPiece(int sq, int pc) { // �������Ϸ�һö����
        currentMap[sq] = pc;
        // �췽�ӷ֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)����
        if (pc < 16) {
            Value.vlWhite += Value.cucvlPiecePos[pc - 8][sq];
        } else {
            Value.vlBlack += Value.cucvlPiecePos[pc - 16][centreFlip(sq)];
        }
    }

    public static void delPiece(int sq, int pc) {         // ������������һö����
        currentMap[sq] = 0;
        // �췽���֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)�ӷ�
        if (pc < 16) {
            Value.vlWhite -= Value.cucvlPiecePos[pc - 8][sq];
        } else {
            Value.vlBlack -= Value.cucvlPiecePos[pc - 16][centreFlip(sq)];
        }
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
        int pcCaptured = movePiece(mv);
        if (Rule.isChecked()) {
            undoMovePiece(mv, pcCaptured);
            return false;
        }
        changeSide();
        ++Engine.nDistance;
        return true;
    }

    public static void undoMakeMove(int mv, int pcCaptured) { // ������һ����
        --Engine.nDistance;
        changeSide();
        undoMovePiece(mv, pcCaptured);
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
