package task01;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

/**
 * FeeWindow is the presentation layer for the fee payment system.
 * It is solely responsible for collecting user input, displaying progress,
 * and presenting user feedback. All business logic is delegated to FeeService.
 */
public class FeeWindow extends Frame {
    private final TextField regNoField = new TextField(18);
    private final TextField amountField = new TextField(18);
    private final Button payButton = new Button("Pay");
    private final Button clearButton = new Button("Clear");
    private final Button receiptButton = new Button("Receipt");
    private final Label statusLabel = new Label("Ready to accept payment.", Label.CENTER);

    private final FeeService feeService;
    private String lastReceipt = null;

    public FeeWindow(FeeService feeService) {
        super("Student Fee Payment System (Redesigned)");
        this.feeService = feeService;
        initializeUI();
        registerEventHandlers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel in Center
        Panel formPanel = new Panel(new GridLayout(2, 2, 8, 8));
        Label regLabel = new Label("Register Number:");
        Label amtLabel = new Label("Fee Amount (INR):");
        regLabel.setFont(new Font("Arial", Font.BOLD, 12));
        amtLabel.setFont(new Font("Arial", Font.BOLD, 12));

        formPanel.add(regLabel);
        formPanel.add(regNoField);
        formPanel.add(amtLabel);
        formPanel.add(amountField);

        // Button Panel in South
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        payButton.setBackground(new Color(46, 125, 50));
        payButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(211, 47, 47));
        clearButton.setForeground(Color.WHITE);
        receiptButton.setBackground(new Color(25, 118, 210));
        receiptButton.setForeground(Color.WHITE);

        buttonPanel.add(payButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(receiptButton);

        // Status Label at the bottom
        statusLabel.setForeground(new Color(33, 33, 33));
        statusLabel.setBackground(new Color(238, 238, 238));

        Panel bottomPanel = new Panel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(420, 220);
        setLocationRelativeTo(null);
    }

    private void registerEventHandlers() {
        // Individual dedicated listener for Pay
        payButton.addActionListener(e -> handlePayment());

        // Individual dedicated listener for Clear
        clearButton.addActionListener(e -> handleClear());

        // Individual dedicated listener for Receipt
        receiptButton.addActionListener(e -> handleReceipt());

        // Window closing adapter
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void handlePayment() {
        String regNo = regNoField.getText();
        String amtStr = amountField.getText();

        try {
            // Validation in service layer
            feeService.validate(regNo, amtStr);
            double amount = Double.parseDouble(amtStr.trim());

            // Disable controls to prevent duplicate clicks during operation
            setControlsEnabled(false);
            statusLabel.setText("Processing payment with bank... Please wait.");
            statusLabel.setForeground(new Color(230, 81, 0));

            // Offload database and sleep operations to a background worker thread
            new Thread(() -> {
                try {
                    String receipt = feeService.processPayment(regNo, amount);
                    lastReceipt = receipt;

                    // Safely dispatch feedback back to UI
                    java.awt.EventQueue.invokeLater(() -> {
                        statusLabel.setText("Payment successful! Receipt generated.");
                        statusLabel.setForeground(new Color(46, 125, 50));
                        JOptionPane.showMessageDialog(this, 
                            "Payment of INR " + String.format("%.2f", amount) + " was successful!\n" + receipt,
                            "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE);
                        setControlsEnabled(true);
                    });
                } catch (Exception ex) {
                    java.awt.EventQueue.invokeLater(() -> {
                        statusLabel.setText("Payment failed: " + ex.getMessage());
                        statusLabel.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(this, 
                            "Payment Error: " + ex.getMessage(),
                            "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                        setControlsEnabled(true);
                    });
                }
            }).start();

        } catch (IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleClear() {
        regNoField.setText("");
        amountField.setText("");
        statusLabel.setText("Fields cleared.");
        statusLabel.setForeground(Color.DARK_GRAY);
    }

    private void handleReceipt() {
        if (lastReceipt == null) {
            JOptionPane.showMessageDialog(this, 
                "No successful payment found. Please complete a payment first.", 
                "Receipt Unavailable", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "=== OFFICIAL FEE RECEIPT ===\n" + lastReceipt, 
                "Student Fee Receipt", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setControlsEnabled(boolean enabled) {
        payButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        receiptButton.setEnabled(enabled);
        regNoField.setEnabled(enabled);
        amountField.setEnabled(enabled);
    }

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/collegedb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
        FeeService service = new FeeService(dbUrl, "root", "Dhanush7184");
        FeeWindow window = new FeeWindow(service);
        window.setVisible(true);
    }
}
