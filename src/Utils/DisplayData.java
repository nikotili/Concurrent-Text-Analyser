package Utils;

import java.util.List;
import java.util.Map;

public class DisplayData implements Runnable {
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(Constants.DISPLAYER_SLEEP_INTERVAL);
                System.out.println(Thread.currentThread().getName());
                List<Map.Entry> wordList = SharedRepository.getInstance().getWordsToDisplay();
                List<Map.Entry> unigramList = SharedRepository.getInstance().getUnigramsToDisplay();
                List<Map.Entry> bigramList = SharedRepository.getInstance().getBigramsToDisplay();
                System.out.println(unigramList);
                System.out.println(bigramList);
                System.out.println(wordList);
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
