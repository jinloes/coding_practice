import org.junit.jupiter.api.BeforeEach;

public abstract class BaseBSTTest {
    protected BSTNode<Integer> root;
    protected BSTNode<Integer> l1a;
    protected BSTNode<Integer> l1b;
    protected BSTNode<Integer> l2a;
    protected BSTNode<Integer> l2b;
    protected BSTNode<Integer> l2c;
    protected BSTNode<Integer> l2d;
    protected BSTNode<Integer> l3a;
    protected BSTNode<Integer> l3b;
    protected BSTNode<Integer> l3d;
    protected BSTNode<Integer> l3f;
    protected BSTNode<Integer> l3h;
    protected BSTNode<Integer> l4i;
    protected BSTNode<Integer> l4j;
    protected BSTNode<Integer> l4k;
    protected BSTNode<Integer> l5l;

    @BeforeEach
    void setUp() {
        root = new BSTNode<>(19);
        l1a = new BSTNode<>(7);
        l1b = new BSTNode<>(43);
        l2a = new BSTNode<>(3);
        l2b = new BSTNode<>(11);
        l2c = new BSTNode<>(23);
        l2d = new BSTNode<>(47);
        l3a = new BSTNode<>(2);
        l3b = new BSTNode<>(5);
        l3d = new BSTNode<>(17);
        l3f = new BSTNode<>(37);
        l3h = new BSTNode<>(53);
        l4i = new BSTNode<>(13);
        l4j = new BSTNode<>(29);
        l4k = new BSTNode<>(41);
        l5l = new BSTNode<>(31);

        root.left = l1a;
        root.right = l1b;

        l1a.left = l3a;
        l1a.left = l3b;

        l1b.left = l2c;
        l1b.right = l2d;

        l2a.left = l3a;
        l2a.right = l3b;

        l2b.right = l3d;

        l2c.right = l3f;

        l2d.right = l3h;

        l3d.left = l4i;

        l3f.left = l4j;
        l3f.right = l4k;

        l4j.right = l5l;
    }
}
