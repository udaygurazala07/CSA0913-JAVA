package task02;
import java.util.List;
public class Task02Test {
    public static void main(String[] args) {
        System.out.println("=== TASK 2: HOSPITAL APPOINTMENT TEST SUITE ===");
        AppointmentService service = new AppointmentService();

        // Test 1: Verify Department & Doctor Loading
        System.out.println("Departments: " + service.getDepartments());
        assert service.getDepartments().size() == 4 : "Expected 4 departments";
        System.out.println("[PASS] Test 1: Departments verified.");

        // Test 2: Verify Dynamic Doctor Filtering
        List<Doctor> cardioDocs = service.getDoctorsByDepartment("Cardiology");
        System.out.println("Cardiology Doctors: " + cardioDocs);
        assert cardioDocs.size() == 2 : "Expected 2 cardiologists";
        System.out.println("[PASS] Test 2: Dynamic doctor filtering verified.");

        // Test 3: Appointment Booking
        String bookingId = service.bookAppointment("PAT-1001", cardioDocs.get(0).getId(), "Cardiology", "09:00 AM - 09:30 AM");
        System.out.println("Booking created: " + bookingId);
        assert bookingId != null : "Booking should succeed";
        System.out.println("[PASS] Test 3: Appointment booked successfully.");

        // Test 4: Conflict Rejection (Same doctor, same slot)
        String conflictId = service.bookAppointment("PAT-1002", cardioDocs.get(0).getId(), "Cardiology", "09:00 AM - 09:30 AM");
        System.out.println("Conflict booking attempt returned: " + conflictId);
        assert conflictId == null : "Slot conflict should have returned null";
        System.out.println("[PASS] Test 4: Double-booking prevention verified.");

        System.out.println("=== ALL TASK 2 TESTS COMPLETED SUCCESSFULLY ===");
    }
}