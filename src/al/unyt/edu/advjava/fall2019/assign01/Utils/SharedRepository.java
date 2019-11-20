package al.unyt.edu.advjava.fall2019.assign01.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SharedRepository {

    private static SharedRepository sharedRepository;
    private Map<Unigram, AtomicLong> unigramMap;
    private Map<Bigram, AtomicLong> bigramMap;
    private Map<Word, AtomicLong> wordMap;
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

        sharedRepository.wordMap.put(new Word("w1"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w2"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w3"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w4"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w5"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w6"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w7"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w8"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w9"), new AtomicLong(23L));
        sharedRepository.wordMap.put(new Word("w0"), new AtomicLong(23L));

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

        long filesCount = FolderConsumer.getTotalFilesCount();

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


    private <T extends Sequence> double computeAndGetSequenceEntropy(Map<T, AtomicLong> map) {
        final long totalSequences = getCurrentTotalSequenceCount(map);
        return -1 * map
                .values()
                .stream()
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
        AtomicLong oldNumOfOccurrences = map.getOrDefault(sequence, new AtomicLong(0));

        if (oldNumOfOccurrences.longValue() == 0L) {
            AtomicLong atomicLong = new AtomicLong(1);
            map.put(sequence, atomicLong);
        }
        else
            map.get(sequence).incrementAndGet();
    }

    public void putInUnigramMap(Unigram unigram) {
        putInMap(unigram, unigramMap);
    }

    public void putInBigramMap(Bigram bigram) {
        putInMap(bigram, bigramMap);
    }

    public void putInWordMap(Word word) {
        putInMap(word, wordMap);
    }

    public List<Map.Entry<Unigram, AtomicLong>> getUnigramsToDisplay() {
        return getSequencesToDisplay(unigramMap);
    }

    public List<Map.Entry<Bigram, AtomicLong>> getBigramsToDisplay() {
        return getSequencesToDisplay(bigramMap);
    }

    public List<Map.Entry<Word, AtomicLong>> getWordsToDisplay() {
        return getSequencesToDisplay(wordMap);
    }

    public long getCurrentTotalWordCount() {
        return getCurrentTotalSequenceCount(wordMap);
    }

    private <T extends Sequence> long getCurrentTotalSequenceCount(Map<T, AtomicLong> map) {
        return map.values().stream().mapToLong(AtomicLong::longValue).reduce(0L, Long::sum);
    }

    private <T extends Sequence> List<Map.Entry<T, AtomicLong>> getSequencesToDisplay(Map<T, AtomicLong> map) {
        Stream<Map.Entry<T, AtomicLong>> s = map.entrySet()
                .stream();
        return s
                .sorted(new MapEntryComparator().reversed())
                .limit(Constants.ELEMENTS_TO_DISPLAY)
                .collect(Collectors.toList());
    }
}