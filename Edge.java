public class Edge {
    public final Station to;
    public final double distanceKm; // primary distance
    public final double timeMin;    // travel time in minutes
    public final String line;       // line name (e.g., "Red", "Blue")

    public Edge(Station to, double distanceKm, double timeMin, String line) {
        this.to = to;
        this.distanceKm = distanceKm;
        this.timeMin = timeMin;
        this.line = line;
    }

    @Override
    public String toString() {
        return String.format("-> %s | line=%s | dist=%.2fkm | time=%.1fmin", to.name(), line, distanceKm, timeMin);
    }
}
