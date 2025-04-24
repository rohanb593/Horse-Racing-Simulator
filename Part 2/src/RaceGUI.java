import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public RaceGUI() {
        initializeRace();
        createAndShowGUI();
    }

    private void initializeRace() {
        race = new Race2(RACE_LENGTH);

        horses = new Horse2[9];

        horses[0] = new Horse2('A', "Thunder", 0.9);
        horses[1] = new Horse2('B', "Lightning", 0.8);
        horses[2] = new Horse2('C', "Storm", 0.7);
        horses[3] = new Horse2('D', "Doom", 0.6);
        horses[4] = new Horse2('E', "Rain", 0.5);
        horses[5] = new Horse2('F', "Lion", 0.4);
        horses[6] = new Horse2('G', "Elephant", 0.3);
        horses[7] = new Horse2('H', "Hunter", 0.2);
        horses[8] = new Horse2('I', "Iron", 0.1);

        for (int i = 0; i < horses.length; i++) {
            race.addHorse(horses[i], i + 1);
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Horse Race");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int panelHeight = (horses.length * (LANE_HEIGHT + MARGIN)) + 100;

        racePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRace(g);
            }
        };
        racePanel.setPreferredSize(new Dimension(900, panelHeight));
        racePanel.setBackground(new Color(240, 240, 240));

        JPanel controlPanel = new JPanel();
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

        raceTimer = new Timer(100, e -> {
            if (!raceInProgress) return;

            boolean allHorsesFallen = true;
            boolean raceWon = false;

            for (Horse2 horse : horses) {
                if (race.raceWonBy(horse)) {
                    endRace(horse.getName() + " wins the race!");
                    raceWon = true;
                    break;
                }
            }

            if (raceWon) return;

            for (Horse2 horse : horses) {
                if (!horse.hasFallen()) {
                    allHorsesFallen = false;
                    race.moveHorse(horse);
                    if (horse.hasFallen()) {
                        infoArea.append(horse.getName() + " has fallen!\n");
                    }
                }
            }

            if (allHorsesFallen) {
                endRace("All horses have fallen! Race over.");
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void drawRace(Graphics g) {
        int scale = 15;
        int startY = 30;

        g.setColor(Color.RED);
        int finishX = 50 + RACE_LENGTH * scale;
        g.fillRect(finishX, 0, FINISH_LINE_WIDTH, horses.length * (LANE_HEIGHT + MARGIN) + 50);

        for (int i = 0; i < horses.length; i++) {
            int y = startY + i * (LANE_HEIGHT + MARGIN);

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(50, y, RACE_LENGTH * scale, LANE_HEIGHT);

            g.setColor(Color.BLACK);
            g.drawRect(50, y, RACE_LENGTH * scale, LANE_HEIGHT);

            drawHorse(g, horses[i], y, scale);
        }
    }

    private void drawHorse(Graphics g, Horse2 horse, int y, int scale) {
        int x = 50 + horse.getDistanceTravelled() * scale;

        g.setColor(getHorseColor(horse));
        g.fillRect(x, y, 40, LANE_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        FontMetrics fm = g.getFontMetrics();
        String symbol = String.valueOf(horse.getSymbol());
        int symbolWidth = fm.stringWidth(symbol);
        int symbolX = x + (40 - symbolWidth) / 2;
        int symbolY = y + (LANE_HEIGHT + fm.getAscent()) / 2;
        g.drawString(symbol, symbolX, symbolY);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String status = horse.hasFallen() ? "✖ FALLEN" : String.format("Conf: %.1f", horse.getConfidence());
        g.drawString(horse.getName() + " - " + status, x, y + LANE_HEIGHT + 15);
    }

    private Color getHorseColor(Horse2 horse) {
        switch (horses.length - java.util.Arrays.asList(horses).indexOf(horse) - 1) {
            case 0: return new Color(255, 50, 50);
            case 1: return new Color(50, 150, 255);
            case 2: return new Color(50, 200, 50);
            case 3: return new Color(255, 200, 50);
            case 4: return new Color(200, 50, 200);
            case 5: return new Color(255, 150, 50);
            case 6: return new Color(50, 200, 200);
            default: return new Color(150, 150, 150);
        }
    }

    private void startRace() {
        raceInProgress = true;
        startButton.setEnabled(false);
        infoArea.setText("Starting new race...\n");

        for (Horse2 horse : horses) {
            horse.goBackToStart();
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