package al.unyt.edu.advjava.fall2019.assign01.Utils;

public class Bigram extends Sequence {
    public static final int BIGRAM_LENGTH = 2;

    public Bigram(String value) {
        setValue(value);
    }

    @Override
    protected void validate(String value) throws ClassCastException {
        this.setValue(value);
    }
}
