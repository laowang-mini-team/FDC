import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * Created by ttx on 22/04/2017.
 */
public class test {
    public static void main(String[] args) {
       int[] test = new int[2];
       test[0] = 1;
       int[] cop = test;
       test= new int[2];
       test[0] = 2;
        System.out.println(cop[0]);
    }
}
