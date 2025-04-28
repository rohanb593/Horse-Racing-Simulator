import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

public class BettingPanel extends JPanel {
    private final RaceGUI raceGUI;
    private JComboBox<String> horseCombo;
    private JSpinner amountSpinner;
    private JLabel moneyLabel;
    private JLabel statsLabel;  // New label for performance stats
    private final BettingLogic bettingLogic;
    private double playerMoney = 1000.0;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public BettingPanel(RaceGUI raceGUI, Horse2[] horses) {
        this.raceGUI = raceGUI;
        this.bettingLogic = new BettingLogic();
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Betting Panel"));
        initializeComponents(horses);
    }

    private void initializeComponents(Horse2[] horses) {
        // Main panel with vertical layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 5, 5)); // Single column layout

        // Horse selection panel
        JPanel horsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        horsePanel.add(new JLabel("Select Horse:"));
        horseCombo = new JComboBox<>();
        updateHorseCombo(horses);
        horseCombo.addActionListener(e -> updateHorseStats());  // Add listener
        horsePanel.add(horseCombo);
        formPanel.add(horsePanel);

        // Horse stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statsLabel = new JLabel("Win Ratio: - | Avg Speed: -");
        statsPanel.add(statsLabel);
        formPanel.add(statsPanel);

        // Bet amount panel
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        amountPanel.add(new JLabel("Amount: $"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10.0, 1.0, playerMoney, 1.0);
        amountSpinner = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(amountSpinner, "#,##0.00");
        amountSpinner.setEditor(editor);
        amountSpinner.setPreferredSize(new Dimension(100, 25));
        amountPanel.add(amountSpinner);
        formPanel.add(amountPanel);

        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton placeBetButton = new JButton("Place Bet");
        placeBetButton.addActionListener(this::placeBetAction);
        buttonPanel.add(placeBetButton);
        formPanel.add(buttonPanel);

        // Money display panel
        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        moneyLabel = new JLabel("Current Money: $" + df.format(playerMoney));
        moneyPanel.add(moneyLabel);
        formPanel.add(moneyPanel);

        add(formPanel, BorderLayout.NORTH);

        if (horses != null && horses.length > 0 && horses[0] != null && raceGUI.performanceMetrics != null) {
            updateHorseStats();
        } else {
            statsLabel.setText("Win Ratio: - | Avg Speed: -");
        }
    }

    private void updateHorseStats() {
        String selected = (String) horseCombo.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            statsLabel.setText("Win Ratio: - | Avg Speed: -");
            return;
        }

        String horseSymbol = selected.split(" - ")[0];
        Horse2 selectedHorse = findHorseBySymbol(horseSymbol);

        if (selectedHorse != null) {
            // Get win ratio directly from the horse
            double winRatio = selectedHorse.getWinRatio();

            // Get average speed from performance metrics if available
            double avgSpeed = raceGUI.performanceMetrics != null ?
                    raceGUI.performanceMetrics.getAverageSpeed(selectedHorse) : 0;

            String stats = String.format("Win Ratio: %.1f%% | Avg Speed: %.2f",
                    winRatio * 100,
                    avgSpeed);
            statsLabel.setText(stats);
        } else {
            statsLabel.setText("Win Ratio: - | Avg Speed: -");
        }
    }

    private void placeBetAction(ActionEvent e) {
        try {
            String selected = (String) horseCombo.getSelectedItem();
            if (selected == null || selected.isEmpty()) {
                throw new IllegalArgumentException("Please select a horse");
            }

            double amount = ((Number) amountSpinner.getValue()).doubleValue();
            if (amount > playerMoney) {
                throw new IllegalArgumentException("You don't have enough money");
            }

            String horseSymbol = selected.split(" - ")[0];
            Horse2 selectedHorse = findHorseBySymbol(horseSymbol);

            bettingLogic.placeBet(selectedHorse, amount);
            playerMoney -= amount;
            updateMoneyDisplay();

            raceGUI.appendToInfoArea("Bet placed: $" + df.format(amount) +
                    " on " + selectedHorse.getName() +
                    " (Win ratio: " + df.format(selectedHorse.getWinRatio() * 100) + "%)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Betting Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHorseCombo(Horse2[] horses) {
        horseCombo.removeAllItems();
        for (Horse2 horse : horses) {
            if (horse != null) {
                horseCombo.addItem(horse.getSymbol() + " - " + horse.getName());
            }
        }
    }

    private Horse2 findHorseBySymbol(String symbol) {
        for (Horse2 horse : raceGUI.getHorses()) {
            if (horse != null && horse.getSymbol() == symbol.charAt(0)) {
                return horse;
            }
        }
        return null;
    }

    public void updateHorses(Horse2[] horses) {
        updateHorseCombo(horses);
        updateHorseStats();  // Update stats when horses list changes
    }

    public void processWinnings(Horse2 winner) {
        if (winner != null) {
            double winnings = bettingLogic.calculateWinnings(winner);
            if (winnings > 0) {
                playerMoney += winnings;
                updateMoneyDisplay();
                raceGUI.appendToInfoArea("You won $" + df.format(winnings) +
                        " on " + winner.getName() + "!");
            }
            bettingLogic.clearBets();
        }
    }

    private void updateMoneyDisplay() {
        moneyLabel.setText("Current Money: $" + df.format(playerMoney));
        ((SpinnerNumberModel) amountSpinner.getModel()).setMaximum(playerMoney);
    }
}