public class Main {
    private static final int NR_OF_BUYERS = 7;
    private static final int NR_OF_VISITORS = 20;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        MonitorAutoRAI autoRAI = new MonitorAutoRAI();
        Thread[] visitors = new Thread[NR_OF_VISITORS];
        Thread[] buyers = new Thread[NR_OF_BUYERS];


        for (int i = 0; i<NR_OF_BUYERS; i++) {
            buyers[i] = new Buyer("B" + i, autoRAI);
            buyers[i].start();
        }

        for (int i = 0; i<NR_OF_VISITORS; i++) {
            visitors[i] = new Visitor("V" + i, autoRAI);
            visitors[i].start();
        }
    }
}
