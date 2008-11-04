package mis2.markov;

import java.util.Vector;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import mis2.util.*;
import mis2.states.BbsState;
import mis2.util.ParametersContainer;

public class IndexCalculator {

	private int[] server;
	private Vector<BbsState[]> states;
	private DenseVector pi;
	private int numJobs;
	private Matrix routingMatrix;
	private double[] serviceRate;
	private int[] block;
	private int[] capacity;

	private DenseVector totU;
	private DenseVector totX;
	private DenseVector totL;
	private DenseVector totT;

	/**
	 * 
	 * @param numJobs
	 * @param states
	 * @param pi
	 * @param routingMatrix
	 */
	public IndexCalculator(int numJobs, Vector<BbsState[]> states, DenseVector pi, Matrix routingMatrix) {
		this.routingMatrix = routingMatrix;
		this.server = ParametersContainer.getServer();
		this.capacity = ParametersContainer.getCapacity();
		this.serviceRate = ParametersContainer.getServiceRate();
		this.block = ParametersContainer.getBlock();
		this.numJobs = numJobs;
		this.states = states;
		this.pi = pi;

		this.totU = new DenseVector(this.block.length);
		this.totX = new DenseVector(this.block.length);
		this.totL = new DenseVector(this.block.length);
		this.totT = new DenseVector(this.block.length);

	}

	private double calcF(int n) {
		return (n==0)? 0.0 : 1.0;
	}

//	public double calcPi(int X, int n) {
//
//		double total = 0.0;
//		if(capacity[X]==0) 
//		{
//			for(int S=0; S<states.size(); S++) {
//				if(states.get(S)[X].getNum() == n) {
//					total += pi.get(S);
//				}
//			}
//		}
//		else 
//		{
//			for(int S=0; S<states.size(); S++) {
//				if(this.calcPiCond(X, S, n)) {
//					total += pi.get(S);
//				}
//			}
//		}
//		return total;
//	}

	/**
	 * 
	 * 
	 * @param i
	 * @param indexState indice dello stato desiderato
	 * @return
	 */
//	private boolean calcPiCond(int X, int S, int n) {
//
//		//RS-RD
//		if(this.block[X]==1) 
//		{ 
//			return (this.states.get(S)[X].getNum() == n);
//		}
//
//		//BBS-SO
//		else if(this.block[X]==0) 
//		{ 
////			if( (this.states.get(S)[X].isBlocked() && 
////			(n==this.states.get(S)[X].getNum()) ) ||
////			((!this.states.get(S)[X].isBlocked()) &&
////			(n==this.states.get(S)[X].getNum())) ) {
//			if( n==this.states.get(S)[X].getNum() ) {
//				return true;
//			}
//		}
//
//		return false;
//	}


//	public double calcZeta(int X, int n, int z) {
//		double total = 0.0;
//		for(int S=0; S<states.size(); S++) {
//			if(this.calcZetaCond(X, S, n, z)) {
//				total += pi.get(S);
//			}
//		}
//		return total;
//	}

	/**
	 * 0 <= z_i <= min(n_i, K_i)
	 * 
	 * @return
	 */
//	private boolean calcZetaCond(int X, int S, int n, int z) {
//
//		//RS-RD
//		if(this.block[X]==1) 
//		{ 
//			return (this.states.get(S)[X].getNum() == n) && 
//			(z == Math.min(n, server[X]));
//		}
//
//		//BBS-SO
//		if(this.block[X]==0) 
//		{ 
//			if( this.states.get(S)[X].isBlocked() && 
//					(n==this.states.get(S)[X].getNum()) ) 
//			{
//				int totalNs = 0;
//				totalNs += this.states.get(S)[X].getNS(0);
//				for(int k=0; k<this.states.get(S)[X].getDest().size(); k++) {
//					if(this.states.get(S)[k].getNum() < capacity[k]) {
//						totalNs += this.states.get(S)[X].getNS(k);
//					}
//				}
//				if(z == totalNs) {
//					return true;
//				}
//				return false;
//			}
//			else if( (!this.states.get(S)[X].isBlocked()) && 
//					(n==this.states.get(S)[X].getNum()) ) 
//			{
//				if(z == Math.min(n, server[X])) {
//					return true;
//				}
//				return false;
//			}
//		}
//
//		return false;
//	}

	public double calcUtilizationOf(int i) {
		double utilization = 0;
//
//		if(capacity[i]==0) 
//		{       // Senza blocco
//			for(int n=1; n<=numJobs; n++) {
//				utilization += this.calcPi(i, n);
//			}
//		}
//		else 
//		{       // RS-RD, BBS
//			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
//				utilization += ( ((double)Math.min(n, server[i]))/
//						((double)server[i]) )*
//						this.calcPi(i, n);
//			}
//		}

		utilization = this.calcEffectiveUtilizationOf(i);
		return utilization;
	}

