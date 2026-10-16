package com.jinloes.practice;

/**
 * Scaling workload for Social Feed. User 0 follows n users who each wrote three posts, so one feed
 * request has to look at every followee. Posts are built outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        Solution feed = new Solution();
        int id = 0;
        for (int user = 1; user <= size; user++) {
            feed.follow(0, user);
            for (int j = 0; j < 3; j++) {
                feed.post(user, id++);
            }
        }
        return new Object[]{feed, id};
    }

    static void run(Object state) {
        Object[] parts = (Object[]) state;
        var result = ((Solution) parts[0]).feed(0);
        int total = (Integer) parts[1];
        if (result.size() != 10 || result.get(0) != total - 1 || result.get(9) != total - 10) {
            throw new IllegalStateException("Unexpected feed");
        }
    }

    static int units(int size) {
        return 1;
    }
}
