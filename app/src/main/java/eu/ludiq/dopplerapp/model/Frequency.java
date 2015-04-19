package eu.ludiq.dopplerapp.model;

public class Frequency implements Comparable<Frequency> {

    public final double frequency, magnitude;

    public Frequency(double frequency, double magnitude) {
        this.frequency = frequency;
        this.magnitude = magnitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getMagnitude() {
        return magnitude;
    }

    @Override
    public int compareTo(Frequency another) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        if (this.magnitude > another.magnitude) {
            return -1;
        }
        if (this.magnitude < another.magnitude) {
            return 1;
        }
        return 0;
    }
}
