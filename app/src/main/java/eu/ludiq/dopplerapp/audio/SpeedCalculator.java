package eu.ludiq.dopplerapp.audio;

public class SpeedCalculator {
  
    private double speedOfSound;
    
    public SpeedCalculator() {
        this.speedOfSound = 0.0;
    }
  
    public void setSpeedOfSound(double speedOfSound) {
        this.speedOfSound = speedOfSound;
    }
    
    public double getSpeedOfSound() {
        return this.speedOfSound;
    }
    
    public double getSpeedOfObject(double frequencyApproaching, double frequencyLeaving) {
        if (this.speedOfSound == 0.0) {
            throw new IllegalStateException();
        }
        return (frequencyApproaching - frequencyLeaving) * this.speedOfSound / (frequencyApproaching + frequencyLeaving);
    }
}
