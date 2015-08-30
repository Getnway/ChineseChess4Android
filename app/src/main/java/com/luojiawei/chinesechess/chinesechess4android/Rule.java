package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-24.
 * �����߷�����
 */
public class Rule {
    // ���ӱ��
    public final static int PIECE_KING = 0;
    public final static int PIECE_ADVISOR = 1;
    public final static int PIECE_BISHOP = 2;
    public final static int PIECE_KNIGHT = 3;
    public final static int PIECE_ROOK = 4;
    public final static int PIECE_CANNON = 5;
    public final static int PIECE_PAWN = 6;

    final static int MAX_GEN_MOVES = 128; // ���������߷���

    // �ж������Ƿ��������е�����
    final static short[] ccInBoard = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    // �ж������Ƿ��ھŹ�������
    final static short[] ccInFort = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    // �жϲ����Ƿ�����ض��߷������飬1=˧(��)��2=��(ʿ)��3=��(��)
    final static short[] ccLegalSpan = {
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
    };

    // ���ݲ����ж����Ƿ����ȵ�����
    final static short[] ccKnightPin = {
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, -16, 0, -16, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 16, 0, 16, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
    };

    // ˧(��)�Ĳ���
    final static short[] ccKingDelta = {-16, -1, 1, 16};
    // ��(ʿ)�Ĳ���
    final static short[] ccAdvisorDelta = {-17, -15, 15, 17};
    // ��Ĳ�������˧(��)�Ĳ�����Ϊ����
    final static short[][] ccKnightDelta = {{-33, -31}, {-18, 14}, {-14, 18}, {31, 33}};
    // �������Ĳ���������(ʿ)�Ĳ�����Ϊ����
    final static short[][] ccKnightCheckDelta = {{-33, -18}, {-31, -14}, {14, 31}, {18, 33}};

    // �ж������Ƿ���������
    static boolean isInBoard(int sq) {
        return ccInBoard[sq] != 0;
    }

    // �ж������Ƿ��ھŹ���
    static boolean isInFort(int sq) {
        return ccInFort[sq] != 0;
    }

    // �߷��Ƿ����˧(��)�Ĳ���
    static boolean isKingSpan(int sqSrc, int sqDst) {
        return ccLegalSpan[sqDst - sqSrc + 256] == 1;
    }

    // �߷��Ƿ������(ʿ)�Ĳ���
    static boolean isAdvisorSpan(int sqSrc, int sqDst) {
        return ccLegalSpan[sqDst - sqSrc + 256] == 2;
    }

    // �߷��Ƿ������(��)�Ĳ���
    static boolean isBishopSpan(int sqSrc, int sqDst) {
        return ccLegalSpan[sqDst - sqSrc + 256] == 3;
    }

    // ��(��)�۵�λ��
    static int getBishopPin(int sqSrc, int sqDst) {
        return (sqSrc + sqDst) >> 1;
    }

    // ���ȵ�λ��
    static int getKnightPin(int sqSrc, int sqDst) {
        return sqSrc + ccKnightPin[sqDst - sqSrc + 256];
    }

    // �Ƿ�δ����
    static boolean isInHomeHalf(int sq, int sd) {
        return (sq & 0x80) != (sd << 7);
    }

    // �Ƿ��ѹ���
    static boolean isInAwayHalf(int sq, int sd) {
        return (sq & 0x80) == (sd << 7);
    }

    // �Ƿ��ںӵ�ͬһ��
    static boolean isSameHalf(int sqSrc, int sqDst) {
        return ((sqSrc ^ sqDst) & 0x80) == 0;
    }

    // δ���ӵı����䣩��ȡ��ǰĿ���
    static int getForwardSquare(int sq, int sd) {
        return sq - 16 + (sd << 5);
    }

    // �Ƿ���ͬһ��
    static boolean isSameRow(int sqSrc, int sqDst) {
        return ((sqSrc ^ sqDst) & 0xf0) == 0;
    }

