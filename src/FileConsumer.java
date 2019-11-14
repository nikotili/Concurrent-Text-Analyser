import Utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileConsumer<P extends Path> implements Consumer<P> {

    private static final Pattern PATTERN = Pattern.compile("\\s+");
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Constants.NUMBER_OF_THREADS);
    private static final SharedRepository sharedRepository = SharedRepository.getInstance();
    @Override
    public void accept(P path) {
        THREAD_POOL.execute(() -> loadFile(path));
    }


    private synchronized void loadFile(P path) {
        System.out.println("Loading file " + path.getFileName() + " by thread " + Thread.currentThread().getName());
        try {
            Stream<Word> wordStream =
                    Files.lines(path)
                    .map(line -> line.replaceAll(Constants.SPECIAL_CHARS_REGEX, Constants.EMPTY_STRING))
                    .flatMap(PATTERN::splitAsStream)
                    .map(Word::new);

            Supplier<Stream<Word>> streamSupplier = () -> wordStream;

            Stream<Unigram> unigramStream = streamSupplier.get().flatMap(Word::getUnigramStream);
            unigramStream.forEach(sharedRepository::putInUnigramMap);

            Stream<Bigram> bigramStream = streamSupplier.get().flatMap(Word::getBigramStream);
            bigramStream.forEach(sharedRepository::putInBigramMap);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Word stringToWord(String s) {
        return new Word(s);
    }

}