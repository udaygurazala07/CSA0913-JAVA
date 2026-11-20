package q7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Generates the test datasets for Question 7:
 * 1. Hand-checked dataset: 20 files (14 valid, 3 corrupt, 3 missing)
 * 2. Full semester workload: 10,000 files (9,500 valid, 300 corrupt, 200 missing)
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class DatasetGenerator {

    public static void generateDataset(String dirPath, int totalFiles, int corruptFiles, int missingFiles) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Random rng = new Random(42); // fixed seed for exact reproducibility
        int validFiles = totalFiles - corruptFiles - missingFiles;

        System.out.printf("[GENERATOR] Creating %d total file entries in %s (%d valid, %d corrupt, %d missing)...\n",
                totalFiles, dirPath, validFiles, corruptFiles, missingFiles);

        // 1. Generate Valid Files
        for (int i = 1; i <= validFiles; i++) {
            File f = new File(dir, String.format("student_%05d.txt", i));
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(String.format("STUDENT_ID: STU%05d\n", i));
                bw.write("COURSE: CS301_JAVA\n");
                int m1 = 40 + rng.nextInt(61);
                int m2 = 40 + rng.nextInt(61);
                int m3 = 40 + rng.nextInt(61);
                bw.write(String.format("MARKS: %d, %d, %d\n", m1, m2, m3));
            }
        }

        // 2. Generate Corrupt Files
        for (int i = 1; i <= corruptFiles; i++) {
            int fileNum = validFiles + i;
            File f = new File(dir, String.format("student_%05d.txt", fileNum));
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(String.format("STUDENT_ID: STU%05d\n", fileNum));
                bw.write("COURSE: CS301_JAVA\n");
                if (i % 3 == 0) {
                    bw.write("MARKS: NaN, CorruptScore, 85\n"); // Non-numeric
                } else if (i % 3 == 1) {
                    bw.write("MARKS: 85, -15, 90\n"); // Out of bounds negative
                } else {
                    bw.write("MARKS: 95, 140, 88\n"); // Out of bounds > 100
                }
            }
        }

        // 3. Missing files: simply not created on disk, but their filenames are listed in manifest!
        System.out.println("[GENERATOR] Dataset generated successfully.");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Generating datasets for Q7 Semester Result Processor...");
        // 1. Hand-checked set (20 files: 14 valid, 3 corrupt, 3 missing)
        generateDataset("data_hand_checked", 20, 3, 3);

        // 2. Full 10,000 files workload (9,500 valid, 300 corrupt, 200 missing)
        generateDataset("data_10k_workload", 10000, 300, 200);
        System.out.println("All datasets ready on disk.");
    }
}
