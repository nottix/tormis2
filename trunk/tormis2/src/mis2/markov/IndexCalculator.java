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

	public double calcUtilizationOf(int i) {
		double utilization = 0;

		utilization = this.calcEffectiveUtilizationOf(i);

		return utilization;
	}

	public double calcEffectiveUtilizationOf(int i) {

		double utilization = 0;

		// BBS-SO
		if(block[i]==0) 
		{
			for(int stato=0; stato<this.states.size(); stato++){
				if(this.states.get(stato)[i].getNum()>0 && this.states.get(stato)[i].getNum()<=this.capacity[i]){
//					for(int k=0; k<Math.min(this.states.get(stato)[i].getNum(), this.server[i]); k++) {
//							z +=  
//					}
				//	System.out.println("MATH: "+this.pi.get(stato)*(double)Math.min(this.states.get(stato)[i].getNum(), this.server[i])/((double)this.server[i]));
					//System.out.println("DEST: "+this.states.get(stato)[i].getZ());
					utilization += this.pi.get(stato)*((double)Math.min(this.states.get(stato)[i].getNum(), this.server[i]))/((double)this.server[i]);
				}
			}
		}	// RS-RD
		else if(block[i]==1) 
		{
			if(capacity[i]==0) {
				for(int stato=0; stato<this.states.size(); stato++) {
					for(int j=0; j<this.block.length; j++) {
						if((this.states.get(stato)[i].getNum() > 0) && (this.routingMatrix.get(i, j) > 0) && (this.states.get(stato)[j].getNum()<this.capacity[j])) {
							//System.out.println("Util["+i+"]: "+utilization);
							utilization += (Math.min(this.states.get(stato)[i].getNum(), this.numJobs)/(double)this.numJobs)*pi.get(stato)*this.routingMatrix.get(i, j);
						}
					}
				}
			}
			else {
				for(int stato=0; stato<this.states.size(); stato++){
					if(this.states.get(stato)[i].getNum()>0) {
						for(int j=0; j<this.block.length; j++){
							if(this.routingMatrix.get(i, j) > 0) {
								if(capacity[j]==0) {
									utilization += pi.get(stato) * this.routingMatrix.get(i, j)/*((double)Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i])*/;
								}
								else if(this.states.get(stato)[j].getNum() < this.capacity[j]) {
									utilization += pi.get(stato) * this.routingMatrix.get(i, j)/*((double)Math.min(this.states.get(stato)[i].getNum(), this.server[i])/this.server[i])*/;
								}
							}
						}
					}
				}
			}
		}
		return utilization;
	}

	public double calcThroughputOf(int i) {
		double throughput = 0.0;
		
//		if(i==7) {
//			System.out.println("THRO: "+this.serviceRate[i]+", "+this.calcEffectiveUtilizationOf(i)+", "+this.server[i]);
//		}
		
		if(this.capacity[i]==0 /*|| this.capacity[i]==1*/) {
			throughput = this.serviceRate[i]*this.calcEffectiveUtilizationOf(i)*this.numJobs;
		}
		else {
			throughput = this.serviceRate[i]*this.calcEffectiveUtilizationOf(i)*this.server[i];
		}
		return throughput;
	}

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
//		if(i==7) {
//			System.out.println("i: "+i+", meanQueue: "+calcMeanQueueOf(i)+", throughput: "+this.calcThroughputOf(i));
//		}
		return (this.calcMeanQueueOf(i) / this.calcThroughputOf(i));
	}

//	public double calcMeanResponseTimeOf2(int i, double thr) {
//		return (this.calcMeanQueueOf(i) / thr);
//	}

	public DenseVector centerResponseTime(DenseVector responseTimes, Matrix lookRapport){
		DenseVector res=new DenseVector(this.block.length);
		for(int i=0; i<this.block.length; i++){
			for(int j=0; j<this.block.length; j++){
				if(i!=j) {
//					if(i==0)
//						System.out.println("RESPTIME: "+lookRapport.get(j, i));
					res.add(i, responseTimes.get(j)*lookRapport.get(j, i) );
				}
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
