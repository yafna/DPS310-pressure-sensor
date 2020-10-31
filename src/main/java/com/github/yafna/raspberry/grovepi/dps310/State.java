package com.github.yafna.raspberry.grovepi.dps310;

import lombok.Data;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@Data
public class State {
    Integer tempFlags;
    Integer pressureFlags;
    Integer measFlags;
    Integer configFlags;

    Map<Integer, Map.Entry<Double, String>> oversampling = new HashMap<Integer, Map.Entry<Double, String>>() {{
        put(1, new AbstractMap.SimpleImmutableEntry<>(524288.0, "0000")); // 1  single
        put(2, new SimpleImmutableEntry<>(1572864.0, "0001"));  // 2 times
        put(4, new SimpleImmutableEntry<>(3670016.0, "0010"));  // 4 times
        put(8, new SimpleImmutableEntry<>(7864320.0, "0011"));  // 8 times
        put(16, new SimpleImmutableEntry<>(253952.0, "0100")); // 16 times  - must be used with the bit shift config in the corresponded field in the 0x09
        put(32, new SimpleImmutableEntry<>(516096.0, "0101"));   //32 - must be used with the bit shift config in the corresponded field in the 0x09
        put(64, new SimpleImmutableEntry<>(1040384.0, "0110"));  //64 - must be used with the bit shift config in the corresponded field in the 0x09
        put(128, new SimpleImmutableEntry<>(2088960.0, "0111"));  //128  - must be used with the bit shift config in the corresponded field in the 0x09
    }};

    Map.Entry<Double, String> tempOversampling;
    Map.Entry<Double, String> prsOversampling;

    // coefficients
    long prsC00;
    long prsC10;
    long prsC01;
    long prsC11;
    long prsC20;
    long prsC21;
    long prsC30;
    double tempCoef0Half = 0.0;
    int tempCoef1 = 0;

    // saved temperature ?
    double tempRawSc = 0.0;

}
