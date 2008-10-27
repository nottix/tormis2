package mis2.markov;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Vector;

import mis2.states.BbsState;
import mis2.util.ParametersContainer;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.DenseVector;

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
	  private int NUM_JOB;          // Numero di jobs nella rete
	private Matrix meanQueue;          //popolazione media
	private Matrix rapVisite;   //rapporto tra le visite
	private Matrix meanTime;          //tempo di risp
	private Matrix throughput;       //througput     
	private DenseVector globalMeanTime;
	private DenseVector globalMeanQueue;
	private DenseVector U;
	double sommaTot;
	private String index;
	private final int NMAX = 30;

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
		rapVisite= new DenseMatrix(M,M);   //rapporto tra le visite
		this.rapVisite = new Gauss(this.routingMatrix).getRapVisite();
		 for(int ni=1;ni<=NMAX;ni++){   
	         index += "\tN = " + ni + " --> " ;
	         setVariables(ni);
	         makeMVA();
	         makeIndex();
	     } 
		
	}	
	/**
     * Inizializza gli indici di prestazione.
     * @param N Numero di job della rete
     */
    private void setVariables(int N){
        this.NUM_JOB = N;
       // E = new float[M][NUM_JOB];
    	meanTime= new DenseMatrix(M,NUM_JOB);          //tempo di risp
	//lambda = new float[ncentri][NUM_JOB];
    	throughput= new DenseMatrix(M,NUM_JOB);       //througput
      //  E_n = new float[ncentri][NUM_JOB+1];
        meanQueue= new DenseMatrix(M,NUM_JOB+1);  
    }
    
    /**
     * Cuore della classe. Implementa l'algoritmo di Mean Value Analisys.
     */
    private void makeMVA(){
        
        double appoggio=0;
        
        for(int n=1; n<=NUM_JOB; n++){
	    // Vado a ciclare sui jobs
	    for(int i=0; i<M; i++){
		  if(i==0)
			//E[i][n-1] = ts[i];
			  meanTime.set(i,n-1,ts[i]);
		  else if(i==6){  // Caso in cui il centro sia multiservente
		//	appoggio = Math.ceil(E_n[i][n-1]);
			appoggio = Math.ceil(meanQueue.get(i,n-1));
                        if(appoggio <= 0){
                            appoggio = 1;
                        }
                       // E[i][n-1] = (float) (Double.valueOf(E_tsi[i] / Math.min(appoggio, 3)) * (Math.max(E_n[i][n - 1] - 2, 0)) + E_tsi[i]);
                        meanTime.set(i,n-1,(float) (Double.valueOf(ts[i] / Math.min(appoggio, 3)) * (Math.max( meanQueue.get(i,n - 1) - 2, 0)) + ts[i]));        
		  }
                  else
			// Vado a ciclare sui centri
               meanTime.set(i,n-1, ts[i]*(1+meanQueue.get(i,n-1)));
	    
            }

            // Calcolo del throughtput e del tempo di accodamento
	    for(int i=0; i<M; i++){
		float tmp = 0;
		for(int j=0; j<M; j++){
		  //  tmp += v[j][i]*E[j][n-1];
			tmp+=rapVisite.get(j,i)*meanTime.get(j,n-1);
	        }
		//lambda[i][n-1] = (n)/(tmp);
		throughput.set(i,n-1,n/tmp);
		//E_n[i][n] = lambda[i][n-1]*E[i][n-1];
		meanQueue.set(i,n,throughput.get(i,n-1)* meanTime.get(i,n-1));
	    }
				
	}

     }
    
    /**
     * Routine che fornisce gli indici di prestazione derivanti da MVA.
     */
    private void makeIndex(){
        
	
	for(int i=0 ; i<this.M; i++){
		this.U = new DenseVector(M);
		//  U[i] = this.lambda[i][this.NUM_JOB-1] * E_tsi[i];
            U.set(i,this.throughput.get(i,this.NUM_JOB-1) * ts[i]);
				
	}
			
	for(int i=0; i<this.M; i++){
		globalMeanTime.set(i,0.0);
            for(int j=0; j<this.M; j++){
		if(i!=j){
			globalMeanTime.set(i,globalMeanTime.get(i)+ rapVisite.get(j,i)* meanTime.get(j,this.NUM_JOB-1));
		}
            }
				
	}
	for(int i=0; i<this.M; i++){
		globalMeanQueue.set(i,globalMeanTime.get(i)+meanTime.get(i,this.NUM_JOB-1));
	}
	
        index += ""+globalMeanTime.get(0) + "\n";
        save2file();
    }
    private void save2file(){
        
        PrintStream file = null;
        BufferedWriter newFile = null;
        
        try{
            if(!(new File("./indexMVA.txt").exists())){
                newFile = new BufferedWriter(new FileWriter("./indexMVA.txt"));
                newFile.close();
            }
            file = new PrintStream(new File("./indexMVA.txt"));
        }
        catch(Exception e){
            e.printStackTrace();
        } 
        file.append(index);
        file.close();
     }
    
    
}
