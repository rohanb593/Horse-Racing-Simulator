import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RacePanel extends JPanel {
    private Race2 race;
    private Horse2[] horses;
    public final int RACE_LENGTH;
    private final int LANE_HEIGHT = 60;
    private final int MARGIN = 20;
    private final int MAX_HORSES = 10;
    private String currentWeather = "Sunny";

    private static final Color SUNNY_COLOR = new Color(135, 206, 235);
    private static final Color RAINY_COLOR = new Color(169, 169, 169);
    private static final Color MUDDY_COLOR = new Color(139, 69, 19);
    private static final Color ICY_COLOR = new Color(173, 216, 230);

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

    public RacePanel(Race2 race, Horse2[] horses) {
        this.race = race;
        this.horses = horses;
        this.RACE_LENGTH = race != null ? race.getRaceLength() : 50;
        int panelHeight = (MAX_HORSES * (LANE_HEIGHT + MARGIN)) + 100;
        setPreferredSize(new Dimension(900, panelHeight));
        updateBackgroundColor();
    }

    public void setCurrentWeather(String weather) {
        this.currentWeather = weather;
        updateBackgroundColor();
    }

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void setRace(Race2 race) {
        this.race = race;
    }

    public void setHorses(Horse2[] horses) {
        this.horses = horses;
    }

    private void updateBackgroundColor() {
        switch (currentWeather) {
            case "Sunny":
                setBackground(SUNNY_COLOR);
                break;
            case "Rainy":
                setBackground(RAINY_COLOR);
                break;
            case "Muddy":
                setBackground(MUDDY_COLOR);
                break;
            case "Icy":
                setBackground(ICY_COLOR);
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRace(g);
    }

    private void drawRace(Graphics g) {
        int scale = 15;
        int startY = 30;

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Current Weather: " + currentWeather, 50, 20);

        g.setColor(Color.RED);
        int finishX = 50 + RACE_LENGTH * scale;
        int FINISH_LINE_WIDTH = 10;
        g.fillRect(finishX, 0, FINISH_LINE_WIDTH, MAX_HORSES * (LANE_HEIGHT + MARGIN) + 50);

        for (int i = 0; i < horses.length; i++) {
            if (horses[i] == null) continue;

            int y = startY + i * (LANE_HEIGHT + MARGIN);

            Color laneColor = new Color(240, 240, 240, 200);
            g.setColor(laneColor);
            g.fillRect(50, y, RACE_LENGTH * scale, LANE_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(50, y, RACE_LENGTH * scale, LANE_HEIGHT);

            drawHorse(g, horses[i], y, scale);
        }
    }

    private void drawHorse(Graphics g, Horse2 horse, int y, int scale) {
        int x = 50 + horse.getDistanceTravelled() * scale;
        int width = 40;
        int height = LANE_HEIGHT;

        SHAPE_DRAWERS.get(horse.getShape()).draw(g, x, y, width, height, horse.getColor());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        String symbol = String.valueOf(horse.getSymbol());
        int symbolWidth = fm.stringWidth(symbol);
        int symbolX = x + (width - symbolWidth) / 2;
        int symbolY = y + (height + fm.getAscent()) / 2;
        g.drawString(symbol, symbolX, symbolY);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String status = horse.hasFallen() ? "✖ FALLEN" : String.format("Conf: %.1f", horse.getConfidence());
        g.drawString(horse.getName() + " - " + status, x, y + height + 15);
    }
}