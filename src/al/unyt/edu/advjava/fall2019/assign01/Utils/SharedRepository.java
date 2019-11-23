package al.unyt.edu.advjava.fall2019.assign01.Utils;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongToDoubleFunction;
import java.util.stream.Collectors;

public class SharedRepository {
    private static final int ELEMENTS_TO_DISPLAY;
    private static SharedRepository sharedRepository;
    private Map<Unigram, AtomicLong> unigramMap;
    private Map<Bigram, AtomicLong> bigramMap;
    private Map<Word, AtomicLong> wordMap;
    private Map<String, AtomicLong> fileWordCountMap;

    static {
        ELEMENTS_TO_DISPLAY  = 5;
        sharedRepository = new SharedRepository();
    }

    private SharedRepository() {
        unigramMap = new ConcurrentHashMap<>();
        bigramMap = new ConcurrentHashMap<>();
        wordMap = new ConcurrentHashMap<>();
        fileWordCountMap = new ConcurrentHashMap<>();
    }

    public static SharedRepository getInstance() {
        return sharedRepository;
    }


    public Map<String, AtomicLong> getFileWordCountMap() {
        return fileWordCountMap;
    }

    public double computeAndGetStandardDeviationOnWordCount() {
        double mean =
                fileWordCountMap.values()
                        .stream()
                        .mapToLong(AtomicLong::longValue)
                        .average()
                        .orElse(0D);

        LongToDoubleFunction squaredDifferenceFromMean = value -> Math.pow(value - mean, 2);

        double variance =
                fileWordCountMap.values()
                        .stream()
                        .mapToLong(AtomicLong::longValue)
                        .mapToDouble(squaredDifferenceFromMean)
                        .sum() / fileWordCountMap.size();

        return Math.sqrt(variance);
    }

    public double computeAndGetUnigramEntropy() {
        return computeAndGetSequenceEntropy(unigramMap);
    }

    public double computeAndGetBigramEntropy() {
        return computeAndGetSequenceEntropy(bigramMap);
    }


    private <T extends Sequence> double computeAndGetSequenceEntropy(Map<T, AtomicLong> map) {
        final long totalSequences = getCurrentTotalSequenceCount(map);
        return -1 * map
                .values()
                .parallelStream()
                .map(value -> pTimesLogP(value, totalSequences))
                .reduce(0D, Double::sum);
    }

    private static double pTimesLogP(AtomicLong count, long totalCount) {
        return count.doubleValue() / totalCount * log2(count.doubleValue() / totalCount);
    }

    private static double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private <T extends Sequence> void putInMap(T sequence, Map<T, AtomicLong> map) {

        if (map.containsKey(sequence))
            map.get(sequence).incrementAndGet();

        else
            map.put(sequence, new AtomicLong(1));
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

    public List getUnigramsToDisplay() {
        return getSequencesToDisplay(unigramMap);
    }

    public List getBigramsToDisplay() {
        return getSequencesToDisplay(bigramMap);
    }

    public List getWordsToDisplay() {
        return getSequencesToDisplay(wordMap);
    }

    public long getCurrentTotalWordCount() {
        return getCurrentTotalSequenceCount(wordMap);
    }

    private  <T extends Sequence> long getCurrentTotalSequenceCount(Map<T, AtomicLong> map) {
        return map.values().stream().mapToLong(AtomicLong::longValue).reduce(0L, Long::sum);
    }

    private <T extends Sequence> List getSequencesToDisplay(Map<T, AtomicLong> map) {
        try {
            return map.entrySet()
                    .parallelStream()
                    .sorted(new MapEntryComparator().reversed())
                    .limit(ELEMENTS_TO_DISPLAY)
                    .collect(Collectors.toList());
        }
        catch (IllegalArgumentException e) {
            return getSequencesToDisplay(map);
        }
    }
}