package task05.frameactionlistener;

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
 * Style (a): The JFrame itself implements ActionListener.
 */
public class ConverterFrameActionListener extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JButton convertButton = new JButton("Convert");
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public ConverterFrameActionListener() {
        super("Currency Converter - Frame ActionListener");
        setLayout(new FlowLayout());

        add(new JLabel("INR"));
        add(amount);
        add(target);
        add(convertButton);
        add(result);

        // Frame itself is registered as the listener for both button and text field
        convertButton.addActionListener(this);
        amount.addActionListener(this);

        // WindowAdapter for clean disposal
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Dispatches based on event source
        if (e.getSource() == convertButton || e.getSource() == amount) {
            convert();
        }
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
        SwingUtilities.invokeLater(() -> new ConverterFrameActionListener().setVisible(true));
    }
}