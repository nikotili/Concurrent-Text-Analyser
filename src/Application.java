import Utils.Constants;
import Utils.FileConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Application {
    private static final FileConsumer<Path> fileConsumer = new FileConsumer<>();
    private Application() {
    }

    public static void main(String[] args) {
        try {
//            String folderPath = args[0];
            Application application = new Application();

            String folderPath = "C:\\Users\\User\\Desktop\\test";
            application.start(folderPath);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(Constants.NO_PATH_ERROR);
        }
    }

    private void start(String folderPath) {
        loadStopWords();
        readFiles(folderPath);
    }


    private void loadStopWords() {
        try {
            Path swPath = Paths.get(Constants.STOP_WORDS_PATH);
             Constants.STOP_WORDS = Files.lines(swPath).collect(Collectors.toSet());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void readFiles(String folderPathS) {
        try {
            Path path = Paths.get(folderPathS);
            if (!Files.isDirectory(path)) {
                throw new IOException();
            }

            getTxtFilePath(path).forEach(fileConsumer);

        }
        catch (IOException e) {
            System.out.println(Constants.ERROR_READING_FILES);
        }
    }
    
    private Stream<Path> getTxtFilePath(Path folderPath) throws IOException {
        return Files
                .walk(folderPath)
                .filter(file -> file.getFileName().toString().endsWith(Constants.TXT_SUFFIX));
    }

    private void displayData() {

    }
}
