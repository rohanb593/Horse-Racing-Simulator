import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.ArrayList;

public class Race {
    private final int raceLength;
    private ArrayList<Horse> horses;
    private Horse winner;

    public Race(int distance) {
        raceLength = distance;
        horses = new ArrayList<>();
        winner = null;

        // Initialize the horses
        horses.add(new Horse('A', "Thunder", 0.8));
        horses.add(new Horse('B', "Lightning", 0.5));
        horses.add(new Horse('C', "Storm", 0.3));
    }

    // ... [previous methods remain the same until startRace]

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
                    winner = horse;
                    System.out.println("\n" + horse.getName() + " has won the race!");
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

        // After race ends, update confidences and display results
        updateConfidences();
        displayFinalResults();
    }

    private void updateConfidences() {
        if (winner != null) {
            setConfidenceWinner(winner);
        }

        for (Horse horse : horses) {
            if (horse != null && horse != winner) {
                setConfidenceLosers(horse);
            }
        }
    }

    private void setConfidenceWinner(Horse horse) {
        double currentConfidence = horse.getConfidence();
        double newConfidence = Math.min(1.0, currentConfidence + 0.1); // Increase by 0.1, max 1.0
        horse.setConfidence(newConfidence);
    }

    private void setConfidenceLosers(Horse horse) {
        double currentConfidence = horse.getConfidence();
        double penalty = horse.hasFallen() ? 0.15 : 0.05; // Higher penalty for fallen horses
        double newConfidence = Math.max(0.1, currentConfidence - penalty); // Decrease, min 0.1
        horse.setConfidence(newConfidence);
    }

    private void displayFinalResults() {
        System.out.println("\nFINAL RESULTS:");
        System.out.println("==============");

        for (Horse horse : horses) {
            if (horse != null) {
                String status;
                if (horse == winner) {
                    status = "WINNER (Confidence +0.1)";
                } else if (horse.hasFallen()) {
                    status = "FELL (Confidence -0.15)";
                } else {
                    status = "LOSER (Confidence -0.05)";
                }

                System.out.println(horse.getSymbol() + ": " + horse.getName() + " - Final Confidence: " + horse.getConfidence() + " - " + status);

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
            }
            if (Math.random() < (0.05*theHorse.getConfidence()*theHorse.getConfidence())) {
                theHorse.fall();
                System.out.println("\n" + theHorse.getName() + " has fallen!");
            }
        }
    }



    private boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() == raceLength;
    }

    private void printRace() {
        System.out.print('\u000C');  // Clear console

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
            System.out.print('⌢'); // Fallen symbol
        } else {
            System.out.print(theHorse.getSymbol());
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');

        // Display horse info alongside the track
        String status = theHorse.hasFallen() ? " (Fallen)" : "";
        System.out.print("  " + theHorse.getSymbol() + ": " + theHorse.getName() +
                " (Conf: " + String.format("%.2f", theHorse.getConfidence()) + ")" + status);
    }

    private void multiplePrint(char aChar, int times) {
        for (int i = 0; i < times; i++) {
            System.out.print(aChar);
        }
    }



    public static void main(String[] args) {
        // Create a race with 30 units length
        Race race = new Race(30);


        // Alternatively, you could add more horses:
        race.addHorse(new Horse('D', "Rain", 0.85), 4);
        race.startRace();
    }

}