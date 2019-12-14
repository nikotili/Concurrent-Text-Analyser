package al.unyt.edu.advjava.fall2019.assign01.Utils;

import java.util.Objects;

public abstract class Sequence implements Comparable {
    private String value;
    abstract protected void validate(String value) throws ClassCastException;

    protected void setValue(String value) {
        this.value = value;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sequence sequence = (Sequence) o;
        return value.equals(sequence.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
