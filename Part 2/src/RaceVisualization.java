import javax.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RaceVisualization extends JFrame {
    private Race race;
    private Horse[] horses;
    private JPanel raceTrack;
    private Timer timer;
    private final int TRACK_LENGTH = 600;
    private final int HORSE_HEIGHT = 40;
    private final int MARGIN = 20;

    public RaceVisualization() {
        // Initialize race with 3 horses
        race = new Race(50); // Race length of 50 units
        horses = new Horse[3];
        horses[0] = new Horse('A', "Thunder", 0.7);
        horses[1] = new Horse('B', "Lightning", 0.6);
        horses[2] = new Horse('C', "Storm", 0.5);

        for (int i = 0; i < horses.length; i++) {
            race.addHorse(horses[i], i + 1);
        }

        // Set up the JFrame
        setTitle("Horse Race Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(TRACK_LENGTH + 100, 200);

        // Create race track panel
        raceTrack = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRaceTrack(g);
            }
        };
        raceTrack.setBackground(new Color(240, 240, 240));
        add(raceTrack);

        // Start button
        JButton startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRace());
        add(startButton, BorderLayout.SOUTH);

        // Timer to update the visualization
        timer = new Timer(100, e -> {
            if (!raceWon()) {
                moveHorses();
                raceTrack.repaint();
            } else {
                timer.stop();
                showWinner();
            }
        });
    }

    private void drawRaceTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw finish line
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(TRACK_LENGTH, MARGIN, TRACK_LENGTH, MARGIN + 3 * (HORSE_HEIGHT + MARGIN));

        // Draw each horse's progress
        for (int i = 0; i < horses.length; i++) {
            int yPos = MARGIN + i * (HORSE_HEIGHT + MARGIN);

            // Draw track lane
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, yPos, TRACK_LENGTH, HORSE_HEIGHT);

            // Draw horse position
            int horseX = (int) ((double) horses[i].getDistanceTravelled() / race.getRaceLength() * TRACK_LENGTH);
            horseX = Math.min(horseX, TRACK_LENGTH - 30);

            g2d.setColor(getHorseColor(i));
            g2d.fillRect(horseX, yPos, 30, HORSE_HEIGHT);

            // Draw horse name
            g2d.setColor(Color.BLACK);
            g2d.drawString(horses[i].getName(), horseX + 35, yPos + HORSE_HEIGHT / 2 + 5);

            // Mark fallen horses
            if (horses[i].hasFallen()) {
                g2d.setColor(Color.RED);
                g2d.drawString("✖ Fallen!", horseX + 35, yPos + HORSE_HEIGHT / 2 + 20);
            }
        }
    }

    private Color getHorseColor(int index) {
        return switch (index) {
            case 0 -> new Color(65, 105, 225); // Royal Blue
            case 1 -> new Color(220, 20, 60);   // Crimson
            case 2 -> new Color(34, 139, 34);  // Forest Green
            default -> Color.BLACK;
        };
    }

    private void startRace() {
        // Reset all horses
        for (Horse horse : horses) {
            horse.goBackToStart();
        }
        timer.start();
    }

    private void moveHorses() {
        for (Horse horse : horses) {
            if (!horse.hasFallen()) {
                if (Math.random() < horse.getConfidence()) {
                    horse.moveForward();
                }
                if (Math.random() < (0.1 * horse.getConfidence() * horse.getConfidence())) {
                    horse.fall();
                }
            }
        }
    }

    private boolean raceWon() {
        for (Horse horse : horses) {
            if (horse.getDistanceTravelled() >= race.getRaceLength()) {
                return true;
            }
        }
        return false;
    }

    private void showWinner() {
        for (Horse horse : horses) {
            if (horse.getDistanceTravelled() >= race.getRaceLength()) {
                JOptionPane.showMessageDialog(this,
                        horse.getName() + " wins the race!",
                        "Race Finished",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RaceVisualization visualization = new RaceVisualization();
            visualization.setVisible(true);
        });
    }
}


