package parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class Java_MI {
    public static void main(String[] args) {
        double[] a1 = {1,0,1,1};
        double[] a2 = {2,1,2,2};
        System.out.println(compute_normalized_mutual_information(a1, a2));
    }

    static double compute_normalized_mutual_information(int[] aArray, int[] bArray) {
        double[] a = new double[aArray.length];
        for(int i = 0;i < a.length;i++){
            a[i] = (int)aArray[i];
        }
        double[] b = new double[bArray.length];
        for(int i = 0;i < b.length;i++){
            b[i] = (int)bArray[i];
        }
        return compute_normalized_mutual_information(a, b);
    }

    static double compute_mutual_information(double[] aArray, double[] bArray) {
        if(aArray.length != bArray.length) {
            System.err.print("Size of input arrays are not equal!");
            System.exit(-1);
        }
        int length = aArray.length;
        Map<Double, ArrayList<Integer>> amap = getArrayValues(aArray);
        Map<Double, ArrayList<Integer>> bmap = getArrayValues(bArray);
        double mi = computeMI(amap, bmap, length);
        return mi;
    }

    static double compute_normalized_mutual_information(double[] aArray, double[] bArray) {
        double mi = compute_mutual_information(aArray, bArray);
        mi = mi / Math.max(Math.sqrt(compute_entropy(aArray)*compute_entropy(bArray)), 1e-10);
        return mi;
    }
    static double compute_entropy(double[] Array) {
        Map<Double, ArrayList<Integer>> map = getArrayValues(Array);
        Map<Double, Integer> amap = new HashMap<Double, Integer>();
        for (double av:map.keySet()) {
            ArrayList<Integer> indexs = map.get(av);
            amap.put(av, indexs.size());
        }
        if (amap.size() == 1) {
            return 1.0;
        }
        int length = Array.length;
        Set<Double> aset = map.keySet();
        Double[] arr = aset.toArray(new Double[aset.size()]);

        double entropy = 0;
        for(int i = 0; i < map.size(); i++) {
            double pi = (double) amap.get(arr[i]);
            double pi_sum = (double) length;
            entropy += (pi/pi_sum)*(Math.log(pi)-Math.log(pi_sum));
        }
        return Math.abs(entropy);
    }

    public static Map<Double,  ArrayList<Integer>> getArrayValues(double[] Array) {
        Map<Double,  ArrayList<Integer>> map = new HashMap<Double, ArrayList<Integer>>();
        for(int index = 0; index < Array.length; index++) {
            double value = Array[index];
            ArrayList<Integer> count = map.get(value);
            if(count == null) {
                ArrayList<Integer> scount = new ArrayList<Integer>();
                scount.add(index);
                map.put(value, scount);
            } else {
                count.add(index);
                map.put(value, count);
            }
        }
        return map;
    }

    static double computeEntropy(Map<Double, Integer> map, int num) {
        if (map.size() == 1) {
            return 1.0;
        }
        Set<Double> aset = map.keySet();
        Double[] arr = aset.toArray(new Double[aset.size()]);

        double entropy = 0;
        for(int i = 0; i < map.size(); i++) {
            double pi = (double) map.get(arr[i]);
            double pi_sum = (double) num;
            entropy += (pi/pi_sum)*(Math.log(pi)-Math.log(pi_sum));
        }
        return Math.abs(entropy);
    }

    public static  Map<String, Integer> getCrossMap(Map<Double,  ArrayList<Integer>> ainfo, Map<Double,  ArrayList<Integer>> tinfo, int length){
        Map<String, Integer> crossmap = new HashMap<String, Integer>();
        Map<Integer, Double> amap = new HashMap<Integer, Double>();
        Map<Integer, Double> tmap = new HashMap<Integer, Double>();

        for(double av:ainfo.keySet()) {
            ArrayList<Integer> indexs = ainfo.get(av);
            for(Integer index:indexs) {
                amap.put(index, av);
            }
        }

        for(double tv:tinfo.keySet()) {
            ArrayList<Integer> indexs = tinfo.get(tv);
            for(Integer index:indexs) {
                tmap.put(index, tv);
            }
        }

        for (int i=0; i<length; i++) {
            Double av = amap.get(i);
            Double tv = tmap.get(i);
            if (av == null) { av = (double) 0;}
            if(tv == null) {tv = (double) 0;}
            String cross = String.valueOf(av) + " " + String.valueOf(tv);
            Integer count = crossmap.get(cross);
            if(count == null) {
                count = 1;
            } else { count ++;}
            crossmap.put(cross, count);
        }
        return crossmap;
    }

    static double computeMI(Map<Double, ArrayList<Integer>> ainfo, Map<Double, ArrayList<Integer>> tinfo, int length) {
//		checkValidity(a, t);
        Map<Double, Integer> amap = new HashMap<Double, Integer>();
        Map<Double, Integer> tmap = new HashMap<Double, Integer>();
        for (double av:ainfo.keySet()) {
            ArrayList<Integer> indexs = ainfo.get(av);
            amap.put(av, indexs.size());
        }

        for (double tv:tinfo.keySet()) {
            ArrayList<Integer> indexs = tinfo.get(tv);
            tmap.put(tv, indexs.size());
        }

        Map<String, Integer> crossmap = getCrossMap(ainfo, tinfo, length);
        long alen = amap.size();
        long tlen = tmap.size();
        if (alen == tlen) {
            if(alen == 0 | alen == 1) {
                return 1.0;
            }
        }
        double numinst =(double) length;
        double sum = 0;
        Set<Double> aset = amap.keySet();
        Double[] arr = aset.toArray(new Double[aset.size()]);
        Set<Double> tset = tmap.keySet();
        Double[] trr = tset.toArray(new Double[tset.size()]);

        for (int i = 0;i < arr.length;i++) {
            double av = arr[i];
            for (int j = 0;j < trr.length;j++) {
                double tv = trr[j];
                String cross = String.valueOf(av) + " " + String.valueOf(tv);
                Integer value = crossmap.get(cross);
                if(value != null) {
                    double contingency_nm = (double) value/numinst;
                    double outer = (double) amap.get(av) * (double) tmap.get(tv);
                    double log_outer = -Math.log(outer) + 2*Math.log(numinst);
                    double sumtmp = contingency_nm*(Math.log(value)-Math.log(numinst)) + contingency_nm*log_outer;
                    sum += sumtmp;
                }
            }
        }
        return Math.abs(sum);
    }
}