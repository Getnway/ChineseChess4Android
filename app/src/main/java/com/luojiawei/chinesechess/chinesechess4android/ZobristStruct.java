package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-26.
 * Zobrist结构
 */
public class ZobristStruct {
    int key, lock0, lock1;

    static void initZobrist(ZobristStruct player, ZobristStruct[][] table) {
        LogUtil.i("ZobristStruct","initZobrist()");
        int i, j;
        RC4Struct rc4 = new RC4Struct();
        rc4.initZero();
        player.initRC4(rc4);
        for (i = 0; i < 14; i ++) {
            for (j = 0; j < 256; j ++) {
                table[i][j].initRC4(rc4);
            }
        }
        LogUtil.i("ZobristStruct","initZobrist() end");
    }

    void initZero() {                 // 用零填充Zobrist
        key = lock0 = lock1 = 0;
    }

    void initRC4(RC4Struct rc4) {        // 用密码流填充Zobrist
        key = rc4.nextLong();
        lock0 = rc4.nextLong();
        lock1 = rc4.nextLong();
    }

    void xor(ZobristStruct zobr) { // 执行XOR操作
        key ^= zobr.key;
        lock0 ^= zobr.lock0;
        lock1 ^= zobr.lock1;
    }

    void xor(ZobristStruct zobr1, ZobristStruct zobr2) {
        key ^= zobr1.key ^ zobr2.key;
        lock0 ^= zobr1.lock0 ^ zobr2.lock0;
        lock1 ^= zobr1.lock1 ^ zobr2.lock1;
    }

    // RC4密码流生成器
    private static class RC4Struct {
        int[] s = new int[256];
        int x, y;

        public void initZero() {   // 用空密钥初始化密码流生成器
            int i, j;
            int uc;

            x = y = j = 0;
            for (i = 0; i < 256; i++) {
                s[i] = i;
            }
            for (i = 0; i < 256; i++) {
                j = (j + s[i]) & 255;
                uc = s[i];
                s[i] = s[j];
                s[j] = uc;
            }
        }

        int next() {  // 生成密码流的下一个字节
            int uc;
            x = (x + 1) & 255;
            y = (y + s[x]) & 255;
            uc = s[x];
            s[x] = s[y];
            s[y] = uc;
            return s[(s[x] + s[y]) & 255];
        }

        int nextLong() { // 生成密码流的下四个字节
            int uc0, uc1, uc2, uc3;
            uc0 = next();
            uc1 = next();
            uc2 = next();
            uc3 = next();
            return uc0 + (uc1 << 8) + (uc2 << 16) + (uc3 << 24);
        }
    }
}
