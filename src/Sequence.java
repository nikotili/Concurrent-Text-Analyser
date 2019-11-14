public abstract class Sequence {
    private String value;
    abstract protected void validate(String value) throws ClassCastException;

    protected void setValue(String value) {
        this.value = value.toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
