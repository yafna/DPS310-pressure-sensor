package org.iot.raspberry.dps310;

import java.util.HashMap;
import java.util.Map;

public class Configs {
    public static final int PRS_CFG = 0x06;
    public static final int TMP_CFG = 0x07;
    public static final int MEAS_CFG = 0x08;
    public static final int CFG_REG = 0x09;
    public static final int COEF = 0x10;

    //  keeping together < oversampling(presicion) , < compensation scale factor, bytes for config setup > >
    public Map<Integer, Map.Entry<Double, String>> oversampling = new HashMap<Integer, Map.Entry<Double, String>>() {{
        put(1, new SimpleImmutableEntry<>(524288.0, "0000")); // 1  single
        put(2, new SimpleImmutableEntry<>(1572864.0, "0001"));  // 2 times
        put(4, new SimpleImmutableEntry<>(3670016.0, "0010"));  // 4 times
        put(8, new SimpleImmutableEntry<>(7864320.0, "0011"));  // 8 times
        put(16, new SimpleImmutableEntry<>(253952.0, "0100")); // 16 times  - must be used with the bit shift config in the corresponded field in the 0x09
        put(32, new SimpleImmutableEntry<>(516096.0, "0101"));   //32 - must be used with the bit shift config in the corresponded field in the 0x09
        put(64, new SimpleImmutableEntry<>(1040384.0, "0110"));  //64 - must be used with the bit shift config in the corresponded field in the 0x09
        put(128, new SimpleImmutableEntry<>(2088960.0, "0111"));  //128  - must be used with the bit shift config in the corresponded field in the 0x09
    }};

    int MEAS_CFG_TMP_RDY = 0x20;
    int FIFO_STS = 0x0B;
    int INT_STS = 0x0A;
    int RESET = 0x0C;

}
