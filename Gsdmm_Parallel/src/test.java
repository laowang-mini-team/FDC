/**
 * Created by ttx on 2017/6/11.
 */
public class test extends  Thread{
    public void run(){

    }

    public void s() throws InterruptedException {
        test t = new test();
        t.start();
        t.join();
    }

    public static void main(String[] args) {
        test t = new test();
        t.start();
    }
}
