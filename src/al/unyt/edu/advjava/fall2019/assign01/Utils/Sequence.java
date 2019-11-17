package al.unyt.edu.advjava.fall2019.assign01.Utils;

public abstract class Sequence implements Comparable {
    private String value;
    abstract protected void validate(String value) throws ClassCastException;

    protected void setValue(String value) {
        this.value = value.replaceAll("_", "");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        return value.compareTo(o.toString());
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
