package Utils;

import java.util.HashSet;
import java.util.Set;

public final class Constants {
    public static final String TXT_SUFFIX = ".txt";
    public static final String STOP_WORDS_PATH = "stopwords.txt";
    public static Set<String> STOP_WORDS = new HashSet<>();
    public static final String ERROR_READING_FILES = "Error while reading";
    public static final String NO_PATH_ERROR = "Please specify folder path";
    public static final String EMPTY_STRING = "";
    public static final int NUMBER_OF_THREADS = 50;
    public static final String SPECIAL_CHARS_REGEX = "\\W";
    public static final String WHITE_SPACES_REGEX = "\\s+";
    public static final int ELEMENTS_TO_DISPLAY = 5;
    public static final long DISPLAYER_SLEEP_INTERVAL  = 500;


}
