import java.util.Random;

public class Buyer extends Thread {

    private Random RNG = new Random();

    private String name;
    private MonitorAutoRAI monitorAutoRAI;

    public Buyer(String name, MonitorAutoRAI monitorAutoRAI) {
        this.name = name;
        this.monitorAutoRAI = monitorAutoRAI;
    }

    public void run() {
        while(true) {
            try {
                // live, and then decide to go to autoRAI
                StayhomeAndThenGoToAutoRAI();

                //attend autoRAI
                monitorAutoRAI.attend(this);

                // watch cars
                lookAndBuyCar();

                // go home with car
                monitorAutoRAI.leave(this);
            } catch (InterruptedException e) {
                System.out.println(name + " Got interrupted trying to live");
            }
        }
    }

    /**
     * Lets the current thread sleep for a random amount of time
     * @throws InterruptedException
     */
    private void StayhomeAndThenGoToAutoRAI() throws InterruptedException {
        try {
            System.out.println(name + " Is sleeping");
            Thread.sleep(RNG.nextInt(5)*1000);
        } catch (InterruptedException ie) {
            System.out.println("Thread " + name + " sleeping went wrong");
        }
    }

    /**
     * Lets the current thread sleep for a random amount of time
     * @throws InterruptedException
     */
    private void lookAndBuyCar() {
        try {
            System.out.println(name + " Is buying a car");
            Thread.sleep(RNG.nextInt(5)*500);
        } catch (InterruptedException ie) {
            System.out.println("Thread " + name + " watching cars went wrong (This is not in monitor)");
        }
    }

    public String getThreadName() {
        return this.name;
    }

}