	public double calcEffectiveUtilizationOf(int i) {

		double utilization = 0;

		// BBS-SO
		if(block[i]==0) 
		{
			
			
//			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
//				for(int z=1; z<Math.min(n, server[i]); z++) {
//					utilization += ((double)z/(double)server[i])*
//					this.calcZeta(i, n, z);
//				}
//			}
			
//			for(int n=0; n<stati.size(); n++){
//				if(i==6){
//					if((((int[])((Vector)stati.get(n)).get(i))[0]!=0)|| (((Integer)((Vector)stati.get(n)).get(7)).intValue()==Network.queueCapacity[8]))
//						ris=ris+((probQLengthDistribution[n]*((int[])((Vector)stati.get(n)).get(i))[1])/Network.numServenti[i+1]);
//				}
//				else{
//					if((((int[])((Vector)stati.get(n)).get(i))[0]==0) || (((Integer)((Vector)stati.get(n)).get(5)).intValue()==Network.queueCapacity[6]))
//						ris=ris+probQLengthDistribution[n];
//				}
//			}
//			if(i==6){
//				res[i]=ris;
//			}
//			else
//				res[i]=1-ris;
			
			for(int stato=0; stato<this.states.size(); stato++){
//				if(server[i]>1) {
//					if(this.states.get(stato)[i].getNum()!=0 || this.states.get(stato)[i].getNum()==this.capacity[i]) {
//						utilization += this.pi.get(stato)*this.states.get(stato)[i].getNum()/this.server[i];
//					}
//				}
//				else if(this.states.get(stato)[i].getNum()==0 || this.states.get(stato)[i].getNum()==this.capacity[i]) {
//					utilization += this.pi.get(stato);
//				}
					
//				if(this.states.get(stato)[i].getNum()>0 && this.states.get(stato)[i].getNum()<this.capacity[i]) {
//					for(int d=0; d<this.states.get(stato)[i].getDest().size(); d++) {
//						int j = this.states.get(stato)[i].getDest().get(d);
//						if(this.states.get(stato)[j].getNum()<this.capacity[j]) {
//							if(this.server[i]>1)
//								utilization += this.pi.get(stato)*Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i];
//							else
//								utilization += this.pi.get(stato);
////						}
////					}
//
//		        	//matrix_stato_2[stato][(nodo*2)+1] >= multiserver)){ 
//		        }
				if(this.states.get(stato)[i].getNum()>0 /*&& this.states.get(stato)[i].getNum()>=this.capacity[i]*/){
					for(int j=0; j<this.block.length; j++){
						if(this.routingMatrix.get(i, j) != 0){
							if(this.states.get(stato)[j].getNum()<this.capacity[j]) {
//								if(this.server[i]>1)
//								utilization += this.pi.get(stato)*Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i];
//								else
								utilization += this.pi.get(stato);
							}
						}
					}

				}
		         //   Ue += (probabilita.get(stato)*Math.min(multiserver, (matrix_stato_2[stato][nodo*2] - matrix_stato_2[stato][(nodo*2)+1]))) / multiserver;
		    }
		}	// RS-RD
		else if(block[i]==1) 
		{
//			for(int n=1; n<Math.min(this.capacity[i], this.numJobs); n++) {
			for(int stato=0; stato<this.states.size(); stato++){
				if(this.states.get(stato)[i].getNum()>0) {
		            for(int j=0; j<this.block.length; j++){
		                if(this.routingMatrix.get(i, j) != 0){
		                    if(!(this.states.get(stato)[j].getNum() >= this.capacity[j] && this.capacity[j] > 0)){ //Capacità = 0 equivale ad infinito
		                                utilization += pi.get(stato) * this.routingMatrix.get(i, j);
		                    }
		                }
		            }
		        }
//				else if(this.states.get(stato)[i].getNum()>0 && this.capacity[i]>0){
////				else if(this.states.get(stato)[i].getNum()>0 && this.states.get(stato)[i].getNum()>=this.capacity[i]){
//					for(int d=0; d<this.states.get(stato)[i].getDest().size(); d++) {
//						int j = this.states.get(stato)[i].getDest().get(d);
//						if(this.routingMatrix.get(i, j) != 0){
//							utilization += this.pi.get(stato)*this.routingMatrix.get(i, j);
//						}
//						else
//							utilization += this.pi.get(stato);
////						if(this.states.get(stato)[j].getNum()<this.capacity[j]) {
////							if(this.server[i]>1)
////								utilization += this.pi.get(stato)*Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i];
////							else
////								utilization += this.pi.get(stato);
////						}
//					}
//
//				}

		    }
			
			
//			double nested = 0.0;
//			for(int k=1; k<Math.min(capacity[i], numJobs); k++) {
//				for(int n=0; n<this.states.size(); n++) {
//					if(this.states.get(n)[i].getNum() == k) {
//						for(int j=0; j<capacity.length; j++) {
//							if(this.states.get(n)[j].getNum() < capacity[j])
//								nested += this.pi.get(n)*
//								this.routingMatrix.get(i, j);
//						}
//					}
//				}
//				utilization += ((double)Math.min(k, server[i])/(double)server[i])
//				*nested;
//				nested = 0.0;
//			}
		}
		//System.out.println("I: "+i+", U"+utilization);

		return utilization;
	}

