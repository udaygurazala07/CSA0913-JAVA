package task06.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import task06.service.RegistrationService;

/**
 * Main application window hosting JMenuBar, JTabbedPane, and status bar.
 */
public class MainFrame extends JFrame {
    private final RegistrationService registrationService = new RegistrationService();
    private final JLabel lblGlobalStatus = new JLabel("Status: Application Initialized | Ready.");
    private final JTabbedPane tabbedPane = new JTabbedPane();

    private RegistrationPanel registrationPanel;
    private RegistrationTablePanel tablePanel;

    public MainFrame() {
        super("Campus Event Registration System - SIMATS Engineering (CO4)");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        // 1. Menu Bar
        MenuController menuController = new MenuController(this);
        setJMenuBar(menuController.createMenuBar());

        // 2. Status Bar at the bottom
        lblGlobalStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblGlobalStatus.setForeground(new Color(33, 33, 33));
        lblGlobalStatus.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        add(lblGlobalStatus, BorderLayout.SOUTH);

        // 3. Tabbed Content
        registrationPanel = new RegistrationPanel(this, registrationService, lblGlobalStatus);
        tablePanel = new RegistrationTablePanel(registrationService, lblGlobalStatus);

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.addTab("Student Registration Form", registrationPanel);
        tabbedPane.addTab("Committee Registration Table", tablePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Window Closing Handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        setSize(850, 560);
        setMinimumSize(new Dimension(650, 450));
        setLocationRelativeTo(null);
    }

    public void refreshTable() {
        if (tablePanel != null) {
            tablePanel.applyFilter();
        }
    }

    public void handleExit() {
        int opt = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the Campus Event Registration System?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (opt == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public RegistrationPanel getRegistrationPanel() { return registrationPanel; }
    public RegistrationTablePanel getTablePanel() { return tablePanel; }
    public JLabel getGlobalStatus() { return lblGlobalStatus; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}