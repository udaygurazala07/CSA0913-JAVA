package q4;

import java.util.Objects;

/**
 * Domain model representing a pharmaceutical item in hospital inventory.
 *
 * Student Name: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class Medicine implements Comparable<Medicine> {
    private final String code;
    private final String name;
    private final String category;
    private int stock;
    private final double unitPrice;

    public Medicine(String code, String name, String category, int stock, double unitPrice) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.unitPrice = unitPrice;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }
    public double getUnitPrice() { return unitPrice; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public int compareTo(Medicine other) {
        return this.code.compareTo(other.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(code, medicine.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-22s | Qty: %5d | Price: $%.2f", code, name, stock, unitPrice);
    }
}