    // �Ƿ���ͬһ��
    static boolean isSameColumn(int sqSrc, int sqDst) {
        return ((sqSrc ^ sqDst) & 0x0f) == 0;
    }

    // ���������߷������"bCapture"Ϊ"TRUE"��ֻ���ɳ����߷�
    static int generateMoves(Integer[] mvs, boolean bCapture) {
        int i, j, nGenMoves, nDelta, sqSrc, sqDst;
        int pcSelfSide, pcOppSide, pcSrc, pcDst;
        int[] ucpcSquares;
        int sdPlayer;

        // ���������߷�����Ҫ�������¼������裺
        ucpcSquares = ChessboardUtil.currentMap;
        sdPlayer = ChessboardUtil.sdPlayer;
        nGenMoves = 0;
        pcSelfSide = ChessboardUtil.getSideTag(sdPlayer);
        pcOppSide = ChessboardUtil.getOppositeSideTag(sdPlayer);
        for (sqSrc = 0; sqSrc < 256; sqSrc++) {

            // 1. �ҵ�һ���������ӣ����������жϣ�
            pcSrc = ucpcSquares[sqSrc];
            if ((pcSrc & pcSelfSide) == 0) {
                continue;
            }

            // 2. ��������ȷ���߷�
            switch (pcSrc - pcSelfSide) {
                case PIECE_KING:
                    for (i = 0; i < 4; i++) {
                        sqDst = sqSrc + ccKingDelta[i];
                        if (!isInFort(sqDst)) {
                            continue;
                        }
                        pcDst = ucpcSquares[sqDst];
                        if (bCapture ? (pcDst & pcSelfSide) != 0 : (pcDst & pcSelfSide) == 0) {
                            mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                            nGenMoves++;
                        }
                    }
                    break;
                case PIECE_ADVISOR:
                    for (i = 0; i < 4; i++) {
                        sqDst = sqSrc + ccAdvisorDelta[i];
                        if (!isInFort(sqDst)) {
                            continue;
                        }
                        pcDst = ucpcSquares[sqDst];
                        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
                            mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                            nGenMoves++;
                        }
                    }
                    break;
                case PIECE_BISHOP:
                    for (i = 0; i < 4; i++) {
                        sqDst = sqSrc + ccAdvisorDelta[i];
                        //���ڱ����࣬�������������
                        if (!(isInBoard(sqDst) && isInHomeHalf(sqDst, sdPlayer) && ucpcSquares[sqDst] == 0)) {
                            continue;
                        }
                        sqDst += ccAdvisorDelta[i];
                        pcDst = ucpcSquares[sqDst];
                        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
                            mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                            nGenMoves++;
                        }
                    }
                    break;
                case PIECE_KNIGHT:
                    for (i = 0; i < 4; i++) {
                        sqDst = sqSrc + ccKingDelta[i];
                        if (ucpcSquares[sqDst] != 0) {  //�������������
                            continue;
                        }
                        for (j = 0; j < 2; j++) {
                            sqDst = sqSrc + ccKnightDelta[i][j];
                            if (!isInBoard(sqDst)) {
                                continue;
                            }
                            pcDst = ucpcSquares[sqDst];
                            if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
                                mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                                nGenMoves++;
                            }
                        }
                    }
                    break;
                case PIECE_ROOK:
                    for (i = 0; i < 4; i++) {
                        nDelta = ccKingDelta[i];
                        sqDst = sqSrc + nDelta;
                        while (isInBoard(sqDst)) {
                            pcDst = ucpcSquares[sqDst];
                            if (pcDst == 0) {
                                if(!bCapture) {
                                    mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                                    nGenMoves++;
                                }
                            } else {
                                if ((pcDst & pcOppSide) != 0) { //�Է����ӣ��ɳ�
                                    mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                                    nGenMoves++;
                                }
                                break;
                            }
                            sqDst += nDelta;
                        }
                    }
                    break;
                case PIECE_CANNON:
                    for (i = 0; i < 4; i++) {
                        nDelta = ccKingDelta[i];
                        sqDst = sqSrc + nDelta;
                        while (isInBoard(sqDst)) {
                            pcDst = ucpcSquares[sqDst];
                            if (pcDst == 0) {
                                if (!bCapture) {
                                    mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                                    nGenMoves++;
                                }
                            } else {
                                break;  //������̨���������赲��
                            }
                            sqDst += nDelta;
                        }
                        sqDst += nDelta;
                        //����̨����Ѱ�ҶԷ�����
                        while (isInBoard(sqDst)) {
                            pcDst = ucpcSquares[sqDst];
                            if (pcDst != 0) {
                                if ((pcDst & pcOppSide) != 0) { //�Է�����
                                    mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                                    nGenMoves++;
                                }
                                break;
                            }
                            sqDst += nDelta;
                        }
                    }
                    break;
                case PIECE_PAWN:
                    sqDst = getForwardSquare(sqSrc, sdPlayer);  //��ȡ��ǰ��Ŀ���
                    if (isInBoard(sqDst)) {
                        pcDst = ucpcSquares[sqDst];
                        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
                            mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                            nGenMoves++;
                        }
                    }
                    if (isInAwayHalf(sqSrc, sdPlayer)) {    //�����䣩���ӣ����������߷�
                        for (nDelta = -1; nDelta <= 1; nDelta += 2) {
                            sqDst = sqSrc + nDelta;
                            if (isInBoard(sqDst)) {
                                pcDst = ucpcSquares[sqDst];
                                if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
                                    mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                                    nGenMoves++;
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return nGenMoves;
    }

    // �ж��߷��Ƿ����
    static boolean isLegalMove(int mv) {
        int sqSrc, sqDst, sqPin;
        int pcSelfSide, pcSrc, pcDst, nDelta;
        int[] ucpcSquares;
        int sdPlayer;

        // �ж��߷��Ƿ�Ϸ�����Ҫ�������µ��жϹ��̣�
        ucpcSquares = ChessboardUtil.currentMap;
        sdPlayer = ChessboardUtil.sdPlayer;

        // 1. �ж���ʼ���Ƿ����Լ�������
        sqSrc = ChessboardUtil.getMoveSrc(mv);
        pcSrc = ucpcSquares[sqSrc];
        pcSelfSide = ChessboardUtil.getSideTag(sdPlayer);
        if ((pcSrc & pcSelfSide) == 0) {
            return false;
        }

        // 2. �ж�Ŀ����Ƿ����Լ�������
        sqDst = ChessboardUtil.getMoveDst(mv);
        pcDst = ucpcSquares[sqDst];
        if ((pcDst & pcSelfSide) != 0) {
            return false;
        }

        // 3. �������ӵ����ͼ���߷��Ƿ����
        switch (pcSrc - pcSelfSide) {
            case PIECE_KING:
                return isInFort(sqDst) && isKingSpan(sqSrc, sqDst);
            case PIECE_ADVISOR:
                return isInFort(sqDst) && isAdvisorSpan(sqSrc, sqDst);
            case PIECE_BISHOP:
                return isSameHalf(sqSrc, sqDst) && isBishopSpan(sqSrc, sqDst) &&
                        ucpcSquares[getBishopPin(sqSrc, sqDst)] == 0;
            case PIECE_KNIGHT:
                sqPin = getKnightPin(sqSrc, sqDst);
                return (sqPin != sqSrc) && ucpcSquares[sqPin] == 0;
            case PIECE_ROOK:
            case PIECE_CANNON:
                if (isSameRow(sqSrc, sqDst)) {
                    nDelta = (sqDst < sqSrc ? -1 : 1);
                } else if (isSameColumn(sqSrc, sqDst)) {
                    nDelta = (sqDst < sqSrc ? -16 : 16);
                } else {
                    return false;
                }
                sqPin = sqSrc + nDelta;
                while (sqPin != sqDst && ucpcSquares[sqPin] == 0) {
                    sqPin += nDelta;
                }
                if (sqPin == sqDst) {
                    return pcDst == 0 || pcSrc - pcSelfSide == PIECE_ROOK;  //Ŀ������ӻ򳵳���
                } else if (pcDst != 0 && pcSrc - pcSelfSide == PIECE_CANNON) {
                    sqPin += nDelta;
                    while (sqPin != sqDst && ucpcSquares[sqPin] == 0) {
                        sqPin += nDelta;
                    }
                    return sqPin == sqDst;
                } else {
                    return false;
                }
            case PIECE_PAWN:
                if (isInAwayHalf(sqDst, sdPlayer) && (sqDst == sqSrc - 1 || sqDst == sqSrc + 1)) {
                    return true;
                }
                return sqDst == getForwardSquare(sqSrc, sdPlayer);
            default:
                return false;
        }
    }

    // �ж��Ƿ񱻽���
    static boolean isChecked() {
        int i, j, sqSrc, sqDst;
        int pcSelfSide, pcOppSide, pcDst, nDelta;
        int[] ucpcSquares;
        int sdPlayer;

        ucpcSquares = ChessboardUtil.currentMap;
        sdPlayer = ChessboardUtil.sdPlayer;


        pcSelfSide = ChessboardUtil.getSideTag(sdPlayer);
        pcOppSide = ChessboardUtil.getOppositeSideTag(sdPlayer);
        // �ҵ������ϵ�˧(��)�����������жϣ�

        for (sqSrc = 0; sqSrc < 256; sqSrc++) {
            if (ucpcSquares[sqSrc] != pcSelfSide + PIECE_KING) {
                continue;
            }

            // 1. �ж��Ƿ񱻶Է��ı�(��)����
            if (ucpcSquares[getForwardSquare(sqSrc, sdPlayer)] == pcOppSide + PIECE_PAWN) {
                return true;
            }
            for (nDelta = -1; nDelta <= 1; nDelta += 2) {
                if (ucpcSquares[sqSrc + nDelta] == pcOppSide + PIECE_PAWN) {
                    return true;
                }
            }

            // 2. �ж��Ƿ񱻶Է�������(����(ʿ)�Ĳ�����������)
            for (i = 0; i < 4; i++) {
                if (ucpcSquares[sqSrc + ccAdvisorDelta[i]] != 0) {
                    continue;
                }
                for (j = 0; j < 2; j++) {
                    pcDst = ucpcSquares[sqSrc + ccKnightCheckDelta[i][j]];
                    if (pcDst == pcOppSide + PIECE_KNIGHT) {
                        return true;
                    }
                }
            }

            // 3. �ж��Ƿ񱻶Է��ĳ����ڽ���(������˧����)
            for (i = 0; i < 4; i++) {
                nDelta = ccKingDelta[i];
                sqDst = sqSrc + nDelta;
                while (isInBoard(sqDst)) {
                    pcDst = ucpcSquares[sqDst];
                    if (pcDst != 0) {
                        if (pcDst == pcOppSide + PIECE_ROOK || pcDst == pcOppSide + PIECE_KING) {
                            return true;
                        }
                        break;
                    }
                    sqDst += nDelta;
                }
                sqDst += nDelta;
                while (isInBoard(sqDst)) {
                    pcDst = ucpcSquares[sqDst];
                    if (pcDst != 0) {
                        if (pcDst == pcOppSide + PIECE_CANNON) {
                            return true;
                        }
                        break;
                    }
                    sqDst += nDelta;
                }
            }
            return false;
        }
        return false;
    }

    // �ж��Ƿ�ɱ
    static boolean isMate() {
        int i, nGenMoveNum, pcCaptured;
        Integer[] mvs = new Integer[MAX_GEN_MOVES];

        nGenMoveNum = generateMoves(mvs, false);
        for (i = 0; i < nGenMoveNum; i++) {
            pcCaptured = ChessboardUtil.movePiece(mvs[i]);
            if (!isChecked()) {
                ChessboardUtil.undoMovePiece(mvs[i], pcCaptured);
                return false;
            } else {
                ChessboardUtil.undoMovePiece(mvs[i], pcCaptured);
            }
        }
        return true;
    }
}
