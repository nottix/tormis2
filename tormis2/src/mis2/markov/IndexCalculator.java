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
	
	public IndexCalculator(int numJobs, Vector<BbsState[]> states, DenseVector pi, Matrix routingMatrix) {
		this.routingMatrix = routingMatrix;
		this.server = ParametersContainer.getServer();
		this.capacity = ParametersContainer.getCapacity();
		this.serviceRate = ParametersContainer.getServiceRate();
		this.block = ParametersContainer.getBlock();
		this.numJobs = numJobs;
		this.states = states;
		this.pi = pi;
	}
	
	public double calcPi(int i, int k) {
		double total = 0;
		if(capacity[i]==0) {
//			System.out.println("cap 0");
			for(int j=0; j<states.size(); j++) {
				if(states.get(j)[i].getNum() == k) {
					total += pi.get(j);
				}
			}
		}
		else {
//			System.out.println("cap >0");
			for(int j=0; j<states.size(); j++) {
				if(this.calcPiCond(i, j, k)) {
//					if(i==1)
//						System.out.println("pi: "+pi.get(j));
					total += pi.get(j);
				}
			}
		}
		return total;
	}
	
	public double calcZeta(int i, int k, int z) {
		double total = 0;
		for(int j=0; j<states.size(); j++) {
			if(this.calcZetaCond(i, j, k, z)) {
				total += pi.get(j);
			}
		}
		return total;
	}
	
	/**
	 * 
	 * 
	 * @param i
	 * @param indexState indice dello stato desiderato
	 * @return
	 */
	private boolean calcPiCond(int i, int indexState, int num) {
		if(this.block[i]==1) { //RS-RD
			return this.states.get(indexState)[i].getNum() == num;
		}
		else if(this.block[i]==0) { //BBS-SO
//                        System.out.println("\nCondizione BBS: blocked(" + 
//                                this.states.get(indexState)[i].isBlocked() +
//                                ") num(" + num + ") == numI(" + 
//                                this.states.get(indexState)[i].getNum() + ")");
			if( (this.states.get(indexState)[i].isBlocked() && 
                            (num==this.states.get(indexState)[i].getNum())) ||
                            ((!this.states.get(indexState)[i].isBlocked()) &&
                            (num==this.states.get(indexState)[i].getNum()))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 0 <= z_i <= min(n_i, K_i)
	 * 
	 * @return
	 */
	private boolean calcZetaCond(int i, int indexState, int num, int z) {
		if(this.block[i]==1) { //RS-RD
			return (this.states.get(indexState)[i].getNum() == num) && (z == Math.min(num, server[i]));
		}
		else if(this.block[i]==0) { //BBS-SO
			if(this.states.get(indexState)[i].isBlocked() && (num==this.states.get(indexState)[i].getNum())) {
				int totalNs = 0;
				for(int k=0; k<this.states.get(indexState)[i].getDest().size(); k++) {
					if(this.states.get(indexState)[k].getNum() < capacity[k]) {
						totalNs += this.states.get(indexState)[i].getNS(k);
					}
					totalNs += this.states.get(indexState)[i].getNS(0);
				}
				if(z == totalNs) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public double calcUtilizationOf(int i) {
		double utilization = 0;
		
		if(capacity[i]==0) {      // Senza blocco
			for(int j=1; j<=numJobs; j++) {
				utilization += this.calcPi(i, j);
			}
		}
		else {          // RS-RD, BBS
			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
				utilization += (((double)Math.min(n, server[i]))/((double)server[i]))*this.calcPi(i, n);
			}
		}
		
		return utilization;
	}
	
	public double calcEffectiveUtilizationOf(int i) {
		double utilization = 0;
		
		if(block[i]==0) {
			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
				for(int z=1; z<Math.min(n, server[i]); z++) {
					utilization += (z/server[i])*this.calcZeta(i, n, z);
				}
			}
		}
		else if(block[i]==1) {
			double nested = 0;
			for(int k=1; k<Math.min(capacity[i], numJobs); k++) {
				for(int j=0; j<this.states.size(); j++) {
					if(this.states.get(j)[i].getNum() == k) {
						for(int r=0; r<capacity.length; r++) {
							if(this.states.get(j)[r].getNum() < capacity[j])
								nested += this.pi.get(j)*this.routingMatrix.get(i, r);
						}
					}
				}
				utilization += (Math.min(k, server[i])/server[i])*nested;
				nested = 0;
			}
		}
		
		return utilization;
	}
	
	public double calcThroughputOf(int i) {
		double throughput = 0;
		
		if(i==0) {
			throughput = this.serviceRate[i]*this.calcUtilizationOf(i);
		}
		else {
			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
				for(int z=1; z<Math.min(n, this.server[i]); z++) {
					if(n>0) {
						throughput += this.serviceRate[i]*this.calcZeta(i, n, z);
					}
				}
			}
		}
		
		return throughput;
	}
	
	public double calcEffectiveThroughputOf(int i) {
		double throughput = 0;
		double nested = 0;
		for(int k=1; k<Math.min(capacity[i], numJobs); k++) {
			for(int n=0; n<this.states.size(); n++) {
				if(this.states.get(n)[i].getNum() == k) {
					for(int j=0; j<capacity.length; j++) {
						if(this.states.get(n)[j].getNum() < capacity[j]) {
							nested += this.pi.get(n)*this.routingMatrix.get(i, j);
						}
					}
					
					if(this.states.get(n)[i].getNum() > 0)
						throughput += this.serviceRate[i]*nested;
					nested = 0;
				}
			}
		}
		
		return throughput;
	}
	
	public double calcMeanQueueOf(int i) {
		double meanQueue = 0;
		
		for(int n=0; n<this.states.size(); n++) {
			meanQueue += this.states.get(n)[i].getNum()*this.calcPi(i, this.states.get(n)[i].getNum());
		}
		
		return meanQueue;
	}
	
	public double calcMeanResponseTimeOf(int i) {
		return this.calcMeanQueueOf(i) / this.calcUtilizationOf(i);
	}
	
	//-----------------------------//
	
//	public double calcUsageOf(int i) {
//		double total = 0;
//		if(this.server[i]==1) {
//			for(int j=1; j<=numJobs; j++) {
//				total += this.calcPi(i, j);
//			}
//		}
//		else if(this.server[i]>1) {
//			for(int j=1; j<=(this.server[i]-1); j++) {
//				total += j*this.calcPi(i, j);
//			}
//			total += (1/this.server[i]);
//			for(int j=this.server[i]; j<=numJobs; j++) {
//				total += this.calcPi(i, j);
//			}
//		}
//		return total;
//	}
//	
//	public double calcThroughputOf(int i) {
//		double total = 0;
//		if(this.server[i]==1) {
//			total = this.serviceRate[i]*this.calcUsageOf(i);
//		}
//		else if(this.server[i]>1) {
//			total = this.server[i]*this.serviceRate[i]*this.calcUsageOf(i);
//		}
//		return total;
//	}
	
//	public double calcMeanPopOf(int i) {
//		double total = 0;
//		for(int j=1; j<=numJobs; j++) {
//			total += j*this.calcPi(i, j);
//		}
//		return total;
//	}
//	
//	public double calcMeanTimeOf(int i) {
//		return this.calcMeanPopOf(i)/this.calcThroughputOf(i);
//	}
}
