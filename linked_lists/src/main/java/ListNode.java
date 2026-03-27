import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListNode)) return false;
        ListNode<?> that = (ListNode<?>) o;
        return Objects.equals(data, that.data) && Objects.equals(next, that.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, next);
    }
}
