/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import ie.gti.asdl.rey.gtirecord.core.service.ServiceManager;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URL;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.PERSON;
import static ie.gti.asdl.rey.gtirecord.desktop.util.ImageUtils.resizeIcon;

/**
 *
 * @author Andrei
 */
public class PersonFrame extends AbstractFrame {

//    static class PhoneNumberValidator implements FocusListener {
//        private static final Pattern PHONE_PATTERN = Pattern.compile(
//                "^\\+?(\\d{1,3})?\\s?(\\(?\\d{3}\\)?)?\\s?\\d{3}[- ]?\\d{2}[- ]?\\d{2}$"
//        );
//
//        private final JTextField textField;
//        private final JLabel errorLabel;
//
//        public PhoneNumberValidator(JTextField textField, JLabel errorLabel) {
//            this.textField = textField;
//            this.errorLabel = errorLabel;
//        }
//
//        @Override
//        public void focusGained(FocusEvent e) {
//            // No validation when gaining focus
//        }
//
//        @Override
//        public void focusLost(FocusEvent e) {
//            String input = textField.getText().trim();
//            if (!input.isEmpty() && !PHONE_PATTERN.matcher(input).matches()) {
//                // Set the error icon when the phone number is invalid
//                errorLabel.setIcon(resizeIcon(new ImageIcon(getClass().getResource("/img/error.png")), 16, 16)); // Path to your error icon image
//            } else {
//                // Clear the error icon when the phone number is valid
//                errorLabel.setIcon(null);
//            }
////                textField.requestFocus(); // Return focus to fix the error
//        }
//    }

static class PhoneNumberValidator implements DocumentListener {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?(\\d{1,3})?\\s?(\\(?\\d{3}\\)?)?\\s?\\d{3}[- ]?\\d{2}[- ]?\\d{2}$"
    );

    private final JTextField textField;
    private final JLabel errorLabel;

    public PhoneNumberValidator(JTextField textField, JLabel errorLabel) {
        this.textField = textField;
        this.errorLabel = errorLabel;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        validateInput();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        validateInput();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Not needed for plain text components
    }

