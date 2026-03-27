

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTreeTest {
    protected NaryTreeNode<Integer> root;
    protected NaryTreeNode<String> nAryRoot;

    @BeforeEach
    public void setUp() {

        /*
         * <pre>
         *     1
         *    2 3
         *   4 5
         * </pre>
         */
        NaryTreeNode<Integer> child5 = new NaryTreeNode<>(5);
        NaryTreeNode<Integer> child4 = new NaryTreeNode<>(4);
        NaryTreeNode<Integer> child3 = new NaryTreeNode<>(3);
        NaryTreeNode<Integer> child2 = new NaryTreeNode<>(2, Lists.newArrayList(child4, child5));
        root = new NaryTreeNode<>(1, Lists.newArrayList(child2, child3));

        /*
         * <pre>
         *      A
         *    B   C
         *  D E F
         * </pre>
         */
        NaryTreeNode<String> childF = new NaryTreeNode<>("F");
        NaryTreeNode<String> childE = new NaryTreeNode<>("E");
        NaryTreeNode<String> childD = new NaryTreeNode<>("D");
        NaryTreeNode<String> childC = new NaryTreeNode<>("C");
        NaryTreeNode<String> childB = new NaryTreeNode<>("B", Lists.newArrayList(childD, childE, childF));
        nAryRoot = new NaryTreeNode<>("A", Lists.newArrayList(childB, childC));
    }
}
