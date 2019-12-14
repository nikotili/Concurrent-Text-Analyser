package al.unyt.edu.advjava.fall2019.assign01.Utils;

import al.unyt.edu.advjava.fall2019.assign01.Controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FileConsumer<P extends Path> implements Consumer<P> {

    private static final Pattern PATTERN;
    public static final int NUMBER_OF_THREADS;
    private static final ExecutorService THREAD_POOL;
    private static final SharedRepository sharedRepository;
    private static AtomicLong processedFilesCount;
    private static AtomicLong totalFilesCount;

    static {
        PATTERN = Pattern.compile(Controller.WHITE_SPACES_REGEX);
        NUMBER_OF_THREADS = 5;
        THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        sharedRepository = SharedRepository.getInstance();
        totalFilesCount = new AtomicLong(0L);
        processedFilesCount = new AtomicLong(0L);
    }

    @Override
    public void accept(P path) {
        THREAD_POOL.execute(() -> loadFile(path));
    }


    private void loadFile(P path) {
//        System.out.println(Thread.currentThread().getName());
        AtomicLong numOfWordsInCurrentFile = new AtomicLong(0L);
        try {
                Files.lines(path, StandardCharsets.ISO_8859_1)
                        .flatMap(PATTERN::splitAsStream)
                        .map(s -> s.replaceAll(Controller.SPECIAL_CHARS_REGEX, Controller.EMPTY_STRING))
                        .map(String::toLowerCase)
//                        .filter(this::isStopWord)
                        .map(Word::new)
                        .forEach(word -> processWord(numOfWordsInCurrentFile, word));

            updateFileWordCountMap(path, numOfWordsInCurrentFile);
            processedFilesCount.getAndIncrement();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFileWordCountMap(P path, AtomicLong numOfWordsInCurrentFile) {
        sharedRepository
                .getFileWordCountMap()
                .put(path.getFileName().toString(), numOfWordsInCurrentFile);
    }

    private void processWord(AtomicLong numOfWordsInCurrentFile, Word word) {
        extractSequences(word);
        numOfWordsInCurrentFile.getAndIncrement();
    }


    private boolean isStopWord(Word word) {
        return Controller.STOP_WORDS.contains(word.toString());
    }

    private boolean isStopWord(String word) {
        return Controller.STOP_WORDS.contains(word);
    }

    private void extractSequences(Word word) {
        word.getUnigramStream().forEach(sharedRepository::putInUnigramMap);
        word.getBigramStream().forEach(sharedRepository::putInBigramMap);
        if(!isStopWord(word))
            sharedRepository.putInWordMap(word);
    }


    public static long getProcessedFilesCount() {
        return processedFilesCount.get();
    }

    public static long getTotalFilesCount() {
        return totalFilesCount.get();
    }

    public void setTotalFilesCount(long totalFilesCount) {
        FileConsumer.totalFilesCount.set(totalFilesCount);
    }
}