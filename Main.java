import java.util.*;

public class Main {
    private static final MetroGraph graph = new MetroGraph();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        buildSampleNetwork();
        System.out.println("Metro Route Finder (Console)");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": findRoute(); break;
                case "2": addStation(); break;
                case "3": removeStation(); break;
                case "4": addRoute(); break;
                case "5": removeRoute(); break;
                case "6": updateRoute(); break;
                case "7": graph.printGraph(); break;
                case "8": runTests(); break;
                case "9": running = false; break;
                default: System.out.println("Invalid choice. Try again."); break;
            }
            System.out.println();
        }
        System.out.println("Bye!");
    }

    private static void printMenu() {
        System.out.println("Choose:");
        System.out.println("1) Find optimal route (time/distance, minimal interchanges)");
        System.out.println("2) Add station");
        System.out.println("3) Remove station");
        System.out.println("4) Add route");
        System.out.println("5) Remove route");
        System.out.println("6) Update route (distance/time)");
        System.out.println("7) Print graph");
        System.out.println("8) Run sample tests");
        System.out.println("9) Exit");
        System.out.print("> ");
    }

    private static void findRoute() {
        System.out.print("From (name or id): ");
        String src = sc.nextLine().trim();
        System.out.print("To (name or id): ");
        String dst = sc.nextLine().trim();
        System.out.print("Metric (time/distance) [time]: ");
        String metric = sc.nextLine().trim();
        if (metric.isEmpty()) metric = "time";
        RouteFinder.RouteResult res = RouteFinder.findShortest(graph, src, dst, metric);
        if (res == null) {
            System.out.println("No path found between those stations.");
            return;
        }
        System.out.println("Route:");
        for (int i = 0; i < res.path.size(); i++) {
            System.out.print(res.path.get(i).name());
            if (i + 1 < res.path.size()) System.out.print(" -> ");
        }
        System.out.println();
        System.out.printf("Total distance: %.2f km | Total time: %.1f min | Interchanges: %d%n",
                res.totalDistanceKm, res.totalTimeMin, res.interchanges);
    }

    private static void addStation() {
        System.out.print("Station id (unique): ");
        String id = sc.nextLine().trim();
        System.out.print("Station name: ");
        String name = sc.nextLine().trim();
        Station s = graph.addStation(id, name);
        System.out.println("Added " + s);
    }

    private static void removeStation() {
        System.out.print("Station id: ");
        String id = sc.nextLine().trim();
        boolean ok = graph.removeStation(id);
        System.out.println(ok ? "Removed." : "Station not found.");
    }

    private static void addRoute() {
        System.out.print("From station id: ");
        String a = sc.nextLine().trim();
        System.out.print("To station id: ");
        String b = sc.nextLine().trim();
        System.out.print("Distance (km): ");
        double d = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Time (min): ");
        double t = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Line name: ");
        String line = sc.nextLine().trim();
        System.out.print("Bidirectional? (y/n) [y]: ");
        String bdir = sc.nextLine().trim();
        boolean bidir = bdir.isEmpty() || bdir.equalsIgnoreCase("y");
        boolean ok = graph.addRoute(a, b, d, t, line, bidir);
        System.out.println(ok ? "Route added." : "Failed — check station ids.");
    }

    private static void removeRoute() {
        System.out.print("From station id: ");
        String a = sc.nextLine().trim();
        System.out.print("To station id: ");
        String b = sc.nextLine().trim();
        System.out.print("Line name: ");
        String line = sc.nextLine().trim();
        boolean ok = graph.removeRoute(a, b, line);
        System.out.println(ok ? "Route removed (if existed)." : "No such route/station.");
    }

    private static void updateRoute() {
        System.out.print("From station id: ");
        String a = sc.nextLine().trim();
        System.out.print("To station id: ");
        String b = sc.nextLine().trim();
        System.out.print("Line name: ");
        String line = sc.nextLine().trim();
        System.out.print("New distance (km) or leave blank: ");
        String ds = sc.nextLine().trim();
        System.out.print("New time (min) or leave blank: ");
        String ts = sc.nextLine().trim();
        Double nd = ds.isEmpty() ? null : Double.parseDouble(ds);
        Double nt = ts.isEmpty() ? null : Double.parseDouble(ts);
        boolean ok = graph.updateRoute(a, b, line, nd, nt);
        System.out.println(ok ? "Route updated." : "Failed to update (check ids/line).");
    }

    private static void buildSampleNetwork() {
        // stations
        graph.addStation("A", "Central");
        graph.addStation("B", "Park Street");
        graph.addStation("C", "Lake View");
        graph.addStation("D", "Museum");
        graph.addStation("E", "Airport");
        graph.addStation("F", "Riverside");

        // Red Line edges
        graph.addRoute("A", "B", 2.0, 4.0, "Red", true);
        graph.addRoute("B", "C", 3.0, 6.0, "Red", true);
        graph.addRoute("C", "D", 2.5, 5.0, "Red", true);

        // Blue Line edges
        graph.addRoute("A", "D", 6.0, 10.0, "Blue", true);
        graph.addRoute("D", "E", 8.0, 12.0, "Blue", true);

        // Green Line edges
        graph.addRoute("B", "F", 4.0, 7.0, "Green", true);
        graph.addRoute("F", "E", 6.0, 11.0, "Green", true);

        // Interchange connections (same station but different lines already share station id)
        // Example: B (Park Street) connects Red and Green lines; D connects Red and Blue via node D.

        System.out.println("Sample network built (stations A-F).");
    }

    private static void runTests() {
        System.out.println("Running simple tests...");

        // Test 1: Central -> Airport (time metric)
        RouteFinder.RouteResult r1 = RouteFinder.findShortest(graph, "Central", "Airport", "time");
        if (r1 == null) System.out.println("Test1: failed - no path");
        else System.out.printf("Test1: Central->Airport | time=%.1fmin inter=%d | path=%s%n",
                r1.totalTimeMin, r1.interchanges, stationsToNames(r1.path));

        // Test 2: Central -> Airport (distance metric)
        RouteFinder.RouteResult r2 = RouteFinder.findShortest(graph, "A", "E", "distance");
        if (r2 == null) System.out.println("Test2: failed - no path");
        else System.out.printf("Test2: A->E | dist=%.2fkm inter=%d | path=%s%n",
                r2.totalDistanceKm, r2.interchanges, stationsToNames(r2.path));

        // Additional assertion-like checks
        boolean pass = r1 != null && r2 != null;
        System.out.println("Tests completed. Some outputs printed above. pass=" + pass);
    }

    private static String stationsToNames(List<Station> list) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<list.size();i++){
            sb.append(list.get(i).name());
            if (i+1<list.size()) sb.append("->");
        }
        return sb.toString();
    }
}
