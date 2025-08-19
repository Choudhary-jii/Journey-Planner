public class Station {
    private final String id;
    private final String name;

    public Station(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() { return id; }
    public String name() { return name; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station s = (Station) o;
        return id.equals(s.id);
    }
}
