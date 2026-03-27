public class ListNode<T> {
    T data;
    ListNode<T> next;

    public ListNode() {}

    public ListNode(T data) {
        this(data, null);
    }

    public ListNode(T data, ListNode<T> next) {
        this.data = data;
        this.next = next;
    }
}
