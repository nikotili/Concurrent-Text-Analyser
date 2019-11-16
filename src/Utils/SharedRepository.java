package Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SharedRepository {

    private static SharedRepository sharedRepository = new SharedRepository();
    private Stream<Unigram> unigramStream;
    private Stream<Bigram> bigramStream;
    private ConcurrentHashMap<Unigram, Long> unigramMap;
    private ConcurrentHashMap<Bigram, Long> bigramMap;
    private ConcurrentHashMap<Word, Long> wordMap;
    private ConcurrentHashMap<String, AtomicLong> fileWordCountMap;

    private SharedRepository() {
        unigramMap = new ConcurrentHashMap<>();
        bigramMap = new ConcurrentHashMap<>();
        wordMap = new ConcurrentHashMap<>();
        fileWordCountMap = new ConcurrentHashMap<>();
    }

    public static SharedRepository getInstance() {
        return sharedRepository;
    }



    public ConcurrentHashMap<String, AtomicLong> getFileWordCountMap() {
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
                        .sum();

        return Math.sqrt(variance);
    }

    public double computeAndGetUnigramEntropy() {
        return computeAndGetSequenceEntropy(unigramMap);
    }

    public double computeAndGetBigramEntropy() {
        return computeAndGetSequenceEntropy(bigramMap);
    }


    private <T extends Sequence> double computeAndGetSequenceEntropy(ConcurrentHashMap<T, Long> map) {
        final long totalSequences = getCurrentTotalSequenceCount(map);
        double entropy = -1 * map
                .values()
                .stream()
                .map(value -> pTimesLogP(value, totalSequences))
                .reduce(0D, Double::sum);
        return entropy;
    }

    private static double pTimesLogP(Long count, long totalCount) {
        return count.doubleValue() / totalCount * log2(count.doubleValue() / totalCount);
    }

    private static double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private <T extends Sequence> void putInMap(T sequence, ConcurrentHashMap<T, Long> map) {
        Long oldNumOfOccurrences = map.getOrDefault(sequence, 0L);

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

    public long getCurrentTotalWordCount() {
        return getCurrentTotalSequenceCount(wordMap);
    }

    private  <T extends Sequence> long getCurrentTotalSequenceCount(ConcurrentHashMap<T, Long> map) {
        return map.values().stream().reduce(0L, Long::sum);
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