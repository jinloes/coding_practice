import com.google.common.base.MoreObjects;

import java.util.Objects;

public class BinaryTreeNode<T> {
    public BinaryTreeNode<T> left;
    public BinaryTreeNode<T> right;
    public T val;


    public BinaryTreeNode(T val) {
        this(null, null, val);
    }

    public BinaryTreeNode(BinaryTreeNode<T> left, BinaryTreeNode<T> right, T val) {
        this.left = left;
        this.right = right;
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTreeNode<?> that = (BinaryTreeNode<?>) o;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("val", val)
                .toString();
    }
}
