package eu.ludiq.dopplerapp.model;

import java.util.ArrayList;

public class AudioSample {

    private ArrayList<Frequency[]> samples;

    public AudioSample() {
        this.samples = new ArrayList<>();
    }

    public void addSample(Frequency[] frequencies) {
        this.samples.add(frequencies);
    }

    public void clearSamples() {
        this.samples.clear();
    }

    public int getSize() {
        return samples.size();
    }

    public Frequency[] getSample(int index) {
        return samples.get(index);
    }
}
