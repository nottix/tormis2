package mis2.markov;

import java.util.Iterator;
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

	public double calcPi(int X, int n) {

		double total = 0.0;
		if(capacity[X]==0) 
		{
			for(int S=0; S<states.size(); S++) {
				if(states.get(S)[X].getNum() == n) {
					total += pi.get(S);
				}
			}
		}
//		else 
//		{
//			for(int S=0; S<states.size(); S++) {
//				if(this.calcPiCond(X, S, n)) {
//					total += pi.get(S);
//				}
//			}
//		}
		return total;
	}

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

		if(server[i]>1)
			utilization = this.calcEffectiveUtilizationOf(i);
		else
			utilization = 1-this.calcEffectiveUtilizationOf(i);
		
		return utilization;
	}
	
	/**
     * Tale metodo prende in input l'indice i del centro e
     * calcola l'utilizzazione eettiva del centro i 
     */
	private double calcUtil(int i) {
		double ut = 0;
		double pi = 0;
		double pib = 0;
		BbsState[] st;
		if (this.server[i] == 1){ 
			if (this.block[i] == 1) { //RS-RD
				for(int k=0; k<this.states.size(); k++) {
					st = this.states.get(k);
					if (st[i].getNum() == 0) {
						pi += this.pi.get(k);
					}
					else {
						for (int j = 0; j < this.block.length; j++) {
							if (this.routingMatrix.get(i, j) != 0) {
								if (st[j].getNum() == this.capacity[j]) {
									pib += (this.pi.get(k) * this.routingMatrix.get(i, j));
								}
							}
						}
					}
				}
				ut = (double) (1 - pi - pib);
				return ut;
			}
			if (this.block[i] == 0) {
				int l = 0;
				for (int k=0; k<this.states.size(); k++) {
					st = (BbsState[]) states.get(k);
					if (st[i].getNum() == 0) {
						pi += this.pi.get(k);
					} else {
						for (int j = 0; j < this.block.length; j++) {
							if (i != j) {
								if (this.routingMatrix.get(i, j) != 0 && (int) st[j].getNum() > 0) {
									for(int n=0; n<st[i].getDest().size(); n++) {
										l = st[i].getDestAt(n);
										if (this.capacity[l-1] == st[l-1].getNum()) {
											pib += this.pi.get(k);	
										}
									}
								}
							}
						}
					}
					ut = (double) 1 - pi - pib;
					return ut;
				}
			}
		}
		else {
			int p,limit;
			int nums = this.server[i];

			for(int stato=0; stato<this.states.size(); stato++) {
				ut += this.pi.get(stato)*Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i];
			}
//			for (p = 1; p < nums && p<=net.getCenter(i).getMarginal_prob_size(); p++ ){
//			ut += p*net.getCenter(i).getMarginal_prob(p-1);
//			}
//			ut /= nums ;
//			if (N<=net.getCenter(i).getCapacity())
//			limit=N;
//			else
//			limit=net.getCenter(i).getCapacity();
//			for (p = nums; p <= limit; p++  ){
//			ut += net.getCenter(i).getMarginal_prob(p-1);
//			}
//			net.getCenter(i).setUtil(ut);
			return ut;
		}
		return 0.0;
	}

	public double calcEffectiveUtilizationOf(int i) {

		double utilization = 0;

		// BBS-SO
		if(block[i]==0) 
		{
			for(int ni=1; ni<=Math.min(capacity[i], this.numJobs); ni++) {
				for(double zi=1.0; zi<=Math.min(ni, server[i]); zi++) {
					for(int stato=0; stato<this.states.size(); stato++){
//						if(this.states.get(stato)[i].getNum()>0 /*&& this.states.get(stato)[i].getNum()>=this.capacity[i]*/){
//							for(int j=0; j<this.block.length; j++) {
//								if(this.routingMatrix.get(i, j) != 0) {
//									if(this.states.get(stato)[j].getNum()> 0) {
//										for(int n=0; n<this.states.get(stato)[i].getDest().size(); n++) {
//											int k = this.states.get(stato)[i].getDestAt(n);
//											if (this.capacity[k] == this.states.get(stato)[k].getNum()) {
//												utilization += this.pi.get(stato)*Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i];
//											}
//										}
//									}
//									else
//										utilization += this.pi.get(stato);
//								}
//							}

//						System.out.println("ni: "+ni+", n: "+this.states.get(stato)[i].getNum()+", zi: "+zi+", z: "+Math.min(ni, server[i])+", "+this.pi.get(stato)+", "+(zi/server[i]));
						if(ni==this.states.get(stato)[i].getNum() && zi==Math.min(ni, server[i])) {
							utilization += this.pi.get(stato)*(zi/server[i]);
						}
//						System.out.println("Utilization of "+i+": "+utilization);
					}
				}
			}
		}	// RS-RD
		else if(block[i]==1) 
		{
			for(int k=1; k<=Math.min(capacity[i], this.numJobs); k++) {
				for(int stato=0; stato<this.states.size(); stato++){
					if(this.states.get(stato)[i].getNum()==k) {
//					if(this.states.get(stato)[i].getNum()>0) {
						for(int j=0; j<this.block.length; j++){
							if(this.routingMatrix.get(i, j) != 0){
								//if(!(this.states.get(stato)[j].getNum() >= this.capacity[j] && this.capacity[j] > 0)){ //Capacità = 0 equivale ad infinito
//								System.out.println("i: "+i+", "+pi.get(stato)+", "+this.routingMatrix.get(i, j)+", "+(Math.min(k, this.server[i])/this.server[i]));
								if(this.states.get(stato)[j].getNum() < this.capacity[j] || capacity[j]==0) {
									utilization += pi.get(stato) * this.routingMatrix.get(i, j)*((double)Math.min(k, this.server[i])/this.server[i]);
								}
								else if(this.capacity[i]==0)
									utilization += pi.get(stato) * this.routingMatrix.get(i, j);
							}
						}
					}
//					else {
//						utilization += this.pi.get(stato);
//					}
				}
			}
//			System.out.println("Utilization of "+i+": "+utilization);
		}
		return utilization;
	}

	public double calcThroughputOf(int i) {

		double throughput = 0.0;
		
		//throughput += this.serviceRate[i]*this.calcEffectiveUtilizationOf(i)*this.server[i];

		if(this.capacity[i]==0 /*|| this.capacity[i]==1*/) {
			//throughput = 0;
			throughput = this.serviceRate[i]*this.calcUtilizationOf(i)*this.numJobs;


//			for(int stato=0; stato<this.states.size(); stato++) {
//				if(states.get(stato)[i].getNum() > 0) {
//					for(int j=0; j<this.block.length; j++) {
//						if(this.routingMatrix.get(i, j) != 0) {
//							if(!(this.states.get(stato)[j].getNum() >= this.capacity[j] && this.capacity[j] > 0)) { //Capacità = 0 equivale ad infinito
//								double mu = this.states.get(stato)[i].getNum() * this.serviceRate[i];
//								throughput += mu * this.pi.get(stato) * this.routingMatrix.get(i, j);
//							}
//						}    
//					}    
//				}
//			}
		}
		else {
				//for(int n=1; n<this.numJobs; n++) {
					//throughput += ((double)this.serviceRate[i])*this.calcF(n)*this.calcZeta(i, n, 1);
					throughput = this.serviceRate[i]*this.calcEffectiveUtilizationOf(i)*this.server[i];
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
//		System.out.println("i: "+i+", meanQueue: "+calcMeanQueueOf(i)+", throughput: "+this.calcThroughputOf(i));
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
