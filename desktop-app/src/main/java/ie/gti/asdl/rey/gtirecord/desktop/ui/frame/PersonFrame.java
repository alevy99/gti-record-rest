/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.AddressService;
import ie.gti.asdl.rey.gtirecord.core.service.PersonService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.core.validation.*;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.model.util.AddressUtils;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.PERSON;
import static ie.gti.asdl.rey.gtirecord.desktop.util.ImageUtils.resizeIcon;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.addTextFieldValidation;

/**
 * @author Andrei
 */
public class PersonFrame extends AbstractFrame {

    @Setter
    private User user;

    @Setter
    private Person person;

    private DatePicker dobDatePicker;

    private final PersonService personService;

    private final UserService userService;

    private final AddressService addressService;

    private final Map<JTextField, Boolean> validationStatusMap = new HashMap<>();

    /**
     * Creates new form PersonFrame
     */
    public PersonFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);

        personService = serviceManager.getPersonService();
        userService = serviceManager.getUserService();
        addressService = serviceManager.getAddressService();
        initDOBDatePicker();
        initComponents();
        initUI();
    }

    @Override
    protected void initFrame() {
        super.initFrame();
        reInitForm();
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        reInitForm();
    }

    private void reInitForm() {
        resetValidation();
        loadData();
        updateUI();
        lblLoggedInUsername.setText(getFrameManager().getActiveUser().getUsername());

    }

    private void resetValidation() {
        updateValidationStatus(tfFirstName, false);
        updateValidationStatus(tfLastName, false);
    }

    private void loadData() {
        if (person == null) {
            if ((user != null) && (user.getPersonId() != null)) {
                person = personService.getById(user.getPersonId()).orElseGet(() -> InstanceFactory.create(Person.class));
            } else {
                person = InstanceFactory.create(Person.class);
            }
        }
        if (person.getId() != null) {
            // Try to load address if not present
            if (person.getAddress().getPersonId() == null) {
                addressService.getByPersonId(person.getId()).ifPresent(address -> {
                    person.setAddress(address);
                });
            }
            user = user != null ? user : userService.getByPersonId(person.getId())
                    .orElseGet(() -> {
                        User user = InstanceFactory.create(User.class);
                        user.setUsername("");
                        return user;
                    });
        }
    }

    private void resetUI() {
        lblUsername.setText("");
        tfFirstName.setText("");
        lblFNValidStatus.setText("");
        tfLastName.setText("");
        cbGender.setSelectedItem(null);
        dobDatePicker.setDate(null);
        tfEmail.setText("");
        tfPhoneNumber.setText("");
        tfPPSN.setText("");
        tfAddressLine1.setText("");
        tfAddressLine2.setText("");
        tfAddressCounty.setText("");
        tfAddressCity.setText("");
        tfAddressCountry.setText("");
        tfAddressEircode.setText("");

        lblFNValidStatus.setText("");
        lblLNValidStatus.setText("");
    }

    private void updateValidationStatus(JTextField field, Boolean isValid) {
        validationStatusMap.put(field, isValid);
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        boolean allValid = validationStatusMap.values().stream().allMatch(Boolean::booleanValue);
        boolean enabled = isEditable() && allValid;
        btnSave.setEnabled(enabled);
    }

    private boolean isEditable() {
        return getFrameManager().isLoggedInAsAdmin() || getFrameManager().getActiveUser().equals(user);
    }

    private void updateUI() {
        // Reset UI first
        resetUI();

        lblUsername.setText(user.getUsername());
        tfFirstName.setText(person.getFirstName());
        addTextFieldValidation(tfFirstName, lblFNValidStatus, new NameValidator(), this::updateValidationStatus);

        tfLastName.setText(person.getLastName());
        addTextFieldValidation(tfLastName, lblLNValidStatus, new NameValidator(), this::updateValidationStatus);

        cbGender.setSelectedItem(person.getGender());
        dobDatePicker.setDate(person.getDateOfBirth());

        tfEmail.setText(person.getEmail());
        addTextFieldValidation(tfEmail, lblEmailValidStatus, new OptionalValidator<>(new EmailValidator()), this::updateValidationStatus);

        tfPhoneNumber.setText(person.getPhoneNum());
        addTextFieldValidation(tfPhoneNumber, lblPhoneValidStatus, new OptionalValidator<>(new PhoneNumberValidator()), this::updateValidationStatus);

        tfPPSN.setText(person.getPpsn());
        addTextFieldValidation(tfPPSN, lblPpsnValidStatus, new OptionalValidator<>(new PpsnValidator()), this::updateValidationStatus);

        Address address = person.getAddress();
        if (address != null) {
            tfAddressLine1.setText(address.getLine1());
            addTextFieldValidation(tfAddressLine1, lblAddrLine1ValidStatus, new OptionalValidator<>(new LengthValidator(0, 100)), this::updateValidationStatus);
            tfAddressLine2.setText(address.getLine2());
            addTextFieldValidation(tfAddressLine2, lblAddrLine2ValidStatus, new OptionalValidator<>(new LengthValidator(0, 100)), this::updateValidationStatus);
            tfAddressCity.setText(address.getCity());
            addTextFieldValidation(tfAddressCity, lblCityValidStatus, new OptionalValidator<>(new LengthValidator(0, 45)), this::updateValidationStatus);
            tfAddressCounty.setText(address.getCounty());
            addTextFieldValidation(tfAddressCounty, lblCountyValidStatus, new OptionalValidator<>(new LengthValidator(0, 100)), this::updateValidationStatus);
            tfAddressCountry.setText(address.getCountry());
            addTextFieldValidation(tfAddressCountry, lblCountryValidStatus, new OptionalValidator<>(new LengthValidator(0, 100)), this::updateValidationStatus);
            tfAddressEircode.setText(address.getEirCode());
            addTextFieldValidation(tfAddressEircode, lblEircodeValidStatus, new OptionalValidator<>(new EircodeValidator()), this::updateValidationStatus);
        }

        tfFirstName.setEditable(getFrameManager().isLoggedInAsAdmin());
        tfLastName.setEditable(getFrameManager().isLoggedInAsAdmin());

        boolean editable = isEditable();
        cbGender.setEditable(editable);
        dobDatePicker.setEnabled(editable);
        tfEmail.setEditable(editable);
        tfPhoneNumber.setEditable(editable);
        tfPPSN.setEditable(editable);
        tfAddressLine1.setEditable(editable);
        tfAddressLine2.setEditable(editable);
        tfAddressCity.setEditable(editable);
        tfAddressCounty.setEditable(editable);
        tfAddressCountry.setEditable(editable);
        tfAddressEircode.setEditable(editable);

        updateSaveButtonState();
    }

    private void fillPersonFromUI() {
        person.setFirstName(tfFirstName.getText());
        person.setLastName(tfLastName.getText());
        person.setGender(cbGender.getSelectedItem() == null ? "" : cbGender.getSelectedItem().toString().trim());
        person.setDateOfBirth(dobDatePicker.getDate());
        person.setEmail(tfEmail.getText());
        person.setPhoneNum(tfPhoneNumber.getText());
        person.setPpsn(tfPPSN.getText());
        Address address = person.getAddress();
        if (address == null) {
            address = InstanceFactory.create(Address.class);
        }
        address.setLine1(tfAddressLine1.getText());
        address.setLine2(tfAddressLine2.getText());
        address.setCounty(tfAddressCounty.getText());
        address.setCity(tfAddressCity.getText());
        address.setCountry(tfAddressCountry.getText());
        address.setEirCode(tfAddressEircode.getText());

        // Do not add an Address object in case the address is empty
        // In this way we won't add address into DB
        if (!AddressUtils.isAddressEmpty(address)) {
            person.setAddress(address);
        }
    }

    @Override
    protected void resetFrame() {
        super.resetFrame();
        user = null;
        person = null;
    }

    private void initUI() {
        tfDOB.setVisible(false);

        javax.swing.GroupLayout pnlDOBLayout = new javax.swing.GroupLayout(pnlDOB);
        pnlDOB.setLayout(pnlDOBLayout);
        pnlDOBLayout.setHorizontalGroup(
                pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDOBLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(32, Short.MAX_VALUE))
        );
        pnlDOBLayout.setVerticalGroup(
                pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

//        ((AbstractDocument) tfFirstName.getDocument()).setDocumentFilter(new AutoFormatFilter(tfFirstName));

//        tfFirstName.addFocusListener(new PhoneNumberValidator(tfFirstName, lblError));
//        tfPhoneNumber.getDocument().addDocumentListener(new PhoneNumberValidator(tfPhoneNumber, lblPhoneValidStatus));
//
//        ((AbstractDocument) tfPhoneNumber.getDocument()).setDocumentFilter(new DocumentFilter() {
//
//            private boolean isValidInput(String text) {
//                return text != null && text.matches("[0-9+()\\-\\s]*"); // Allow only valid characters
//            }
//
//            @Override
//            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
//                    throws BadLocationException {
//                if (isValidInput(string)) {
//                    super.insertString(fb, offset, string, attr);
//                }
//            }
//
//            @Override
//            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr)
//                    throws BadLocationException {
//                if (isValidInput(string)) {
//                    super.replace(fb, offset, length, string, attr);
//                }
//            }
//        });

    }

    private void initDOBDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dobDatePicker = new DatePicker(dateSettings);

        URL dateImageURL = PersonFrame.class.getResource("/img/datepickerbutton.png");
        Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
        ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
        dateExampleIcon = resizeIcon(dateExampleIcon, 16, 16);

        final LocalDate today = LocalDate.now();
        dateSettings.setDateRangeLimits(today.minusYears(150), today.minusYears(10));
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
//        dobDatePicker.setDate(LocalDate.now().minusYears(18));
        JButton datePickerButton = dobDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(dateExampleIcon);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pnlMain = new javax.swing.JPanel();
        pnlUsername = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        pnlFirstName = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfFirstName = new javax.swing.JTextField();
        lblFNValidStatus = new javax.swing.JLabel();
        pnlLastName = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfLastName = new javax.swing.JTextField();
        lblLNValidStatus = new javax.swing.JLabel();
        pnlGender = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        cbGender = new javax.swing.JComboBox<>();
        pnlDOB = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tfDOB = new javax.swing.JTextField();
        pnlPhoneNumber = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tfPhoneNumber = new javax.swing.JTextField();
        lblPhoneValidStatus = new javax.swing.JLabel();
        pnlEmail = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        tfEmail = new javax.swing.JTextField();
        lblEmailValidStatus = new javax.swing.JLabel();
        pnlPpsn = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        tfPPSN = new javax.swing.JTextField();
        lblPpsnValidStatus = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        pnlAddressLine1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tfAddressLine1 = new javax.swing.JTextField();
        lblAddrLine1ValidStatus = new javax.swing.JLabel();
        pnlAddressLine2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        tfAddressLine2 = new javax.swing.JTextField();
        lblAddrLine2ValidStatus = new javax.swing.JLabel();
        pnlAddressCity = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        tfAddressCity = new javax.swing.JTextField();
        lblCityValidStatus = new javax.swing.JLabel();
        pnlAddressCounty = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        tfAddressCounty = new javax.swing.JTextField();
        lblCountyValidStatus = new javax.swing.JLabel();
        pnlAddressCountry = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        tfAddressCountry = new javax.swing.JTextField();
        lblCountryValidStatus = new javax.swing.JLabel();
        pnlAddressEircode = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        tfAddressEircode = new javax.swing.JTextField();
        lblEircodeValidStatus = new javax.swing.JLabel();
        jCloseBtn = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        pnlLoggedInAs4 = new javax.swing.JPanel();
        lblLoggedInUsername = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PERSON");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnlUsername.setMaximumSize(new java.awt.Dimension(339, 339));

        jLabel4.setText("Username");

        javax.swing.GroupLayout pnlUsernameLayout = new javax.swing.GroupLayout(pnlUsername);
        pnlUsername.setLayout(pnlUsernameLayout);
        pnlUsernameLayout.setHorizontalGroup(
            pnlUsernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsernameLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUsernameLayout.setVerticalGroup(
            pnlUsernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
                .addComponent(lblFNValidStatus)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        pnlFirstNameLayout.setVerticalGroup(
            pnlFirstNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFirstNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(tfFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblFNValidStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLNValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlLastNameLayout.setVerticalGroup(
            pnlLastNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLastNameLayout.createSequentialGroup()
                .addGroup(pnlLastNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblLNValidStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlLastNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(tfLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setText("Gender");

        cbGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        javax.swing.GroupLayout pnlGenderLayout = new javax.swing.GroupLayout(pnlGender);
        pnlGender.setLayout(pnlGenderLayout);
        pnlGenderLayout.setHorizontalGroup(
            pnlGenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGenderLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGenderLayout.setVerticalGroup(
            pnlGenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel16)
                .addComponent(cbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDOBLayout.setVerticalGroup(
            pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDOBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel7)
                .addComponent(tfDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel8.setText("Phone number");

        tfPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPhoneNumberActionPerformed(evt);
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
                .addComponent(tfPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPhoneValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPhoneNumberLayout.setVerticalGroup(
            pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(tfPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblPhoneValidStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel9.setText("Email");

        tfEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEmailLayout = new javax.swing.GroupLayout(pnlEmail);
        pnlEmail.setLayout(pnlEmailLayout);
        pnlEmailLayout.setHorizontalGroup(
            pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmailLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEmailValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEmailLayout.setVerticalGroup(
            pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblEmailValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel10.setText("PPSN");

        tfPPSN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPPSNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPpsnLayout = new javax.swing.GroupLayout(pnlPpsn);
        pnlPpsn.setLayout(pnlPpsnLayout);
        pnlPpsnLayout.setHorizontalGroup(
            pnlPpsnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPpsnLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfPPSN, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPpsnValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPpsnLayout.setVerticalGroup(
            pnlPpsnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPpsnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(tfPPSN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblPpsnValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel3.setText("Address:");

        jLabel11.setText("Line 1");

        tfAddressLine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAddressLine1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddressLine1Layout = new javax.swing.GroupLayout(pnlAddressLine1);
        pnlAddressLine1.setLayout(pnlAddressLine1Layout);
        pnlAddressLine1Layout.setHorizontalGroup(
            pnlAddressLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLine1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAddrLine1ValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressLine1Layout.setVerticalGroup(
            pnlAddressLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel11)
                .addComponent(tfAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblAddrLine1ValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel12.setText("Line 2");

        tfAddressLine2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAddressLine2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddressLine2Layout = new javax.swing.GroupLayout(pnlAddressLine2);
        pnlAddressLine2.setLayout(pnlAddressLine2Layout);
        pnlAddressLine2Layout.setHorizontalGroup(
            pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLine2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
            .addGroup(pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlAddressLine2Layout.createSequentialGroup()
                    .addGap(298, 298, 298)
                    .addComponent(lblAddrLine2ValidStatus)
                    .addContainerGap(41, Short.MAX_VALUE)))
        );
        pnlAddressLine2Layout.setVerticalGroup(
            pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel12)
                .addComponent(tfAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblAddrLine2ValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))
        );

        jLabel13.setText("City");

        tfAddressCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAddressCityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddressCityLayout = new javax.swing.GroupLayout(pnlAddressCity);
        pnlAddressCity.setLayout(pnlAddressCityLayout);
        pnlAddressCityLayout.setHorizontalGroup(
            pnlAddressCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCityLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAddressCity, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCityValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressCityLayout.setVerticalGroup(
            pnlAddressCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel13)
                .addComponent(tfAddressCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblCityValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel14.setText("County");

        tfAddressCounty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAddressCountyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddressCountyLayout = new javax.swing.GroupLayout(pnlAddressCounty);
        pnlAddressCounty.setLayout(pnlAddressCountyLayout);
        pnlAddressCountyLayout.setHorizontalGroup(
            pnlAddressCountyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCountyLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCountyValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressCountyLayout.setVerticalGroup(
            pnlAddressCountyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCountyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel14)
                .addComponent(tfAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblCountyValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnlAddressCountry.setPreferredSize(new java.awt.Dimension(302, 22));

        jLabel15.setText("Country");

        tfAddressCountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAddressCountryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddressCountryLayout = new javax.swing.GroupLayout(pnlAddressCountry);
        pnlAddressCountry.setLayout(pnlAddressCountryLayout);
        pnlAddressCountryLayout.setHorizontalGroup(
            pnlAddressCountryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCountryLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAddressCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCountryValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressCountryLayout.setVerticalGroup(
            pnlAddressCountryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCountryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel15)
                .addComponent(tfAddressCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblCountryValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnlAddressEircode.setPreferredSize(new java.awt.Dimension(302, 22));

        jLabel17.setText("Eircode");

        tfAddressEircode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAddressEircodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddressEircodeLayout = new javax.swing.GroupLayout(pnlAddressEircode);
        pnlAddressEircode.setLayout(pnlAddressEircodeLayout);
        pnlAddressEircodeLayout.setHorizontalGroup(
            pnlAddressEircodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressEircodeLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAddressEircode, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEircodeValidStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressEircodeLayout.setVerticalGroup(
            pnlAddressEircodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressEircodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel17)
                .addComponent(tfAddressEircode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblEircodeValidStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlAddressLine2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAddressLine1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlMainLayout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlLastName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlDOB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlPpsn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlGender, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnlAddressCity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAddressCounty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAddressEircode, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                    .addComponent(pnlAddressCountry, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(pnlUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPpsn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddressCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddressCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddressEircode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jCloseBtn.setText("Close");
        jCloseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseBtnActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblLoggedInUsername.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLoggedInUsername.setForeground(new java.awt.Color(255, 0, 0));
        lblLoggedInUsername.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLoggedInUsername.setText("logged in as ");

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/profile.png"))); // NOI18N

        javax.swing.GroupLayout pnlLoggedInAs4Layout = new javax.swing.GroupLayout(pnlLoggedInAs4);
        pnlLoggedInAs4.setLayout(pnlLoggedInAs4Layout);
        pnlLoggedInAs4Layout.setHorizontalGroup(
            pnlLoggedInAs4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoggedInAs4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLoggedInUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlLoggedInAs4Layout.setVerticalGroup(
            pnlLoggedInAs4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
            .addGroup(pnlLoggedInAs4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblLoggedInUsername)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCloseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlLoggedInAs4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlLoggedInAs4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(jCloseBtn))
                .addGap(53, 53, 53))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfFirstNameActionPerformed

    private void tfLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfLastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfLastNameActionPerformed

    private void tfPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftfPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ftfPhoneNumberActionPerformed

    private void tfDOBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDOBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDOBActionPerformed

    private void jCloseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCloseBtnActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_jCloseBtnActionPerformed

    private void tfEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEmailActionPerformed

    private void tfPPSNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPPSNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPPSNActionPerformed

    private void tfAddressLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAddressLine1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAddressLine1ActionPerformed

    private void tfAddressLine2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAddressLine2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAddressLine2ActionPerformed

    private void tfAddressCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAddressCityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAddressCityActionPerformed

    private void tfAddressCountyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAddressCountyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAddressCountyActionPerformed

    private void tfAddressCountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAddressCountryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAddressCountryActionPerformed

    private void tfAddressEircodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAddressEircodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAddressEircodeActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        onSavePerson();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void onSavePerson() {
        fillPersonFromUI();
        if (user == null) {
            personService.save(person);
        } else {
            personService.saveWithUser(person, user);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.setActiveUser(context.getBean(UserService.class).getByUsername("johnm").orElse(null));
                PersonFrame personFrame = manager.getFrame(PERSON);

                context.getBean(PersonService.class).getById(6).ifPresent(personFrame::setPerson);

                manager.showSub(PERSON);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbGender;
    private javax.swing.JButton jCloseBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAddrLine1ValidStatus;
    private javax.swing.JLabel lblAddrLine2ValidStatus;
    private javax.swing.JLabel lblCityValidStatus;
    private javax.swing.JLabel lblCountryValidStatus;
    private javax.swing.JLabel lblCountyValidStatus;
    private javax.swing.JLabel lblEircodeValidStatus;
    private javax.swing.JLabel lblEmailValidStatus;
    private javax.swing.JLabel lblFNValidStatus;
    private javax.swing.JLabel lblLNValidStatus;
    private javax.swing.JLabel lblLoggedInUsername;
    private javax.swing.JLabel lblPhoneValidStatus;
    private javax.swing.JLabel lblPpsnValidStatus;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlAddressCity;
    private javax.swing.JPanel pnlAddressCountry;
    private javax.swing.JPanel pnlAddressCounty;
    private javax.swing.JPanel pnlAddressEircode;
    private javax.swing.JPanel pnlAddressLine1;
    private javax.swing.JPanel pnlAddressLine2;
    private javax.swing.JPanel pnlDOB;
    private javax.swing.JPanel pnlEmail;
    private javax.swing.JPanel pnlFirstName;
    private javax.swing.JPanel pnlGender;
    private javax.swing.JPanel pnlLastName;
    private javax.swing.JPanel pnlLoggedInAs4;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPhoneNumber;
    private javax.swing.JPanel pnlPpsn;
    private javax.swing.JPanel pnlUsername;
    private javax.swing.JTextField tfAddressCity;
    private javax.swing.JTextField tfAddressCountry;
    private javax.swing.JTextField tfAddressCounty;
    private javax.swing.JTextField tfAddressEircode;
    private javax.swing.JTextField tfAddressLine1;
    private javax.swing.JTextField tfAddressLine2;
    private javax.swing.JTextField tfDOB;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfFirstName;
    private javax.swing.JTextField tfLastName;
    private javax.swing.JTextField tfPPSN;
    private javax.swing.JTextField tfPhoneNumber;
    // End of variables declaration//GEN-END:variables


}
