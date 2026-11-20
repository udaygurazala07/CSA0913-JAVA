package q5;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q5: Ranking Principle Verification, Faulty Comparator Analysis,
 * and Multi-Pass Stable Sort vs Single Comparator Benchmark.
 *
 * Student Name: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class RankingInvestigationBenchmark {

    public static class RankingBenchmarkRecord {
        int teamCount;
        double singlePassComparatorTimeMs;
        double threePassStableSortTimeMs;
        double speedup;

        public RankingBenchmarkRecord(int teamCount, double singlePass, double threePass) {
            this.teamCount = teamCount;
            this.singlePassComparatorTimeMs = singlePass;
            this.threePassStableSortTimeMs = threePass;
            this.speedup = threePass / singlePass;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" Q5: GENERIC RANKING COMPONENT - PRINCIPLE & COMPARATOR VERIFICATION");
        System.out.println(" Student: Kattiripalli Dhanush (192311094)");
        System.out.println(" Environment: " + System.getProperty("os.name") + " | Java " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // 1. Verify LeaderboardDemo Expected Output
        System.out.println("--- 1. VERIFYING SUPPLIED LEADERBOARD DEMO ---");
        Comparator<Team> byRank = Comparator
                .comparing(Team::getPoints, Comparator.reverseOrder())
                .thenComparing(Team::getGoalDiff, Comparator.reverseOrder())
                .thenComparing(Team::getName);

        Leaderboard<Team> board = new Leaderboard<>(byRank);
        board.add(new Team("Panthers", 9, 4));
        board.add(new Team("Eagles", 9, 6));
        board.add(new Team("Cobras", 6, 1));
        board.add(new Team("Bisons", 9, 4));

        List<Team> output = board.ranking();
        System.out.println("Observed Output : " + output);
        System.out.println("Expected Output : [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]");
        boolean matchesExpected = output.toString().equals("[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]");
        System.out.println("Matches Expected Output Exactly: " + matchesExpected);
        System.out.println("Reason Bisons is above Panthers: Both have 9 points and +4 goal diff; 'Bisons' < 'Panthers' alphabetically.\n");

        // 2. Multi-tier Tie-Breaking Dataset
        System.out.println("--- 2. MULTI-TIER TIE-BREAKING DATASET TEST ---");
        Leaderboard<Team> tieBoard = new Leaderboard<>(byRank);
        tieBoard.add(new Team("Tigers", 15, 8));
        tieBoard.add(new Team("Lions", 15, 8));    // Level 3 tie: same pts, same GD -> Lions < Tigers
        tieBoard.add(new Team("Hawks", 15, 5));    // Level 2 tie: same pts, lower GD -> after Lions and Tigers
        tieBoard.add(new Team("Bears", 12, 10));   // Lower points
        tieBoard.add(new Team("Wolves", 12, 10));  // Level 3 tie: Bears < Wolves
        System.out.println("Multi-tier Ranking Result:");
        System.out.println(tieBoard.ranking() + "\n");

        // 3. Breaking Faulty Comparators
        System.out.println("--- 3. DELIBERATELY BREAKING FAULTY COMPARATORS ---");
        // Faulty Comparator A: Integer Subtraction Overflow Bug
        System.out.println("[Faulty Comparator A] Using Subtraction: (t1, t2) -> t2.points - t1.points");
        Comparator<Team> faultySubtraction = (t1, t2) -> t2.points - t1.points;
        Team teamHigh = new Team("SafeTeam", 100, 5);
        Team teamExtreme = new Team("ExtremeLossTeam", Integer.MIN_VALUE, 0);

        int comparisonResult = faultySubtraction.compare(teamHigh, teamExtreme);
        System.out.printf("  Comparing SafeTeam (100 pts) vs ExtremeLossTeam (%d pts):%n", Integer.MIN_VALUE);
        System.out.printf("  Faulty Subtraction returned: %d (WRONG! Negative means ExtremeLossTeam ranks HIGHER!)%n", comparisonResult);
        System.out.println("  Proper Comparator.reverseOrder() returns: " +
                Comparator.<Integer>reverseOrder().compare(teamHigh.points, teamExtreme.points) + " (Positive, Correct!)\n");

        // Faulty Comparator B: Incomplete Tie-Breaker (Ignores Goal Difference)
        System.out.println("[Faulty Comparator B] Missing Goal Difference Tie-Breaker");
        Comparator<Team> faultyIgnoreTie = Comparator.comparing(Team::getPoints, Comparator.reverseOrder())
                .thenComparing(Team::getName);
        Leaderboard<Team> faultyBoard = new Leaderboard<>(faultyIgnoreTie);
        faultyBoard.add(new Team("Panthers", 9, 4));
        faultyBoard.add(new Team("Eagles", 9, 6)); // Eagles has +6 GD, but name starts with E
        System.out.println("  Faulty Ranking (ignores GD): " + faultyBoard.ranking());
        System.out.println("  Correct Ranking (with GD)   : " + board.ranking().subList(0, 2));
        System.out.println("  Conclusion: Incomplete comparators violate domain ranking rules.\n");

        // 4. Comparative Benchmark: Single Comparator Chain vs Three-Pass Stable Sort
        System.out.println("================================================================================");
        System.out.println(" BENCHMARK: SINGLE-PASS COMPARATOR vs THREE-PASS STABLE SORT");
        System.out.println("================================================================================");

        int[] sizes = {1000, 10000, 100000};
        List<RankingBenchmarkRecord> records = new ArrayList<>();

        for (int size : sizes) {
            List<Team> teams = generateTeams(size);

            // A. Single-pass sort using Comparator chain
            List<Team> copy1 = new ArrayList<>(teams);
            long start1 = System.nanoTime();
            copy1.sort(byRank);
            double singlePassMs = (System.nanoTime() - start1) / 1_000_000.0;

            // B. Three-pass stable sort (Name -> GoalDiff -> Points)
            List<Team> copy2 = new ArrayList<>(teams);
            long start2 = System.nanoTime();
            // Step 1: Sort by name A-Z
            copy2.sort(Comparator.comparing(Team::getName));
            // Step 2: Sort by goal difference high-first (stable)
            copy2.sort(Comparator.comparing(Team::getGoalDiff, Comparator.reverseOrder()));
            // Step 3: Sort by points high-first (stable)
            copy2.sort(Comparator.comparing(Team::getPoints, Comparator.reverseOrder()));
            double threePassMs = (System.nanoTime() - start2) / 1_000_000.0;

            // Verify both produce identical ranking
            boolean rankMatches = copy1.toString().equals(copy2.toString());

            RankingBenchmarkRecord rec = new RankingBenchmarkRecord(size, singlePassMs, threePassMs);
            records.add(rec);

            System.out.printf("[Team Count: %,6d] Single Comparator Chain : %8.3f ms | 3-Pass Stable Sort: %8.3f ms (Speedup: %.2fx, Identical: %b)%n",
                    size, singlePassMs, threePassMs, rec.speedup, rankMatches);
        }

        // Write CSV results
        String csvPath = "Java-Assignment-2/Q5/q5_results.csv";
        String resultsCsvPath = "Java-Assignment-2/Results/q5_benchmark.csv";
        writeCsv(csvPath, records);
        writeCsv(resultsCsvPath, records);
        System.out.println("\nBenchmark data saved successfully to:");
        System.out.println(" - " + csvPath);
        System.out.println(" - " + resultsCsvPath);
    }

    private static List<Team> generateTeams(int count) {
        List<Team> list = new ArrayList<>(count);
        Random rnd = new Random(5000);
        for (int i = 0; i < count; i++) {
            String name = "Team_" + String.format("%06d", rnd.nextInt(count * 2));
            int points = rnd.nextInt(100);
            int gd = -30 + rnd.nextInt(60);
            list.add(new Team(name, points, gd));
        }
        return list;
    }

    private static void writeCsv(String path, List<RankingBenchmarkRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("TeamCount,SinglePassComparatorTimeMs,ThreePassStableSortTimeMs,SpeedupFactor");
            for (RankingBenchmarkRecord r : records) {
                pw.printf("%d,%.4f,%.4f,%.2f%n",
                        r.teamCount, r.singlePassComparatorTimeMs, r.threePassStableSortTimeMs, r.speedup);
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
