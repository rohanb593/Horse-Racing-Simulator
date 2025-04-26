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
    private boolean raceInProgress;
    private final int MAX_HORSES = 10;
    private JMenuBar menuBar;
    private JMenuItem addHorseMenuItem;
    private JComboBox<String> weatherCombo;
    private static final int RACE_LENGTH = 50;

    private static final String[] SHAPE_OPTIONS = {"Rectangle", "Circle", "Triangle", "Diamond", "Star"};
    private static final String[] WEATHER_OPTIONS = {"Sunny", "Rainy", "Muddy", "Icy"};
    private static final Color[] COLOR_OPTIONS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    public RaceGUI() {
        racePanel = new RacePanel(null, null); // Initialize first with null values
        initializeRaceWithDefaults();
        createAndShowGUI();
    }

    private void initializeRaceWithDefaults() {
        race = new Race2(RACE_LENGTH); // Use the constant instead of racePanel.RACE_LENGTH
        horses = new Horse2[MAX_HORSES];

        horses[0] = new Horse2('A', "Thunder", 0.9, "Rectangle", Color.RED);
        horses[1] = new Horse2('B', "Lightning", 0.8, "Circle", Color.BLUE);
        horses[2] = new Horse2('C', "Storm", 0.7, "Triangle", Color.GREEN);
        horses[3] = new Horse2('D', "Doom", 0.6, "Diamond", Color.YELLOW);
        horses[4] = new Horse2('E', "Rain", 0.5, "Star", Color.MAGENTA);

        for (int i = 0; i < horses.length; i++) {
            if (horses[i] != null) {
                race.addHorse(horses[i], i + 1);
            }
        }

        // Now update the racePanel with the actual race and horses
        racePanel.setRace(race);
        racePanel.setHorses(horses);
    }

    private void createAndShowGUI() {
        frame = new JFrame("Horse Race");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        createMenuBar();

        racePanel = new RacePanel(race, horses);
        frame.add(racePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());

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

        startButton = new JButton("Start Race");
        startButton.addActionListener(_ -> startRace());
        controlPanel.add(startButton);

        infoArea = new JTextArea(5, 80);
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.SOUTH);

        animationTimer = new Timer(50, _ -> racePanel.repaint());
        setupRaceTimer();

        frame.pack();
        frame.setVisible(true);
    }

    private void setupRaceTimer() {
        raceTimer = new Timer(100, _ -> {
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

            double fallChance = 0.05 * horse.getConfidence() * horse.getConfidence();
            if (racePanel.getCurrentWeather().equals("Muddy")) {
                fallChance *= 1.5;
            } else if (racePanel.getCurrentWeather().equals("Icy")) {
                fallChance *= 2.0;
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

    private void createMenuBar() {
        menuBar = new JMenuBar();

        JMenu optionsMenu = new JMenu("Options");
        addHorseMenuItem = new JMenuItem("Add Horse");
        addHorseMenuItem.addActionListener(_ -> addNewHorse());

        optionsMenu.add(addHorseMenuItem);
        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);
    }

    private void addNewHorse() {
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

        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField nameField = new JTextField();
        JTextField confidenceField = new JTextField();
        JComboBox<String> shapeCombo = new JComboBox<>(SHAPE_OPTIONS);
        JComboBox<Color> colorCombo = getColorJComboBox();

        panel.add(new JLabel("Horse Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Confidence (0.1-1.0):"));
        panel.add(confidenceField);
        panel.add(new JLabel("Shape:"));
        panel.add(shapeCombo);
        panel.add(new JLabel("Color:"));
        panel.add(colorCombo);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Add New Horse", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double confidence = Double.parseDouble(confidenceField.getText());
                String shape = (String)shapeCombo.getSelectedItem();
                Color color = (Color)colorCombo.getSelectedItem();

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

                racePanel.repaint();
                infoArea.append("Added new horse: " + name + " (" + shape + ")\n");

                if (nextAvailableSlot == MAX_HORSES - 1) {
                    addHorseMenuItem.setEnabled(false);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid confidence value. Please enter a number between 0.1 and 1.0",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame,
                        e.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComboBox<Color> getColorJComboBox() {
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

        SwingUtilities.invokeLater(() -> {
            infoArea.append(message + "\n");
            startButton.setEnabled(true);
            racePanel.repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RaceGUI());
    }
}