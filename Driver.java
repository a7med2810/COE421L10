import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Driver {
    public static void main(String[] args) {
        UI ui = new UI();
// Start Eren with 50 energy.
        Eren e = new Eren(50, ui);
        TeamLeader Levi = new TeamLeader("Levi Ackerman", 20, ui, e);
        Friend Mikasa = new Friend("Mikasa", ui, e);
        Friend Armin = new Friend("Armin", ui, e);
        Titan Armor = new Titan("Armored Titan", ui, e);
        Titan Colossal = new Titan("Colossal Titan", ui, e);
    }
}

interface ErenObserver {
    void onTransformation(int energy);
}

class UI {
    public void show(String message) {
        System.out.println(message);
    }

    public void say(String character, String message) {
        show(character + ": " + message);
    }
}

class Eren {
    private final UI ui;
    private final List<ErenObserver> observers = new CopyOnWriteArrayList<>();
    private int energy;

    public Eren(int energy, UI ui) {
        this.energy = energy;
        this.ui = ui;
        new Thread(this::transformUntilExhausted, "Eren").start();
    }

    public void addObserver(ErenObserver observer) {
        observers.add(observer);
    }

    private void transformUntilExhausted() {
        try {
            while (energy > 0) {
                int waitSeconds = ThreadLocalRandom.current().nextInt(5, 11);
                Thread.sleep(waitSeconds * 1000L);

                energy = Math.max(0, energy - 5);
                ui.show("Eren transformed into a Titan. Energy: " + energy);
                for (ErenObserver observer : observers) {
                    observer.onTransformation(energy);
                }

                Thread.sleep(10_000L);
                ui.show("Eren returned to human form.");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

class TeamLeader implements ErenObserver {
    private final String name;
    private final UI ui;
    private int patience;

    public TeamLeader(String name, int patience, UI ui, Eren eren) {
        this.name = name;
        this.patience = patience;
        this.ui = ui;
        eren.addObserver(this);
    }

    @Override
    public void onTransformation(int energy) {
        if (patience <= 0) {
            return;
        }

        patience = Math.max(0, patience - 5);
        switch (patience) {
            case 10:
                ui.say(name, "My patience is waning!");
                break;
            case 5:
                ui.say(name, "Is this entertaining?");
                break;
            case 0:
                ui.say(name, "Omae wa mou shindeiru");
                break;
        }
    }
}

class Friend implements ErenObserver {
    private final String name;
    private final UI ui;

    public Friend(String name, UI ui, Eren eren) {
        this.name = name;
        this.ui = ui;
        eren.addObserver(this);
    }

    @Override
    public void onTransformation(int energy) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            ui.say(name, "Eren, please be safe!");
        }
        if (energy < 20) {
            ui.say(name, "Rage, rage against the dying of the light");
        }
    }
}

class Titan implements ErenObserver {
    private final String name;
    private final UI ui;

    public Titan(String name, UI ui, Eren eren) {
        this.name = name;
        this.ui = ui;
        eren.addObserver(this);
    }

    @Override
    public void onTransformation(int energy) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            ui.show(name + " attacks Eren!");
        }
        if (energy > 40) {
            ui.say(name, "Now we got problems, and I don't think we can solve 'em");
        }
    }
}
