package com.jinloes.simple_functions.graph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Given a list of projects and a list of dependencies. Find a build order that will allow the projects to be built.
 */
public class ProjectBuilder {

    // Create and adjacency map of incoming edges
    // Select the projects with no incoming dependencies and mark them to be built
    // If a project had been marked to be built remove it from the map
    // Remove the projects from the other projects dependency list
    // If the map is empty at the end all projects have been successfully built
    public List<String> buildProjects(List<String> projects, List<Pair<String, String>> dependencies) {
        // Map projects to those that depend on it
        Map<String, Set<String>> dependencyMap = new HashMap<>();
        for (String project : projects) {
            dependencyMap.put(project, new HashSet<>());
        }

        for (Pair<String, String> dependencyPair : dependencies) {
            String dependency = dependencyPair.getSecond();
            String depender = dependencyPair.getFirst();

            dependencyMap.compute(dependency, (s, strings) -> {
                if (strings == null) {
                    strings = new HashSet<>();
                } else {
                    strings.add(depender);
                }
                return strings;
            });
        }

        List<String> buildOrder = new ArrayList<>();

        List<String> startPoints = dependencyMap.entrySet()
                .stream()
                .filter(stringListEntry -> stringListEntry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        while (!startPoints.isEmpty()) {
            buildOrder.addAll(startPoints);
            startPoints.forEach(dependencyMap::remove);

            Iterator<Map.Entry<String, Set<String>>> iterator = dependencyMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Set<String>> incomingEdgesEntry = iterator.next();
                Set<String> incomingEdges = incomingEdgesEntry.getValue();
                incomingEdges.removeAll(startPoints);
            }

            startPoints = dependencyMap.entrySet()
                    .stream()
                    .filter(stringListEntry -> stringListEntry.getValue().isEmpty())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        }
        return buildOrder;
    }

    public List<String> buildProjectsDfs(List<String> projects, List<Pair<String, String>> dependencies) {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        projects.forEach(graph::addVertex);
        dependencies.forEach(dependency -> graph.addEdge(dependency.getFirst(), dependency.getSecond()));

        Set<String> visited = new HashSet<>();

        Stack<String> path = new Stack<>();
        projects.forEach(project -> {
            Set<String> visiting = new HashSet<>();
            doDfs(graph, path, visited, visiting, project);

        });

        List<String> result = new ArrayList<>();
        while (!path.isEmpty()) {
            result.add(path.pop());
        }
        return result;
    }

    private void doDfs(Graph<String, DefaultEdge> graph, Stack<String> path, Set<String> visited, Set<String> visiting,
                       String vertex) {
        if (visiting.contains(vertex)) {
            throw new IllegalStateException("Cycle detected");
        }
        if (visited.contains(vertex)) {
            return;
        }
        visiting.add(vertex);
        List<String> successors = Graphs.successorListOf(graph, vertex);

        successors.forEach(successor -> doDfs(graph, path, visited, visiting, successor));
        visited.remove(vertex);
        visited.add(vertex);
        path.push(vertex);
    }
}
