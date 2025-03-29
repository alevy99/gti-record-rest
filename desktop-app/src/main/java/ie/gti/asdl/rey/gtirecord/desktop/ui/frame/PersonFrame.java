/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import ie.gti.asdl.rey.gtirecord.core.service.PersonService;
import ie.gti.asdl.rey.gtirecord.core.service.ServiceManager;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.PhoneNumberValidator;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.PERSON;
import static ie.gti.asdl.rey.gtirecord.desktop.util.ImageUtils.resizeIcon;

/**
 *
 * @author Andrei
 */
public class PersonFrame extends AbstractFrame {

    private Integer personId;

    private Person person;

    private DatePicker dobDatePicker;

    private final PersonService personService;

    /**
     * Creates new form PersonFrame
     */
    public PersonFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);

        personService = serviceManager.getPersonService();
        initDatePicker();
        initComponents();
        initUI();
    }

    private void initPerson() {
        loadPerson(personId);
        initPersonUI();
    }

    private void loadPerson(Integer personId) {
        if (personId == null) {
            person = new Person();
            return;
        }
        Optional<Person> personOpt = personService.getById(personId);
        person = personOpt.orElseGet(Person::new);
    }

    private void initPersonUI() {
        tfFirstName.setText(person.getFirstName());
        tfLastName.setText(person.getLastName());
        dobDatePicker.setDate(person.getDateOfBirth());
        tfEmail.setText(person.getEmail());
        tfPhoneNumber.setText(person.getPhoneNum());
        tfPPSN.setText(person.getPpsn());

        Address address = person.getAddress();
        if (address != null) {
            tfAddressLine1.setText(address.getLine1());
            tfAddressLine2.setText(address.getLine2());
            tfAddressCounty.setText(address.getCounty());
            tfAddressCity.setText(address.getCity());
            tfAddressCountry.setText(address.getCountry());
            tfAddressEircode.setText(address.getEirCode());
        }
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

    }

    private void initDatePicker() {
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

    @Override
    protected int getDefaultCloseOperationValue() {
        return JFrame.HIDE_ON_CLOSE;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
        initPerson();
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
        tfPhoneNumber = new javax.swing.JTextField();
        pnlEmail = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        tfEmail = new javax.swing.JTextField();
        pnlPpsn = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        tfPPSN = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        pnlAddressLine1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tfAddressLine1 = new javax.swing.JTextField();
        pnlAddressLine2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        tfAddressLine2 = new javax.swing.JTextField();
        pnlAddressCity = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        tfAddressCity = new javax.swing.JTextField();
        pnlAddressCounty = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        tfAddressCounty = new javax.swing.JTextField();
        pnlAddressCountry = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        tfAddressCountry = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        pnlAddressEircode = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        tfAddressEircode = new javax.swing.JTextField();
        jCloseBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PERSON");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
                .addComponent(lblError)
                .addContainerGap(43, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPhoneNumberLayout.setVerticalGroup(
            pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhoneNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(tfPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEmailLayout.setVerticalGroup(
            pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPpsnLayout.setVerticalGroup(
            pnlPpsnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPpsnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(tfPPSN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressLine1Layout.setVerticalGroup(
            pnlAddressLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel11)
                .addComponent(tfAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressLine2Layout.setVerticalGroup(
            pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel12)
                .addComponent(tfAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressCityLayout.setVerticalGroup(
            pnlAddressCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel13)
                .addComponent(tfAddressCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(49, Short.MAX_VALUE))
        );
        pnlAddressCountyLayout.setVerticalGroup(
            pnlAddressCountyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCountyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel14)
                .addComponent(tfAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressCountryLayout.setVerticalGroup(
            pnlAddressCountryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressCountryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel15)
                .addComponent(tfAddressCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
                .addContainerGap(53, Short.MAX_VALUE))
        );
        pnlAddressEircodeLayout.setVerticalGroup(
            pnlAddressEircodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressEircodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel17)
                .addComponent(tfAddressEircode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlAddressCountry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlAddressEircode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(pnlPpsn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(pnlAddressCity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnlAddressCounty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(pnlFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPpsn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jCloseBtn.setText("Close");
        jCloseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseBtnActionPerformed(evt);
            }
        });

        jButton1.setText("Save");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCloseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jCloseBtn))
                .addContainerGap(92, Short.MAX_VALUE))
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, true, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.showSub(PERSON);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jCloseBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblError;
    private javax.swing.JPanel pnlAddressCity;
    private javax.swing.JPanel pnlAddressCountry;
    private javax.swing.JPanel pnlAddressCounty;
    private javax.swing.JPanel pnlAddressEircode;
    private javax.swing.JPanel pnlAddressLine1;
    private javax.swing.JPanel pnlAddressLine2;
    private javax.swing.JPanel pnlDOB;
    private javax.swing.JPanel pnlEmail;
    private javax.swing.JPanel pnlFirstName;
    private javax.swing.JPanel pnlLastName;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPhoneNumber;
    private javax.swing.JPanel pnlPpsn;
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
