import java.util.HashMap;
import java.util.Map;

public class BettingLogic {
    private final Map<Horse2, Double> bets;

    public BettingLogic() {
        bets = new HashMap<>();
    }

    public void placeBet(Horse2 horse, double amount) {
        if (horse == null) throw new IllegalArgumentException("No horse selected");
        if (amount <= 0) throw new IllegalArgumentException("Bet amount must be positive");
        bets.put(horse, bets.getOrDefault(horse, 0.0) + amount);
    }

    public double calculateWinnings(Horse2 winner) {
        if (winner == null || !bets.containsKey(winner)) return 0;

        double totalPool = bets.values().stream().mapToDouble(Double::doubleValue).sum();
        double betOnWinner = bets.get(winner);
        double odds = (totalPool - betOnWinner) / betOnWinner;

        return betOnWinner * (1 + odds);
    }

    public void clearBets() {
        bets.clear();
    }


}