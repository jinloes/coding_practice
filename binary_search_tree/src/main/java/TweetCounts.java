import java.util.*;

public class TweetCounts {
    private Map<String, TreeSet<Node>> tweetToTime;
    private int nonce;

    public TweetCounts() {
        tweetToTime = new HashMap<>();
        nonce = 0;
    }

    public void recordTweet(String tweetName, int time) {
        tweetToTime.putIfAbsent(tweetName, new TreeSet<>());
        tweetToTime.get(tweetName).add(new Node(time, nonce));
        nonce++;
    }

    public List<Integer> getTweetCountsPerFrequency(String freq, String tweetName, int startTime, int endTime) {
        int delta = 1;

        if (freq.equals("minute")) {
            delta = 60;
        } else if (freq.equals("hour")) {
            delta = 60 * 60;
        } else {
            delta = 60 * 60 * 24;
        }

        TreeSet<Node> tweetTimes = tweetToTime.get(tweetName);
        List<Integer> result = new LinkedList<>();

        while (startTime < endTime + 1) {
            int end = Math.min(startTime + delta - 1, endTime + 1);
            Set<Node> slice = tweetTimes.subSet(new Node(startTime, 0), true, new Node(end, nonce), false);
            result.add(slice.size());

            startTime += delta;
        }

        return result;
    }

    private static class Node implements Comparable<Node> {
        int time;

        int nonce;

        Node(int time, int nonce) {
            this.time = time;
            this.nonce = nonce;
        }

        @Override
        public int compareTo(Node o) {
            if (time != o.time) {
                return time - o.time;
            } else {
                return nonce - o.nonce;
            }
        }
    }
}
