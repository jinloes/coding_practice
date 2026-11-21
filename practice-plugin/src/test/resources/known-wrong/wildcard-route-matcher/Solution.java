package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private record Route(String[] segments, String handler) {
    }

    private final List<Route> routes = new ArrayList<>();

    public Solution() {
    }

    public void register(String pattern, String handler) {
        String[] segments = "/".equals(pattern) ? new String[0] : pattern.substring(1).split("/");
        routes.add(new Route(segments, handler));
    }

    public String match(String path) {
        String[] target = "/".equals(path) ? new String[0] : path.substring(1).split("/");
        for (Route route : routes) {
            if (route.segments.length != target.length) {
                continue;
            }
            boolean ok = true;
            for (int i = 0; i < target.length; i++) {
                if (!"*".equals(route.segments[i]) && !route.segments[i].equals(target[i])) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return route.handler;
            }
        }
        return "";
    }
}
