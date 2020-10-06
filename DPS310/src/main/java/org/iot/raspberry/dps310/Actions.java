package org.iot.raspberry.dps310;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import lombok.extern.java.Log;
import org.iot.raspberry.grovepi.pi4j.IO;

import java.io.IOException;
import java.util.logging.Level;

@Log
public class Actions {
    private I2CBus bus = null;
    private IO io;

    public Actions(int busNumber, int deviceAddress) {
        init(busNumber, deviceAddress);
    }

    private void init(int busNumber, int deviceAddress) {
        try {
            bus = I2CFactory.getInstance(busNumber);
            IO io = new IO(bus.getDevice(deviceAddress));
            //do not start until rdy
            int check = 0;
            while (check < 192) {  //192
                Thread.sleep(500);
                io.write(Configs.MEAS_CFG);
                check = io.read();
                log.log(Level.WARNING, "check initial state = " + Integer.toString(check, 2));
            }

        } catch (InterruptedException | IOException | I2CFactory.UnsupportedBusNumberException e) {
            log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            try {
                bus.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setPrsConfig(State state){

    }

    public void readCalculateCoefficients(State state) {
        int[] coef = new int[18];
        try {
            log.log(Level.INFO, "Reading calibration coefficients");
            io.write(0x10);
            for (int i = 0; i < coef.length; i++) {
                coef[i] = io.read();
                log.log(Level.INFO, "coef[i] = " + coef[i] + "   " + Integer.toString(coef[i], 2));
            }
            log.log(Level.INFO, "Calculating calibration coefficients");
            state.setTempCoef0Half(complement((coef[0] << 4) | (coef[1] >> 4), 12) / 2.0);
            log.log(Level.INFO, "m_c0Half = " + state.getTempCoef0Half());
            state.setTempCoef1(complement((coef[1] & 0x0F) << 8 | coef[2], 12));
            log.log(Level.INFO, "m_c1  = " + state.getTempCoef1());

            state.setPrsC00(complement(coef[3] << 12 | coef[4] << 4 | (coef[5] >> 4) & 0x0F, 20));
            log.log(Level.INFO, "prsC00  = " + state.getPrsC00());
            state.setPrsC10(complement((coef[5] & 0x0F) << 16 | coef[6] << 8 | coef[7], 20));
            log.log(Level.INFO, "prsC10  = " + state.getPrsC10());
            state.setPrsC01(complement(coef[8] << 8 | coef[9], 16));
            log.log(Level.INFO, "prsC01  = " + state.getPrsC01());
            state.setPrsC11(complement(coef[10] << 8 | coef[11], 16));
            log.log(Level.INFO, "prsC11  = " + state.getPrsC11());
            state.setPrsC20(complement(coef[12] << 8 | coef[13], 16));
            log.log(Level.INFO, "prsC20  = " + state.getPrsC20());
            state.setPrsC21(complement(coef[14] << 8 | coef[15], 16));
            log.log(Level.INFO, "prsC21  = " + state.getPrsC21());
            state.setPrsC30(complement(coef[16] << 8 | coef[17], 16));
            log.log(Level.INFO, "prsC30  = " + state.getPrsC30());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private long complement(long raw, long length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }
}
