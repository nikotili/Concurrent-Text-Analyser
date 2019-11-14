import Utils.Constants;
import Utils.DisplayData;
import Utils.SharedRepository;
import Utils.Word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FileConsumer<P extends Path> implements Consumer<P> {

    private static final Pattern PATTERN = Pattern.compile("\\s+");
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Constants.NUMBER_OF_THREADS);
    private static final SharedRepository sharedRepository = SharedRepository.getInstance();

    public FileConsumer() {
        THREAD_POOL.execute(new DisplayData());
    }

    @Override
    public void accept(P path) {
        THREAD_POOL.execute(() -> loadFile(path));
    }


    private void loadFile(P path) {
//        System.out.println("Loading file " + path.getFileName() + " by thread " + Thread.currentThread().getName());
        try {
                Files.lines(path)
                        .map(line -> line.replaceAll(Constants.SPECIAL_CHARS_REGEX, Constants.EMPTY_STRING))
                        .flatMap(PATTERN::splitAsStream)
                        .map(Word::new)
                        .forEach(this::extractSequences);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractSequences(Word word) {
//        System.out.println("word: " + word + ", count = " + ++c);
        word.getUnigramStream().forEach(sharedRepository::putInUnigramMap);
        word.getBigramStream().forEach(sharedRepository::putInBigramMap);
        sharedRepository.putInWordMap(word);
    }

}