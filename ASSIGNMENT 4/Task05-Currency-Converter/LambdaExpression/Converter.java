package task05.lambdaexpression;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Converter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter() {
        super("Currency Converter");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);

        // TODO 1: Register ONE lambda listener for both the Convert button and the
        // Enter key in the amount field
        ActionListener listener = e -> convert();
        convertButton.addActionListener(listener);
        amount.addActionListener(listener);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void convert() {
        // TODO 2: Parse the amount and show the converted value with 2 decimals, or
        // "Invalid amount" if parsing fails
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            String curr = (String) target.getSelectedItem();
            double converted = inr * rate(curr);
            result.setText(String.format("%.2f %s", converted, curr));
        } catch (NumberFormatException e) {
            result.setText("Invalid amount");
        }
    }

    // Helper methods for automated testing / capture
    public JTextField getAmountField() { return amount; }
    public JComboBox<String> getTargetCombo() { return target; }
    public JLabel getResultLabel() { return result; }
    public void triggerConvert() { convert(); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter().setVisible(true));
    }
}