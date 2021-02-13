package com.jinloes.simple_functions.graph;

import org.assertj.core.util.Lists;
import org.jgrapht.alg.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectBuilderTest {
    private ProjectBuilder projectBuilder;

    @Before
    public void setUp() throws Exception {
        projectBuilder = new ProjectBuilder();
    }

    @Test
    public void buildProjects() {
        List<String> projects = Lists.newArrayList("a", "b", "c", "d", "e", "f");
        List<Pair<String, String>> dependencies = Lists.newArrayList(
                Pair.of("a", "d"),
                Pair.of("f", "b"),
                Pair.of("b", "d"),
                Pair.of("f", "a"),
                Pair.of("d", "c")
        );

        List<String> expected = Lists.newArrayList("e", "f", "a", "b", "d", "c");

        assertThat(projectBuilder.buildProjects(projects, dependencies))
                .isEqualTo(expected);
    }

    @Test
    public void buildProjectsDfs() {
        List<String> projects = Lists.newArrayList("a", "b", "c", "d", "e", "f");
        List<Pair<String, String>> dependencies = Lists.newArrayList(
                Pair.of("a", "d"),
                Pair.of("f", "b"),
                Pair.of("b", "d"),
                Pair.of("f", "a"),
                Pair.of("d", "c")
        );

        List<String> expected = Lists.newArrayList("f", "e", "b", "a", "d", "c");

        assertThat(projectBuilder.buildProjectsDfs(projects, dependencies))
                .isEqualTo(expected);
    }

    @Test
    public void buildProjectsDfs2() {
        List<String> projects = Lists.newArrayList("a", "b", "c", "d", "e", "f", "g", "h");
        List<Pair<String, String>> dependencies = Lists.newArrayList(
                Pair.of("f", "c"),
                Pair.of("f", "b"),
                Pair.of("f", "a"),
                Pair.of("c", "a"),
                Pair.of("b", "a"),
                Pair.of("b", "e"),
                Pair.of("b", "h"),
                Pair.of("a", "e"),
                Pair.of("d", "g")
        );

        List<String> expected = Lists.newArrayList("f", "d", "g", "c", "b", "h", "a", "e");

        assertThat(projectBuilder.buildProjectsDfs(projects, dependencies))
                .isEqualTo(expected);
    }
}