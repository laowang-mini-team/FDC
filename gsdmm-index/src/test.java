import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.Random;

/**
 * Created by ttx on 22/04/2017.
 */
public class test {
    public static void main(String[] args) {
        Random random = new Random(10);
        Random random2 = new Random(10);
        for(int i = 0; i < 5;i++){
            System.out.println(random.nextDouble() + "," + random2.nextDouble());
        }
    }
}
