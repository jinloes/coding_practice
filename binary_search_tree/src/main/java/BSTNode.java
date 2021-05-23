public class BSTNode<T> {
    public T data;
    public BSTNode<T> left;
    public BSTNode<T> right;

    public BSTNode(T data) {
        this(data, null, null);

    }

    public BSTNode(T data, BSTNode<T> left, BSTNode<T> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }
}
