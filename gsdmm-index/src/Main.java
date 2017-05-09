import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) throws Exception
    {
        int K = 20;
        double alpha = 0.1;
        double beta = 0.1;
        int iterNum = 5;
        String dataset = "20ng";
        GSDMM gsdmm = new GSDMM(K, alpha, beta, iterNum, dataset);

        long startTime = System.currentTimeMillis();
        gsdmm.getDocuments();
        long endTime = System.currentTimeMillis();
        System.out.println("getDocuments Time Used:" + (endTime-startTime)/1000.0 + "s");

//        gsdmm.scale(10);

        startTime = System.currentTimeMillis();
        gsdmm.calculateIndex(20000000, 1e100,0.1);
        endTime = System.currentTimeMillis();
        System.out.println("calculateIndex Time Used:" + (endTime-startTime)/1000.0 + "s");

        startTime = System.currentTimeMillis();
        gsdmm.runGSDMM(true,true, 2);
        endTime = System.currentTimeMillis();
        System.out.println("gibbsSampling Time Used:" + (endTime-startTime)/1000.0 + "s");

        int[] z = gsdmm.z;
        System.out.println(Java_MI.compute_normalized_mutual_information(z ,load_labels("data/" + dataset)));
    }

    static int[] load_labels(String path) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = "";
        ArrayList<Integer> labels = new ArrayList<>();
        while((line = in.readLine()) != null){
            JSONObject js = new JSONObject(line);
            labels.add(js.getInt("cluster"));
        }
        int[] result = new int[labels.size()];
        for(int i = 0;i < labels.size();i++){
            result[i] = labels.get(i);
        }
        return result;

    }
}
