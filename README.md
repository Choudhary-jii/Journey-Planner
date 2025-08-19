# Metro Route Finder (Console, Java)

A console-based Java tool to compute optimal metro routes using Dijkstra's algorithm.
Features:
- Computes shortest routes by **time** or **distance**.
- Minimizes **interchanges** as a secondary objective.
- Supports dynamic updates: add/remove stations, add/remove/update routes.
- Console UI for quick interaction and a sample network pre-loaded.

## Files
- Station.java
- Edge.java
- MetroGraph.java
- RouteFinder.java
- Main.java

## Requirements
- Java 8+ (tested with Java 11)

## Compile & Run
```bash
javac *.java
java Main