	public double calcThroughputOf(int i) {

		double throughput = 0.0;
		
		//throughput += this.serviceRate[i]*this.calcEffectiveUtilizationOf(i)*this.server[i];

		if(this.capacity[i]==0 || this.capacity[i]>=1) {
			//throughput = 0;
			//throughput = this.serviceRate[i]*this.calcUtilizationOf(i);


			for(int stato=0; stato<this.states.size(); stato++) {
				if(states.get(stato)[i].getNum() > 0) {
					for(int j=0; j<this.block.length; j++) {
						if(this.routingMatrix.get(i, j) != 0) {
							if(!(this.states.get(stato)[j].getNum() >= this.capacity[j] && this.capacity[j] > 0)) { //Capacità = 0 equivale ad infinito
								double mu = this.states.get(stato)[i].getNum() * this.serviceRate[i];
								throughput += mu * this.pi.get(stato) * this.routingMatrix.get(i, j);
							}
						}    
					}    
				}
			}
		}
		else {
				//for(int n=1; n<this.numJobs; n++) {
					//throughput += ((double)this.serviceRate[i])*this.calcF(n)*this.calcZeta(i, n, 1);
					throughput += this.serviceRate[i]*this.calcEffectiveUtilizationOf(i)*this.server[i];
				//}
				//for(int n=1; n<this.numJobs; n++) {
					//for(int z=1; z<this.numJobs; z++) {
						//throughput += ((double)this.serviceRate[i])*this.calcF(n)*this.calcZeta(i, n, z);
					//}
				//}
		}

		//System.out.println("Throughput "+i+": "+throughput);
		return throughput;
	}

//	public double calcEffectiveThroughputOf(int i) {
//
//		double throughput = 0.0;
//		double nested = 0.0;
//		for(int k=1; k<Math.min(numJobs, capacity[i]); k++) {
//			for(int n=0; n<this.states.size(); n++) {
//				if(this.states.get(n)[i].getNum() == k) {
//					for(int j=0; j<capacity.length; j++) {
//						if(this.states.get(n)[j].getNum() < capacity[j]) {
//							nested += this.pi.get(n)*
//							this.routingMatrix.get(i, j);
//						}
//					}
//
//					throughput += ( this.serviceRate[i]*
//							this.calcF(n) )*
//							nested;
//					nested = 0.0;
//				}
//			}
//		}
//
//		return throughput;
//	}

	/**
	 * L
	 * @param i
	 * @return
	 */
	public double calcMeanQueueOf(int i) {

		double meanQueue = 0.0;
		
		for(int n=0; n<this.states.size(); n++) {
			meanQueue += this.states.get(n)[i].getNum()*this.pi.get(n);
//			this.calcPi(i, this.states.get(n)[i].getNum());
		}

		return meanQueue;
	}

	public double calcMeanResponseTimeOf(int i) {
		System.out.println("i: "+i+", meanQueue: "+calcMeanQueueOf(i)+", throughput: "+this.calcThroughputOf(i));
		return (this.calcMeanQueueOf(i) / this.calcThroughputOf(i));
	}

//	public double calcMeanResponseTimeOf2(int i, double thr) {
//		return (this.calcMeanQueueOf(i) / thr);
//	}

	public DenseVector centerResponseTime(DenseVector responseTimes, Matrix lookRapport){
		DenseVector res=new DenseVector(this.block.length);
		for(int i=0; i<this.block.length; i++){
			for(int j=0; j<this.block.length; j++){
				if(i!=j)
					res.add(i, responseTimes.get(j)*lookRapport.get(j, i) );
//				(Network.y[j+1]/Network.y[i+1]);
			}   
		}    
		return res;
	}

	/* Calcolo indici globali */
	public DenseVector getTotU() {

		for(int i=0; i<this.block.length; i++) {
			this.totU.set(i, this.calcUtilizationOf(i));
		}

		return this.totU;
	}

	public DenseVector getTotX() {

		for(int i=0; i<this.block.length; i++) {
			this.totU.set(i, this.calcThroughputOf(i));
		}

		return this.totX;
	}

	public DenseVector getTotL() {

		for(int i=0; i<this.block.length; i++) {
			this.totL.set(i, this.calcMeanQueueOf(i));
		}

		return this.totL;
	}

	public DenseVector getTotT() {

		for(int i=0; i<this.block.length; i++) {
			this.totT.set(i, this.calcMeanResponseTimeOf(i));
		}

		return this.totT;
	}
}
