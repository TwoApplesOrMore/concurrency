import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorAutoRAI {

    // Define the amount of people who can enter the RAI at maximum
    private static final int PLACE_FOR_VISITORS = 10;

    // lock and all conditions
    private Lock lock;
    private Condition visitorplaceAvailable;
    private Condition visitorQueAvailable;
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
        visitorQueAvailable = lock.newCondition();

    }


    /**
     * Generic entrance method, to streamline distribution of buyers and visitors     *
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
     * Generic exit method, to streamline distribution of buyers and visitors
     * @param person Thread calling this method, either Buyer or Visitor
     */
    public void leave(Thread person) {
        if (person instanceof Buyer) {
            leaveBuyer((((Buyer) person)));
        } else {
            leaveVisitor(((Visitor) person));
        }
    }

    /**
     * Gets called when a buyer attends the autoRAI. Simulates a buyer waiting in line until he gets access
     */
    private void attendAsBuyer(Buyer buyer) {
        lock.lock();
        try {
            buyersWaiting++;

            while (!buyerAllowed()) {
                buyerplaceAvailable.await();
            }

            buyersWaiting--;

            buyerInside = true;

            System.out.println("Buyer " + buyer.getThreadName() + " went inside as buyer "+buyerInside);

        } catch (Exception e) {
            System.out.println("AttendAsBuyer crashed: " + e.toString());
        } finally {
            lock.unlock();
        }

    }

    /**
     * Gets called when a buyer wants to leave autoRAI. Simulates a buyer leaving and signalling the next person in line
     */
    private void leaveBuyer(Buyer buyer) {
        lock.lock();
        try {

            buyerInside = false;
            successiveBuyers++;
            System.out.println("Buyer " + buyer.getThreadName() + " has left the place successive: "+ successiveBuyers);

            if ((successiveBuyers >= 4 || buyersWaiting == 0) && visitorsWaiting > 0) {
                visitorsFront = visitorsWaiting;
                visitorplaceAvailable.signalAll();
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
     * Gets called when a visitor wants to leave autoRAI. Simulates a visitor leaving and signalling the next person in line
     */
    private void leaveVisitor(Visitor visitor) {
        lock.lock();
        try {
            System.out.println("Visitor " + visitor.getThreadName() + " left the place");
            nrOfVisitors--;

            if(nrOfVisitors == 0 && successiveBuyers == 0){
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
     * Gets called when a visitor wants to attend autoRAI. Simulates a visitor waiting in line until he gets access.
     */
    private void attendAsVisitor(Visitor visitor) {
        lock.lock();
        try {

            // wait until allowed in
            while(visitorsFront > 0){
                visitorQueAvailable.await();
            }
            visitorsWaiting++;

            while (!visitorAllowed()) {

                visitorplaceAvailable.await();
            }


            visitorsWaiting--;
            if (visitorsFront > 0) {
                visitorsFront--;
            } else {
                successiveBuyers = 0;
                visitorQueAvailable.signalAll();
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

    /**
     * Checks of a buyer is allowed inside
     * @return boolean access
     */
    private  boolean buyerAllowed() {
        return (!buyerInside && nrOfVisitors == 0 && (successiveBuyers < 4 || visitorsWaiting == 0));
    }
    /**
     * Checks of a buyer is allowed inside
     * @return boolean access
     */
    private  boolean visitorAllowed() {
        return (!buyerInside && nrOfVisitors < PLACE_FOR_VISITORS && (buyersWaiting == 0 || successiveBuyers >= 4));
    }
}
