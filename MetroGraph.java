import java.util.*;

public class MetroGraph {
    private final Map<String, Station> stationsById = new HashMap<>();
    private final Map<Station, List<Edge>> adj = new HashMap<>();

    // Create or get station by id
    public Station addStation(String id, String name) {
        if (stationsById.containsKey(id)) return stationsById.get(id);
        Station s = new Station(id, name);
        stationsById.put(id, s);
        adj.put(s, new ArrayList<>());
        return s;
    }

    public boolean removeStation(String id) {
        Station s = stationsById.remove(id);
        if (s == null) return false;
        adj.remove(s);
        // remove edges to s
        for (List<Edge> edges : adj.values()) {
            edges.removeIf(e -> e.to.equals(s));
        }
        return true;
    }

    public boolean addRoute(String fromId, String toId, double distanceKm, double timeMin, String line, boolean bidirectional) {
        Station from = stationsById.get(fromId);
        Station to = stationsById.get(toId);
        if (from == null || to == null) return false;
        adj.get(from).add(new Edge(to, distanceKm, timeMin, line));
        if (bidirectional) adj.get(to).add(new Edge(from, distanceKm, timeMin, line));
        return true;
    }

    public boolean removeRoute(String fromId, String toId, String line) {
        Station from = stationsById.get(fromId);
        Station to = stationsById.get(toId);
        if (from == null || to == null) return false;
        boolean removed = adj.get(from).removeIf(e -> e.to.equals(to) && e.line.equals(line));
        // also try reverse
        removed |= adj.get(to).removeIf(e -> e.to.equals(from) && e.line.equals(line));
        return removed;
    }

    public boolean updateRoute(String fromId, String toId, String line, Double newDistance, Double newTime) {
        Station from = stationsById.get(fromId);
        Station to = stationsById.get(toId);
        if (from == null || to == null) return false;
        boolean updated = false;
        List<Edge> edges = adj.get(from);
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.to.equals(to) && e.line.equals(line)) {
                double d = newDistance == null ? e.distanceKm : newDistance;
                double t = newTime == null ? e.timeMin : newTime;
                edges.set(i, new Edge(to, d, t, line));
                updated = true;
            }
        }
        // also update reverse if exists (assumes symmetrical)
        edges = adj.get(to);
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.to.equals(from) && e.line.equals(line)) {
                double d = newDistance == null ? e.distanceKm : newDistance;
                double t = newTime == null ? e.timeMin : newTime;
                edges.set(i, new Edge(from, d, t, line));
                updated = true;
            }
        }
        return updated;
    }

    public Station getStationById(String id) {
        return stationsById.get(id);
    }

    public Station getStationByName(String name) {
        for (Station s : stationsById.values()) {
            if (s.name().equalsIgnoreCase(name) || s.id().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    public List<Edge> neighbors(Station s) {
        return adj.getOrDefault(s, Collections.emptyList());
    }

    public Collection<Station> allStations() {
        return stationsById.values();
    }

    public void printGraph() {
        System.out.println("Stations and routes:");
        for (Station s : stationsById.values()) {
            System.out.println(s + ":");
            for (Edge e : adj.get(s)) System.out.println("  " + e);
        }
    }
}