    private void validateInput() {
        String input = textField.getText().trim();
        if (!input.isEmpty() && !PHONE_PATTERN.matcher(input).matches()) {
            // Set the error icon when the phone number is invalid
            errorLabel.setIcon(new ImageIcon(getClass().getResource("/img/error.png"))); // Path to your error icon image
        } else {
            // Clear the error icon when the phone number is valid
            errorLabel.setIcon(null);
        }
    }
}


    private DatePicker datePicker;

    private FrameManager frameManager;

    /**
     * Creates new form PersonFrame
     */
    public PersonFrame(FrameManager frameManager, ServiceManager serviceManager) {
        this.frameManager = frameManager;

        initDatePicker();
        initComponents();
//        SwingUtilities.invokeLater(this::init2);
        init();
    }

    private void init() {
        tfDOB.setVisible(false);

        javax.swing.GroupLayout pnlDOBLayout = new javax.swing.GroupLayout(pnlDOB);
        pnlDOB.setLayout(pnlDOBLayout);
        pnlDOBLayout.setHorizontalGroup(
                pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDOBLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(32, Short.MAX_VALUE))
        );
        pnlDOBLayout.setVerticalGroup(
                pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

//        ((AbstractDocument) tfFirstName.getDocument()).setDocumentFilter(new AutoFormatFilter(tfFirstName));

//        tfFirstName.addFocusListener(new PhoneNumberValidator(tfFirstName, lblError));
        tfFirstName.getDocument().addDocumentListener(new PhoneNumberValidator(tfFirstName, lblError));

        ((AbstractDocument) tfFirstName.getDocument()).setDocumentFilter(new DocumentFilter() {
//            private String regex = "^\\+?\\(?\\d{1,3}\\)?[-\\s]?\\d{2,3}[-\\s]?\\d{2}[-\\s]?\\d{2,3}$";

            private boolean isValidInput(String text) {
                return text.matches("[0-9+()\\-\\s]*"); // Allow only valid characters
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (isValidInput(string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                    throws BadLocationException {
                if (isValidInput(string)) {
                    super.replace(fb, offset, length, string, attr);
                }
            }
        });

//        try {
//            // Define two different masks
//            MaskFormatter mask6Digits = new MaskFormatter("(###) ###-##");
//            MaskFormatter mask7Digits = new MaskFormatter("(###) ###-###");
//
//            // Create a formatter factory that can switch between formats
//            DefaultFormatterFactory factory = new DefaultFormatterFactory(mask6Digits, mask7Digits);
//            ftfPhoneNumber.setFormatterFactory(factory);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        ftfPhoneNumber.setColumns(15);
    }

//    private static GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth) {
//        return getConstraints(gridx, gridy, gridwidth, GridBagConstraints.WEST);
//    }
//
//    private static GridBagConstraints getConstraints(
//            int gridx, int gridy, int gridwidth, int anchor) {
//        GridBagConstraints gc = new GridBagConstraints();
//        gc.fill = GridBagConstraints.NONE;
//        gc.anchor = anchor;
//        gc.gridx = gridx;
//        gc.gridy = gridy;
//        gc.gridwidth = gridwidth;
//        return gc;
//    }

    private void initDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        datePicker = new DatePicker(dateSettings);

        URL dateImageURL = PersonFrame.class.getResource("/img/datepickerbutton.png");
        Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
        ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
        dateExampleIcon = resizeIcon(dateExampleIcon, 16, 16);

        final LocalDate today = LocalDate.now();
        dateSettings.setDateRangeLimits(today.minusYears(150), today.minusYears(10));
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePicker.setDate(LocalDate.now().minusYears(18));
        JButton datePickerButton = datePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(dateExampleIcon);
    }

    @Override
    protected int getDefaultCloseOperationValue() {
        return JFrame.HIDE_ON_CLOSE;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pnlFirstName = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfFirstName = new javax.swing.JTextField();
        lblError = new javax.swing.JLabel();
        pnlLastName = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfLastName = new javax.swing.JTextField();
        pnlDOB = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tfDOB = new javax.swing.JTextField();
        pnlPhoneNumber = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        ftfPhoneNumber = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PERSON");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnlFirstName.setMaximumSize(new java.awt.Dimension(339, 32767));

        jLabel2.setText("First name");

        tfFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfFirstNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFirstNameLayout = new javax.swing.GroupLayout(pnlFirstName);
        pnlFirstName.setLayout(pnlFirstNameLayout);
        pnlFirstNameLayout.setHorizontalGroup(
            pnlFirstNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFirstNameLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblError, 0, 40, Short.MAX_VALUE)
                .addGap(0, 43, Short.MAX_VALUE))
        );
        pnlFirstNameLayout.setVerticalGroup(
            pnlFirstNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFirstNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(tfFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblError, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setText("Last name");

        tfLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfLastNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLastNameLayout = new javax.swing.GroupLayout(pnlLastName);
        pnlLastName.setLayout(pnlLastNameLayout);
        pnlLastNameLayout.setHorizontalGroup(
            pnlLastNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLastNameLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        pnlLastNameLayout.setVerticalGroup(
            pnlLastNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLastNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(tfLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel7.setText("Date of birth");

        tfDOB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDOBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDOBLayout = new javax.swing.GroupLayout(pnlDOB);
        pnlDOB.setLayout(pnlDOBLayout);
        pnlDOBLayout.setHorizontalGroup(
            pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDOBLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        pnlDOBLayout.setVerticalGroup(
            pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel7)
                .addComponent(tfDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel8.setText("Phone number");

        ftfPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftfPhoneNumberActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPhoneNumberLayout = new javax.swing.GroupLayout(pnlPhoneNumber);
        pnlPhoneNumber.setLayout(pnlPhoneNumberLayout);
        pnlPhoneNumberLayout.setHorizontalGroup(
            pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhoneNumberLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ftfPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        pnlPhoneNumberLayout.setVerticalGroup(
            pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(ftfPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 146, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(pnlFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(207, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 942, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addGap(90, 90, 90)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfFirstNameActionPerformed

    private void tfLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfLastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfLastNameActionPerformed

    private void ftfPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftfPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ftfPhoneNumberActionPerformed

    private void tfDOBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDOBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDOBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, true, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.showFrame(PERSON);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField ftfPhoneNumber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblError;
    private javax.swing.JPanel pnlDOB;
    private javax.swing.JPanel pnlFirstName;
    private javax.swing.JPanel pnlLastName;
    private javax.swing.JPanel pnlPhoneNumber;
    private javax.swing.JTextField tfDOB;
    private javax.swing.JTextField tfFirstName;
    private javax.swing.JTextField tfLastName;
    // End of variables declaration//GEN-END:variables


}
