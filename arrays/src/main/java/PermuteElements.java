/**
 * Given an array A of n elements and a permutation P, apply P to A.
 */
public class PermuteElements {

    public static void permute(int[] elements, int[] permutation) {
        boolean[] visited = new boolean[elements.length];

        for (int i = 0; i < elements.length; i++) {
            // start at idx
            int nextIdx = i;

            // loop until we visited all the permutation indices
            while (!visited[permutation[nextIdx]]) {
                int newIdx = permutation[nextIdx];
                // mark idx as visited
                visited[newIdx] = true;

                // move element to new idx
                int tmp = elements[newIdx];
                elements[newIdx] = elements[i];
                elements[i] = tmp;

                // move next idx
                nextIdx = newIdx;
            }
        }
    }

}
