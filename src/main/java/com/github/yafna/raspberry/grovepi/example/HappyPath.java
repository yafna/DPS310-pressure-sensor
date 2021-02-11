package com.github.yafna.raspberry.grovepi.example;

import com.github.yafna.raspberry.grovepi.GrovePi;
import com.github.yafna.raspberry.grovepi.dps310.Commands;
import com.github.yafna.raspberry.grovepi.dps310.State;
import com.github.yafna.raspberry.grovepi.pi4j.GrovePi4J;
import com.github.yafna.raspberry.grovepi.pi4j.IO;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HappyPath {
    public static void main(String[] args) {
        GrovePi grovepi = null;
        I2CBus bus = null;
        I2CDevice dev = null;
        IO io = null;
        try {
            grovepi = new GrovePi4J();
            bus = I2CFactory.getInstance(1);
            dev = bus.getDevice(0x77);
            io = new IO(dev);
            // read coefficients - setup settings - read temp - read pressure
            State state = new State();
            Commands commands = new Commands(io);

            //do not start until ready
            while (!commands.checkCoeffReady()) {
                Thread.sleep(500);
            }
            while (!commands.checkPressureSensorReady()) {
                Thread.sleep(500);
            }

            state.setInternalSensor(commands.isSensorInternal());

            commands.readAndCalcCoefs(state);
            state.setPrsOversampling(state.getOversampling().get(16));
            state.setTempOversampling(state.getOversampling().get(16));
            int pressureConfig = Integer.parseInt("0" + state.getMeasurmentRate().get(1) + state.getPrsOversampling().getValue(), 2);
            commands.setPRS_CFG(pressureConfig);
            int tempConfig = Integer.parseInt("" + (state.getInternalSensor() ? 0 : 1) + state.getMeasurmentRate().get(1) +  state.getTempOversampling().getValue(), 2);
            commands.setTMP_CFG(tempConfig);

            int cfgReg = Integer.parseInt("00000000",2);
            commands.setCFG_REG(cfgReg);

        } catch (IOException | I2CFactory.UnsupportedBusNumberException | InterruptedException e) {
           log.error(e.getLocalizedMessage(), e);
        } finally {
            if (bus != null)
                try {
                    bus.close();
                } catch (Exception ex) {
                    log.error(ex.getLocalizedMessage(), ex);
                }
            if (grovepi != null) {
                grovepi.close();
            }
        }
    }
}
