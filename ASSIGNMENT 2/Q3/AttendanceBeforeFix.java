package q3;

import java.util.HashSet;
import java.util.Set;

/**
 * Attendance program BEFORE completing TODO 1.
 * Demonstrates the bug caused by not overriding hashCode().
 */
class StudentUnfixed {
    private final String regNo;
    private final String name;

    StudentUnfixed(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentUnfixed)) return false;
        return regNo.equals(((StudentUnfixed) o).regNo);
    }

    // hashCode() is NOT overridden (TODO 1 missing)

    @Override
    public String toString() {
        return regNo + " " + name;
    }
}

public class AttendanceBeforeFix {
    public static void main(String[] args) {
        Set<StudentUnfixed> present = new HashSet<>();
        present.add(new StudentUnfixed("192311001", "Arun")); // scanned at Gate 1
        present.add(new StudentUnfixed("192311002", "Divya"));
        present.add(new StudentUnfixed("192311001", "Arun")); // scanned again at Gate 3
        System.out.println("Output before completing TODO 1:");
        System.out.println("Unique students: " + present.size());
    }
}
