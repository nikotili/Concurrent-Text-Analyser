package al.unyt.edu.advjava.fall2019.assign01.Utils;

import java.time.Instant;

public class Controller extends Thread {

    private static SharedRepository sharedRepository;
    private static Controller controller;
    private final Instant START_TIME;

    public static Controller getInstance() {
        return controller;
    }

    static {
        controller = new Controller();
        sharedRepository = SharedRepository.getInstance();
    }

    private Controller() {
        START_TIME = Instant.now();
    }


    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(Constants.CONTROLLER_SLEEP_INTERVAL);
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

    private void printElapsedTime() {
        System.out.printf("%s %d ms\n", ( finished() ? "final execution time:" : "elapsed:"),getElapsedTime());
    }

    private long getElapsedTime() {
        return Instant.now().toEpochMilli() - START_TIME.toEpochMilli();
    }

    private void printSeparator() {
        System.out.println("================================================================================");
        System.out.println("================================================================================");
    }

    private void printProcessingFilesCount() {
        System.out.println(FolderConsumer.getTotalFilesCount() - FolderConsumer.getProcessedFilesCount() + " files processing");
    }

    private void printProcessedFilesCount() {
        System.out.println(FolderConsumer.getProcessedFilesCount() + " files processed");
    }

    private void printWordAnalysis() {
        System.out.println("Total words: " + sharedRepository.getCurrentTotalWordCount());
        System.out.printf("Std. Dev: %.4f\n", sharedRepository.computeAndGetStandardDeviationOnWordCount());
    }

    private void printUnigramEntropy() {
        System.out.printf("unigram entropy: %.4f\n", sharedRepository.computeAndGetUnigramEntropy());
    }

    private void printBigramEntropy() {
        System.out.printf("bigram entropy: %.4f\n", sharedRepository.computeAndGetBigramEntropy());
    }

    private void printUnigrams() {
        System.out.println("letters: " +  sharedRepository.getUnigramsToDisplay());
    }

    private void printBigrams() {
        System.out.println("pairs: " + sharedRepository.getBigramsToDisplay());
    }

    private void printWords() {
        System.out.println("words: " + sharedRepository.getWordsToDisplay());
    }

    private boolean finished() {
        return FolderConsumer.getProcessedFilesCount() == FolderConsumer.getTotalFilesCount();
    }
}
