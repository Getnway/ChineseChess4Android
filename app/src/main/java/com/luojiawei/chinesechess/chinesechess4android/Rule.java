package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-24.
 * 象棋走法规则
 */
public class Rule {
    // 棋子编号
    public final static int PIECE_KING = 0;
    public final static int PIECE_ADVISOR = 1;
    public final static int PIECE_BISHOP = 2;
    public final static int PIECE_KNIGHT = 3;
    public final static int PIECE_ROOK = 4;
    public final static int PIECE_CANNON = 5;
    public final static int PIECE_PAWN = 6;

    final static int MAX_GEN_MOVES = 128; // 最大的生成走法数

    // 判断棋子是否在棋盘中的数组
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

    // 判断棋子是否在九宫的数组
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

    // 判断步长是否符合特定走法的数组，1=帅(将)，2=仕(士)，3=相(象)
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

    // 根据步长判断马是否蹩腿的数组
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

    // 帅(将)的步长
    final static short[] ccKingDelta = {-16, -1, 1, 16};
    // 仕(士)的步长
    final static short[] ccAdvisorDelta = {-17, -15, 15, 17};
    // 马的步长，以帅(将)的步长作为马腿
    final static short[][] ccKnightDelta = {{-33, -31}, {-18, 14}, {-14, 18}, {31, 33}};
    // 马被将军的步长，以仕(士)的步长作为马腿
    final static short[][] ccKnightCheckDelta = {{-33, -18}, {-31, -14}, {14, 31}, {18, 33}};

    // 判断棋子是否在棋盘中
    static boolean isInBoard(int sq) {
        return ccInBoard[sq] != 0;
    }

    // 判断棋子是否在九宫中
    static boolean isInFort(int sq) {
        return ccInFort[sq] != 0;
    }

    // 走法是否符合帅(将)的步长
    static boolean isKingSpan(int sqSrc, int sqDst) {
        return ccLegalSpan[sqDst - sqSrc + 256] == 1;
    }

    // 走法是否符合仕(士)的步长
    static boolean isAdvisorSpan(int sqSrc, int sqDst) {
        return ccLegalSpan[sqDst - sqSrc + 256] == 2;
    }

    // 走法是否符合相(象)的步长
    static boolean isBishopSpan(int sqSrc, int sqDst) {
        return ccLegalSpan[sqDst - sqSrc + 256] == 3;
    }

    // 相(象)眼的位置
    static int getBishopPin(int sqSrc, int sqDst) {
        return (sqSrc + sqDst) >> 1;
    }

    // 马腿的位置
    static int getKnightPin(int sqSrc, int sqDst) {
        return sqSrc + ccKnightPin[sqDst - sqSrc + 256];
    }

    // 是否未过河
    static boolean isInHomeHalf(int sq, int sd) {
        return (sq & 0x80) != (sd << 7);
    }

    // 是否已过河
    static boolean isInAwayHalf(int sq, int sd) {
        return (sq & 0x80) == (sd << 7);
    }

    // 是否在河的同一边
    static boolean isSameHalf(int sqSrc, int sqDst) {
        return ((sqSrc ^ sqDst) & 0x80) == 0;
    }

    // 未过河的兵（卒）获取向前目标点
    static int getForwardSquare(int sq, int sd) {
        return sq - 16 + (sd << 5);
    }

    // 是否在同一行
    static boolean isSameRow(int sqSrc, int sqDst) {
        return ((sqSrc ^ sqDst) & 0xf0) == 0;
    }

    // 是否在同一列
    static boolean isSameColumn(int sqSrc, int sqDst) {
        return ((sqSrc ^ sqDst) & 0x0f) == 0;
    }

