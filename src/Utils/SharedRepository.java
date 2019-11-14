package Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SharedRepository {

    private Stream<Unigram> unigramStream;
    private Stream<Bigram> bigramStream;

    private ConcurrentHashMap<Unigram, Long> unigramMap;
    private ConcurrentHashMap<Bigram, Long> bigramMap;
    private ConcurrentHashMap<Word, Long> wordMap;
    private static SharedRepository sharedRepository = new SharedRepository();

    private SharedRepository() {
        unigramMap = new ConcurrentHashMap<>();
        bigramMap = new ConcurrentHashMap<>();
        wordMap = new ConcurrentHashMap<>();
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

    public static void main(String[] args) {
        sharedRepository.putInUnigramMap(new Unigram('a'));
        sharedRepository.putInUnigramMap(new Unigram('a'));
        sharedRepository.putInUnigramMap(new Unigram('a'));
        sharedRepository.putInUnigramMap(new Unigram('a'));
        sharedRepository.putInUnigramMap(new Unigram('a'));
        sharedRepository.putInUnigramMap(new Unigram('a'));
    }

    private <T extends Sequence> void putInMap(T sequence, ConcurrentHashMap<T, Long> map) {
        Long oldNumOfOccurrences
                = map.entrySet()
                .stream()
                .filter(
                        entry -> entry.getKey()
                                .toString()
                                .equals(sequence.toString())
                )
                .mapToLong(Map.Entry::getValue).sum();

        if (oldNumOfOccurrences.equals(0L))
            map.put(sequence, 1L);
        else
            incrementNumOfOccurrencesByOne(sequence, oldNumOfOccurrences, map);
    }

    public synchronized void putInUnigramMap(Unigram unigram) {
        putInMap(unigram, unigramMap);
    }

    public synchronized void putInBigramMap(Bigram bigram) {
        putInMap(bigram, bigramMap);
    }

    public synchronized void putInWordMap(Word word) {
        putInMap(word, wordMap);
    }

    public List<Map.Entry> getUnigramsToDisplay() {
        return getSequencesToDisplay(unigramMap);
    }

    public List<Map.Entry> getBigramsToDisplay() {
        return getSequencesToDisplay(bigramMap);
    }

    public List<Map.Entry> getWordsToDisplay() {
        return getSequencesToDisplay(wordMap);
    }

    private <T extends Sequence> List<Map.Entry> getSequencesToDisplay(ConcurrentHashMap<T, Long> map) {
        return map.entrySet()
                .stream()
                .sorted(new MapEntryComparator().reversed())
                .limit(Constants.ELEMENTS_TO_DISPLAY)
                .collect(Collectors.toList());
    }

    private <T extends Sequence> void incrementNumOfOccurrencesByOne(T sequence, Long oldNumOfOccurrences, ConcurrentHashMap<T, Long> map) {
        map.replace(sequence, oldNumOfOccurrences, oldNumOfOccurrences + 1);
    }
}