package com.github.yafna.raspberry.grovepi.dps310;

public class Util {
    public static Long complementLong(long raw, long length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }

    public static Integer complement(int raw, int length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }

    public int buildCommand(int... parts) {
        int res = 0;
       //TODO
        return res;
    }

    public static int buildTMP_SFG(boolean internalSensor, String measurementRate, String overSampling) {
        return Integer.parseInt("" + (internalSensor ? 0 : 1) + measurementRate + overSampling, 2);
    }
}