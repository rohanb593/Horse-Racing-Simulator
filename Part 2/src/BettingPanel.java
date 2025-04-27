import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

public class BettingPanel extends JPanel {
    private final RaceGUI raceGUI;
    private JComboBox<String> horseCombo;
    private JSpinner amountSpinner;
    private JButton placeBetButton;
    private JButton toHorsesButton;
    private JLabel moneyLabel;
    private BettingLogic bettingLogic;
    private double playerMoney = 1000.0;
    private DecimalFormat df = new DecimalFormat("0.00");

    public BettingPanel(RaceGUI raceGUI, Horse2[] horses) {
        this.raceGUI = raceGUI;
        this.bettingLogic = new BettingLogic();
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Betting Panel"));
        initializeComponents(horses);
    }

    private void initializeComponents(Horse2[] horses) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        toHorsesButton = new JButton("Go to Horses");
        toHorsesButton.setPreferredSize(new Dimension(120, 25));
        toHorsesButton.addActionListener(e -> raceGUI.showAddHorsePanel());
        topPanel.add(toHorsesButton);

        horseCombo = new JComboBox<>();
        updateHorseCombo(horses);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 1, playerMoney, 1);
        amountSpinner = new JSpinner(spinnerModel);
        amountSpinner.setPreferredSize(new Dimension(80, 25));

        placeBetButton = new JButton("Place Bet");
        placeBetButton.setPreferredSize(new Dimension(100, 25));
        placeBetButton.addActionListener(this::placeBetAction);

        moneyLabel = new JLabel("Money: $" + df.format(playerMoney));
        moneyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        topPanel.add(new JLabel("Select Horse:"));
        topPanel.add(horseCombo);
        topPanel.add(new JLabel("Amount: $"));
        topPanel.add(amountSpinner);
        topPanel.add(placeBetButton);
        topPanel.add(moneyLabel);

        add(topPanel, BorderLayout.NORTH);
    }

    private void placeBetAction(ActionEvent e) {
        try {
            String selected = (String) horseCombo.getSelectedItem();
            if (selected == null || selected.isEmpty()) {
                throw new IllegalArgumentException("No horse selected");
            }

            double amount = (double) amountSpinner.getValue();
            if (amount > playerMoney) {
                throw new IllegalArgumentException("Not enough money");
            }

            String horseSymbol = selected.split(" - ")[0];
            Horse2 selectedHorse = findHorseBySymbol(horseSymbol);

            bettingLogic.placeBet(selectedHorse, amount);
            playerMoney -= amount;
            updateMoneyDisplay();

            double winRatio = selectedHorse.getWinRatio();
            raceGUI.appendToInfoArea("Placed $" + df.format(amount) +
                    " bet on " + selectedHorse.getName() +
                    " (Win ratio: " + df.format(winRatio * 100) + "%)");
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
    }

    public void processWinnings(Horse2 winner) {
        if (winner != null) {
            double winnings = bettingLogic.calculateWinnings(winner);
            if (winnings > 0) {
                playerMoney += winnings;
                updateMoneyDisplay();
                raceGUI.appendToInfoArea("You won $" + String.format("%.2f", winnings) +
                        " on " + winner.getName() + "!");
            }
        }
        bettingLogic.clearBets();
    }

    private void updateMoneyDisplay() {
        moneyLabel.setText("Money: $" + String.format("%.2f", playerMoney));
    }
}