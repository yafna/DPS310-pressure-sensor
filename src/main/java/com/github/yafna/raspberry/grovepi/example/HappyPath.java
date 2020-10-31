package com.github.yafna.raspberry.grovepi.example;

import com.github.yafna.raspberry.grovepi.GrovePi;
import com.github.yafna.raspberry.grovepi.pi4j.GrovePi4J;

import java.io.IOException;

public class HappyPath {
    public static void main(String[] args) {
        try {
            GrovePi grovepi = new GrovePi4J();
            // read coefficients - setup settings - read temp - read pressure
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
