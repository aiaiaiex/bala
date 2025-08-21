package event;

import java.util.ArrayList;
import java.util.List;

public final class Subject {
    private static List<Observer> observers = new ArrayList<Observer>(1);

    private Subject() {}

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void notifyObservers(Event event) {
        observers.forEach(observer -> observer.notify(event));
    }
}
