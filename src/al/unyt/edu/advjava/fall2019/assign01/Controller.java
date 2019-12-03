package al.unyt.edu.advjava.fall2019.assign01;

import al.unyt.edu.advjava.fall2019.assign01.Utils.FileConsumer;
import al.unyt.edu.advjava.fall2019.assign01.Utils.SharedRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public final class Controller extends Thread {

    public static final String TXT_SUFFIX;
    public static final String STOP_WORDS_PATH;
    public static final String NOT_A_DIRECTORY_ERROR_MESSAGE;
    public static final String EMPTY_DIRECTORY_ERROR_MESSAGE;
    public static final String NO_PATH_ERROR;
    public static final long CONTROLLER_SLEEP_INTERVAL;
    public static Set<String> STOP_WORDS;
    private static final SharedRepository SHARED_REPOSITORY;
    private static Controller controller;
    private static final Instant START_TIME;

    public static Controller getInstance() {
        return controller;
    }

    static {
        START_TIME = Instant.now();
        TXT_SUFFIX = ".txt";
        STOP_WORDS_PATH = "stopwords.txt";
        NOT_A_DIRECTORY_ERROR_MESSAGE = "Specified path is not a directory";
        EMPTY_DIRECTORY_ERROR_MESSAGE = "No .txt files in specified path";
        NO_PATH_ERROR = "Please specify folder path";
        CONTROLLER_SLEEP_INTERVAL = 500;
        STOP_WORDS = new HashSet<>();
        controller = new Controller();
        SHARED_REPOSITORY = SharedRepository.getInstance();
    }

    private Controller() {
        setPriority(Thread.MAX_PRIORITY);
    }


    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(CONTROLLER_SLEEP_INTERVAL);
                printSeparator();
                printElapsedTime();
                if (!finished()) {
                    printProcessedFilesCount();
                    printProcessingFilesCount();
                }
                else {
                    printProcessedFilesCount();
                    printWordAnalysis();
                }

                printUnigrams();
                printBigrams();
                printWords();
                printUnigramEntropy();
                printBigramEntropy();

                printSeparator();
                if (finished()) {

                    System.exit(0);
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            Thread.currentThread().interrupt();
        }
    }

    private static void printElapsedTime() {
        System.out.printf("%s %d ms\n", ( finished() ? "final execution time:" : "elapsed:"),getElapsedTime());
    }

    private static long getElapsedTime() {
        return Instant.now().toEpochMilli() - START_TIME.toEpochMilli();
    }

    private static void printSeparator() {
        System.out.println("================================================================================");
        System.out.println("================================================================================");
    }

    private static void printProcessingFilesCount() {
        System.out.println(FileConsumer.getTotalFilesCount() - FileConsumer.getProcessedFilesCount() + " files processing");
    }

    private static void printProcessedFilesCount() {
        System.out.println(FileConsumer.getProcessedFilesCount() + " files processed");
    }

    private static void printWordAnalysis() {
        System.out.println("Total words: " + SHARED_REPOSITORY.getCurrentTotalWordCount());
        System.out.printf("Std. Dev: %.4f\n", SHARED_REPOSITORY.computeAndGetStandardDeviationOnWordCount());
    }

    private static void printUnigramEntropy() {
        System.out.printf("unigram entropy: %.4f\n", SHARED_REPOSITORY.computeAndGetUnigramEntropy());
    }

    private static void printBigramEntropy() {
        System.out.printf("bigram entropy: %.4f\n", SHARED_REPOSITORY.computeAndGetBigramEntropy());
    }

    private static void printUnigrams() {
        System.out.println("letters: " +  SHARED_REPOSITORY.getUnigramsToDisplay());
    }

    private static void printBigrams() {
        System.out.println("pairs: " + SHARED_REPOSITORY.getBigramsToDisplay());
    }

    private static void printWords() {
        System.out.println("words: " + SHARED_REPOSITORY.getWordsToDisplay());
    }

    public static void displayErrorMessage(String message) {
        System.err.println(message);
    }

    private static boolean finished() {
        return FileConsumer.getProcessedFilesCount() == FileConsumer.getTotalFilesCount();
    }
}
