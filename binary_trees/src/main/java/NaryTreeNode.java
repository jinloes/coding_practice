import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NaryTreeNode<V> {
    private final V value;
    private final List<NaryTreeNode<V>> children;

    public NaryTreeNode(V value) {
        this(value, null);
    }

    public NaryTreeNode(V value, List<NaryTreeNode<V>> children) {
        this.value = value;
        this.children = Optional.ofNullable(children)
                .orElseGet(ArrayList::new);
    }

    public void addChild(NaryTreeNode<V> child) {
        children.add(child);
    }

    public List<NaryTreeNode<V>> getChildren() {
        return children;
    }

    public V getValue() {
        return value;
    }
}