    // 生成所有走法，如果"bCapture"为"TRUE"则只生成吃子走法
    static int generateMoves(Integer[] mvs, boolean bCapture) {
        int i, j, nGenMoves, nDelta, sqSrc, sqDst;
        int pcSelfSide, pcOppSide, pcSrc, pcDst;
        int[] ucpcSquares;
        int sdPlayer;

        // 生成所有走法，需要经过以下几个步骤：
        ucpcSquares = ChessboardUtil.currentMap;
        sdPlayer = ChessboardUtil.sdPlayer;
        nGenMoves = 0;
        pcSelfSide = ChessboardUtil.getSideTag(sdPlayer);
        pcOppSide = ChessboardUtil.getOppositeSideTag(sdPlayer);
        for (sqSrc = 0; sqSrc < 256; sqSrc++) {

            // 1. 找到一个本方棋子，再做以下判断：
            pcSrc = ucpcSquares[sqSrc];
            if ((pcSrc & pcSelfSide) == 0) {
                continue;
            }

            // 2. 根据棋子确定走法
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
                        //不在本方侧，或有象眼则继续
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
                        if (ucpcSquares[sqDst] != 0) {  //上左右下有马脚
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
                                if ((pcDst & pcOppSide) != 0) { //对方棋子，可吃
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
                                break;  //遇到炮台（有棋子阻挡）
                            }
                            sqDst += nDelta;
                        }
                        sqDst += nDelta;
                        //延炮台方向寻找对方棋子
                        while (isInBoard(sqDst)) {
                            pcDst = ucpcSquares[sqDst];
                            if (pcDst != 0) {
                                if ((pcDst & pcOppSide) != 0) { //对方棋子
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
                    sqDst = getForwardSquare(sqSrc, sdPlayer);  //获取向前走目标点
                    if (isInBoard(sqDst)) {
                        pcDst = ucpcSquares[sqDst];
                        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
                            mvs[nGenMoves] = ChessboardUtil.getMove(sqSrc, sqDst);
                            nGenMoves++;
                        }
                    }
                    if (isInAwayHalf(sqSrc, sdPlayer)) {    //兵（卒）过河，增加左右走法
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

    // 判断走法是否合理
    static boolean isLegalMove(int mv) {
        int sqSrc, sqDst, sqPin;
        int pcSelfSide, pcSrc, pcDst, nDelta;
        int[] ucpcSquares;
        int sdPlayer;

        // 判断走法是否合法，需要经过以下的判断过程：
        ucpcSquares = ChessboardUtil.currentMap;
        sdPlayer = ChessboardUtil.sdPlayer;

        // 1. 判断起始格是否有自己的棋子
        sqSrc = ChessboardUtil.getMoveSrc(mv);
        pcSrc = ucpcSquares[sqSrc];
        pcSelfSide = ChessboardUtil.getSideTag(sdPlayer);
        if ((pcSrc & pcSelfSide) == 0) {
            return false;
        }

        // 2. 判断目标格是否有自己的棋子
        sqDst = ChessboardUtil.getMoveDst(mv);
        pcDst = ucpcSquares[sqDst];
        if ((pcDst & pcSelfSide) != 0) {
            return false;
        }

        // 3. 根据棋子的类型检查走法是否合理
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
                    return pcDst == 0 || pcSrc - pcSelfSide == PIECE_ROOK;  //目标点无子或车吃子
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

    // 判断是否被将军
    static boolean isChecked() {
        int i, j, sqSrc, sqDst;
        int pcSelfSide, pcOppSide, pcDst, nDelta;
        int[] ucpcSquares;
        int sdPlayer;

        ucpcSquares = ChessboardUtil.currentMap;
        sdPlayer = ChessboardUtil.sdPlayer;


        pcSelfSide = ChessboardUtil.getSideTag(sdPlayer);
        pcOppSide = ChessboardUtil.getOppositeSideTag(sdPlayer);
        // 找到棋盘上的帅(将)，再做以下判断：

        for (sqSrc = 0; sqSrc < 256; sqSrc++) {
            if (ucpcSquares[sqSrc] != pcSelfSide + PIECE_KING) {
                continue;
            }

            // 1. 判断是否被对方的兵(卒)将军
            if (ucpcSquares[getForwardSquare(sqSrc, sdPlayer)] == pcOppSide + PIECE_PAWN) {
                return true;
            }
            for (nDelta = -1; nDelta <= 1; nDelta += 2) {
                if (ucpcSquares[sqSrc + nDelta] == pcOppSide + PIECE_PAWN) {
                    return true;
                }
            }

            // 2. 判断是否被对方的马将军(以仕(士)的步长当作马腿)
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

            // 3. 判断是否被对方的车或炮将军(包括将帅对脸)
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

    // 判断是否被杀
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
