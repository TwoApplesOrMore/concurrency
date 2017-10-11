import java.util.concurrent.Semaphore;

/**
 * @author Jan Stroet
 */

public class RopeBridge {
    /**
     * your s(hared) data structures to garantee correct behaviour of the people
     * in passing the rope bridge
     **/

    private static final int NR_OF_PEOPLE = 20;
    private static final int BRIDGE_CAPACITY = 3;


    private Person[] person = new Person[NR_OF_PEOPLE];

    private Semaphore freeBridgeSpace;
    private Semaphore mutex;
    private Semaphore sideMutex;
    private Semaphore queMutex;
    private int peopleOnBridge = 0;
    private int queLeft = 0;
    private int queRight = 0;
    private String bridgeSide = "";


    public RopeBridge() {
        freeBridgeSpace = new Semaphore(BRIDGE_CAPACITY, true);
        mutex = new Semaphore(1);
        sideMutex = new Semaphore(1);
        queMutex = new Semaphore(1);
        for (int i = 0; i < NR_OF_PEOPLE; i++) {
            person[i] = new Person("p" + i); /* argument list can be extended */
            person[i].start();
        }


    }

    class Person extends Thread {
        boolean living;
        String side;

        public Person(String name) {
            super(name);
            living = true;
            if ((int) (Math.random()*2) == 0) {
                side = "Left";
            } else {
                side = "Right";
            }
        }

        public void run() {
            while (living) {

                try {

                    justLive();
                    queMutex.acquire();
                    if (side.equals("Right")){
                        queRight++;
                    }else{
                        queLeft++;
                    }
                    queMutex.release();


                    while (!bridgeSide.equals(side)) {
                        sideMutex.acquire();
                        if (peopleOnBridge == 0 ) {
                            bridgeSide = side;
                            System.out.println("Switched bridge side to: " + side + " queLeft = "+ queLeft + " queRight = "+queRight);
                        }
                        sideMutex.release();
                    }

                    freeBridgeSpace.acquire();
                    walk();
                    System.out.println(getName() + " Arrived at the "+ side);
                    freeBridgeSpace.release();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }

        private void waiting(){
            try {
                Thread.sleep((int) (Math.random()*100));
            } catch (InterruptedException e) {
            }
        }
        private void walk() {
            try {
                queMutex.acquire();
                if (side.equals("Right")){
                    queRight--;
                }else{
                    queLeft--;
                }
                queMutex.release();
                System.out.println(getName() + " walking the bridge from the " + side + " [" + (peopleOnBridge + 1) + "/" + BRIDGE_CAPACITY + "]");
                mutex.acquire();
                peopleOnBridge++;
                mutex.release();
                Thread.sleep((int) (Math.random() * 1000));
                mutex.acquire();
                peopleOnBridge--;
                mutex.release();
                if(side.equals("Left")){
                    side = "Right";
                }else{
                    side = "Left";
                }

            } catch (InterruptedException e) {
            }
        }

        private void justLive() {
            try {
                System.out.println(getName() + " working/getting education " + side);
                Thread.sleep((int) (Math.random() * 10000));
            } catch (InterruptedException e) {
            }
        }
    }
}
