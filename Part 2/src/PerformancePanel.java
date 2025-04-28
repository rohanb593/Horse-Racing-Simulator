import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class PerformancePanel extends JPanel {
    public final PerformanceMetrics metrics;
    private JComboBox<Horse2> horseSelector;
    private JComboBox<String> trackSelector;
    private JTextArea displayArea;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public PerformancePanel(PerformanceMetrics metrics, Horse2[] horses) {
        this.metrics = metrics;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        initializeComponents(horses);
    }

    private void initializeComponents(Horse2[] horses) {
        // Main panel with vertical layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 5, 5)); // Single column layout

        // Horse selection panel
        JPanel horsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        horsePanel.add(new JLabel("Select Horse:"));
        horseSelector = new JComboBox<>();
        updateHorseSelector(horses);
        horseSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Horse2 horse) {
                    setText(horse.getName() + " (" + horse.getSymbol() + ")");
                }
                return this;
            }
        });
        horsePanel.add(horseSelector);
        formPanel.add(horsePanel);

        // Track condition selection panel
        JPanel trackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        trackPanel.add(new JLabel("Track Condition:"));
        trackSelector = new JComboBox<>(new String[]{"All", "Sunny", "Rainy", "Muddy", "Icy"});
        trackPanel.add(trackSelector);
        formPanel.add(trackPanel);

        // Add action listeners
        horseSelector.addActionListener(e -> updateDisplay());
        trackSelector.addActionListener(e -> updateDisplay());

        // Display area
        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Performance Details"));

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateHorseSelector(Horse2[] horses) {
        horseSelector.removeAllItems();
        for (Horse2 horse : horses) {
            if (horse != null) {
                horseSelector.addItem(horse);
            }
        }
    }

    private void updateDisplay() {
        Horse2 selectedHorse = (Horse2) horseSelector.getSelectedItem();
        String trackCondition = (String) trackSelector.getSelectedItem();

        if (selectedHorse == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Performance Metrics for ").append(selectedHorse.getName()).append("\n\n");
        sb.append(String.format("%-20s: %s\n", "Average Speed", df.format(metrics.getAverageSpeed(selectedHorse))));
        sb.append(String.format("%-20s: %s%%\n", "Win Ratio", df.format(metrics.getWinRatio(selectedHorse) * 100)));

        if (!"All".equals(trackCondition)) {
            sb.append(String.format("%-20s: %s\n", "Best " + trackCondition + " Distance",
                    df.format(metrics.getTrackRecord(selectedHorse, trackCondition))));
        }

        sb.append("\nConfidence History:\n");
        List<Double> confidenceHistory = metrics.getConfidenceHistory(selectedHorse);
        if (confidenceHistory.isEmpty()) {
            sb.append("No race history available\n");
        } else {
            for (int i = 0; i < confidenceHistory.size(); i++) {
                sb.append(String.format("Race %-3d: %s\n", i + 1, df.format(confidenceHistory.get(i))));
            }
        }

        displayArea.setText(sb.toString());
    }

    public void updateHorses(Horse2[] horses) {
        horseSelector.removeAllItems();
        for (Horse2 horse : horses) {
            if (horse != null) {
                horseSelector.addItem(horse);
            }
        }
        updateDisplay();
    }
}