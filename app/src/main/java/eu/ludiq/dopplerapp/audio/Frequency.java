package eu.ludiq.dopplerapp.audio;

import java.io.Serializable;

public class Frequency implements Serializable {

    public final double frequency, magnitude;

    public Frequency(double frequency, double magnitude) {
        this.frequency = frequency;
        this.magnitude = magnitude;
    }

    @Override
    public String toString() {
        return "(" + this.frequency + ", " + this.magnitude + ")";
    }
}
