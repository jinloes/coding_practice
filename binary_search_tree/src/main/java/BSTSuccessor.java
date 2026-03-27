

import java.util.Objects;

public class BSTSuccessor {

    public BSTNode<Integer> getSuccessor(BSTNode<Integer> current) {
        if (current == null) {
            return null;
        }

        if (current.right != null) {
            BSTNode<Integer> tmp = current.right;
            while (tmp.left != null) {
                tmp = tmp.left;
            }
            return tmp;
        } else {
            BSTNode<Integer> parent = current.parent;
            BSTNode<Integer> child = current;
            while (parent != null && !Objects.equals(parent.left, child)) {
                child = parent;
                parent = parent.parent;
            }
            return parent;
        }
    }
}
