package task05.anonymousinnerclass;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Style (c): Event handling via Anonymous Inner Class.
 */
public class ConverterAnonymousInnerClass extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JButton convertButton = new JButton("Convert");
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public ConverterAnonymousInnerClass() {
        super("Currency Converter - Anonymous Inner Class");
        setLayout(new FlowLayout());

        add(new JLabel("INR"));
        add(amount);
        add(target);
        add(convertButton);
        add(result);

        // Shared anonymous listener instance
        ActionListener anonymousHandler = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convert();
            }
        };

        convertButton.addActionListener(anonymousHandler);
        amount.addActionListener(anonymousHandler);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void convert() {
        try {
            double val = Double.parseDouble(amount.getText().trim());
            String curr = (String) target.getSelectedItem();
            double converted = val * rate(curr);
            String resText = String.format("%.2f %s", converted, curr);
            result.setText(resText);
            JOptionPane.showMessageDialog(this, "Converted Amount: " + resText, "Conversion Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
            JOptionPane.showMessageDialog(this, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterAnonymousInnerClass().setVisible(true));
    }
}