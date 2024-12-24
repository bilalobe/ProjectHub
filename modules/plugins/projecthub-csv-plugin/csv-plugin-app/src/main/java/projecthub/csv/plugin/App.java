package projecthub.csv.plugin;

import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        logger.info(new App().getGreeting());
    }

    public String getGreeting() {
        return "Hello World!";
    }
}
