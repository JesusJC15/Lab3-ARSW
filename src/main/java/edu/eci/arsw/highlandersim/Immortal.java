package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    private int health;
    private int defaultDamageValue;
    private final List<Immortal> immortalsPopulation;
    private final String name;
    private final Random r = new Random(System.currentTimeMillis());

    private static final AtomicBoolean paused = new AtomicBoolean(false);
    private static final AtomicBoolean stopped = new AtomicBoolean(false);

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (!stopped.get()) {
            while (paused.get() && !stopped.get()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (immortalsPopulation.size() <= 1) continue;

            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);
            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        updateCallback.processReport(this + " stopped.\n");
    }

    public void fight(Immortal i2) {
        Immortal first = this;
        Immortal second = i2;

        if(System.identityHashCode(first) > System.identityHashCode(second)){
            first = i2;
            second = this;
        }

        synchronized (first) {
            synchronized (second) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                    if(i2.getHealth()<=0){
                        immortalsPopulation.remove(i2);
                    }
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    public synchronized void changeHealth(int v) {
        health = v;
    }

    public synchronized int getHealth() {
        return health;
    }

    @Override
    public String toString() {
        return name + "[" + health + "]";
    }

    public static void pauseAll() {
        paused.set(true);
    }

    public static void resumeAll() {
        paused.set(false);
    }

    public static void stopAll() {
        stopped.set(true);
    }
}