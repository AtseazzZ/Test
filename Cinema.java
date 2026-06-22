package demo;

class Station extends Thread {

    // 共享的静态票数
    private static int tickets = 20;

    // 静态锁对象（保证锁唯一）
    private static final Object lock = new Object();

    public Station(String name) {
        super(name); // 设置线程名字， "窗口1"
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {  // 多线程同步锁
                if (tickets <= 0) {
                    System.out.println(getName() + "：票卖完了！");
                    break;
                }

                // 模拟出票
                System.out.println(getName() + " 卖出了第 " + (21 - tickets) + " 张票");
                tickets--;

                try {
                    Thread.sleep(100); // 模拟出票耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

public class Cinema {
    public static void main(String[] args) {

        Station s1 = new Station("窗口1");
        Station s2 = new Station("窗口2");
        Station s3 = new Station("窗口3");

        s1.start();
        s2.start();
        s3.start();
    }
}
