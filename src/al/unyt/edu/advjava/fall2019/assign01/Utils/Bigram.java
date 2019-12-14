package al.unyt.edu.advjava.fall2019.assign01.Utils;

import al.unyt.edu.advjava.fall2019.assign01.Controller;

/***
 * Wraps a bigram in an object
 */

public class Bigram extends Sequence {
    public static final int BIGRAM_LENGTH = 2;

    public Bigram(String value) {
        validate(value);
    }

    @Override
    protected void validate(String value) throws ClassCastException {
        if (Controller.SEQUENCE_VALIDATION_ENABLED)
            if (value.length() != BIGRAM_LENGTH)
                throw new ClassCastException(Controller.NOT_A_BIGRAM_ERROR_MESSAGE + " " + this);
        this.setValue(value);
    }
}
