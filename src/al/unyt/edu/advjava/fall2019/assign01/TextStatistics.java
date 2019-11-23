package al.unyt.edu.advjava.fall2019.assign01;

import al.unyt.edu.advjava.fall2019.assign01.Utils.FileConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TextStatistics {
    private static final Controller CONTROLLER;
    private static final FileConsumer<Path> FILE_CONSUMER;

    static {
        CONTROLLER = Controller.getInstance();
        FILE_CONSUMER = new FileConsumer<>();
    }

    private TextStatistics() {}

    public static void main(String[] args) {
        try {
            TextStatistics application = new TextStatistics();
            application.start(args[0]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            Controller.displayErrorMessage(Controller.NO_PATH_ERROR);
        }
    }

    private void start(String folderPath) {
        CONTROLLER.start();
        loadStopWords();
        readFiles(folderPath);
    }


    private void loadStopWords() {
        try {
            Path swPath = Paths.get(Controller.STOP_WORDS_PATH);
             Controller.STOP_WORDS = Files.lines(swPath).collect(Collectors.toSet());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void readFiles(String folderPathS) {
        try {
            Path path = Paths.get(folderPathS);
            if (!Files.isDirectory(path)) {
                throw new IOException(Controller.NOT_A_DIRECTORY_ERROR_MESSAGE);
            }

            long txtFilesCount = getTxtFiles(path).count();

            if (txtFilesCount == 0)
                throw new IOException(Controller.EMPTY_DIRECTORY_ERROR_MESSAGE);

            FILE_CONSUMER.setTotalFilesCount(txtFilesCount);
            getTxtFiles(path).forEach(FILE_CONSUMER);

        }
        catch (IOException e) {
            Controller.displayErrorMessage(e.getMessage());
            System.exit(0);
        }
    }
    
    private Stream<Path> getTxtFiles(Path folderPath) throws IOException {
        return Files
                .walk(folderPath)
                .filter(file -> file.getFileName().toString().endsWith(Controller.TXT_SUFFIX));
    }
}
