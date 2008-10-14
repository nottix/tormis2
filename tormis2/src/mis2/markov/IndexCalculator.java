package mis2.markov;

import java.util.Vector;
import no.uib.cipr.matrix.DenseVector;

import mis2.states.BbsState;
import mis2.util.ParametersContainer;

public class IndexCalculator {

	private int[] server;
	private Vector<BbsState[]> states;
	private DenseVector pi;
	private int numJobs;
	private double[] serviceRate;
	
	public IndexCalculator(int numJobs, Vector<BbsState[]> states, DenseVector pi) {
		this.server = ParametersContainer.getServer();
		this.serviceRate = ParametersContainer.getServiceRate();
		this.numJobs = numJobs;
		this.states = states;
		this.pi = pi;
	}
	
	public double calcP(int i, int k) {
		double total = 0;
		for(int j=0; j<states.size(); j++) {
			if(states.get(j)[i].getNum() == k) {
				total += pi.get(j);
			}
		}
		return total;
	}
	
	public double calcUsageOf(int i) {
		double total = 0;
		if(this.server[i]==1) {
			for(int j=1; j<=numJobs; j++) {
				total += this.calcP(i, j);
			}
		}
		else if(this.server[i]>1) {
			for(int j=1; j<=(this.server[i]-1); j++) {
				total += j*this.calcP(i, j);
			}
			total += (1/this.server[i]);
			for(int j=this.server[i]; j<=numJobs; j++) {
				total += this.calcP(i, j);
			}
		}
		return total;
	}
	
	public double calcThroughputOf(int i) {
		double total = 0;
		if(this.server[i]==1) {
			total = this.serviceRate[i]*this.calcUsageOf(i);
		}
		else if(this.server[i]>1) {
			total = this.server[i]*this.serviceRate[i]*this.calcUsageOf(i);
		}
		return total;
	}
	
	public double calcMeanPopOf(int i) {
		double total = 0;
		for(int j=1; j<=numJobs; j++) {
			total += j*this.calcP(i, j);
		}
		return total;
	}
	
	public double calcMeanTimeOf(int i) {
		return this.calcMeanPopOf(i)/this.calcThroughputOf(i);
	}
}
