import java.util.*;

public class RouteFinder {
    public static class RouteResult {
        public final List<Station> path;
        public final double totalDistanceKm;
        public final double totalTimeMin;
        public final int interchanges;

        public RouteResult(List<Station> path, double totalDistanceKm, double totalTimeMin, int interchanges) {
            this.path = path;
            this.totalDistanceKm = totalDistanceKm;
            this.totalTimeMin = totalTimeMin;
            this.interchanges = interchanges;
        }
    }

    private static class State {
        Station station;
        double cost; // cost by chosen metric
        int interchanges;
        String currentLine;

        State(Station s, double cost, int interchanges, String currentLine) {
            this.station = s;
            this.cost = cost;
            this.interchanges = interchanges;
            this.currentLine = currentLine;
        }
    }

    private static class PrevInfo {
        Station prev;
        String lineUsed;
        PrevInfo(Station prev, String lineUsed) { this.prev = prev; this.lineUsed = lineUsed; }
    }

    /**
     * Find shortest path from srcName to destName using metric = "time" or "distance".
     * Minimizes primary metric, and among equal-primary solutions minimizes interchanges.
     */
    public static RouteResult findShortest(MetroGraph graph, String srcName, String destName, String metric) {
        Station src = graph.getStationByName(srcName);
        Station dest = graph.getStationByName(destName);
        if (src == null || dest == null) return null;

        Comparator<State> cmp = (a, b) -> {
            if (a.cost != b.cost) return Double.compare(a.cost, b.cost);
            return Integer.compare(a.interchanges, b.interchanges);
        };
        PriorityQueue<State> pq = new PriorityQueue<>(cmp);

        Map<Station, Double> bestCost = new HashMap<>();
        Map<Station, Integer> bestInter = new HashMap<>();
        Map<Station, String> bestLine = new HashMap<>();
        Map<Station, PrevInfo> prev = new HashMap<>();

        for (Station s : graph.allStations()) {
            bestCost.put(s, Double.POSITIVE_INFINITY);
            bestInter.put(s, Integer.MAX_VALUE);
            bestLine.put(s, null);
        }

        bestCost.put(src, 0.0);
        bestInter.put(src, 0);
        bestLine.put(src, null);
        pq.add(new State(src, 0.0, 0, null));

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            if (cur.cost > bestCost.get(cur.station) + 1e-9) continue;
            if (cur.cost == bestCost.get(cur.station) && cur.interchanges > bestInter.get(cur.station)) continue;
            if (cur.station.equals(dest)) break;

            for (Edge e : graph.neighbors(cur.station)) {
                Station nxt = e.to;
                double w = metric.equalsIgnoreCase("distance") ? e.distanceKm : e.timeMin;
                double newCost = cur.cost + w;
                int newInter = cur.interchanges + ((cur.currentLine == null || cur.currentLine.equals(e.line)) ? 0 : 1);

                double bestC = bestCost.get(nxt);
                int bestI = bestInter.get(nxt);

                boolean better = false;
                if (newCost + 1e-9 < bestC) better = true;
                else if (Math.abs(newCost - bestC) < 1e-9 && newInter < bestI) better = true;

                if (better) {
                    bestCost.put(nxt, newCost);
                    bestInter.put(nxt, newInter);
                    bestLine.put(nxt, e.line);
                    prev.put(nxt, new PrevInfo(cur.station, e.line));
                    pq.add(new State(nxt, newCost, newInter, e.line));
                }
            }
        }

        if (!prev.containsKey(dest) && !src.equals(dest)) {
            // no path
            return null;
        }

        // Reconstruct path
        LinkedList<Station> path = new LinkedList<>();
        Station cur = dest;
        path.addFirst(cur);
        while (!cur.equals(src)) {
            PrevInfo p = prev.get(cur);
            if (p == null) break; // reached src
            cur = p.prev;
            path.addFirst(cur);
        }

        // compute total distance and time and interchanges by walking edges and using stored line decisions
        double totalDistance = 0.0;
        double totalTime = 0.0;
        int interchanges = 0;
        String prevLine = null;
        for (int i = 0; i < path.size() - 1; i++) {
            Station a = path.get(i);
            Station b = path.get(i + 1);
            // find edge used (use line info stored in prev of b)
            PrevInfo pi = prev.get(b);
            String usedLine = (pi != null) ? pi.lineUsed : null;
            Edge usedEdge = null;
            for (Edge e : graph.neighbors(a)) {
                if (e.to.equals(b) && (usedLine == null || e.line.equals(usedLine))) {
                    usedEdge = e;
                    break;
                }
            }
            if (usedEdge == null) {
                // fallback: pick first edge to b
                for (Edge e : graph.neighbors(a)) {
                    if (e.to.equals(b)) { usedEdge = e; break; }
                }
            }
            if (usedEdge != null) {
                totalDistance += usedEdge.distanceKm;
                totalTime += usedEdge.timeMin;
                if (prevLine != null && !prevLine.equals(usedEdge.line)) interchanges++;
                prevLine = usedEdge.line;
            }
        }

        return new RouteResult(path, totalDistance, totalTime, interchanges);
    }
}
