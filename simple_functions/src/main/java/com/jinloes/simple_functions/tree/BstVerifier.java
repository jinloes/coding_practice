package com.jinloes.simple_functions.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class BstVerifier {
    public static boolean verify(Node<Integer> root) {
        List<Node<Integer>> elements = new ArrayList<>();
        addElements(root, elements);
        elements.forEach(element -> System.out.println(element.getValue()));
        if (elements.size() == 1) {
            return true;
        }
        return IntStream.range(0, elements.size() - 1)
                .allMatch(i -> elements.get(i).getValue().compareTo(elements.get(i + 1).getValue()) <= 0);
    }

    private static void addElements(Node<Integer> root, List<Node<Integer>> elements) {
        if (root == null) {
            return;
        }
        elements.add(root);
        addElements(root.getLeft(), elements);
        addElements(root.getRight(), elements);
    }
}
