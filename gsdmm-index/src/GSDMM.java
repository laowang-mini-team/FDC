import java.io.FileNotFoundException;
import java.util.HashMap;

public class GSDMM
{
	int K;
	double alpha;
	double beta;
	int iterNum;
	String dataset;
	
	HashMap<String, Integer> wordToIdMap;
	int V;
	DocumentSet documentSet;
	String dataDir = "data/"; 
	String outputPath = "result/";
	int[] z;
	double[] numerator_index_num;
	int[] numerator_index_overflow;
	double[] denominator_index_num;
	int[] denominator_index_overflow;

	public GSDMM(int K, double alpha, double beta, int iterNum, String dataset) {
		this.K = K;
		this.alpha = alpha;
		this.beta = beta;
		this.iterNum = iterNum;
		this.dataset = dataset;
		this.wordToIdMap = new HashMap<String, Integer>();
	}
	
	public void getDocuments() throws Exception
	{
		documentSet = new DocumentSet(dataDir + dataset, wordToIdMap);
		V = wordToIdMap.size();
	}

	public void scale(int scale){
		for(Document document:documentSet.documents){
			for(int i = 0;i < document.wordFreArray.length;i++){
				document.wordFreArray[i] *= scale;
			}
			document.num *= scale;
		}
	}

	public void calculateIndex(int len, double largeDouble,double beta) throws FileNotFoundException {
		double[] num = new double[len];
		int[] overflow = new int[len];
		num[0] = 1;
		double basic = V * beta;
		for(int i = 1;i < len;i++){
			num[i] = num[i-1] * (basic + i - 1);
			if(num[i] > largeDouble){
				overflow[i] = overflow[i-1] + 1;
				num[i] /= largeDouble;
			}else{
				overflow[i] = overflow[i-1];
			}
		}
		this.denominator_index_num = num;
		this.denominator_index_overflow = overflow;

		num = new double[len];
		overflow = new int[len];
		num[0] = 1;
		basic = beta;
		for(int i = 1;i < len;i++){
			num[i] = num[i-1] * (basic + i -1);
			if(num[i] > largeDouble){
				overflow[i] = overflow[i-1] + 1;
				num[i] /= largeDouble;
			}else{
				overflow[i] = overflow[i-1];
			}
		}
		this.numerator_index_num = num;
		this.numerator_index_overflow = overflow;

//		denominator_index_num_reverse = new double[len];
//		numerator_index_num_reverse = new double[len];
//		for(int i = 0;i < len;i++){
//			denominator_index_num_reverse[i] = 1.0/denominator_index_num[i];
//
//			numerator_index_num_reverse[i] = 1.0/numerator_index_num[i];
//		}


	}

	
	public void runGSDMM(boolean useIndex,boolean useWordProb,int startI) throws Exception
	{
		String ParametersStr = "K"+K+"iterNum"+ iterNum +"alpha" + String.format("%.3f", alpha)
								+ "beta" + String.format("%.3f", beta);
		Model model = new Model(K, V, iterNum,alpha, beta, dataset,  ParametersStr);
		if(useIndex == true){
			model.numerator_index_num = numerator_index_num;
			model.numerator_index_overflow = numerator_index_overflow;

			model.denominator_index_num = denominator_index_num;
			model.denominator_index_overflow = denominator_index_overflow;
		}


		model.intialize(documentSet);

		if(useWordProb){
			model.gibbsSampling_wordProb(documentSet, startI);
		}else{
			model.gibbsSampling(documentSet,useIndex);
		}
		model.output(documentSet, outputPath);
		this.z = model.z;
	}



}
