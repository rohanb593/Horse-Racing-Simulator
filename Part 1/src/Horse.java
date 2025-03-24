
/**
 * Write a description of class Horse here.
 *
 * @Author
 * @version (a version number or a date)
 */
public class Horse
{
    private String horseName;
    private char horseSymbol;
    private double horseConfidence;
    private int distanceTravelled;
    private boolean horseHasFallen;
    /**
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, double horseConfidence)
    {
        this.horseName = horseName;
        this.horseSymbol = horseSymbol;
        this.horseConfidence = horseConfidence;
        this.distanceTravelled = 0;
        this.horseHasFallen = false;
    }







    //Other methods of class Horse
    public void fall()
    {
        this.horseHasFallen = true;
    }

    public double getConfidence()
    {
        return horseConfidence;
    }

    public int getDistanceTravelled()
    {
        return distanceTravelled;
    }

    public String getName()
    {
        return horseName;
    }

    public char getSymbol()
    {
        return horseSymbol;
    }

    public void goBackToStart()
    {
        this.distanceTravelled = 0;
        this.horseHasFallen = false;
    }

    public boolean hasFallen()
    {
        return horseHasFallen;
    }

    public void moveForward()
    {
        this.distanceTravelled++;
    }

    public void setConfidence(double newConfidence)
    {
        if (newConfidence >= 0 && newConfidence <= 1){
            this.horseConfidence = newConfidence;
        } else {
            throw new IllegalArgumentException("Confidence should be between 0 and 1");
        }
    }

    public void setSymbol(char newSymbol)
    {
        this.horseSymbol = newSymbol;
    }

}
