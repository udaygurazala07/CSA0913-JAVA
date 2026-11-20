package task06.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import task06.model.Event;
import task06.service.RegistrationService;
import task06.service.ValidationService;

/**
 * Presentation panel providing the Student Registration Form.
 */
public class RegistrationPanel extends JPanel {
    private final JTextField txtRollNo = new JTextField(15);
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtEmail = new JTextField(15);
    private final JComboBox<String> cmbDept = new JComboBox<>(new String[]{
        "Select Department", "Computer Science & Engg", "Information Technology",
        "Electronics & Comm Engg", "Electrical & Electronics", "Mechanical Engineering", "Biotechnology"
    });
    private final JComboBox<String> cmbYear = new JComboBox<>(new String[]{
        "1st Year", "2nd Year", "3rd Year", "4th Year"
    });
    private final JComboBox<Event> cmbEvent = new JComboBox<>();

    private final JButton btnRegister = new JButton("Register for Event");
    private final JButton btnClear = new JButton("Clear Form");
    private final JLabel lblStatus;

    private final RegistrationService registrationService;
    private final MainFrame mainFrame;

    public RegistrationPanel(MainFrame mainFrame, RegistrationService service, JLabel statusLabel) {
        this.mainFrame = mainFrame;
        this.registrationService = service;
        this.lblStatus = statusLabel;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        initComponents();
        loadEvents();
    }

    private void initComponents() {
        // Title Panel
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 4, 4));
        JLabel lblTitle = new JLabel("STUDENT CAMPUS EVENT REGISTRATION", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(25, 118, 210));

        JLabel lblSub = new JLabel("Fill out the details below to secure your event participation seat", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        add(titlePanel, BorderLayout.NORTH);

        // Center Form Grid
        JPanel formGrid = new JPanel(new GridLayout(6, 2, 12, 12));
        formGrid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Student & Event Details"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        formGrid.add(createBoldLabel("Student Roll Number:"));
        formGrid.add(txtRollNo);

        formGrid.add(createBoldLabel("Full Student Name:"));
        formGrid.add(txtName);

        formGrid.add(createBoldLabel("Official Email Address:"));
        formGrid.add(txtEmail);

        formGrid.add(createBoldLabel("Academic Department:"));
        formGrid.add(cmbDept);

        formGrid.add(createBoldLabel("Academic Year:"));
        formGrid.add(cmbYear);

        formGrid.add(createBoldLabel("Select Campus Event:"));
        formGrid.add(cmbEvent);

        add(formGrid, BorderLayout.CENTER);

        // South Button Bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRegister.setBackground(new Color(46, 125, 50));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnClear.setBackground(new Color(211, 47, 47));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));

        buttonBar.add(btnRegister);
        buttonBar.add(btnClear);
        add(buttonBar, BorderLayout.SOUTH);

        // Event Handlers
        btnRegister.addActionListener(e -> handleRegistration());
        btnClear.addActionListener(e -> handleClear());
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    public void loadEvents() {
        new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return registrationService.getAllEvents();
            }

            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    cmbEvent.removeAllItems();
                    for (Event ev : events) {
                        cmbEvent.addItem(ev);
                    }
                    lblStatus.setText("Events loaded: " + events.size() + " active campus events.");
                } catch (Exception ex) {
                    lblStatus.setText("Failed to load events: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleRegistration() {
        String roll = txtRollNo.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String dept = (String) cmbDept.getSelectedItem();
        int year = cmbYear.getSelectedIndex() + 1;
        Event event = (Event) cmbEvent.getSelectedItem();

        setFormEnabled(false);
        lblStatus.setText("Verifying and submitting registration... Please wait.");

        // Asynchronous submission via SwingWorker keeps EDT fully responsive
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return registrationService.register(roll, name, email, dept, year, event);
            }

            @Override
            protected void done() {
                setFormEnabled(true);
                try {
                    int regId = get();
                    lblStatus.setText("Registration #" + regId + " confirmed for " + roll);
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                        "Registration Confirmed Successfully!\n" +
                        "Registration ID: REG-" + regId + "\n" +
                        "Student: " + name + " (" + roll + ")\n" +
                        "Department: " + dept + " (Year " + year + ")\n" +
                        "Event: " + event.getTitle() + "\n" +
                        "Venue: " + event.getVenue(),
                        "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

                    handleClear();
                    mainFrame.refreshTable();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof ValidationService.ValidationException) {
                        lblStatus.setText("Validation Error: " + cause.getMessage());
                        JOptionPane.showMessageDialog(RegistrationPanel.this,
                            cause.getMessage(), "Input Validation Failed", JOptionPane.WARNING_MESSAGE);
                    } else if (cause instanceof RegistrationService.DuplicateRegistrationException) {
                        lblStatus.setText("Duplicate Rejected: " + cause.getMessage());
                        JOptionPane.showMessageDialog(RegistrationPanel.this,
                            cause.getMessage(), "Duplicate Registration Rejected", JOptionPane.ERROR_MESSAGE);
                    } else {
                        lblStatus.setText("Database Error: " + cause.getMessage());
                        JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "Database Error: " + cause.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private void handleClear() {
        txtRollNo.setText("");
        txtName.setText("");
        txtEmail.setText("");
        cmbDept.setSelectedIndex(0);
        cmbYear.setSelectedIndex(0);
        if (cmbEvent.getItemCount() > 0) cmbEvent.setSelectedIndex(0);
        lblStatus.setText("Form cleared.");
    }

    private void setFormEnabled(boolean enabled) {
        btnRegister.setEnabled(enabled);
        btnClear.setEnabled(enabled);
        txtRollNo.setEnabled(enabled);
        txtName.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        cmbDept.setEnabled(enabled);
        cmbYear.setEnabled(enabled);
        cmbEvent.setEnabled(enabled);
    }

    public JTextField getTxtRollNo() { return txtRollNo; }
    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JComboBox<String> getCmbDept() { return cmbDept; }
    public JComboBox<Event> getCmbEvent() { return cmbEvent; }
    public JButton getBtnRegister() { return btnRegister; }
}