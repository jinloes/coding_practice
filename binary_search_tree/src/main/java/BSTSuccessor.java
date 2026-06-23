public class BSTSuccessor {

    public BSTNode<Integer> getSuccessor(BSTNode<Integer> current) {
        if (current == null) {
            return null;
        }

        if (current.right != null) {
            BSTNode<Integer> successor = current.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            return successor;
        }

        BSTNode<Integer> parent = current.parent;
        BSTNode<Integer> child = current;
        while (parent != null && parent.left != child) {
            child = parent;
            parent = parent.parent;
        }

        return parent;
    }
}
