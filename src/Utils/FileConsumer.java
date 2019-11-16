package Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FileConsumer<P extends Path> implements Consumer<P> {

    private static final Pattern PATTERN = Pattern.compile("\\s+");
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Constants.NUMBER_OF_THREADS);
    private static SharedRepository sharedRepository;
    private static AtomicInteger numOfProcessedFiles;

    public FileConsumer() {
        THREAD_POOL.execute(new DataDisplayer());
        sharedRepository = SharedRepository.getInstance();
        numOfProcessedFiles = new AtomicInteger(0);
    }

    @Override
    public void accept(P path) {
        THREAD_POOL.execute(() -> loadFile(path));
    }


    private void loadFile(P path) {
        AtomicLong numOfWordsInCurrentFile = new AtomicLong(0L);
//        System.out.println("Loading file " + path.getFileName() + " by thread " + Thread.currentThread().getName());
        try {
                Files.lines(path)
                        .map(line -> line.replaceAll(Constants.SPECIAL_CHARS_REGEX, Constants.EMPTY_STRING))
                        .flatMap(PATTERN::splitAsStream)
                        .filter(s -> !isStopWord(s))
                        .map(Word::new)
                        .forEach(word -> processWord(numOfWordsInCurrentFile, word));

            updateFileWordCountMap(path, numOfWordsInCurrentFile);
            numOfProcessedFiles.getAndIncrement();
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


    private boolean isStopWord(String s) {
        return Constants.STOP_WORDS.contains(s);
    }

    private void extractSequences(Word word) {
//        System.out.println("word: " + word + ", count = " + ++c);
        word.getUnigramStream().forEach(sharedRepository::putInUnigramMap);
        word.getBigramStream().forEach(sharedRepository::putInBigramMap);
        sharedRepository.putInWordMap(word);
    }


    public static AtomicInteger getNumOfProcessedFiles() {
        return numOfProcessedFiles;
    }
}