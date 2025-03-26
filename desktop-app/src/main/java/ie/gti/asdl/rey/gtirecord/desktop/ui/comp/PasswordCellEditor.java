package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PasswordCellEditor extends DefaultCellEditor {
    private final JPasswordField passwordField;
    private final JButton toggleButton;
    private boolean isPasswordVisible = false;
    private final Icon eyeOpenIcon;
    private final Icon eyeClosedIcon;

    public PasswordCellEditor() {
        super(new JTextField());
        passwordField = new JPasswordField();

        // Load and resize eye icons
        eyeOpenIcon = resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/eye_opened.png"))), 16, 16);
        eyeClosedIcon = resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/eye_closed.png"))), 16, 16);

        toggleButton = new JButton(eyeClosedIcon);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setPreferredSize(new Dimension(20, 20));
        toggleButton.setToolTipText("Show Password");

        toggleButton.addActionListener(e -> togglePasswordVisibility());

        // Panel with password field + eye button
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(toggleButton, BorderLayout.EAST);
        editorComponent = panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Load existing password from the cell
        // Store password from the cell
        if (value != null) {
            passwordField.setText(value.toString());
        } else {
            passwordField.setText("");
        }

        // Always start in hidden mode
        isPasswordVisible = false;
        passwordField.setEchoChar('•');
        toggleButton.setIcon(eyeClosedIcon);

        return editorComponent;
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setEchoChar(isPasswordVisible ? (char) 0 : '•');
        toggleButton.setIcon(isPasswordVisible ? eyeOpenIcon : eyeClosedIcon);
        toggleButton.setToolTipText(isPasswordVisible ? "Hide Password" : "Show Password");
    }

    @Override
    public Object getCellEditorValue() {
        return new String(passwordField.getPassword());
    }

    // Resize icon method
    private Icon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}

