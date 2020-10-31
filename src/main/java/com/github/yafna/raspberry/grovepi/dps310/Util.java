package com.github.yafna.raspberry.grovepi.dps310;

public class Util {
    public static long complement(long raw, long length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }

    public static int complement(int raw, int length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }
}
