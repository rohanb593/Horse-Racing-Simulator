import javax.swing.*;
import java.awt.*;


public class RaceGUI {
    private Race2 race;
    private Horse2[] horses;
    private JFrame frame;
    private RacePanel racePanel;
    private JButton startButton;
    private JTextArea infoArea;
    private Timer animationTimer;
    private Timer raceTimer;
    private boolean raceInProgress = false;
    private final int MAX_HORSES = 10;
    private JComboBox<String> weatherCombo;
    private static final int RACE_LENGTH = 50;
    private AddHorsePanel addHorsePanel;

    private BettingPanel bettingPanel;
    public PerformanceMetrics performanceMetrics;
    private PerformancePanel performancePanel;
    private int raceDurationTicks = 0;





    public static final String[] SHAPE_OPTIONS = {"Rectangle", "Circle", "Triangle", "Diamond", "Star"};
    public static final String[] WEATHER_OPTIONS = {"Sunny", "Rainy", "Muddy", "Icy"};
    public static final Color[] COLOR_OPTIONS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    public void appendToInfoArea(String message) {
        infoArea.append(message + "\n");
    }

    public Horse2[] getHorses() {
        return horses;
    }

    public RaceGUI() {
        performanceMetrics = new PerformanceMetrics();
        racePanel = new RacePanel(null, null);
        initializeRaceWithDefaults();
        createAndShowGUI();
    }

    private void initializeRaceWithDefaults() {
        race = new Race2(RACE_LENGTH);
        horses = new Horse2[MAX_HORSES];

        horses[0] = new Horse2('A', "Thunder", 0.9, "Rectangle", Color.RED);
        horses[1] = new Horse2('B', "Lightning", 0.8, "Circle", Color.BLUE);
        horses[2] = new Horse2('C', "Storm", 0.7, "Triangle", Color.GREEN);
        horses[3] = new Horse2('D', "Doom", 0.6, "Diamond", Color.YELLOW);
        horses[4] = new Horse2('E', "Rain", 0.75, "Star", Color.MAGENTA);

        for (int i = 0; i < horses.length; i++) {
            if (horses[i] != null) {
                race.addHorse(horses[i], i + 1);
            }
        }

        racePanel.setRace(race);
        racePanel.setHorses(horses);
    }

    public void updateAllHorseDropdowns() {
        bettingPanel.updateHorses(horses);
        performancePanel.updateHorses(horses);
    }







    private void createAndShowGUI() {
        frame = new JFrame("Horse Race");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        performanceMetrics = new PerformanceMetrics();
        // Initialize horses array first
        horses = new Horse2[MAX_HORSES];
        initializeRaceWithDefaults();

        // Now create panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);

        addHorsePanel = new AddHorsePanel(this);
        bettingPanel = new BettingPanel(this, horses);  // Now horses is initialized
        performancePanel = new PerformancePanel(performanceMetrics, horses);

        splitPane.setLeftComponent(addHorsePanel);

        // Create main split pane (left for controls, right for race)
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);

        // Create control panels
        addHorsePanel = new AddHorsePanel(this);
        bettingPanel = new BettingPanel(this, horses);

        // Start with horse panel visible
        splitPane.setLeftComponent(addHorsePanel);

        // Right Panel - Race and Controls
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Race Panel
        racePanel = new RacePanel(race, horses);
        rightPanel.add(racePanel, BorderLayout.CENTER);

        // Bottom Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout());

        // Weather Controls
        JPanel weatherPanel = new JPanel();
        weatherPanel.add(new JLabel("Weather:"));
        weatherCombo = new JComboBox<>(WEATHER_OPTIONS);
        weatherCombo.addActionListener(_ -> {
            String currentWeather = (String) weatherCombo.getSelectedItem();
            racePanel.setCurrentWeather(currentWeather);
            applyWeatherEffects();
            infoArea.append("Weather changed to: " + currentWeather + "\n");
        });
        weatherPanel.add(weatherCombo);
        controlPanel.add(weatherPanel);

        // Start Button
        startButton = new JButton("Start Race");
        startButton.addActionListener(_ -> startRace());
        controlPanel.add(startButton);

        // Results Button
        JButton resultsButton = new JButton("Show Results");
        resultsButton.addActionListener(_ -> showRaceResults());
        controlPanel.add(resultsButton);

