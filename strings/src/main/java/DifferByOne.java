import java.util.HashMap;
import java.util.Map;

public class DifferByOne {
    public boolean differByOne(String[] dict) {
        TrieNode root = new TrieNode();
        for (String s : dict) {
            TrieNode current = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                current.children.putIfAbsent(c, new TrieNode());

                current = current.children.get(c);
                current.count++;
            }
            current.isWord = true;
        }

        for (String s : dict) {
            TrieNode current = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                for (Character key : current.children.keySet()) {
                    if (c == key) {
                        continue;
                    }
                    TrieNode node = current.children.get(key);
                    if (innerSearch(node, s, i + 1)) {
                        return true;
                    }
                }

                if (current.children.get(c).count <= 1) {
                    break;
                }
                current = current.children.get(c);
            }
        }
        return false;
    }

    private boolean innerSearch(TrieNode root, String s, int idx) {
        TrieNode current = root;
        for (int i = idx; i < s.length(); i++) {
            char c = s.charAt(i);
            current = current.children.get(c);
            if (current == null) {
                return false;
            }
        }

        return current.isWord;

    }

    public class TrieNode {
        int count;
        boolean isWord;
        Map<Character, TrieNode> children;

        public TrieNode() {
            this.count = 0;
            this.isWord = false;
            this.children = new HashMap<>();
        }
    }
}