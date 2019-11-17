package al.unyt.edu.advjava.fall2019.assign01;

import al.unyt.edu.advjava.fall2019.assign01.Utils.Constants;
import al.unyt.edu.advjava.fall2019.assign01.Utils.Controller;
import al.unyt.edu.advjava.fall2019.assign01.Utils.FileConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TextStatistics {
    private static Controller controller = Controller.getInstance();
    private static final FileConsumer<Path> fileConsumer = new FileConsumer<>();

    private TextStatistics() {}

    public static void main(String[] args) {
        try {
//            String folderPath = args[0];
            TextStatistics application = new TextStatistics();

            String folderPath = "C:\\Users\\User\\Desktop\\test";
            application.start(folderPath);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(Constants.NO_PATH_ERROR);
        }
    }

    private void start(String folderPath) {
        controller.start();
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
                throw new IOException(Constants.NOT_A_DIRECTORY_ERROR_MESSAGE);
            }

            long txtFilesCount = getTxtFiles(path).count();

            if (txtFilesCount == 0)
                throw new IOException(Constants.EMPTY_DIRECTORY_ERROR_MESSAGE);

            fileConsumer.setTotalFilesCount(txtFilesCount);
            getTxtFiles(path).forEach(fileConsumer);

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    private Stream<Path> getTxtFiles(Path folderPath) throws IOException {
        return Files
                .walk(folderPath)
                .filter(file -> file.getFileName().toString().endsWith(Constants.TXT_SUFFIX));
    }
}
