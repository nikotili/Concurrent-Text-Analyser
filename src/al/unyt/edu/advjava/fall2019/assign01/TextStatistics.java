package al.unyt.edu.advjava.fall2019.assign01;


public class TextStatistics {
    private static final Controller CONTROLLER;

    static {
        CONTROLLER = Controller.getInstance();
    }

    private TextStatistics() {}

    public static void main(String[] args) {
        try {
            TextStatistics application = new TextStatistics();
            application.start(args[0]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            Controller.displayErrorMessage(Controller.NO_PATH_ERROR_MESSAGE);
        }
    }

    private void start(String folderPath) {
        Controller.folderPath = folderPath;
        CONTROLLER.start();
    }

}
