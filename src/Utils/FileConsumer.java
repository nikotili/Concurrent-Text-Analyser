package Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FileConsumer<P extends Path> implements Consumer<P> {

    private static final Pattern PATTERN = Pattern.compile(Constants.WHITE_SPACES_REGEX);

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


    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("s1 fas da=fasd as asd as fas");
        list.add("s2=fas sadg ewawsd  gfads asd fasd a g we as");
        list.add("s3f sdsdfsdsdfs$@#@$%@hgjtwea ss  rd ");
        list.add("s4!@#$%^&*(*&^%$#@Wdfv");
        list.add("s5 sd a");
        list.add("s6fe }{}{}/'asd ");
        list.add("    ");
        list.add("s8asd");
        list.add("s9a }s");
        list.add("s10\"");

        list.stream()
                .flatMap(PATTERN::splitAsStream)
                .map(line -> line.replaceAll("\\W", Constants.EMPTY_STRING))
                .forEach(System.out::println);
    }


    private void loadFile(P path) {
        AtomicLong numOfWordsInCurrentFile = new AtomicLong(0L);
        try {
                Files.lines(path)
                        .flatMap(PATTERN::splitAsStream)
                        .map(s -> s.replaceAll(Constants.SPECIAL_CHARS_REGEX, Constants.EMPTY_STRING))
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
        return Constants.STOP_WORDS.contains(s)
                || s.trim().equals("");
    }

    private void extractSequences(Word word) {
        word.getUnigramStream().forEach(sharedRepository::putInUnigramMap);
        word.getBigramStream().forEach(sharedRepository::putInBigramMap);
        sharedRepository.putInWordMap(word);
    }


    public static int getNumOfProcessedFiles() {
        return numOfProcessedFiles.get();
    }
}