package al.unyt.edu.advjava.fall2019.assign01.Utils;

import al.unyt.edu.advjava.fall2019.assign01.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/***
 * Wraps a word in an object
 */

public class Word extends Sequence {

    public Word(String value) {
        validate(value);
    }
    @Override
    protected void validate(String value) throws ClassCastException {
        if (Controller.SEQUENCE_VALIDATION_ENABLED) {
            if (value.isEmpty())
                throw new ClassCastException();
            value.chars().forEach(this::isSpecial);
        }
        setValue(value);
    }

    private void isSpecial(int c) {
        if (!Character.isAlphabetic(c) && !Character.isDigit(c))
            throw new ClassCastException(Controller.NOT_A_WORD_ERROR_MESSAGE + " " + this);
    }


    public Stream<Unigram> getUnigramStream() {
        return  this
                .toString()
                .chars()
                .mapToObj(Unigram::new);
    }

    public Stream<Bigram> getBigramStream() {
        char[] chars = this.toString().toCharArray();
        List<String> bigramList = new ArrayList<>();
        int bound = chars.length - 1;
        for (int i = 0; i < bound; i++) {
            String b = chars[i] + "" + chars[i+1];
            bigramList.add(b);
        }

        return bigramList.stream().map(Bigram::new);
    }
}
