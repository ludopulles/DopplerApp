package eu.ludiq.dopplerapp.model;

public class VelocityCalculator {
  
    private double speedOfSound;
    
    public VelocityCalculator() {
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
