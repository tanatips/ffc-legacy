package th.in.ffc.map.database;

public class SpinnerItem implements Comparable<SpinnerItem> {
    private String name;
    private String id;
    private int position;

    public SpinnerItem(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }

    public void setInt(int i) {
        position = i;
    }


    public int getInt() {
        return position;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(SpinnerItem another) {
        if (id != null)
            return name.compareTo(another.getName())
                    + id.compareTo(another.getID());

        return name.compareTo(another.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SpinnerItem) {
            SpinnerItem tmp = (SpinnerItem) o;
            if (id != null) {
                return id.equals(tmp.getID()) && name.equals(tmp.getName());
            } else {
                return name.equals(tmp.getName());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (id != null)
            return id.hashCode();

        return name.hashCode();
    }
}
