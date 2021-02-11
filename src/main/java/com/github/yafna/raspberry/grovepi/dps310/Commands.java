package com.github.yafna.raspberry.grovepi.dps310;

import com.github.yafna.raspberry.grovepi.pi4j.IO;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.io.IOException;

@Log
public class Commands {
    int PRS_CFG = 0x06;
    int MEAS_CFG = 0x08;
    int MEAS_CFG_TMP_RDY = 0x20;
    int FIFO_STS = 0x0B;
    int INT_STS = 0x0A;
    int TMP_CFG = 0x07;
    int COEF = 0x10;
    int CFG_REG = 0x09;
    int RESET = 0x0C;
    int ID = 0x0D;

    IO io;

    public Commands(@NonNull IO io) {
        this.io = io;
    }

    //After reading out all data from the FIFO, write 0x80 to clear all old data.
    public void fifoFlush() throws IOException {
        log.info("reset : sending 0x80 to 0x0C");
        io.write(RESET, 0x80);
    }

    // '1001' to generate a soft reset. A soft reset will run though the same sequences as in power-on reset
    public void softReset() throws IOException {
        log.info("reset : sending 9 to 0x0C");
        io.write(RESET, 9);
    }

    public int getProductAndRevisionId() throws IOException {
        log.info("get product and revision id");
        io.write(ID);
        return io.read();
    }

    public void setPRS_CFG(int command) throws IOException {
        log.info("set PRS_CFG to " + Integer.toString(command, 2));
        io.write(PRS_CFG, command);
    }

    public void setTMP_CFG(int command) throws IOException {
        log.info("set TMP_CFG to " + Integer.toString(command, 2));
        io.write(TMP_CFG, command);
    }

    public void setCFG_REG(int command) throws IOException {
        log.info("set CFG_REG to " + Integer.toString(command, 2));
        io.write(CFG_REG, command);
    }

    public void readAndCalcCoefs(State state) throws IOException {
        log.info("reading coefficients");
        int[] coef = new int[18];
        io.write(0x10);
        for (int i = 0; i < coef.length; i++) {
            coef[i] = io.read();
            log.info("coefficient[i]  " + Integer.toString(coef[i], 2));
        }
        log.info("calculating coefficients");

        state.setTempCoef0Half(Util.complement((coef[0] << 4) | (coef[1] >> 4), 12) / 2.0);
        state.setTempCoef1(Util.complement((coef[1] & 0x0F) << 8 | coef[2], 12));

        log.info("c0 halfed = " + state.getTempCoef0Half() + "\n c1  = " + state.getTempCoef1());

        state.setPrsC00(Util.complementLong(coef[3] << 12 | coef[4] << 4 | (coef[5] >> 4) & 0x0F, 20));
        state.setPrsC10(Util.complementLong((coef[5] & 0x0F) << 16 | coef[6] << 8 | coef[7], 20));
        state.setPrsC01(Util.complementLong(coef[8] << 8 | coef[9], 16));
        state.setPrsC11(Util.complementLong(coef[10] << 8 | coef[11], 16));
        state.setPrsC20(Util.complementLong(coef[12] << 8 | coef[13], 16));
        state.setPrsC21(Util.complementLong(coef[14] << 8 | coef[15], 16));
        state.setPrsC30(Util.complementLong(coef[16] << 8 | coef[17], 16));

        log.info("prs00 = " + state.getPrsC00() + "prs10  = " + state.getPrsC10() + "prs01 = " + state.getPrsC01() +
                "prs11 = " + state.getPrsC11() + "prs20 = " + state.getPrsC20() + "prs21 = " + state.getPrsC21() +
                "prs30 = " + state.getPrsC30());
    }

    public int[] readTempOnce() throws IOException {
        io.write(MEAS_CFG, 0x02);
        int[] t = new int[3];
        io.write(0x03);
        for (int i = 0; i < t.length; i++) {
            t[i] = io.read();
        }
        return t;
    }

    public int[] readPressureOnce() throws IOException {
        io.write(MEAS_CFG, 0x01);
        int[] t = new int[3];
        io.write(0x00);
        for (int i = 0; i < t.length; i++) {
            t[i] = io.read();
        }
        return t;
    }

    //COEF_RDY   position : 7   Coefficients will be read to the Coefficents Registers after start-up:
    //  0 - Coefficients are not available yet.
    //  1 - Coefficients are available.
    public boolean checkCoeffReady() throws IOException {
        io.write(MEAS_CFG);
        int check = io.read();
        log.info("checking if coefficients are ready : " + check);
        return (check & (1 << 7)) > 0;
    }

    //SENSOR_RDY  position : 6     The pressure sensor is running through self initialization afterstart-up.
    // 0 - Sensor initialization not complete
    // 1 - Sensor initialization complete
    // It is recommend not to start measurements until the sensor hascompleted the self intialization
    public boolean checkPressureSensorReady() throws IOException {
        io.write(MEAS_CFG);
        int check = io.read();
        log.info("checking if pressure sensor is ready : " + check);
        return (check & (1 << 6)) > 0;
    }

    //TMP_COEF_SRCE    position : 7
    // Temperature coefficients are based on:
    // 0 - Internal temperature sensor (of ASIC)
    // 1 - External temperature sensor (of pressure sensor MEMSelement)
    public boolean isSensorInternal() throws IOException {
        io.write(0x28);
        int sensorType = io.read();
        System.out.println("internalSensor = " + sensorType);
        return (sensorType & (1 << 7)) == 0;
    }
}
