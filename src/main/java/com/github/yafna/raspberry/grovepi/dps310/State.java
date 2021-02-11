package com.github.yafna.raspberry.grovepi.dps310;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class State {
    Integer tempFlags;
    Integer pressureFlags;
    //MEAS_CFG
    Integer measFlags;
    Integer configFlags;

    //TMP_RATE   ! Applicable for measurements in Background mode only  (  Background mode can bee set up in the MEAS_CFG )
    //<measurement pr. sec , binary value>
    Map<Integer, String> measurmentRate = new HashMap<Integer, String>() {{
        put(1, "000"); // 1 measurement pr. sec
        put(2, "001");
        put(4, "010");
        put(8, "011");
        put(16, "100");
        put(32, "101");
        put(64, "110");
        put(128, "111");
    }};

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

    Boolean internalSensor;
    // coefficients
    Long prsC00;
    Long prsC10;
    Long prsC01;
    Long prsC11;
    Long prsC20;
    Long prsC21;
    Long prsC30;
    Double tempCoef0Half = 0.0;
    Integer tempCoef1 = 0;

    // saved temperature raw value to calculate pressure
    Double tempRawSc;

    public double calcTemp(int t0, int t1, int t2) {
        if (tempCoef0Half == null || tempCoef1 == null) {
            log.error("calculate coefficients first");
            throw new IllegalStateException("coefficents must be read and calculated first");
        } else {
            int temRaw = Util.complement((t0 << 16 | t1 << 8 | t2), 24);
            tempRawSc = temRaw / tempOversampling.getKey();
            double tempValue = tempCoef0Half + tempCoef1 * tempRawSc;
            log.info("temperature  = " + tempValue);
            return tempValue;
        }
    }

    public double calcPressure(int p0, int p1, int p2) {
        if (prsC01 == null || prsC20 == null) {
            log.error("calculate coefficients first");
            throw new IllegalStateException("coefficents must be read and calculated first");
        }
        if (tempRawSc == null) {
            log.error("read temperature first");
            throw new IllegalStateException("read temperature first");
        }
        int prsRaw = Util.complement(p0 << 16 | p1 << 8 | p2, 24);
        double prsRawSc = prsRaw / prsOversampling.getKey();
        double prsValue = prsC00 + prsRawSc * (prsC10 + prsRawSc * (prsC20 + prsRawSc * prsC30)) + tempRawSc * prsC01 + tempRawSc * prsRawSc * (prsC11 + prsRawSc * prsC21);
        log.info("pressure  = " + prsValue);
        return prsValue;
    }
}
