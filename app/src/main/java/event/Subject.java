package event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import utilities.GlobalLogger;

public final class Subject {
    private static final Logger logger = GlobalLogger.getLogger();

    private static List<Observer> observers = new ArrayList<Observer>(1);

    private Subject() {}

    public static void addObserver(Observer observer) {
        logger.fine(() -> String.format("Method called with: (observer=%1$s)", observer));

        logger.fine(() -> String.format("Old observers: %1$s", observers));
        observers.add(observer);
        logger.fine(() -> String.format("New observers: %1$s", observers));

        logger.fine("Method returned: void");
    }

    public static void notifyObservers(Event event) {
        logger.fine(() -> String.format("Method called with: (event=%1$s)", event));

        observers.forEach(observer -> observer.notify(event));

        logger.fine("Method returned: void");
    }
}
