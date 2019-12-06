package al.unyt.edu.advjava.fall2019.assign01.Utils;

public class Unigram extends Sequence {
    public static final int UNIGRAM_LENGTH = 1;

    public Unigram (int charCode) {
        setValue(Character.toString((char) charCode));
    }

    @Override
    public void validate(String value) throws ClassCastException {
        this.setValue(value);
    }
}
