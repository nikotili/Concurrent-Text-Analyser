package Utils;

import Utils.Bigram;
import Utils.Sequence;
import Utils.Unigram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Word extends Sequence {

    public Word(String value) {
        validate(value);
    }
    @Override
    protected void validate(String value) throws ClassCastException {
        setValue(value);
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
