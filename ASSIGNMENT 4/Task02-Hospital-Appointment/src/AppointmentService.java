package task02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service providing in-memory doctor caching and appointment booking logic.
 */
public class AppointmentService {
    private final Map<String, List<Doctor>> doctorCache = new HashMap<>();
    private final Set<String> bookedSlots = new HashSet<>();

    public AppointmentService() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        List<Doctor> cardio = Arrays.asList(
            new Doctor("D101", "Dr. A. Sharma", "Cardiology", "MD, DM (Cardio)"),
            new Doctor("D102", "Dr. R. Venkat", "Cardiology", "FRCP, FACC")
        );
        List<Doctor> ortho = Arrays.asList(
            new Doctor("D201", "Dr. S. Nair", "Orthopaedics", "MS (Ortho), MCh"),
            new Doctor("D202", "Dr. K. Patel", "Orthopaedics", "DNB, FRCS")
        );
        List<Doctor> neuro = Arrays.asList(
            new Doctor("D301", "Dr. M. Joseph", "Neurology", "DM (Neuro)"),
            new Doctor("D302", "Dr. P. Deshmukh", "Neurology", "MD, PhD")
        );
        List<Doctor> pedia = Arrays.asList(
            new Doctor("D401", "Dr. L. Fernandez", "Paediatrics", "MD (Ped), DCH"),
            new Doctor("D402", "Dr. V. Rao", "Paediatrics", "MRCPCH")
        );

        doctorCache.put("Cardiology", cardio);
        doctorCache.put("Orthopaedics", ortho);
        doctorCache.put("Neurology", neuro);
        doctorCache.put("Paediatrics", pedia);
    }

    public List<String> getDepartments() {
        return new ArrayList<>(doctorCache.keySet());
    }

    public List<Doctor> getDoctorsByDepartment(String dept) {
        return doctorCache.getOrDefault(dept, new ArrayList<>());
    }

    public List<String> getAvailableSlots() {
        return Arrays.asList("09:00 AM - 09:30 AM", "09:30 AM - 10:00 AM",
                             "10:00 AM - 10:30 AM", "11:00 AM - 11:30 AM",
                             "02:00 PM - 02:30 PM", "03:00 PM - 03:30 PM");
    }

    public boolean isSlotAvailable(String doctorId, String slot) {
        String key = doctorId + "|" + slot;
        return !bookedSlots.contains(key);
    }

    public String bookAppointment(String patientId, String doctorId, String department, String slot) {
        String key = doctorId + "|" + slot;
        if (bookedSlots.contains(key)) {
            return null; // Slot conflict
        }
        bookedSlots.add(key);
        return "APT-" + (1000 + bookedSlots.size());
    }
}