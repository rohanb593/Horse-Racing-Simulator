import java.awt.Color;

public class Horse2 {
    private char horseSymbol;
    private String horseName;
    private double horseConfidence;
    private int distanceTravelled;
    private boolean horseHasFallen;
    private String shape;
    private Color color;
    public double speedModifier = 1.0;

    public Horse2(char horseSymbol, String horseName, double horseConfidence, String shape, Color color) {
        this.horseSymbol = horseSymbol;
        this.horseName = horseName;
        this.horseConfidence = horseConfidence;
        this.distanceTravelled = 0;
        this.horseHasFallen = false;
        this.shape = shape;
        this.color = color;
    }

    public void applyWeatherEffect(String weather) {
        switch (weather) {
            case "Muddy":
                this.speedModifier = 0.7;
                this.horseConfidence = Math.max(0.1, this.horseConfidence - 0.1);
                break;
            case "Icy":
                this.speedModifier = 0.5;
                this.horseConfidence = Math.max(0.1, this.horseConfidence - 0.15);
                break;
            case "Rainy":
                this.speedModifier = 0.8;
                this.horseConfidence = Math.max(0.1, this.horseConfidence - 0.05);
                break;
            case "Sunny":
            default:
                this.speedModifier = 1.0;
                break;
        }
    }

    public void fall() {
        this.horseHasFallen = true;
    }

    public double getConfidence() {
        return horseConfidence;
    }

    public int getDistanceTravelled() {
        return distanceTravelled;
    }

    public String getName() {
        return horseName;
    }

    public char getSymbol() {
        return horseSymbol;
    }

    public String getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public void goBackToStart() {
        this.distanceTravelled = 0;
        this.horseHasFallen = false;
    }

    public boolean hasFallen() {
        return horseHasFallen;
    }

    public void moveForward() {
        this.distanceTravelled++;
    }

    public void setConfidence(double newConfidence) {
        if (newConfidence >= 0 && newConfidence <= 1) {
            this.horseConfidence = newConfidence;
        } else {
            throw new IllegalArgumentException("Confidence should be between 0 and 1");
        }
    }

    public void setSymbol(char newSymbol) {
        this.horseSymbol = newSymbol;
    }
}