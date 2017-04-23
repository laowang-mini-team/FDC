import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Created by ttx on 21/04/2017.
 */
public class Index {
    public static void main(String[] args) throws FileNotFoundException {
        double start = System.currentTimeMillis();
        double largeDouble = 1e100;
        int len = 2000000;
        String path = "result/index";
        calculateIndex(len ,largeDouble, path);
        double end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public static void calculateIndex(int len, double largeDouble, String path ) throws FileNotFoundException {
        double[] num = new double[len];
        int[] overflow = new int[len];
        num[0] = 1;
        for(int i = 1;i < len;i++){
            num[i] = num[i-1] * i;
            if(num[i] > largeDouble){
                overflow[i] = overflow[i-1] + 1;
                num[i] /= largeDouble;
            }else{
                overflow[i] = overflow[i-1];
            }
        }
    }

}
