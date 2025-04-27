import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;

class AddHorsePanel extends JPanel {
    private final RaceGUI raceGUI;
    private JTextField nameField;
    private JTextField confidenceField;
    private JComboBox<String> shapeCombo;
    private JComboBox<Color> colorCombo;
    private JButton addButton;
    private JButton toBettingButton;

    public AddHorsePanel(RaceGUI raceGUI) {
        this.raceGUI = raceGUI;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Horse Management"));
        createComponents();
    }

    private void createComponents() {
        // Top navigation panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toBettingButton = new JButton("Go to Betting");
        toBettingButton.setPreferredSize(new Dimension(120, 25));
        toBettingButton.addActionListener(e -> raceGUI.showBettingPanel());
        topPanel.add(toBettingButton);
        add(topPanel, BorderLayout.NORTH);

        // Main form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        nameField = new JTextField();
        confidenceField = new JTextField();
        shapeCombo = new JComboBox<>(RaceGUI.SHAPE_OPTIONS);
        colorCombo = raceGUI.getColorJComboBox();

        addButton = new JButton("Add Horse");
        addButton.addActionListener(this::addHorseAction);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Confidence (0.1-1.0):"));
        formPanel.add(confidenceField);
        formPanel.add(new JLabel("Shape:"));
        formPanel.add(shapeCombo);
        formPanel.add(new JLabel("Color:"));
        formPanel.add(colorCombo);
        formPanel.add(new JLabel());
        formPanel.add(addButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private void addHorseAction(ActionEvent e) {
        String name = nameField.getText().trim();
        String confidenceText = confidenceField.getText().trim();
        String shape = (String) shapeCombo.getSelectedItem();
        Color color = (Color) colorCombo.getSelectedItem();

        try {
            double confidence = Double.parseDouble(confidenceText);
            raceGUI.addNewHorse(name, confidence, shape, color);
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid confidence value", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        confidenceField.setText("");
        shapeCombo.setSelectedIndex(0);
        colorCombo.setSelectedIndex(0);
    }

    public void disableAdding() {
        addButton.setEnabled(false);
        nameField.setEnabled(false);
        confidenceField.setEnabled(false);
        shapeCombo.setEnabled(false);
        colorCombo.setEnabled(false);
    }
}