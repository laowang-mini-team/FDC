import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Model{
	int K; 
	double alpha;
	double beta;
	String dataset;
	String ParametersStr;
	int V; 
	int D; 
	int iterNum; 
	double alpha0;
	double beta0;
	int[] z;
	int[] m_z; 
	int[][] n_zv;
	int[] n_z;
	double smallDouble = 1e-100;
	double largeDouble = 1e100;

	double[] numerator_index_num;
	int[] numerator_index_overflow;
	double[] denominator_index_num;
	int[] denominator_index_overflow;

	int[][] n_zv_change;//last D operation changes
	int[] from;

	double[][] numerator_last_num;// D K
	int[][] numerator_last_overflow;

	public Model(int K, int V, int iterNum, double alpha, double beta, 
			String dataset, String ParametersStr)
	{
		this.dataset = dataset;
		this.ParametersStr = ParametersStr;
		this.alpha = alpha;
		this.beta = beta;
		this.K = K;
		this.V = V;
		this.iterNum = iterNum;
		this.alpha0 = K * alpha;
		this.beta0 = V * beta;
		
		this.m_z = new int[K];
		this.n_z = new int[K];
		this.n_zv = new int[K][V];
		for(int k = 0; k < K; k++){
			this.n_z[k] = 0;
			this.m_z[k] = 0;
			for(int t = 0; t < V; t++){
				this.n_zv[k][t] = 0;
			}
		}
	}
	public void intialize(DocumentSet documentSet)
	{
		D = documentSet.D;
		z = new int[D];
		for(int d = 0; d < D; d++){
			Document document = documentSet.documents.get(d);
			int cluster = (int) (K * Math.random());
			z[d] = cluster;
			m_z[cluster]++;
			for(int w = 0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				n_zv[cluster][wordNo] += wordFre; 
				n_z[cluster] += wordFre; 
			}
		}
	}
	public void gibbsSampling(DocumentSet documentSet,boolean useIndex)
	{


		for(int i = 0; i < iterNum; i++){
			for(int d = 0; d < D; d++){
				Document document = documentSet.documents.get(d);
				int cluster = z[d];
				m_z[cluster]--;
				for(int w = 0; w < document.wordNum; w++){
					int wordNo = document.wordIdArray[w];
					int wordFre = document.wordFreArray[w];
					n_zv[cluster][wordNo] -= wordFre;
					n_z[cluster] -= wordFre;
				}

				if (useIndex){
						cluster = sampleCluster_useIndex(d, document);

				}else{
					cluster = sampleCluster(d, document);
				}

				z[d] = cluster;
				m_z[cluster]++;
				for(int w = 0; w < document.wordNum; w++){
					int wordNo = document.wordIdArray[w];
					int wordFre = document.wordFreArray[w];
					n_zv[cluster][wordNo] += wordFre;
					n_z[cluster] += wordFre;
				}
			}


		}
	}


	int count;
	public void gibbsSampling_wordProb(DocumentSet documentSet, int startI)
	{
		from = new int[D];
		n_zv_change = new int[K][V];
		numerator_last_num = new double[D][K];
		numerator_last_overflow = new int[D][K];


		for(int i = 0; i < iterNum; i++){
            double start = System.currentTimeMillis();

            count = 0;
		    if(i < startI){
                for(int d = 0; d < D; d++){
                    Document document = documentSet.documents.get(d);
                    int cluster = z[d];
                    m_z[cluster]--;
                    for(int w = 0; w < document.wordNum; w++){
                        int wordNo = document.wordIdArray[w];
                        int wordFre = document.wordFreArray[w];
                        n_zv[cluster][wordNo] -= wordFre;
                        n_z[cluster] -= wordFre;
                    }
                    cluster = sampleCluster_useIndex(d, document);
                    z[d] = cluster;
                    m_z[cluster]++;
                    for(int w = 0; w < document.wordNum; w++){
                        int wordNo = document.wordIdArray[w];
                        int wordFre = document.wordFreArray[w];
                        n_zv[cluster][wordNo] += wordFre;
                        n_z[cluster] += wordFre;
                    }
                }
            }else if(i == startI){
                for(int d = 0; d < D; d++){
                    Document document = documentSet.documents.get(d);
                    int cluster = z[d];
                    m_z[cluster]--;
                    for(int w = 0; w < document.wordNum; w++){
                        int wordNo = document.wordIdArray[w];
                        int wordFre = document.wordFreArray[w];
                        n_zv[cluster][wordNo] -= wordFre;
                        n_z[cluster] -= wordFre;
                    }
                    cluster = sampleCluster_useIndexAndwordProb_prepare(d, document);

                    from[d] = z[d];
                    if(cluster != z[d]){
                        for(int w = 0;w < document.wordNum;w++){
                            int wordNo = document.wordIdArray[w];
                            int wordFre = document.wordFreArray[w];
                            n_zv_change[z[d]][wordNo] -= wordFre;
                            n_zv_change[cluster][wordNo] += wordFre;
                        }
                    }
                    z[d] = cluster;
                    m_z[cluster]++;
                    for(int w = 0; w < document.wordNum; w++){
                        int wordNo = document.wordIdArray[w];
                        int wordFre = document.wordFreArray[w];
                        n_zv[cluster][wordNo] += wordFre;
                        n_z[cluster] += wordFre;
                    }
                }
            }else{
                for(int d = 0; d < D; d++){
                    Document document = documentSet.documents.get(d);
                    int cluster = z[d];
                    m_z[cluster]--;
                    for(int w = 0; w < document.wordNum; w++){
                        int wordNo = document.wordIdArray[w];
                        int wordFre = document.wordFreArray[w];
                        n_zv[cluster][wordNo] -= wordFre;
                        n_z[cluster] -= wordFre;
                    }


                    if(from[d] != z[d]){
                        for(int w = 0;w < document.wordNum;w++){
                            int wordNo = document.wordIdArray[w];
                            int wordFre = document.wordFreArray[w];
                            n_zv_change[from[d]][wordNo] += wordFre;
                            n_zv_change[z[d]][wordNo] -= wordFre;
                        }
                    }

                    cluster = sampleCluster_useIndexAndwordProb(d, document);

                    from[d] = z[d];
                    if(cluster != z[d]){
                        for(int w = 0;w < document.wordNum;w++){
                            int wordNo = document.wordIdArray[w];
                            int wordFre = document.wordFreArray[w];
                            n_zv_change[z[d]][wordNo] -= wordFre;
                            n_zv_change[cluster][wordNo] += wordFre;
                        }
                    }

                    z[d] = cluster;
                    m_z[cluster]++;
                    for(int w = 0; w < document.wordNum; w++){
                        int wordNo = document.wordIdArray[w];
                        int wordFre = document.wordFreArray[w];
                        n_zv[cluster][wordNo] += wordFre;
                        n_z[cluster] += wordFre;
                    }
                }
            }
            double end = System.currentTimeMillis();
            System.out.println(i + "," + (end - start));


		}
	}


	private int sampleCluster_useIndex(int d, Document document){
		double[] prob = new double[K];
		int[] overflowCount = new int[K];

		for(int k = 0; k < K;k++){
			prob[k] = (m_z[k] + alpha) / (D - 1 + alpha0);

			int index1 = n_z[k];
			int index2 = index1 + document.num;
			double denominator_num = denominator_index_num[index2] / denominator_index_num[index1];
			int denominator_overflow = denominator_index_overflow[index2] - denominator_index_overflow[index1];

			double numerator_num = 1.0;
			int numerator_overflow = 0;
			for(int w = 0;w < document.wordNum;w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				index1 = n_zv[k][wordNo];
				index2 = index1 + wordFre;
				numerator_num *= numerator_index_num[index2] / numerator_index_num[index1];
				numerator_overflow += numerator_index_overflow[index2] - numerator_index_overflow[index1];

				if(numerator_num > largeDouble){
					numerator_num /= largeDouble;
					numerator_overflow++;
				}
				while(numerator_num < smallDouble){
					numerator_num *= largeDouble;
					numerator_overflow--;
				}
			}
			prob[k] *= numerator_num/denominator_num;
//			if(prob[k] == 0){
//				System.out.println();
//			}
			overflowCount[k] = numerator_overflow - denominator_overflow;
		}

		reComputeProbs(prob, overflowCount, K);

		return chooseK(prob);
	}

	private int sampleCluster_useIndexAndwordProb(int d, Document document){
		double[] prob = new double[K];
		int[] overflowCount = new int[K];

		for(int k = 0; k < K;k++){
			prob[k] = (m_z[k] + alpha) / (D - 1 + alpha0);

			int index1 = n_z[k];
			int index2 = index1 + document.num;
			double denominator_num = denominator_index_num[index2] / denominator_index_num[index1];
			int denominator_overflow = denominator_index_overflow[index2] - denominator_index_overflow[index1];

//			double numerator_num = 1.0;
//			int numerator_overflow = 0;

//			int changeCount = 0;
//			for(int w = 0;w < document.wordNum;w++){
//			    int wordNo = document.wordIdArray[w];
//				if(n_zv_change[k][wordNo] != 0){
//					changeCount++;
//				}
//			}
//			if(changeCount > document.wordNum/2){
//				for(int w = 0;w < document.wordNum;w++){
//					int wordNo = document.wordIdArray[w];
//					int wordFre = document.wordFreArray[w];
//					index1 = n_zv[k][wordNo];
//					index2 = index1 + wordFre;
//
//					numerator_num *= numerator_index_num[index2] / numerator_index_num[index1];
//					numerator_overflow += numerator_index_overflow[index2] - numerator_index_overflow[index1];
//
//					if(numerator_num > largeDouble){
//						numerator_num /= largeDouble;
//						numerator_overflow++;
//					}
//					if(numerator_num < smallDouble){
//						numerator_num *= largeDouble;
//						numerator_overflow--;
//					}
//				}
//			}else{
				double value = 1;
				int overflow = 0;
				for(int w = 0; w < document.wordNum;w++){
					if(n_zv_change[k][w] != 0){
						int wordNo = document.wordIdArray[w];
						int wordFre = document.wordFreArray[w];
						int change = n_zv_change[k][wordNo];

						index1 = n_zv[k][wordNo];
						index2 = index1 + wordFre;
						value *= numerator_index_num[index2] / numerator_index_num[index1];
						overflow += numerator_index_overflow[index2] - numerator_index_overflow[index1];

						index1 = n_zv[k][wordNo] - change;
						index2 = index1 + wordFre;
						value *= numerator_index_num[index1] / numerator_index_num[index2];
						overflow += numerator_index_overflow[index1] - numerator_index_overflow[index2];

						if(value > largeDouble){
							value /= largeDouble;
							overflow++;
						}
						if(value < smallDouble){
							value *= largeDouble;
							overflow--;
						}
					}
				}
                numerator_last_num[d][k] *= value;
				numerator_last_overflow[d][k] += overflow;

			prob[k] *= numerator_last_num[d][k]/denominator_num;
			overflowCount[k] = numerator_last_overflow[d][k] - denominator_overflow;
		}

		reComputeProbs(prob, overflowCount, K);

		return chooseK(prob);
	}

	private int sampleCluster_useIndexAndwordProb_prepare(int d, Document document){
		double[] prob = new double[K];
		int[] overflowCount = new int[K];

		for(int k = 0; k < K;k++){
			prob[k] = (m_z[k] + alpha) / (D - 1 + alpha0);

			int index1 = n_z[k];
			int index2 = index1 + document.num;
			double denominator_num = denominator_index_num[index2] / denominator_index_num[index1];
			int denominator_overflow = denominator_index_overflow[index2] - denominator_index_overflow[index1];

			double numerator_num = 1.0;
			int numerator_overflow = 0;
			for(int w = 0;w < document.wordNum;w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				index1 = n_zv[k][wordNo];
				index2 = index1 + wordFre;

				numerator_num *= numerator_index_num[index2] / numerator_index_num[index1];
				numerator_overflow += numerator_index_overflow[index2] - numerator_index_overflow[index1];

				if(numerator_num > largeDouble){
					numerator_num /= largeDouble;
					numerator_overflow++;
				}
				while(numerator_num < smallDouble){
					numerator_num *= largeDouble;
					numerator_overflow--;
				}
			}
			numerator_last_num[d][k] = numerator_num;
			numerator_last_overflow[d][k] = numerator_overflow;
			prob[k] *= numerator_num/denominator_num;
			overflowCount[k] = numerator_overflow - denominator_overflow;

		}

		reComputeProbs(prob, overflowCount, K);
		return chooseK(prob);
	}

	private int sampleCluster(int d, Document document)
	{ 
		double[] prob = new double[K];
		int[] overflowCount = new int[K];

		for(int k = 0; k < K; k++){
			prob[k] = (m_z[k] + alpha) / (D - 1 + alpha0);
			double valueOfRule2 = 1.0;
			int i = 0;
			for(int w=0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				for(int j = 0; j < wordFre; j++){
					if(valueOfRule2 < smallDouble){
						overflowCount[k]--;
						valueOfRule2 *= largeDouble;
					}
					valueOfRule2 *= (n_zv[k][wordNo] + beta + j) 
							 / (n_z[k] + beta0 + i);
					i++;
				}
			}
			prob[k] *= valueOfRule2;			
		}
		
		reComputeProbs(prob, overflowCount, K);



		return chooseK(prob);
	}
	
	private void reComputeProbs(double[] prob, int[] overflowCount, int K)
	{
		int max = Integer.MIN_VALUE;
		for(int k = 0; k < K; k++){
			if(overflowCount[k] > max && prob[k] > 0){
				max = overflowCount[k];
			}
		}
		
		for(int k = 0; k < K; k++){			
			if(prob[k] > 0){
				prob[k] = prob[k] * Math.pow(largeDouble, overflowCount[k] - max);
			}
		}		
	}

	private int chooseK(double prob[]){
		for(int k = 1; k < K; k++){
			prob[k] += prob[k - 1];
		}
		double thred = Math.random() * prob[K - 1];
		int kChoosed;
		for(kChoosed = 0; kChoosed < K; kChoosed++){
			if(thred < prob[kChoosed]){
				break;
			}
		}
		if(kChoosed == 10){
			System.out.printf("");
		}
		return  kChoosed;
	}

	public void output(DocumentSet documentSet, String outputPath) throws Exception
	{
		String outputDir = outputPath + dataset + ParametersStr + "/";
		
		File file = new File(outputDir);
		if(!file.exists()){
			if(!file.mkdirs()){
				System.out.println("Failed to create directory:" + outputDir);
			}
		}
		
		outputClusteringResult(outputDir, documentSet);
	}

	public void outputClusteringResult(String outputDir, DocumentSet documentSet) throws Exception
	{
		String outputPath = outputDir + dataset + "ClusteringResult.txt";
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
				(new FileOutputStream(outputPath), "UTF-8"));
		for(int d = 0; d < documentSet.D; d++){
			int topic = z[d];
			writer.write(topic + "\n");
		}
		writer.flush();
		writer.close();
	}




}