        // Info Area
        infoArea = new JTextArea(5, 80);
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);

        rightPanel.add(controlPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        frame.add(splitPane);

        // Initialize timers
        animationTimer = new Timer(50, _ -> racePanel.repaint());
        setupRaceTimer();

        frame.pack();
        frame.setMinimumSize(new Dimension(1000, 600));
        frame.setVisible(true);

        performanceMetrics = new PerformanceMetrics();

        // Create tabbed pane for left side
        JTabbedPane leftTabbedPane = new JTabbedPane();
        addHorsePanel = new AddHorsePanel(this);
        bettingPanel = new BettingPanel(this, horses);
        performancePanel = new PerformancePanel(performanceMetrics, horses);

        leftTabbedPane.addTab("Horses", addHorsePanel);
        leftTabbedPane.addTab("Betting", bettingPanel);
        leftTabbedPane.addTab("Performance", performancePanel);

        splitPane.setLeftComponent(leftTabbedPane);
    }


    private void showRaceResults() {
        if (raceInProgress) {
            JOptionPane.showMessageDialog(frame,
                    "Race is still in progress!",
                    "Race Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder results = new StringBuilder("\n--- RACE RESULTS ---\n");
        for (Horse2 horse : horses) {
            if (horse != null) {
                results.append(horse.getSymbol())
                        .append(": ").append(horse.getName())
                        .append(" - Distance: ").append(horse.getDistanceTravelled())
                        .append(" - ").append(horse.hasFallen() ? "FALLEN" : "FINISHED")
                        .append("\n");
            }
        }
        infoArea.append(results.toString());
    }

    private void setupRaceTimer() {
        raceTimer = new Timer(100, _ -> {
            raceDurationTicks++;  // Track each tick

            if (!raceInProgress) return;

            boolean allHorsesFallen = true;
            boolean raceWon = false;

            for (Horse2 horse : horses) {
                if (horse != null && race.raceWonBy(horse)) {
                    endRace(horse.getName() + " wins the race!");
                    raceWon = true;
                    break;
                }
            }

            if (raceWon) return;

            for (Horse2 horse : horses) {
                if (horse != null && !horse.hasFallen()) {
                    allHorsesFallen = false;
                    moveHorseWithWeather(horse);
                    if (horse.hasFallen()) {
                        infoArea.append(horse.getName() + " has fallen!\n");
                    }
                }
            }

            if (allHorsesFallen) {
                endRace("All horses have fallen! Race over.");
            }
        });
    }

    private void moveHorseWithWeather(Horse2 horse) {
        if (!horse.hasFallen()) {
            if (Math.random() < horse.getConfidence()) {
                if (Math.random() < horse.speedModifier) {
                    horse.moveForward();
                }
            }

            // Reduced base fall chance from 0.05 to 0.02 (2% base chance)
            double fallChance = 0.002 * horse.getConfidence(); // Removed the squared confidence

            // Reduced weather multipliers
            if (racePanel.getCurrentWeather().equals("Muddy")) {
                fallChance *= 1.3;  // Reduced from 1.5
            } else if (racePanel.getCurrentWeather().equals("Icy")) {
                fallChance *= 1.6;  // Reduced from 2.0
            }

            if (Math.random() < fallChance) {
                horse.fall();
            }
        }
    }

    private void applyWeatherEffects() {
        String currentWeather = racePanel.getCurrentWeather();
        for (Horse2 horse : horses) {
            if (horse != null) {
                horse.applyWeatherEffect(currentWeather);
            }
        }
    }

    public void addNewHorse(String name, double confidence, String shape, Color color) {
        int nextAvailableSlot = -1;
        for (int i = 0; i < horses.length; i++) {
            if (horses[i] == null) {
                nextAvailableSlot = i;
                break;
            }
        }

        if (nextAvailableSlot == -1) {
            JOptionPane.showMessageDialog(frame,
                    "Maximum number of horses (" + MAX_HORSES + ") reached!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            if (confidence < 0.1 || confidence > 1.0) {
                throw new IllegalArgumentException("Confidence must be between 0.1 and 1.0");
            }

            char symbol = (char) ('A' + nextAvailableSlot);
            Horse2 newHorse = new Horse2(symbol, name, confidence, shape, color);
            newHorse.applyWeatherEffect(racePanel.getCurrentWeather());
            horses[nextAvailableSlot] = newHorse;
            race.addHorse(newHorse, nextAvailableSlot + 1);

            // Update all dropdown menus
            updateAllHorseDropdowns();

            racePanel.repaint();
            infoArea.append("Added new horse: " + name + " (" + shape + ")\n");

            if (nextAvailableSlot == MAX_HORSES - 1) {
                addHorsePanel.disableAdding();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame,
                    e.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public JComboBox<Color> getColorJComboBox() {
        JComboBox<Color> colorCombo = new JComboBox<>(COLOR_OPTIONS);
        colorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Color) {
                    setBackground((Color)value);
                    setText(" ");
                }
                return this;
            }
        });
        return colorCombo;
    }

    private void startRace() {
        raceInProgress = true;
        startButton.setEnabled(false);
        infoArea.setText("Starting new race... Weather: " + racePanel.getCurrentWeather() + "\n");

        for (Horse2 horse : horses) {
            if (horse != null) {
                horse.goBackToStart();
                horse.applyWeatherEffect(racePanel.getCurrentWeather());
            }
        }

        animationTimer.start();
        raceTimer.start();
    }

    private void endRace(String message) {
        raceInProgress = false;
        raceTimer.stop();
        animationTimer.stop();

        Horse2 winner = findWinningHorse();
        // Record race results for all horses
        for (Horse2 horse : horses) {
            if (horse != null) {
                horse.recordRaceResult(horse == winner);
            }
        }

        if (winner != null) {
            bettingPanel.processWinnings(winner);
        }

        performanceMetrics.recordRace(horses, racePanel.getCurrentWeather(),
                RACE_LENGTH, raceDurationTicks);

        // Reset for next race
        raceDurationTicks = 0;

        SwingUtilities.invokeLater(() -> {
            infoArea.append(message + "\n");
            startButton.setEnabled(true);
            racePanel.repaint();
        });
    }

    private Horse2 findWinningHorse() {
        for (Horse2 horse : horses) {
            if (horse != null && race.raceWonBy(horse)) {
                return horse;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RaceGUI());
    }


}