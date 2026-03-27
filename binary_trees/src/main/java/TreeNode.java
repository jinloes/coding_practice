

public class TreeNode<T> {
    public final T value;
    public final TreeNode<T> left;
    public final TreeNode<T> right;

    public TreeNode(T value) {
        this(value, null, null);
    }

    public TreeNode(T value, TreeNode<T> left, TreeNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}