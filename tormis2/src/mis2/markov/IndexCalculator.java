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
	
        private double calcF(int n) {
//		if(n==0)
//			return 0;
//		else
//			return 1;
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
		else 
                {
			for(int S=0; S<states.size(); S++) {
				if(this.calcPiCond(X, S, n)) {
					total += pi.get(S);
				}
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
	private boolean calcPiCond(int X, int S, int n) {
            
                //RS-RD
		if(this.block[X]==1) 
                { 
			return (this.states.get(S)[X].getNum() == n);
		}
                
                //BBS-SO
		else if(this.block[X]==0) 
                { 
//			if( (this.states.get(S)[X].isBlocked() && 
//                             (n==this.states.get(S)[X].getNum()) ) ||
//                              ((!this.states.get(S)[X].isBlocked()) &&
//                               (n==this.states.get(S)[X].getNum())) ) {
                        if( this.states.get(S)[X].isBlocked() && 
                                (n==this.states.get(S)[X].getNum()) ) {
                            return true;
			}
		}
		
		return false;
	}
        
        
        public double calcZeta(int X, int n, int z) {
		double total = 0.0;
		for(int S=0; S<states.size(); S++) {
			if(this.calcZetaCond(X, S, n, z)) {
				total += pi.get(S);
			}
		}
		return total;
	}
	
	/**
	 * 0 <= z_i <= min(n_i, K_i)
	 * 
	 * @return
	 */
	private boolean calcZetaCond(int X, int S, int n, int z) {
            
                //RS-RD
		if(this.block[X]==1) 
                { 
			return (this.states.get(S)[X].getNum() == n) && 
                                            (z == Math.min(n, server[X]));
		}
                
                //BBS-SO
		if(this.block[X]==0) 
                { 
			if( this.states.get(S)[X].isBlocked() && 
                                (n==this.states.get(S)[X].getNum()) ) 
                        {
				int totalNs = 0;
                                totalNs += this.states.get(S)[X].getNS(0);
				for(int k=0; k<this.states.get(S)[X].getDest().size(); k++) {
                                    if(this.states.get(S)[k].getNum() < capacity[k]) {
                                            totalNs += this.states.get(S)[X].getNS(k);
                                    }
				}
				if(z == totalNs) {
                                    return true;
				}
                                return false;
			}
			else if( (!this.states.get(S)[X].isBlocked()) && 
                                (n==this.states.get(S)[X].getNum()) ) 
                        {
				if(z == Math.min(n, server[X])) {
                                    return true;
				}
                                return false;
			}
		}
                
                return false;
	}
	
	public double calcUtilizationOf(int i) {
		double utilization = 0;
		
		if(capacity[i]==0) 
                {       // Senza blocco
			for(int n=1; n<=numJobs; n++) {
				utilization += this.calcPi(i, n);
			}
		}
		else 
                {       // RS-RD, BBS
			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
                            utilization += ( ((double)Math.min(n, server[i]))/
                                                ((double)server[i]) )*
                                                    this.calcPi(i, n);
			}
		}
		
		return utilization;
	}
	
	public double calcEffectiveUtilizationOf(int i) {
		
                double utilization = 0;
		
                // BBS-SO
		if(block[i]==0) 
                {
			for(int n=1; n<Math.min(capacity[i], numJobs); n++) {
				for(int z=1; z<Math.min(n, server[i]); z++) {
                                    utilization += ((double)z/(double)server[i])*
                                                    this.calcZeta(i, n, z);
				}
			}
		}
                
                // RS-RD
		else if(block[i]==1) 
                {
                    double nested = 0.0;
                    for(int k=1; k<Math.min(capacity[i], numJobs); k++) {
                        for(int n=0; n<this.states.size(); n++) {
                            if(this.states.get(n)[i].getNum() == k) {
                                for(int j=0; j<capacity.length; j++) {
                                    if(this.states.get(n)[j].getNum() < capacity[j])
                                            nested += this.pi.get(n)*
                                                        this.routingMatrix.get(i, j);
                                }
                            }
                        }
                        utilization += ((double)Math.min(k, server[i])/(double)server[i])
                                        *nested;
                        nested = 0.0;
                    }
		}
		
		return utilization;
	}
	
	public double calcThroughputOf(int i) {
		
                double throughput = 0.0;
		
		if(this.capacity[i]==0) {
			throughput = this.serviceRate[i]*
                                        this.calcUtilizationOf(i);
		}
		else {
                    if(this.server[i] == 1){
			for(int n=1; n<this.numJobs; n++) {
                            throughput += ((double)this.serviceRate[i])*
                                            this.calcF(n)*
                                                this.calcZeta(i, n, 1);
			}
                    }
                    else{
                        for(int n=1; n<this.numJobs; n++) {
                            for(int z=1; z<this.numJobs; z++) {
                                throughput += ((double)this.serviceRate[i])*
                                                this.calcF(n)*
                                                    this.calcZeta(i, n, z);
                            }
			}
                    }
		}
		
		return throughput;
	}
	
	public double calcEffectiveThroughputOf(int i) {
		
                double throughput = 0.0;
		double nested = 0.0;
		for(int k=1; k<Math.min(numJobs, capacity[i]); k++) {
                    for(int n=0; n<this.states.size(); n++) {
                        if(this.states.get(n)[i].getNum() == k) {
                            for(int j=0; j<capacity.length; j++) {
                                if(this.states.get(n)[j].getNum() < capacity[j]) {
                                        nested += this.pi.get(n)*
                                                    this.routingMatrix.get(i, j);
                                }
                            }
                            
                            throughput += ( this.serviceRate[i]*
                                            this.calcF(n) )*
                                                nested;
                            nested = 0.0;
                        }
                    }
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
			meanQueue += this.states.get(n)[i].getNum()*
                                        this.calcPi(i, this.states.get(n)[i].getNum());
		}
		
		return meanQueue;
	}
	
	public double calcMeanResponseTimeOf(int i) {
		return (this.calcMeanQueueOf(i) / this.calcUtilizationOf(i));
	}
        
        public double calcMeanResponseTimeOf2(int i, double util) {
		return (this.calcMeanQueueOf(i) / util);
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
