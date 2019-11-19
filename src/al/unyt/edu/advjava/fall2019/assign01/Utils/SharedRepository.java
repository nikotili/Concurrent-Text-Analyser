package al.unyt.edu.advjava.fall2019.assign01.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongToDoubleFunction;
import java.util.stream.Collectors;

public class SharedRepository {

    private static SharedRepository sharedRepository;
    private Map<Unigram, Long> unigramMap;
    private Map<Bigram, Long> bigramMap;
    private Map<Word, Long> wordMap;
    private Map<String, AtomicLong> fileWordCountMap;

    static {
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

    public static void main(String[] args) {
        SharedRepository sharedRepository = getInstance();

        sharedRepository.wordMap.put(new Word("w1"), 23L);
        sharedRepository.wordMap.put(new Word("w2"), 331L);
        sharedRepository.wordMap.put(new Word("w3"), 32L);
        sharedRepository.wordMap.put(new Word("w4"), 31L);
        sharedRepository.wordMap.put(new Word("w5"), 86L);
        sharedRepository.wordMap.put(new Word("w6"), 35L);
        sharedRepository.wordMap.put(new Word("w7"), 3L);
        sharedRepository.wordMap.put(new Word("w8"), 3L);
        sharedRepository.wordMap.put(new Word("w9"), 3L);
        sharedRepository.wordMap.put(new Word("w0"), 3L);

        System.out.println(sharedRepository.computeAndGetSequenceEntropy(sharedRepository.wordMap));
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

        long filesCount = FileConsumer.getTotalFilesCount();

        double variance =
                fileWordCountMap.values()
                        .stream()
                        .mapToLong(AtomicLong::longValue)
                        .mapToDouble(squaredDifferenceFromMean)
                        .sum() / filesCount;

        return Math.sqrt(variance);
    }

    public double computeAndGetUnigramEntropy() {
        return computeAndGetSequenceEntropy(unigramMap);
    }

    public double computeAndGetBigramEntropy() {
        return computeAndGetSequenceEntropy(bigramMap);
    }


    private <T extends Sequence> double computeAndGetSequenceEntropy(Map<T, Long> map) {
        final long totalSequences = getCurrentTotalSequenceCount(map);
        return -1 * map
                .values()
                .stream()
                .map(value -> pTimesLogP(value, totalSequences))
                .reduce(0D, Double::sum);
    }

    private static double pTimesLogP(Long count, long totalCount) {
        return count.doubleValue() / totalCount * log2(count.doubleValue() / totalCount);
    }

    private static double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private <T extends Sequence> void putInMap(T sequence, Map<T, Long> map) {
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

    private  <T extends Sequence> long getCurrentTotalSequenceCount(Map<T, Long> map) {
        return map.values().stream().reduce(0L, Long::sum);
    }

    private <T extends Sequence> List<Map.Entry> getSequencesToDisplay(Map<T, Long> map) {
        return map.entrySet()
                .stream()
                .sorted(new MapEntryComparator().reversed())
                .limit(Constants.ELEMENTS_TO_DISPLAY)
                .collect(Collectors.toList());
    }

    private <T extends Sequence> void incrementNumOfOccurrencesByOne(T sequence, Long oldNumOfOccurrences, Map<T, Long> map) {
        map.replace(sequence, oldNumOfOccurrences, oldNumOfOccurrences + 1);
    }
}