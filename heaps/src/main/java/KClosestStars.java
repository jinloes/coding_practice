import com.google.common.base.MoreObjects;

import java.util.*;

/**
 * How would you compute the k stars which are closest to Earth?
 */
public class KClosestStars {
    public static List<Star> get(int k, List<Star> stars) {
        PriorityQueue<Star> maxHeap = new PriorityQueue<>(k, Comparator.<Star>naturalOrder().reversed());

        for (Star star : stars) {
            if (maxHeap.size() < k) {
                maxHeap.add(star);
            } else if (maxHeap.peek().distance() > star.distance()) {
                maxHeap.add(star);
                maxHeap.poll();
            }
        }

        List<Star> result = new LinkedList<>(maxHeap);
        Collections.sort(result);

        return result;
    }

    public static class Star implements Comparable<Star> {
        private double x;
        private double y;
        private double z;

        public Star(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distance() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        @Override
        public int compareTo(Star rhs) {
            return Double.compare(this.distance(), rhs.distance());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Star star = (Star) o;
            return Double.compare(star.x, x) == 0 &&
                    Double.compare(star.y, y) == 0 &&
                    Double.compare(star.z, z) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("x", x)
                    .add("y", y)
                    .add("z", z)
                    .toString();
        }
    }
}
