package al.unyt.edu.advjava.fall2019.assign01.Utils;

import al.unyt.edu.advjava.fall2019.assign01.Controller;

public class Unigram extends Sequence {
    public static final int UNIGRAM_LENGTH = 1;

    public Unigram (int charCode) {
        validate(Character.toString((char) charCode));
    }

    @Override
    public void validate(String value) throws ClassCastException {
        if (Controller.SEQUENCE_VALIDATION_ENABLED)
            if (value.length() != UNIGRAM_LENGTH)
                throw new ClassCastException(Controller.NOT_A_UNIGRAM_ERROR_MESSAGE + " " + this);
        this.setValue(value);
    }
}
