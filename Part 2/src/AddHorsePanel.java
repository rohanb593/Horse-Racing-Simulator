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

    public AddHorsePanel(RaceGUI raceGUI) {
        this.raceGUI = raceGUI;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Horse Management"));
        createComponents();
    }

    private void createComponents() {
        // Main form panel with tighter spacing
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 1, 5, 5)); // Reduced vertical gap from 5 to 3

        // Name field panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        namePanel.add(new JLabel("Name:"));
        nameField = new JTextField(15); // Reduced width from default
        namePanel.add(nameField);
        formPanel.add(namePanel);

        // Confidence field panel
        JPanel confidencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        confidencePanel.add(new JLabel("Confidence (0.1-1.0):"));
        confidenceField = new JTextField(5); // Very narrow field for numbers
        confidencePanel.add(confidenceField);
        formPanel.add(confidencePanel);

        // Shape combo panel
        JPanel shapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        shapePanel.add(new JLabel("Shape:"));
        shapeCombo = new JComboBox<>(RaceGUI.SHAPE_OPTIONS);
        shapeCombo.setPreferredSize(new Dimension(120, 25)); // Compact combo box
        shapePanel.add(shapeCombo);
        formPanel.add(shapePanel);

        // Color combo panel
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorPanel.add(new JLabel("Color:"));
        colorCombo = raceGUI.getColorJComboBox();
        colorCombo.setPreferredSize(new Dimension(120, 25)); // Compact combo box
        colorPanel.add(colorCombo);
        formPanel.add(colorPanel);

        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        addButton = new JButton("Add Horse");
        addButton.setPreferredSize(new Dimension(120, 25)); // Standard button size
        addButton.addActionListener(this::addHorseAction);
        buttonPanel.add(addButton);
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.NORTH);
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