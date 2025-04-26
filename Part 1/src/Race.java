import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.ArrayList;

public class Race {
    private final int raceLength;
    private final ArrayList<Horse> horses;

    public Race(int distance) {
        raceLength = distance;
        horses = new ArrayList<>();


        // Initialize the horses
        horses.add(new Horse('A', "Thunder", 0.8));
        horses.add(new Horse('B', "Lightning", 0.5));
        horses.add(new Horse('C', "Storm", 0.3));
    }

    public void startRace() {
        boolean finished = false;
        boolean allFallen = false;

        for (Horse horse : horses) {
            if (horse != null) {
                horse.goBackToStart();
            }
        }

        while (!finished && !allFallen) {
            // Move each horse
            for (Horse horse : horses) {
                if (horse != null) {
                    moveHorse(horse);
                }
            }

            printRace();

            // Check if any horse has won
            for (Horse horse : horses) {
                if (horse != null && raceWonBy(horse)) {

                    System.out.println("\n" + horse.getName() + " has won the race!!!!!");
                    finished = true;
                    break;
                }
            }

            // Check if all horses have fallen
            allFallen = true;
            for (Horse horse : horses) {
                if (horse != null && !horse.hasFallen()) {
                    allFallen = false;
                    break;
                }
            }

            if (allFallen) {
                System.out.println("\nAll horses have fallen! The race is over with no winner.");
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addHorse(Horse theHorse, int laneNumber) {
        int index = laneNumber - 1;
        while (horses.size() <= index) {
            horses.add(null);
        }
        horses.set(index, theHorse);
    }

    private void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            if (Math.random() < theHorse.getConfidence()) {
                theHorse.moveForward();

                // Check if this move made the horse win the race
                if (raceWonBy(theHorse)) {
                    double newConfidence = Math.min(1.0, theHorse.getConfidence() + 0.1);
                    theHorse.setConfidence(newConfidence);
                }
            }

            if (Math.random() < (0.05*theHorse.getConfidence()*theHorse.getConfidence())) {
                theHorse.fall();
                System.out.println("\n" + theHorse.getSymbol() + ": " + theHorse.getName() + " has fallen!");
                theHorse.setConfidence(theHorse.getConfidence() - 0.1);
            }
        }
    }


    private boolean raceWonBy(Horse theHorse) {
        if (theHorse.getDistanceTravelled() == raceLength) {
            return true;
        } else {
            return false;
        }
    }

    private void printRace() {
        System.out.println("HORSE RACE SIMULATION");
        multiplePrint('=', raceLength + 30);
        System.out.println();

        for (Horse horse : horses) {
            if (horse != null) {
                printLaneWithInfo(horse);
                System.out.println();
            }
        }

        multiplePrint('=', raceLength + 30);
        System.out.println();
    }

    private void printLaneWithInfo(Horse theHorse) {
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        if(theHorse.hasFallen()) {
            System.out.print('❌' ); // Fallen symbol
        } else {
            System.out.print(theHorse.getSymbol());
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');

        // Display horse info alongside the track
        String status;
        if (theHorse.hasFallen()) {

            status = " (Fallen)";
        }
        else
        {
            status = "";
        }
        System.out.print("  " + theHorse.getSymbol() + ": " + theHorse.getName() +
                " (Current Confidence: " + String.format("%.1f", theHorse.getConfidence()) + ")" + status);
    }

    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    public static void main(String[] args) {
        // Create a race with 30 units length
        Race race = new Race(30);

        // Add a horse with initial symbol 'R' and then change it to 'D'
        Horse horse = new Horse('R', "Rain", 0.85);
        horse.setSymbol('D');  // Using setSymbol() to change the symbol
        horse.setConfidence(0.7);
        race.addHorse(horse, 4);


        race.startRace();
    }

}