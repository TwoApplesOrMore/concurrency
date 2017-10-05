import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorAutoRAI {

    // Define the amount of people who can enter the RAI at maximum
    private static final int PLACE_FOR_VISITORS = 10;

    // lock and all conditions
    private Lock lock;
    private Condition visitorplaceAvailable;
    private Condition buyerplaceAvailable;

    //integer to keep track of how many people are in
    private int nrOfVisitors = 0;
    private boolean buyerInside = false;

    private int buyersWaiting = 0;
    private int visitorsWaiting = 0;
    private int visitorsFront = 0;

    private int successiveBuyers = 0;

    public MonitorAutoRAI() {
        lock = new ReentrantLock();
        visitorplaceAvailable = lock.newCondition();
        buyerplaceAvailable = lock.newCondition();

    }


    /**
     * Generic entrance method, to streamline distribution of buyers and visitors
     * This function is NOT needed, but streamlines and makes things prettier
     *
     * @param person Thread calling this method, either Buyer or Visitor
     */
    public void attend(Thread person) {
        if (person instanceof Buyer) {
            attendAsBuyer(((Buyer) person));
        } else {
            attendAsVisitor(((Visitor) person));
        }
    }

    /**
     * Generic method to create unity between Buyer and Visitor (Like attend)
     * Has the same function as the attend function, to streamline and distribute Buyers and Visitors
     *
     * @param person
     */
    public void leave(Thread person) {
        if (person instanceof Buyer) {
            leaveBuyer((((Buyer) person)));
        } else {
            leaveVisitor(((Visitor) person));
        }
    }

    /**
     * Specific handler for buyers
     */
    private void attendAsBuyer(Buyer buyer) {
        lock.lock();
        try {
            // wait until allowed in
            buyersWaiting++;
            // make sure there are no other visitors or buyers
            while (!noPeople()) {
                //place in que
                buyerplaceAvailable.await();

            }

            buyersWaiting--;




            buyerInside = true;
            System.out.println("Buyer " + buyer.getThreadName() + " went inside as buyer "+buyerInside);
            // go in
            // close all entrances for others
        } catch (Exception e) {
            System.out.println("AttendAsBuyer crashed: " + e.toString());
        } finally {
            lock.unlock();
        }

    }

    /**
     * Called when a buyer wants to leave auto RAI
     */
    public void leaveBuyer(Buyer buyer) {
        lock.lock();
        try {

            // Leave room

            // check if the last of buyers, or #4

            // free room in monitor, and wake up queue depending on result
            buyerInside = false;
            System.out.println("Buyer " + buyer.getThreadName() + " has left the place " + buyerInside);
            successiveBuyers++;
            if (successiveBuyers >= 4) {
                visitorsFront = visitorsWaiting;
                visitorplaceAvailable.signal();
            }else {
                buyerplaceAvailable.signal();
            }

        } catch (Exception e) {
            System.out.println("AttendAsBuyer crashed: " + e.toString());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Called when a visitor wants to leave autoRAI
     */
    private void leaveVisitor(Visitor visitor) {
        lock.lock();
        try {
            System.out.println("Visitor " + visitor.getThreadName() + " left the place");
            nrOfVisitors--;

            if(nrOfVisitors == 0 && successiveBuyers ==0){
                buyerplaceAvailable.signal();
            }else{
                visitorplaceAvailable.signal();
            }
            //
        } catch (Exception e) {
            System.out.println("AttendAsBuyer crashed: " + e.toString());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Specific handler for Visitors
     */
    private void attendAsVisitor(Visitor visitor) {
        lock.lock();
        try {
            visitorsWaiting++;
            // wait until allowed in
            while (!visitorAllowed()) {
                visitorplaceAvailable.await();
            }
            visitorsWaiting--;
            if (visitorsFront > 0) {
                visitorsFront--;
            } else if(visitorsFront <= 0) {
                successiveBuyers = 0;
            }
            // add another visitor to keep track how many are in
            nrOfVisitors++;
            System.out.println("Visitor " + visitor.getThreadName() + " entered. People inside: " + nrOfVisitors);
            //
        } catch (Exception e) {
            System.out.println("AttendAsVisitor crashed: " + e.toString());
        } finally {
            lock.unlock();
        }
    }

    private synchronized boolean noPeople() {
        return (!buyerInside && nrOfVisitors == 0 && successiveBuyers < 4);
    }

    private synchronized boolean visitorAllowed() {
        return (!buyerInside && nrOfVisitors < PLACE_FOR_VISITORS && (buyersWaiting == 0 || successiveBuyers >= 4));
    }
}
