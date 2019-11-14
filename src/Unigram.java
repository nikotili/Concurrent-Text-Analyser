public class Unigram extends Sequence {
    public static final int UNIGRAM_LENGTH = 1;

    public Unigram(String value) {
        validate(value);
    }

    public Unigram (int charCode) {
        setValue(Character.toString((char) charCode));
    }

    @Override
    public void validate(String value) throws ClassCastException {
        if (value.length() == UNIGRAM_LENGTH)
            this.setValue(value);
        else
            throw new ClassCastException();
    }
}
