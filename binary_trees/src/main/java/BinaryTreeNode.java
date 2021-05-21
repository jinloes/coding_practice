import com.google.common.base.MoreObjects;

import java.util.Objects;

public class BinaryTreeNode<T> {
    public T data;
    public BinaryTreeNode<T> left, right;


    public BinaryTreeNode(T data) {
        this(data, null, null);
    }

    public BinaryTreeNode(T data, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTreeNode<?> that = (BinaryTreeNode<?>) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("data", data)
                .toString();
    }
}

