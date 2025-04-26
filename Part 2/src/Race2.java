import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.ArrayList;

public class Race2 {
    private final int raceLength;
    private ArrayList<Horse2> horses;
    private String currentWeather = "Sunny";
    private Horse2 winner;

    public void setWeather(String weather) {
        this.currentWeather = weather;
        for (Horse2 horse : horses) {
            if (horse != null) {
                horse.applyWeatherEffect(weather);
            }
        }
    }

    public Race2(int distance) {
        raceLength = distance;
        horses = new ArrayList<>();
        winner = null;
    }

    public void addHorse(Horse2 theHorse, int laneNumber) {
        int index = laneNumber - 1;
        while (horses.size() <= index) {
            horses.add(null);
        }
        horses.set(index, theHorse);
    }

    public void moveHorse(Horse2 theHorse) {
        if (!theHorse.hasFallen()) {
            // Weather-modified movement
            if (Math.random() < theHorse.getConfidence()) {
                if (Math.random() < theHorse.speedModifier) {
                    theHorse.moveForward();
                }
            }

            // Weather-modified fall chance
            double fallChance = 0.05 * theHorse.getConfidence() * theHorse.getConfidence();
            if (currentWeather.equals("Muddy")) {
                fallChance *= 1.5;
            } else if (currentWeather.equals("Icy")) {
                fallChance *= 2.0;
            }

            if (Math.random() < fallChance) {
                theHorse.fall();
            }
        }
    }

    public boolean raceWonBy(Horse2 theHorse) {
        return theHorse.getDistanceTravelled() == raceLength;
    }

    public void startRace() {
        boolean finished = false;
        boolean allFallen = false;

        for (Horse2 horse : horses) {
            if (horse != null) {
                horse.goBackToStart();
            }
        }

        while (!finished && !allFallen) {
            for (Horse2 horse : horses) {
                if (horse != null) {
                    moveHorse(horse);
                }
            }

            printRace();

            for (Horse2 horse : horses) {
                if (horse != null && raceWonBy(horse)) {
                    winner = horse;
                    finished = true;
                    break;
                }
            }

            allFallen = true;
            for (Horse2 horse : horses) {
                if (horse != null && !horse.hasFallen()) {
                    allFallen = false;
                    break;
                }
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        updateConfidences();
        displayFinalResults();
    }

    private void updateConfidences() {
        if (winner != null) {
            setConfidenceWinner(winner);
        }

        for (Horse2 horse : horses) {
            if (horse != null && horse != winner) {
                setConfidenceLosers(horse);
            }
        }
    }

    private void setConfidenceWinner(Horse2 horse) {
        double currentConfidence = horse.getConfidence();
        double newConfidence = Math.min(1.0, currentConfidence + 0.1);
        horse.setConfidence(newConfidence);
    }

    private void setConfidenceLosers(Horse2 horse) {
        double currentConfidence = horse.getConfidence();
        double penalty = horse.hasFallen() ? 0.15 : 0.05;
        double newConfidence = Math.max(0.1, currentConfidence - penalty);
        horse.setConfidence(newConfidence);
    }

    private void displayFinalResults() {
        System.out.println("\nFINAL RESULTS:");
        System.out.println("==============");

        for (Horse2 horse : horses) {
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

    private void printRace() {
        System.out.print('\u000C');
        System.out.println("HORSE RACE SIMULATION");
        multiplePrint('=', raceLength + 30);
        System.out.println();

        for (Horse2 horse : horses) {
            if (horse != null) {
                printLaneWithInfo(horse);
                System.out.println();
            }
        }

        multiplePrint('=', raceLength + 30);
        System.out.println();
    }

    private void printLaneWithInfo(Horse2 theHorse) {
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        if(theHorse.hasFallen()) {
            System.out.print('⌢');
        } else {
            System.out.print(theHorse.getSymbol());
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');

        String status = theHorse.hasFallen() ? " (Fallen)" : "";
        System.out.print("  " + theHorse.getSymbol() + ": " + theHorse.getName() +
                " (Conf: " + String.format("%.2f", theHorse.getConfidence()) + ")" + status);
    }

    private void multiplePrint(char aChar, int times) {
        for (int i = 0; i < times; i++) {
            System.out.print(aChar);
        }
    }
}