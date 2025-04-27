import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class PerformancePanel extends JPanel {
    private final PerformanceMetrics metrics;
    private JComboBox<Horse2> horseSelector;
    private JComboBox<String> trackSelector;
    private JTextArea displayArea;
    private DecimalFormat df = new DecimalFormat("0.00");

    public PerformancePanel(PerformanceMetrics metrics, Horse2[] horses) {
        this.metrics = metrics;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        initializeComponents(horses);
    }

    private void initializeComponents(Horse2[] horses) {
        // Selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        horseSelector = new JComboBox<>();
        for (Horse2 horse : horses) {
            if (horse != null) horseSelector.addItem(horse);
        }
        horseSelector.addActionListener(e -> updateDisplay());

        trackSelector = new JComboBox<>(new String[]{"All", "Sunny", "Rainy", "Muddy", "Icy"});
        trackSelector.addActionListener(e -> updateDisplay());

        selectionPanel.add(new JLabel("Select Horse:"));
        selectionPanel.add(horseSelector);
        selectionPanel.add(new JLabel("Track Condition:"));
        selectionPanel.add(trackSelector);

        // Display area
        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        add(selectionPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateDisplay() {
        Horse2 selectedHorse = (Horse2) horseSelector.getSelectedItem();
        String trackCondition = (String) trackSelector.getSelectedItem();

        if (selectedHorse == null) return;

        // Build the display string using StringBuilder
        StringBuilder sb = new StringBuilder();
        sb.append("Performance Metrics for ").append(selectedHorse.getName()).append("\n\n");
        sb.append("Average Speed: ").append(df.format(metrics.getAverageSpeed(selectedHorse))).append("\n");
        sb.append("Win Ratio: ").append(df.format(metrics.getWinRatio(selectedHorse) * 100)).append("%\n");


        if (!"All".equals(trackCondition)) {
            sb.append("Best Distance on ").append(trackCondition).append(" track: ")
                    .append(df.format(metrics.getTrackRecord(selectedHorse, trackCondition))).append("\n");
        }

        sb.append("\nConfidence History:\n");
        List<Double> confidenceHistory = metrics.getConfidenceHistory(selectedHorse);
        for (int i = 0; i < confidenceHistory.size(); i++) {
            sb.append("Race ").append(i + 1).append(": ")
                    .append(df.format(confidenceHistory.get(i))).append("\n");
        }

        // Set the complete string at once
        displayArea.setText(sb.toString());
    }

    public void updateHorses(Horse2[] horses) {
        horseSelector.removeAllItems();
        for (Horse2 horse : horses) {
            if (horse != null) horseSelector.addItem(horse);
        }
    }
}