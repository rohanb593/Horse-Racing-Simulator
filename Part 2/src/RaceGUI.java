import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class RaceGUI {
    private Race2 race;
    private Horse2[] horses;
    private JFrame frame;
    private JPanel racePanel;
    private JButton startButton;
    private JTextArea infoArea;
    private Timer animationTimer;
    private Timer raceTimer;
    private boolean raceInProgress;
    private final int RACE_LENGTH = 50;
    private final int LANE_HEIGHT = 60;
    private final int MARGIN = 20;
    private final int FINISH_LINE_WIDTH = 10;
    private final int MAX_HORSES = 10;
    private JMenuBar menuBar;
    private JMenuItem addHorseMenuItem;
    private String currentWeather = "Sunny";
    private JComboBox<String> weatherCombo;

    private static final String[] SHAPE_OPTIONS = {"Rectangle", "Circle", "Triangle", "Diamond", "Star"};
    private static final String[] WEATHER_OPTIONS = {"Sunny", "Rainy", "Muddy", "Icy"};
    private static final Color[] COLOR_OPTIONS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    // Weather background colors
    private static final Color SUNNY_COLOR = new Color(135, 206, 235); // Sky blue
    private static final Color RAINY_COLOR = new Color(169, 169, 169); // Dark gray
    private static final Color MUDDY_COLOR = new Color(139, 69, 19);   // Brown
    private static final Color ICY_COLOR = new Color(173, 216, 230);   // Light blue

    private interface ShapeDrawer {
        void draw(Graphics g, int x, int y, int width, int height, Color color);
    }

    private static final Map<String, ShapeDrawer> SHAPE_DRAWERS = new HashMap<>();
    static {
        SHAPE_DRAWERS.put("Rectangle", (g, x, y, w, h, c) -> {
            g.setColor(c);
            g.fillRect(x, y, w, h);
        });

        SHAPE_DRAWERS.put("Circle", (g, x, y, w, h, c) -> {
            g.setColor(c);
            g.fillOval(x, y, w, h);
        });

        SHAPE_DRAWERS.put("Triangle", (g, x, y, w, h, c) -> {
            g.setColor(c);
            int[] xPoints = {x + w/2, x, x + w};
            int[] yPoints = {y, y + h, y + h};
            g.fillPolygon(xPoints, yPoints, 3);
        });

        SHAPE_DRAWERS.put("Diamond", (g, x, y, w, h, c) -> {
            g.setColor(c);
            int[] xPoints = {x + w/2, x, x + w/2, x + w};
            int[] yPoints = {y, y + h/2, y + h, y + h/2};
            g.fillPolygon(xPoints, yPoints, 4);
        });

        SHAPE_DRAWERS.put("Star", (g, x, y, w, h, c) -> {
            g.setColor(c);
            int centerX = x + w/2;
            int centerY = y + h/2;
            int radius = Math.min(w, h)/2;

            int[] xPoints = new int[10];
            int[] yPoints = new int[10];

            for (int i = 0; i < 10; i++) {
                double angle = Math.PI * 2 * i / 10;
                int r = (i % 2 == 0) ? radius : radius/2;
                xPoints[i] = centerX + (int)(r * Math.cos(angle - Math.PI/2));
                yPoints[i] = centerY + (int)(r * Math.sin(angle - Math.PI/2));
            }
            g.fillPolygon(xPoints, yPoints, 10);
        });
    }

    public RaceGUI() {
        initializeRaceWithDefaults();
        createAndShowGUI();
    }

    private void initializeRaceWithDefaults() {
        race = new Race2(RACE_LENGTH);
        horses = new Horse2[MAX_HORSES];

        // Default horses (5) with default shapes and colors
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
    }

    private void createAndShowGUI() {
        frame = new JFrame("Horse Race");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        createMenuBar();

        int panelHeight = (MAX_HORSES * (LANE_HEIGHT + MARGIN)) + 100;

        racePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRace(g);
            }
        };
        updateBackgroundColor();
        racePanel.setPreferredSize(new Dimension(900, panelHeight));

        JPanel controlPanel = new JPanel(new FlowLayout());

        // Weather control
        JPanel weatherPanel = new JPanel();
        weatherPanel.add(new JLabel("Weather:"));
        weatherCombo = new JComboBox<>(WEATHER_OPTIONS);
        weatherCombo.addActionListener(e -> {
            currentWeather = (String) weatherCombo.getSelectedItem();
            applyWeatherEffects();
            updateBackgroundColor();
            infoArea.append("Weather changed to: " + currentWeather + "\n");
        });
        weatherPanel.add(weatherCombo);
        controlPanel.add(weatherPanel);

        startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRace());
        controlPanel.add(startButton);

        infoArea = new JTextArea(5, 80);
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);

        frame.add(racePanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.SOUTH);

        animationTimer = new Timer(50, e -> racePanel.repaint());

        setupRaceTimer();

        frame.pack();
        frame.setVisible(true);
    }

    private void updateBackgroundColor() {
        switch (currentWeather) {
            case "Sunny":
                racePanel.setBackground(SUNNY_COLOR);
                break;
            case "Rainy":
                racePanel.setBackground(RAINY_COLOR);
                break;
            case "Muddy":
                racePanel.setBackground(MUDDY_COLOR);
                break;
            case "Icy":
                racePanel.setBackground(ICY_COLOR);
                break;
        }
    }

    private void setupRaceTimer() {
        raceTimer = new Timer(100, e -> {
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
            if (currentWeather.equals("Muddy")) {
                fallChance *= 1.5;
            } else if (currentWeather.equals("Icy")) {
                fallChance *= 2.0;
            }

            if (Math.random() < fallChance) {
                horse.fall();
            }
        }
    }

    private void applyWeatherEffects() {
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
        addHorseMenuItem.addActionListener(e -> addNewHorse());

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
                newHorse.applyWeatherEffect(currentWeather);
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

    private void drawRace(Graphics g) {
        int scale = 15;
        int startY = 30;

        // Draw weather indicator
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Current Weather: " + currentWeather, 50, 20);

        // Draw finish line
        g.setColor(Color.RED);
        int finishX = 50 + RACE_LENGTH * scale;
        g.fillRect(finishX, 0, FINISH_LINE_WIDTH, MAX_HORSES * (LANE_HEIGHT + MARGIN) + 50);

        // Draw lanes and horses
        for (int i = 0; i < horses.length; i++) {
            if (horses[i] == null) continue;

            int y = startY + i * (LANE_HEIGHT + MARGIN);

            // Draw lane (semi-transparent to show weather background)
            Color laneColor = new Color(240, 240, 240, 200);
            g.setColor(laneColor);
            g.fillRect(50, y, RACE_LENGTH * scale, LANE_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(50, y, RACE_LENGTH * scale, LANE_HEIGHT);

            // Draw horse
            drawHorse(g, horses[i], y, scale);
        }
    }

    private void drawHorse(Graphics g, Horse2 horse, int y, int scale) {
        int x = 50 + horse.getDistanceTravelled() * scale;
        int width = 40;
        int height = LANE_HEIGHT;

        // Draw the selected shape
        SHAPE_DRAWERS.get(horse.getShape()).draw(g, x, y, width, height, horse.getColor());

        // Draw the symbol on top
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        String symbol = String.valueOf(horse.getSymbol());
        int symbolWidth = fm.stringWidth(symbol);
        int symbolX = x + (width - symbolWidth) / 2;
        int symbolY = y + (height + fm.getAscent()) / 2;
        g.drawString(symbol, symbolX, symbolY);

        // Draw the info text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String status = horse.hasFallen() ? "✖ FALLEN" : String.format("Conf: %.1f", horse.getConfidence());
        g.drawString(horse.getName() + " - " + status, x, y + height + 15);
    }

    private void startRace() {
        raceInProgress = true;
        startButton.setEnabled(false);
        infoArea.setText("Starting new race... Weather: " + currentWeather + "\n");

        for (Horse2 horse : horses) {
            if (horse != null) {
                horse.goBackToStart();
                horse.applyWeatherEffect(currentWeather);
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