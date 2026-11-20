package task06.presentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Controller and builder for the top-level JMenuBar.
 */
public class MenuController implements ActionListener {
    private final MainFrame mainFrame;

    public MenuController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem itemExport = new JMenuItem("Export to CSV...");
        JMenuItem itemExit = new JMenuItem("Exit Application");

        itemExport.setActionCommand("EXPORT");
        itemExit.setActionCommand("EXIT");

        itemExport.addActionListener(this);
        itemExit.addActionListener(this);

        fileMenu.add(itemExport);
        fileMenu.addSeparator();
        fileMenu.add(itemExit);

        // 2. View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem itemRegForm = new JMenuItem("Student Registration Form");
        JMenuItem itemTable = new JMenuItem("Committee Registration Table");

        itemRegForm.setActionCommand("VIEW_FORM");
        itemTable.setActionCommand("VIEW_TABLE");

        itemRegForm.addActionListener(this);
        itemTable.addActionListener(this);

        viewMenu.add(itemRegForm);
        viewMenu.add(itemTable);

        // 3. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem itemAbout = new JMenuItem("About Campus Event App");
        itemAbout.setActionCommand("ABOUT");
        itemAbout.addActionListener(this);
        helpMenu.add(itemAbout);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "EXPORT":
                mainFrame.getTablePanel().handleExport();
                break;
            case "EXIT":
                mainFrame.handleExit();
                break;
            case "VIEW_FORM":
                mainFrame.getTabbedPane().setSelectedIndex(0);
                break;
            case "VIEW_TABLE":
                mainFrame.getTabbedPane().setSelectedIndex(1);
                break;
            case "ABOUT":
                JOptionPane.showMessageDialog(mainFrame,
                    "Campus Event Registration Desktop Application\n" +
                    "Version 1.0 (CO4 - GUI-JDBC Integration)\n" +
                    "Author: Kattiripalli Dhanush (192311094)\n" +
                    "SIMATS Engineering - Department of CSE",
                    "About Application", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
}