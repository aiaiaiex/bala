package event;

import java.util.ArrayList;
import java.util.List;

public final class Subject {
    private static Subject subject = null;

    private List<Observer> observers;

    private Subject() {
        observers = new ArrayList<>(1);
    }

    public static Subject getSubject() {
        if (subject == null) {
            subject = new Subject();
        }

        return subject;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(Event event) {
        observers.forEach(observer -> observer.notify(event));
    }
}
