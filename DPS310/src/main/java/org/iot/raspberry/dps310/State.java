package org.iot.raspberry.dps310;

import lombok.Data;

import java.util.Map;

@Data
public class State {
    private Map.Entry<Double, String> tempOversampling;
    private Map.Entry<Double, String> prsOversampling;

    private Long prsC00;
    private Long prsC10;
    private Long prsC01;
    private Long prsC11;
    private Long prsC20;
    private Long prsC21;
    private Long prsC30;

    private Double tempCoef0Half;
    private Long tempCoef1;
    private Double tempRawSc;
}
