import Utils.Constants;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SharedRepository {
    //todo streams

    private Stream<Unigram> unigramStream;
    private Stream<Bigram> bigramStream;

    private ConcurrentHashMap<Unigram, Long> unigramMap;
    private ConcurrentHashMap<Bigram, Long> bigramMap;
    private static SharedRepository sharedRepository = new SharedRepository();

    private SharedRepository() {
        unigramMap = new ConcurrentHashMap<>();
        bigramMap = new ConcurrentHashMap<>();
    }

    public synchronized void putInUnigramStream(Stream<Unigram> inputStream) {
        if (unigramStream == null)
            unigramStream = inputStream;
        else
            unigramStream = Stream.concat(inputStream, unigramStream);
    }

    public synchronized void putInBigramStream(Stream<Bigram> inputStream) {
        if (bigramStream == null)
            bigramStream = inputStream;

        else
            bigramStream = Stream.concat(inputStream, bigramStream);
    }

    public static SharedRepository getInstance() {
        return sharedRepository;
    }

    public ConcurrentHashMap<Unigram, Long> getUnigramMap() {
        return unigramMap;
    }

    public ConcurrentHashMap<Bigram, Long> getBigramMap() {
        return bigramMap;
    }

    private <T extends Sequence> void putInMap(T sequence, ConcurrentHashMap<T, Long> map) {
        Long oldNumOfOccurrences = map.getOrDefault(sequence, Constants.NO_OCCURRENCES);

        if (oldNumOfOccurrences.equals(Constants.NO_OCCURRENCES))
            map.put(sequence, Constants.ONE_LONG);
        else
            incrementNumOfOccurrencesByOne(sequence, oldNumOfOccurrences, map);
    }

    public void putInUnigramMap(Unigram unigram) {
        putInMap(unigram, unigramMap);
    }

    public void putInBigramMap(Bigram bigram) {
        putInMap(bigram, bigramMap);
    }

    private <T extends Sequence> void incrementNumOfOccurrencesByOne(T sequence, Long oldNumOfOccurrences, ConcurrentHashMap<T, Long> map) {
        map.replace(sequence, oldNumOfOccurrences, oldNumOfOccurrences + Constants.ONE_LONG);
    }
}
