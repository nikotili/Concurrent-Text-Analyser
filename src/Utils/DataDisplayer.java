package Utils;

import java.util.List;
import java.util.Map;

public class DataDisplayer implements Runnable {

    private SharedRepository sharedRepository = SharedRepository.getInstance();
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(Constants.DISPLAYER_SLEEP_INTERVAL);
                System.out.println(FileConsumer.getNumOfProcessedFiles() + " files processed");
                if (FileConsumer.getNumOfProcessedFiles().get() == 408) {
                    System.out.println("Total words: " + sharedRepository.getCurrentTotalWordCount());
                    System.out.println("Standard deviation: " + sharedRepository.computeAndGetStandardDeviationOnWordCount());
                }
                List<Map.Entry> wordList = sharedRepository.getWordsToDisplay();
                List<Map.Entry> unigramList = sharedRepository.getUnigramsToDisplay();
                List<Map.Entry> bigramList = sharedRepository.getBigramsToDisplay();
                System.out.println(unigramList);
                System.out.println(bigramList);
                System.out.println(wordList);
                System.out.println("unigram entropy: " + sharedRepository.computeAndGetUnigramEntropy());
                System.out.println("bigram entropy: " + sharedRepository.computeAndGetBigramEntropy());
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            Thread.currentThread().interrupt();
        }
    }
}
