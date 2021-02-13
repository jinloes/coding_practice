package com.jinloes.simple_functions.tree;

import com.scalified.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Given a binary tree, return a linked list of all the nodes at each depth.
 */
public class ListOfDepths {

    public List<List<TreeNode<Integer>>> nodesAtDepth(TreeNode<Integer> root) {
        List<List<TreeNode<Integer>>> result = new ArrayList<>();
        nodesAtDepth(root, 0, result);

        return result;

    }

    private void nodesAtDepth(TreeNode<Integer> root, int level, List<List<TreeNode<Integer>>> result) {
        if (result.size() <= level) {
            result.add(new ArrayList<>());
        }
        List<TreeNode<Integer>> nodes = result.get(level);
        nodes.add(root);
        root.subtrees()
                .forEach((Consumer<TreeNode<Integer>>) treeNodes -> nodesAtDepth(treeNodes, level + 1, result));

    }
}
