package task06.model;

/**
 * Model representing a student participant.
 */
public class Student {
    private int id;
    private String rollNo;
    private String name;
    private String email;
    private String department;
    private int academicYear;

    public Student(int id, String rollNo, String name, String email, String department, int academicYear) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.department = department;
        this.academicYear = academicYear;
    }

    public Student(String rollNo, String name, String email, String department, int academicYear) {
        this(0, rollNo, name, email, department, academicYear);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public int getAcademicYear() { return academicYear; }

    @Override
    public String toString() {
        return name + " (" + rollNo + ")";
    }
}