package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class LowestCommonAncestorTest extends BaseBinaryTreeTest {

    @Test
    void find() {
        assertThat(LowestCommonAncestor.find(null, child4, child5)).isNull();
        assertThat(LowestCommonAncestor.find(root, child4, child5)).isSameAs(child2);
        assertThat(LowestCommonAncestor.find(root, root, root)).isSameAs(root);
        assertThat(LowestCommonAncestor.find(root, child4, child3)).isSameAs(root);
    }
}