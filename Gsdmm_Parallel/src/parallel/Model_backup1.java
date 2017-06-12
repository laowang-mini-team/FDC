package parallel;//package parallel;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
//import java.util.ArrayList;
//import java.util.Random;
//
//public class Model_backup1 {
//	int K;
//	double alpha;
//	double beta;
//	String dataset;
//	String ParametersStr;
//	int V;
//	int D;
//	int iterNum;
//	double alpha0;
//	double beta0;
//	int[] z;
//	int[] m_z;
//	int[][] n_zv;
//	int[] n_z;
//
//	double[] numerator_index_num;
//	int[] numerator_index_overflow;
//	double[] denominator_index_num;
//	int[] denominator_index_overflow;
//
//	int[] labels;
//	Random random;
//
//	int thread_num;
//	Sampling[] threads;
//
//
//	public Model_backup1(int K, int V, int iterNum, double alpha, double beta,
//                         String dataset, String ParametersStr)
//	{
//		this.dataset = dataset;
//		this.ParametersStr = ParametersStr;
//		this.alpha = alpha;
//		this.beta = beta;
//		this.K = K;
//		this.V = V;
//		this.iterNum = iterNum;
//		this.alpha0 = K * alpha;
//		this.beta0 = V * beta;
//
//		this.m_z = new int[K];
//		this.n_z = new int[K];
//		this.n_zv = new int[K][V];
//		for(int k = 0; k < K; k++){
//			this.n_z[k] = 0;
//			this.m_z[k] = 0;
//			for(int t = 0; t < V; t++){
//				this.n_zv[k][t] = 0;
//			}
//		}
//	}
//
//	public void intialize(DocumentSet documentSet) throws Exception {
//
//		random = new Random(123);
//		D = documentSet.D;
//		z = new int[D];
//		for(int d = 0; d < D; d++){
//			Document document = documentSet.documents.get(d);
//			int cluster = (int) (K * random.nextDouble());
//			z[d] = cluster;
//			m_z[cluster]++;
//			for(int w = 0; w < document.wordNum; w++){
//				int wordNo = document.wordIdArray[w];
//				int wordFre = document.wordFreArray[w];
//				n_zv[cluster][wordNo] += wordFre;
//				n_z[cluster] += wordFre;
//			}
//		}
//		labels = Main.load_labels("data/" + dataset);
//	}
//
//	public void setMultiThread(DocumentSet documentSet,int thread_num,boolean useIndex) throws Exception {
//		this.thread_num = thread_num;
//		threads = new Sampling[thread_num];
//		int D_sub = (int)Math.ceil((double)D/(double)thread_num);
//		System.out.println("D:" + D + ", D_sub: " + D_sub);
//		ArrayList<Document> d = documentSet.documents;
//		for(int i = 0;i < thread_num;i++){
//			threads[i] = new Sampling();
//			threads[i].K = K;
//			threads[i].alpha = alpha;
//			threads[i].alpha0 = alpha0;
//			threads[i].beta = beta;
//			threads[i].beta0 = beta0;
//			threads[i].denominator_index_num = denominator_index_num;
//			threads[i].denominator_index_overflow = denominator_index_overflow;
//			threads[i].numerator_index_num = numerator_index_num;
//			threads[i].numerator_index_overflow = numerator_index_overflow;
//			threads[i].D_sub = (i == thread_num - 1 ? D - D_sub * i : D_sub);
//			System.out.println("th_D_sub: " + threads[i].D_sub);
//			Document[] documents = new Document[D_sub];
//			int count = 0;
//			int[] thread_z = new int[threads[i].D_sub];
//			for(int j = i * D_sub;j < i * D_sub + threads[i].D_sub;j++){
//				documents[count] = d.get(j);
//				thread_z[count] = z[j];
//				count++;
//			}
//			threads[i].z = thread_z;
//			threads[i].documents = documents;
//			threads[i].useIndex = useIndex;
//			threads[i].random = new Random(123);
//		}
//	}
//
//	public void gibbsSampling(DocumentSet documentSet, boolean useIndex)
//	{
//
//		for(int j = 0;j < thread_num;j++){
////			threads[j].n_z = n_z.clone();
////			threads[j].n_zv = n_zv.clone();
////			threads[j].m_z = m_z.clone();
//
//			threads[j].n_z = new int[K];
//			threads[j].n_zv = new int[K][V];
//			threads[j].m_z = new int[K];
////			for(int k = 0;k < K;k++){
////				threads[j].n_z[k] = n_z[k];
////				threads[j].m_z[k] = m_z[k];
////				for(int v = 0;v < V;v++){
////					threads[j].n_zv[k][v] = n_zv[k][v];
////				}
////			}
////
////			threads[j].n_z_change = new int[K];
////			threads[j].n_zv_change = new int[K][V];
////			threads[j].m_z_change = new int[K];
//		}
//
//		for(int i = 0; i < iterNum; i++){
//			for(int j = 0;j < thread_num;j++){
//				for(int k = 0;k < K;k++){
//					threads[j].n_z[k] = n_z[k];
//					threads[j].m_z[k] = m_z[k];
//					for(int v = 0;v < V;v++){
//						threads[j].n_zv[k][v] = n_zv[k][v];
//					}
//				}
//				threads[j].n_z_change = new int[K];
//				threads[j].n_zv_change = new int[K][V];
//				threads[j].m_z_change = new int[K];
//				threads[j].run();
//			}
//
//			for(int j = 0;j < thread_num;j++){
//				int[] n_z_change = threads[j].n_z_change;
//				int[] m_z_change = threads[j].m_z_change;
//				int[][] n_zv_change = threads[j].n_zv_change;
//				for(int k = 0;k < K;k++){
//					m_z[k] += m_z_change[k];
//					n_z[k] += n_z_change[k];
//					for(int v = 0;v < V;v++){
//						n_zv[k][v] += n_zv_change[k][v];
//					}
//				}
//			}
////			boolean same = true;
////			for(int j = 0;j < K;j++){
////				for(int k = 0;k < V;k++){
////					if(n_zv[j][k]!=threads[0].n_zv[j][k]){
////						same  = false;
////					}
////				}
////			}
////			if(same){
////				System.out.println("what??");
////			}
////			Java_MI.compute_normalized_mutual_information(z, labels);
//		}
//
//		int temp = 0;
//		for(int i = 0;i < thread_num;i++){
//			for(int j = 0;j < threads[i].D_sub;j++){
//				z[j + temp] = threads[i].z[j];
//			}
//			temp += threads[i].D_sub;
//		}
//	}
//
//
//
//	public void output(DocumentSet documentSet, String outputPath) throws Exception
//	{
//		String outputDir = outputPath + dataset + ParametersStr + "/";
//
//		File file = new File(outputDir);
//		if(!file.exists()){
//			if(!file.mkdirs()){
//				System.out.println("Failed to create directory:" + outputDir);
//			}
//		}
//
//		outputClusteringResult(outputDir, documentSet);
//	}
//
//	public void outputClusteringResult(String outputDir, DocumentSet documentSet) throws Exception
//	{
//		String outputPath = outputDir + dataset + "ClusteringResult.txt";
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
//				(new FileOutputStream(outputPath), "UTF-8"));
//		for(int d = 0; d < documentSet.D; d++){
//			int topic = z[d];
//			writer.write(topic + "\n");
//		}
//		writer.flush();
//		writer.close();
//	}
//
//
//
//
//}
