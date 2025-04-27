import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceMetrics {
    private Map<Horse2, List<Double>> confidenceHistory;
    private Map<Horse2, Double> averageSpeeds;
    private Map<Horse2, Integer> winCounts;
    private Map<Horse2, Integer> raceCounts;
    private Map<String, Map<Horse2, Double>> trackRecords;

    public PerformanceMetrics() {
        confidenceHistory = new HashMap<>();
        averageSpeeds = new HashMap<>();
        winCounts = new HashMap<>();
        raceCounts = new HashMap<>();
        trackRecords = new HashMap<>();
    }

    public void recordRace(Horse2[] horses, String trackCondition, int raceLength, int durationTicks) {
        for (Horse2 horse : horses) {
            if (horse == null) continue;

            confidenceHistory.computeIfAbsent(horse, k -> new ArrayList<>()).add(horse.getConfidence());

            double speed = (double) horse.getDistanceTravelled() / durationTicks;
            averageSpeeds.merge(horse, speed, (old, newVal) -> (old + newVal) / 2);

            raceCounts.merge(horse, 1, Integer::sum);

            trackRecords.computeIfAbsent(trackCondition, k -> new HashMap<>())
                    .merge(horse, (double) horse.getDistanceTravelled(), Math::max);
        }

        Horse2 winner = findWinner(horses, raceLength);
        if (winner != null) {
            winCounts.merge(winner, 1, Integer::sum);
        }
    }

    private Horse2 findWinner(Horse2[] horses, int raceLength) {
        for (Horse2 horse : horses) {
            if (horse != null && horse.getDistanceTravelled() >= raceLength) {
                return horse;
            }
        }
        return null;
    }

    public double getWinRatio(Horse2 horse) {
        if (!raceCounts.containsKey(horse)) return 0;
        return (double) winCounts.getOrDefault(horse, 0) / raceCounts.get(horse);
    }

    public double getAverageSpeed(Horse2 horse) {
        return averageSpeeds.getOrDefault(horse, 0.0);
    }

    public List<Double> getConfidenceHistory(Horse2 horse) {
        return confidenceHistory.getOrDefault(horse, new ArrayList<>());
    }

    public double getTrackRecord(Horse2 horse, String trackCondition) {
        return trackRecords.getOrDefault(trackCondition, new HashMap<>())
                .getOrDefault(horse, 0.0);
    }
}