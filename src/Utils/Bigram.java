package Utils;

import Utils.Sequence;

public class Bigram extends Sequence {
    public static final int BIGRAM_LENGTH = 2;

    public Bigram(String value) {
        validate(value);
    }

    @Override
    protected void validate(String value) throws ClassCastException {
        if (value.length() == BIGRAM_LENGTH)
            this.setValue(value);
        else
            throw new ClassCastException();
    }
}
