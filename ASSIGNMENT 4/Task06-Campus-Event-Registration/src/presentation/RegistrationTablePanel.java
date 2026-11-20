package task06.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import task06.model.Event;
import task06.model.Registration;
import task06.service.RegistrationService;

/**
 * Presentation panel providing the Committee Table and Export Dashboard.
 */
public class RegistrationTablePanel extends JPanel {
    private final JComboBox<String> cmbFilter = new JComboBox<>();
    private final JTextField txtSearch = new JTextField(12);
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnExport = new JButton("Export to CSV");

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel lblStatus;

    private final RegistrationService registrationService;
    private List<Registration> currentRecords = new ArrayList<>();

    public RegistrationTablePanel(RegistrationService service, JLabel statusLabel) {
        this.registrationService = service;
        this.lblStatus = statusLabel;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 1. Filter and Control Row (North)
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterRow.setBorder(BorderFactory.createEtchedBorder());
        filterRow.setBackground(new Color(245, 245, 245));

        JLabel lblFilter = new JLabel("Filter by Event:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(25, 118, 210));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));

        filterRow.add(lblFilter);
        filterRow.add(cmbFilter);
        filterRow.add(new JLabel("Quick Search:"));
        filterRow.add(txtSearch);
        filterRow.add(btnRefresh);
        filterRow.add(btnExport);

        add(filterRow, BorderLayout.NORTH);

        // 2. Tabular Results Area (Center - expands dynamically)
        String[] columns = {"Reg ID", "Roll No", "Student Name", "Email", "Department", "Event Title", "Registered At", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Official Committee Registration Roster"));
        add(scrollPane, BorderLayout.CENTER);

        // Event Listeners
        cmbFilter.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                applyFilter();
            }
        });

        btnRefresh.addActionListener(e -> applyFilter());
        btnExport.addActionListener(e -> handleExport());

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyLocalSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyLocalSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyLocalSearch(); }
        });

        loadFilterOptions();
        applyFilter();
    }

    public void loadFilterOptions() {
        new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return registrationService.getAllEvents();
            }

            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    cmbFilter.removeAllItems();
                    cmbFilter.addItem("All Events");
                    for (Event ev : events) {
                        cmbFilter.addItem(ev.getTitle());
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    public void applyFilter() {
        String selected = (String) cmbFilter.getSelectedItem();
        if (selected == null) selected = "All Events";
        final String targetEvent = selected;

        setControlsEnabled(false);
        lblStatus.setText("Loading registrations for " + targetEvent + "... Please wait.");

        new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                return registrationService.getRegistrationsByEventTitle(targetEvent);
            }

            @Override
            protected void done() {
                setControlsEnabled(true);
                try {
                    currentRecords = get();
                    renderTable(currentRecords);
                    lblStatus.setText("Loaded " + currentRecords.size() + " registrations for " + targetEvent + ".");
                } catch (Exception ex) {
                    lblStatus.setText("Error loading registrations: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void renderTable(List<Registration> records) {
        tableModel.setRowCount(0);
        for (Registration r : records) {
            tableModel.addRow(new Object[]{
                "REG-" + r.getId(),
                r.getStudentRoll(),
                r.getStudentName(),
                r.getStudentEmail(),
                r.getStudentDept(),
                r.getEventTitle(),
                r.getFormattedDate(),
                r.getStatus()
            });
        }
    }

    private void applyLocalSearch() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            renderTable(currentRecords);
            return;
        }

        List<Registration> filtered = new ArrayList<>();
        for (Registration r : currentRecords) {
            if (r.getStudentRoll().toLowerCase().contains(q) ||
                r.getStudentName().toLowerCase().contains(q) ||
                r.getEventTitle().toLowerCase().contains(q) ||
                r.getStudentDept().toLowerCase().contains(q)) {
                filtered.add(r);
            }
        }
        renderTable(filtered);
    }

    public void handleExport() {
        if (currentRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records available to export.", "Empty Roster", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Registrations to CSV");
        chooser.setSelectedFile(new File("Campus_Event_Registrations.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Comma Separated Files (*.csv)", "csv"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".csv")) {
                target = new File(target.getParentFile(), target.getName() + ".csv");
            }

            final File exportFile = target;
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    registrationService.exportToCSV(currentRecords, exportFile);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        lblStatus.setText("Exported " + currentRecords.size() + " records to " + exportFile.getName());
                        JOptionPane.showMessageDialog(RegistrationTablePanel.this,
                            "Export Completed Successfully!\nSaved to:\n" + exportFile.getAbsolutePath(),
                            "CSV Export Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RegistrationTablePanel.this,
                            "Export Failed: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void setControlsEnabled(boolean enabled) {
        cmbFilter.setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
        btnExport.setEnabled(enabled);
    }

    public JComboBox<String> getCmbFilter() { return cmbFilter; }
    public JTable getTable() { return table; }
    public JButton getBtnExport() { return btnExport; }
    public JButton getBtnRefresh() { return btnRefresh; }
}