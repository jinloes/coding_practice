import io.vavr.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Write a program which takes as input a file containing test scores and returns the student who has the maximum
 * score averaged across his or her top three tests. If the student has fewer than three test scores, ignore that
 * student.
 */
public class TopScoreAverage {
    public static String findStudentWithHighestBestOfThreeScores(List<Tuple2<String, Integer>> nameScoreData) {
        int numScores = 3;

        Map<String, PriorityQueue<Integer>> topStudentScores = new HashMap<>();

        String topStudent = "";
        double topAverage = Double.MIN_VALUE;

        for (Tuple2<String, Integer> record : nameScoreData) {
            String name = record._1();
            int score = record._2();


            topStudentScores.putIfAbsent(name, new PriorityQueue<>());
            PriorityQueue<Integer> topScores = topStudentScores.get(name);

            topScores.add(score);
            if (topScores.size() > numScores) {
                topScores.poll();
            }

            if (topScores.size() == 3) {
                double avg = topScores.stream()
                        .mapToInt(val -> val).average()
                        .orElse(0);

                if (avg > topAverage) {
                    topStudent = name;
                    topAverage = avg;
                }
            }
        }

        return topStudent;
    }

}
