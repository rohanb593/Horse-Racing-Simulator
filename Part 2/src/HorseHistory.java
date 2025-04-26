import java.util.*;
import java.text.SimpleDateFormat;

public class HorseHistory {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Map<String, List<RaceRecord>> horseRecords = new HashMap<>();

    public static class RaceRecord {
        String horseName;
        char symbol;
        Date date;
        String weather;
        int distance;
        double finishTime;
        int finishPosition;
        double confidenceBefore;
        double confidenceAfter;

        public RaceRecord(String horseName, char symbol, String weather, int distance,
                          double finishTime, int finishPosition,
                          double confidenceBefore, double confidenceAfter) {
            this.horseName = horseName;
            this.symbol = symbol;
            this.date = new Date();
            this.weather = weather;
            this.distance = distance;
            this.finishTime = finishTime;
            this.finishPosition = finishPosition;
            this.confidenceBefore = confidenceBefore;
            this.confidenceAfter = confidenceAfter;
        }
    }

    public void recordRace(Horse2 horse, String weather, int distance,
                           double finishTime, int finishPosition,
                           double confidenceBefore, double confidenceAfter) {
        RaceRecord record = new RaceRecord(horse.getName(), horse.getSymbol(), weather,
                distance, finishTime, finishPosition,
                confidenceBefore, confidenceAfter);

        if (!horseRecords.containsKey(horse.getName())) {
            horseRecords.put(horse.getName(), new ArrayList<>());
        }
        horseRecords.get(horse.getName()).add(record);
    }

    public List<RaceRecord> getHorseHistory(String horseName) {
        return horseRecords.getOrDefault(horseName, new ArrayList<>());
    }

    public double getAverageSpeed(String horseName) {
        List<RaceRecord> records = getHorseHistory(horseName);
        if (records.isEmpty()) return 0;

        double totalSpeed = 0;
        for (RaceRecord record : records) {
            totalSpeed += record.distance / record.finishTime;
        }
        return totalSpeed / records.size();
    }

    public double getWinRatio(String horseName) {
        List<RaceRecord> records = getHorseHistory(horseName);
        if (records.isEmpty()) return 0;

        long wins = records.stream().filter(r -> r.finishPosition == 1).count();
        return (double) wins / records.size();
    }

    public double getBestTime(String horseName, String weatherCondition) {
        return getHorseHistory(horseName).stream()
                .filter(r -> weatherCondition == null || r.weather.equals(weatherCondition))
                .mapToDouble(r -> r.finishTime)
                .min()
                .orElse(0);
    }

    public void displayHorseHistory(String horseName) {
        List<RaceRecord> history = getHorseHistory(horseName);
        if (history.isEmpty()) {
            System.out.println("No history available for " + horseName);
            return;
        }

        System.out.println("\nRace History for " + horseName);
        System.out.println("====================================");
        System.out.printf("%-20s %-10s %-8s %-8s %-12s %-8s %-12s %-12s\n",
                "Date", "Weather", "Distance", "Time", "Position", "Conf Before", "Conf After", "Speed");

        for (RaceRecord record : history) {
            double speed = record.distance / record.finishTime;
            System.out.printf("%-20s %-10s %-8d %-8.2f %-12d %-8.2f %-12.2f %-12.2f\n",
                    DATE_FORMAT.format(record.date).substring(0, 16),
                    record.weather,
                    record.distance,
                    record.finishTime,
                    record.finishPosition,
                    record.confidenceBefore,
                    record.confidenceAfter,
                    speed);
        }

        System.out.println("\nStatistics:");
        System.out.println("Average Speed: " + String.format("%.2f", getAverageSpeed(horseName)));
        System.out.println("Win Ratio: " + String.format("%.1f%%", getWinRatio(horseName) * 100));
        System.out.println("Best Time: " + String.format("%.2f", getBestTime(horseName, null)));

        System.out.println("\nWeather-Specific Best Times:");
        for (String weather : new String[]{"Sunny", "Rainy", "Muddy", "Icy"}) {
            double bestTime = getBestTime(horseName, weather);
            if (bestTime > 0) {
                System.out.println(weather + ": " + String.format("%.2f", bestTime));
            }
        }
    }
}