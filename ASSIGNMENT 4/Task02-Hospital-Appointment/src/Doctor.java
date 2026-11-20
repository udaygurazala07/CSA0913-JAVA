package task02;

/**
 * Model class representing a medical specialist.
 */
public class Doctor {
    private final String id;
    private final String name;
    private final String department;
    private final String qualification;

    public Doctor(String id, String name, String department, String qualification) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.qualification = qualification;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getQualification() { return qualification; }

    @Override
    public String toString() {
        return name + " (" + qualification + ")";
    }
}