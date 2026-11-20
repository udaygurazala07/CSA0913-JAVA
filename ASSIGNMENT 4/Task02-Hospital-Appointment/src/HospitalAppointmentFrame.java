package task02;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Hospital Outpatient Appointment Desk GUI implementing event-driven design.
 */
public class HospitalAppointmentFrame extends JFrame {
    private final JTextField txtPatientId = new JTextField(15);
    private final JComboBox<String> cmbDepartment = new JComboBox<>();
    private final JComboBox<Doctor> cmbDoctor = new JComboBox<>();
    private final JComboBox<String> cmbSlot = new JComboBox<>();
    private final JButton btnBook = new JButton("Book Appointment");
    private final JButton btnClear = new JButton("Clear");
    private final JLabel lblStatus = new JLabel("Status: Ready to accept appointments.");

    private final AppointmentService appointmentService;
    private boolean bookingInProgress = false;

    public HospitalAppointmentFrame(AppointmentService service) {
        super("Hospital Outpatient Appointment Desk - SIMATS Health");
        this.appointmentService = service;
        initComponents();
        registerEvents();
        populateInitialData();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        // North Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel("HOSPITAL OUTPATIENT APPOINTMENT DESK", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Fast-Track Specialist Booking System", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(227, 242, 253));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel formGrid = new JPanel(new GridLayout(4, 2, 12, 12));
        formGrid.add(new JLabel("Patient ID (e.g., PAT-1001):"));
        formGrid.add(txtPatientId);

        formGrid.add(new JLabel("Medical Department:"));
        formGrid.add(cmbDepartment);

        formGrid.add(new JLabel("Consultant Specialist:"));
        formGrid.add(cmbDoctor);

        formGrid.add(new JLabel("Available Time Slot:"));
        formGrid.add(cmbSlot);

        formContainer.add(formGrid, BorderLayout.CENTER);
        add(formContainer, BorderLayout.CENTER);

        // South Actions and Status
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        btnBook.setBackground(new Color(46, 125, 50));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnClear.setBackground(new Color(211, 47, 47));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));

        buttonPanel.add(btnBook);
        buttonPanel.add(btnClear);

        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(66, 66, 66));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(lblStatus, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        setSize(520, 340);
        setLocationRelativeTo(null);
    }

    private void registerEvents() {
        // Track input progress
        txtPatientId.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { setDirty(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { setDirty(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { setDirty(); }
            private void setDirty() {
                if (!txtPatientId.getText().trim().isEmpty()) {
                    bookingInProgress = true;
                }
            }
        });

        // Department selection changes doctor list dynamically
        cmbDepartment.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                String dept = (String) cmbDepartment.getSelectedItem();
                refreshDoctorList(dept);
            }
        });

        // Book button handler
        btnBook.addActionListener(e -> handleBooking());

        // Clear button handler
        btnClear.addActionListener(e -> handleClear());

        // Window closing confirmation if booking in progress
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void populateInitialData() {
        cmbDepartment.removeAllItems();
        for (String dept : appointmentService.getDepartments()) {
            cmbDepartment.addItem(dept);
        }

        cmbSlot.removeAllItems();
        for (String slot : appointmentService.getAvailableSlots()) {
            cmbSlot.addItem(slot);
        }

        if (cmbDepartment.getItemCount() > 0) {
            refreshDoctorList((String) cmbDepartment.getSelectedItem());
        }
    }

    private void refreshDoctorList(String department) {
        cmbDoctor.removeAllItems();
        List<Doctor> doctors = appointmentService.getDoctorsByDepartment(department);
        for (Doctor doc : doctors) {
            cmbDoctor.addItem(doc);
        }
        lblStatus.setText("Status: Showing " + doctors.size() + " doctors for " + department);
    }

    private void handleBooking() {
        String patId = txtPatientId.getText().trim();
        String dept = (String) cmbDepartment.getSelectedItem();
        Doctor doc = (Doctor) cmbDoctor.getSelectedItem();
        String slot = (String) cmbSlot.getSelectedItem();

        // Validation
        if (patId.isEmpty()) {
            showError("Patient ID is required.");
            txtPatientId.requestFocus();
            return;
        }

        if (!patId.matches("^PAT-[0-9]{3,6}$")) {
            showError("Invalid Patient ID format. Example: PAT-1001");
            txtPatientId.requestFocus();
            return;
        }

        if (doc == null || slot == null) {
            showError("Please select doctor and appointment slot.");
            return;
        }

        // Check slot availability
        if (!appointmentService.isSlotAvailable(doc.getId(), slot)) {
            showError("The slot " + slot + " with " + doc.getName() + " is already booked.");
            return;
        }

        // Execute booking
        String aptId = appointmentService.bookAppointment(patId, doc.getId(), dept, slot);
        if (aptId != null) {
            bookingInProgress = false;
            lblStatus.setText("Status: " + aptId + " confirmed for " + patId);
            JOptionPane.showMessageDialog(this,
                "Appointment Confirmed!\n" +
                "Booking Reference: " + aptId + "\n" +
                "Patient ID: " + patId + "\n" +
                "Specialist: " + doc.getName() + "\n" +
                "Department: " + dept + "\n" +
                "Scheduled Time: " + slot,
                "Booking Success", JOptionPane.INFORMATION_MESSAGE);
            handleClear();
        } else {
            showError("Booking failed due to slot conflict.");
        }
    }

    private void handleClear() {
        txtPatientId.setText("");
        if (cmbDepartment.getItemCount() > 0) cmbDepartment.setSelectedIndex(0);
        if (cmbSlot.getItemCount() > 0) cmbSlot.setSelectedIndex(0);
        lblStatus.setText("Status: Form cleared.");
        bookingInProgress = false;
    }

    private void handleWindowClosing() {
        if (bookingInProgress) {
            int option = JOptionPane.showConfirmDialog(
                this,
                "An appointment booking is currently in progress.\nAre you sure you want to discard it and exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void showError(String msg) {
        lblStatus.setText("Error: " + msg);
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppointmentService service = new AppointmentService();
            new HospitalAppointmentFrame(service).setVisible(true);
        });
    }
}