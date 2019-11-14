import Utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
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


            application.readFiles(folderPath);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(Constants.NO_PATH_ERROR);
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
}
