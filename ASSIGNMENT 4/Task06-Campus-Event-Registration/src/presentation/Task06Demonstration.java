package task06.presentation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import task06.model.Event;
import task06.model.Registration;
import task06.service.RegistrationService;
import task06.service.ValidationService;

public class Task06Demonstration {
    public static void main(String[] args) throws Exception {
        System.out.println("=== TASK 6: CAMPUS EVENT REGISTRATION DEMONSTRATION & TEST SUITE ===");
        RegistrationService service = new RegistrationService();
        File screenDir = new File("..\\Assignment 1 -java\\Java-Assignment-4\\Task06-Campus-Event-Registration\\Screenshots");
        if (!screenDir.exists()) screenDir.mkdirs();

        // TEST 1: Service Validation Test - Invalid Email
        System.out.println("\n--- TEST CASE 1: Invalid Email Rejection ---");
        try {
            Event ev = service.getAllEvents().get(0);
            service.register("192311094", "Dhanush", "invalid_email_no_at", "Computer Science & Engg", 3, ev);
            System.out.println("[FAIL] Should have rejected malformed email.");
        } catch (ValidationService.ValidationException e) {
            System.out.println("[PASS] Successfully rejected: " + e.getMessage());
        }

        // TEST 2: Service Validation Test - Empty Name
        System.out.println("\n--- TEST CASE 2: Empty Name Rejection ---");
        try {
            Event ev = service.getAllEvents().get(0);
            service.register("192311094", "", "dhanush@simats.edu", "Computer Science & Engg", 3, ev);
            System.out.println("[FAIL] Should have rejected empty name.");
        } catch (ValidationService.ValidationException e) {
            System.out.println("[PASS] Successfully rejected: " + e.getMessage());
        }

        // TEST 3: Duplicate Registration Rejection
        System.out.println("\n--- TEST CASE 3: Duplicate Registration Prevention ---");
        try {
            Event ev1 = service.getAllEvents().get(0);
            service.register("192311094", "Kattiripalli Dhanush", "dhanush@simats.edu", "Computer Science & Engg", 3, ev1);
            System.out.println("[FAIL] Should have rejected duplicate registration.");
        } catch (RegistrationService.DuplicateRegistrationException e) {
            System.out.println("[PASS] Successfully prevented duplicate registration:\n" + e.getMessage());
        }

        // TEST 4: CSV Export Execution
        System.out.println("\n--- TEST CASE 4: Exporting Registrations to CSV ---");
        List<Registration> records = service.getAllRegistrations();
        File csvFile = new File("..\\Assignment 1 -java\\Java-Assignment-4\\Task06-Campus-Event-Registration\\Campus_Registrations_Export.csv");
        service.exportToCSV(records, csvFile);
        System.out.println("[PASS] Exported " + records.size() + " registrations to CSV: " + csvFile.getAbsolutePath() + " (File size: " + csvFile.length() + " bytes)");

        // GUI SCREENSHOT CAPTURE
        System.out.println("\n--- CAPTURING FULL GUI SCREENSHOTS ---");
        SwingUtilities.invokeAndWait(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setSize(new Dimension(850, 560));
                frame.setVisible(true);

                // 1. Capture Registration Form (Tab 0)
                frame.getTabbedPane().setSelectedIndex(0);
                RegistrationPanel regPanel = frame.getRegistrationPanel();
                regPanel.getTxtRollNo().setText("192311094");
                regPanel.getTxtName().setText("Kattiripalli Dhanush");
                regPanel.getTxtEmail().setText("dhanush@simats.edu");
                regPanel.getCmbDept().setSelectedItem("Computer Science & Engg");
                frame.repaint();
                Thread.sleep(500);

                captureFrame(frame, new File(screenDir, "Task06-01-Registration-Form.png"));
                System.out.println("Captured: Task06-01-Registration-Form.png");

                // 2. Capture Committee Table (Tab 1)
                frame.getTabbedPane().setSelectedIndex(1);
                frame.getTablePanel().applyFilter();
                Thread.sleep(700);
                frame.repaint();

                captureFrame(frame, new File(screenDir, "Task06-02-Committee-Table-View.png"));
                System.out.println("Captured: Task06-02-Committee-Table-View.png");

                // 3. Filter by Specific Event
                JComboBox<String> cmbFilter = frame.getTablePanel().getCmbFilter();
                if (cmbFilter.getItemCount() > 1) {
                    cmbFilter.setSelectedIndex(1);
                }
                Thread.sleep(700);
                frame.repaint();

                captureFrame(frame, new File(screenDir, "Task06-03-Filtered-By-Event.png"));
                System.out.println("Captured: Task06-03-Filtered-By-Event.png");

                frame.dispose();
                System.out.println("GUI screenshot capture completed successfully!");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void captureFrame(MainFrame frame, File file) throws Exception {
        BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        frame.printAll(g2);
        g2.dispose();
        ImageIO.write(img, "png", file);
    }
}