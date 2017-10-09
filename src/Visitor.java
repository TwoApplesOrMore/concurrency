import java.util.Random;

public class Visitor extends Thread {

    private Random RNG = new Random();
    private String name;
    private MonitorAutoRAI monitorAutoRAI;


    public Visitor(String name, MonitorAutoRAI monitorAutoRAI) {
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

                // watch the cars
                lookatcars();

                // leave
                monitorAutoRAI.leave(this);

            } catch (InterruptedException e) {
                System.out.println(name + " got interrupted trying to live");
            }
        }
    }

    private void StayhomeAndThenGoToAutoRAI() throws InterruptedException {
        try {
            System.out.println(name + " Is at home");
            Thread.sleep(RNG.nextInt(5)*1000);
        } catch (InterruptedException e) {
            System.out.println("Thread sleeping went wrong");
        }
    }

    private void lookatcars() {
        try {
            System.out.println(name + " Is watching pretty cars");
            Thread.sleep(RNG.nextInt(5)*500);
        } catch (InterruptedException ie) {
            System.out.println("Thread " + name + " watching cars went wrong (This is not in monitor)");
        }
    }

    public String getThreadName() {
        return this.name;
    }
}
