package mis2.markov;

import java.util.Vector;

import mis2.states.BbsState;
import mis2.util.ParametersContainer;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.DenseVector;;

public class MVA {
	private int[] block;
	private int[] capacity;
	private int[] server;
	private double[] ts;
	private double[] serviceRate;
	private Vector<BbsState[]> states;
	private Matrix qMatrix;
	private Matrix routingMatrix;
	
	private int M;    //n centri
	private int numJobs;    //n job
	private Matrix meanQueue;          //popolazione media
	private Matrix rapVisite;   //rapporto tra le visite
	private Matrix meanTime;          //tempo di risp
	private Matrix throughput;       //througput     
	private DenseVector globalMeanTime;
	private DenseVector globalMeanQueue;
	double sommaTot;

	public MVA(int numJobs, Matrix routingMatrix) {
		this.numJobs = numJobs;
		this.M = routingMatrix.numColumns();
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
		this.server = ParametersContainer.getServer();
		this.ts = ParametersContainer.getTs();
		this.serviceRate = ParametersContainer.getServiceRate();
		this.routingMatrix = routingMatrix;
		this.globalMeanTime = new DenseVector(M);
		this.globalMeanQueue = new DenseVector(M);
		meanQueue= new DenseMatrix(numJobs,M);          //popolazione media
		rapVisite= new DenseMatrix(M,M);   //rapporto tra le visite
		meanTime= new DenseMatrix(numJobs,M);          //tempo di risp
		throughput= new DenseMatrix(numJobs,M);       //througput
		
		this.rapVisite = new Gauss(this.routingMatrix).getRapVisite();
		
		risolviMva();
	}
	public void risolviMva(){
		System.out.println("RisolviMVA");
		int i, j, z;
		for(i=0; i<M; i++){
			meanQueue.set(0, i, 0);
//			System.out.printf("L"+i+" = ");
//			System.out.print(meanQueue.get(0,i));
//			System.out.printf("\n");
		}
		for(i=0;i<numJobs;i++){
			System.out.println("Job: "+(i+1));
			for(j=0;j<M;j++){
				if(capacity[j]==0){
					meanTime.set(i,j,ts[j]);
					System.out.printf("T"+j+" = ");
					System.out.print(meanTime.get(i,j));
					System.out.printf("\n");
				}
				else if(server[j]>1) {
					double appoggio = Math.ceil(this.meanQueue.get(i, j));
                    if(appoggio <= 0){
                        appoggio = 1;
                    }
                    meanTime.set(i, j, ts[j]/Math.min(appoggio, 3) * Math.max(meanQueue.get(i, j)-2, 0)+ts[j]);
                    System.out.printf("T"+j+" = ");
					System.out.print(meanTime.get(i,j));
					System.out.printf("\n");
                    //E[i][n-1] = (float) (Double.valueOf(E_tsi[i] / Math.min(appoggio, 3)) * (Math.max(E_n[i][n - 1] - 2, 0)) + E_tsi[i]);
				}
				else {
					meanTime.set(i,j,(ts[j]*(1+meanQueue.get(i,j))));
					System.out.printf("T"+j+" = ");
					System.out.print(meanTime.get(i,j));
					System.out.printf("\n");
				}
			}
			//System.out.printf("Sottociclo2.....\n");
			for(j=0; j<M; j++){
				sommaTot = 0;
				for(z=0; z<M; z++) {
					sommaTot=sommaTot+(meanTime.get(i,z)*rapVisite.get(z,j));
				}
				throughput.set(i, j, (i+1)/sommaTot);
				meanQueue.set(i,j,(throughput.get(i,j)*meanTime.get(i,j)));
				System.out.printf("X"+j+" = ");
				System.out.print(throughput.get(i,j));
				System.out.printf("\n");
				System.out.printf("L"+j+" = ");
				System.out.print(meanQueue.get(i,j));
				System.out.printf("\n");
			}
		}
		for(int njob=1;numJobs>=njob;njob++){
			System.out.println("Njob ="+njob);
			for(int p=0; p<this.M; p++){
				globalMeanTime.set(p, 0.0);
				for(int g=0; g<this.M; g++){
					if(g!=p) {
						globalMeanTime.set(p,globalMeanTime.get(p)+rapVisite.get(g,p)*meanQueue.get(njob-1,g));
					}
				}
				System.out.println("Etr = "+globalMeanTime.get(p));
			}
		}
	}
	
	public static void main(String []args){
		//new MVA();
	}

}
