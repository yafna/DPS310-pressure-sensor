package com.github.yafna.raspberry.grovepi.dps310;

import com.github.yafna.raspberry.grovepi.GrovePi;
import com.github.yafna.raspberry.grovepi.pi4j.GrovePi4J;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            GrovePi grovepi = new GrovePi4J();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
