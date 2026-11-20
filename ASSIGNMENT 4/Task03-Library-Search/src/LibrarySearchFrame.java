package task03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Campus Central Library Search Screen implementing nested layouts.
 * Resizing requirement: Results area in CENTER grows dynamically while
 * search row and buttons keep their standard size.
 */
public class LibrarySearchFrame extends JFrame {

    // Model for Book Record
    public static class Book {
        String id, title, author, category, status;
        public Book(String id, String title, String author, String category, String status) {
            this.id = id; this.title = title; this.author = author;
            this.category = category; this.status = status;
        }
    }

    // UI Components
    private final JTextField txtSearch = new JTextField(18);
    private final JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
        "All Categories", "Computer Science", "Information Tech", "Mathematics", "Electrical", "Mechanical"
    });
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnPrev = new JButton("< Prev");
    private final JButton btnNext = new JButton("Next >");
    private final JButton btnReserve = new JButton("Reserve Selected Book");
    private final JLabel lblStatus = new JLabel("Status: Ready | Enter keywords and click Search");

    private final DefaultTableModel tableModel;
    private final JTable resultsTable;
    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> filteredBooks = new ArrayList<>();

    private int currentPage = 1;
    private static final int PAGE_SIZE = 5;

    public LibrarySearchFrame() {
        super("Campus Central Library - Search & Catalog Explorer");

        // 1. Root Container Layout: BorderLayout
        setLayout(new BorderLayout(8, 8));

        // 2. North Container: BorderLayout (Title + Search Row)
        JPanel northPanel = new JPanel(new BorderLayout(4, 4));
        northPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(21, 101, 192));
        JLabel lblTitle = new JLabel("CAMPUS CENTRAL LIBRARY CATALOGUE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);

        // Search Row Panel: FlowLayout preserves natural component sizes
        JPanel searchRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        searchRowPanel.setBackground(new Color(245, 245, 245));
        searchRowPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel lblQuery = new JLabel("Search Keywords:");
        lblQuery.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnSearch.setBackground(new Color(25, 118, 210));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchRowPanel.add(lblQuery);
        searchRowPanel.add(txtSearch);
        searchRowPanel.add(new JLabel("Category:"));
        searchRowPanel.add(cmbCategory);
        searchRowPanel.add(btnSearch);

        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(searchRowPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // 3. Center Container: Results Table in JScrollPane (absorbs ALL resize expansion)
        String[] columns = {"Book ID", "Title", "Author", "Category", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(24);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results (Expands on Window Resize)"));
        add(scrollPane, BorderLayout.CENTER);

        // 4. South Container: BorderLayout (Buttons + Status)
        JPanel southPanel = new JPanel(new BorderLayout(4, 4));
        southPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 8, 10));

        // Button row: FlowLayout keeps buttons at fixed ergonomic size
        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReserve.setBackground(new Color(46, 125, 50));
        btnReserve.setForeground(Color.WHITE);
        btnReserve.setFont(new Font("Segoe UI", Font.BOLD, 12));

        buttonRowPanel.add(btnPrev);
        buttonRowPanel.add(btnNext);
        buttonRowPanel.add(btnReserve);

        // Status Label
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(33, 33, 33));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        southPanel.add(buttonRowPanel, BorderLayout.NORTH);
        southPanel.add(lblStatus, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Initialize sample catalog data
        loadSampleData();
        performSearch();

        // Register event handlers
        registerHandlers();

        setSize(700, 450);
        setMinimumSize(new Dimension(500, 320));
        setLocationRelativeTo(null);
    }

    private void loadSampleData() {
        allBooks.add(new Book("BK-101", "Core Java Volume I", "Cay S. Horstmann", "Computer Science", "Available"));
        allBooks.add(new Book("BK-102", "Effective Java", "Joshua Bloch", "Computer Science", "Available"));
        allBooks.add(new Book("BK-103", "Design Patterns: Gang of Four", "Erich Gamma et al.", "Computer Science", "Reserved"));
        allBooks.add(new Book("BK-104", "Database System Concepts", "Silberschatz", "Information Tech", "Available"));
        allBooks.add(new Book("BK-105", "Computer Networks", "Andrew Tanenbaum", "Information Tech", "Available"));
        allBooks.add(new Book("BK-106", "Introduction to Algorithms (CLRS)", "Cormen et al.", "Computer Science", "Available"));
        allBooks.add(new Book("BK-107", "Advanced Engineering Mathematics", "Erwin Kreyszig", "Mathematics", "Available"));
        allBooks.add(new Book("BK-108", "Linear Algebra and Its Applications", "Gilbert Strang", "Mathematics", "Reserved"));
        allBooks.add(new Book("BK-109", "Electric Circuits", "James Nilsson", "Electrical", "Available"));
        allBooks.add(new Book("BK-110", "Signals and Systems", "Alan Oppenheim", "Electrical", "Available"));
        allBooks.add(new Book("BK-111", "Engineering Mechanics: Statics", "J.L. Meriam", "Mechanical", "Available"));
        allBooks.add(new Book("BK-112", "Thermodynamics: An Engineering Approach", "Yunus Cengel", "Mechanical", "Available"));
    }

    private void registerHandlers() {
        btnSearch.addActionListener(e -> {
            currentPage = 1;
            performSearch();
        });

        txtSearch.addActionListener(e -> {
            currentPage = 1;
            performSearch();
        });

        cmbCategory.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                currentPage = 1;
                performSearch();
            }
        });

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderTablePage();
            }
        });

        btnNext.addActionListener(e -> {
            int maxPages = (int) Math.ceil((double) filteredBooks.size() / PAGE_SIZE);
            if (currentPage < maxPages) {
                currentPage++;
                renderTablePage();
            }
        });

        btnReserve.addActionListener(e -> handleReservation());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
    }

    private void performSearch() {
        String query = txtSearch.getText().trim().toLowerCase();
        String selectedCategory = (String) cmbCategory.getSelectedItem();

        filteredBooks.clear();
        for (Book b : allBooks) {
            boolean matchesCat = selectedCategory.equals("All Categories") || b.category.equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = query.isEmpty() || 
                                   b.title.toLowerCase().contains(query) || 
                                   b.author.toLowerCase().contains(query) || 
                                   b.id.toLowerCase().contains(query);
            if (matchesCat && matchesQuery) {
                filteredBooks.add(b);
            }
        }
        renderTablePage();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int total = filteredBooks.size();
        int maxPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));

        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, total);

        for (int i = start; i < end; i++) {
            Book b = filteredBooks.get(i);
            tableModel.addRow(new Object[]{b.id, b.title, b.author, b.category, b.status});
        }

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < maxPages);

        lblStatus.setText("Status: Found " + total + " books | Showing Page " + currentPage + " of " + maxPages);
    }

    private void handleReservation() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to reserve.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        if ("Reserved".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Book [" + bookId + "] " + bookTitle + " is already reserved by another student.", "Already Reserved", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update status
        for (Book b : allBooks) {
            if (b.id.equals(bookId)) {
                b.status = "Reserved";
                break;
            }
        }

        tableModel.setValueAt("Reserved", selectedRow, 4);
        JOptionPane.showMessageDialog(this, "Success! You have reserved:\n[" + bookId + "] " + bookTitle, "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchFrame().setVisible(true));
    }
}