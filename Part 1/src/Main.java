public class Main {
    public static void main(String[] args) {
        // Create horses
        Horse horse1 = new Horse('!', "PIPPI LONGSTOCKING", 0.6);
        Horse horse2 = new Horse('#', "KOKOMO", 0.3);
        Horse horse3 = new Horse('$', "EL JEFE", 0.4);

        // Create a race
        Race race = new Race(20); // Race length of 20 units

        // Add horses to the race
        race.addHorse(horse1, 1);
        race.addHorse(horse2, 2);
        race.addHorse(horse3, 3);

        // Start the race
        race.startRace();
    }


}
