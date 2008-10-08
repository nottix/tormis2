package mis2.markov;

import java.util.Vector;

import mis2.states.BbsState;
import mis2.util.ParametersContainer;

public class QMatrixGenerator {

	private int[] block;
	private int[] capacity;
	private int[] server;
	private double[] ts;
	private double[] serviceRate;
	private Vector<BbsState[]> states;
	
	public QMatrixGenerator(Vector<BbsState[]> states) {
		this.states = states;
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
		this.server = ParametersContainer.getServer();
		this.ts = ParametersContainer.getTs();
		this.serviceRate = this.calcServiceRate();
	}
	
	private boolean getDelta(int n) {
		if(n>0)
			return true;
		else
			return false;
	}
	
	private boolean isAccepted(int n, int i) {
		if(n<capacity[i])
			return true;
		else
			return false;
	}
	
	private int calcF(int n) {
		if(n==0)
			return 0;
		else
			return 1;
	}
	
	private double[] calcServiceRate() {
		serviceRate = new double[ts.length];
		for(int i=0; i<serviceRate.length; i++) {
			serviceRate[i] = 1/ts[i];
		}
		return serviceRate;
	}
	
	public void calcQMatrix() {
		int cond2 = 0;
		int cond1 = 0;
		for(int j=0; j<states.size(); j++) {
			for(int i=0; i<states.size(); i++) {
				if(this.checkRsRdCondition1(states.get(j), states.get(i))) {
					
					cond1++;
				}
				else if(this.checkRsRdCondition2(states.get(j), states.get(i))) {
					cond2++;
				}
			}
		}
		System.out.println("Total: "+states.size()*states.size()+", Cond1: "+cond1+", Cond2: "+cond2);
			
	}
	
	private boolean checkRsRdCondition1(BbsState[] from, BbsState[] to) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==1) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if( ((block[i]==0) && (from[i].getNum()>=server[i])) || (block[i]==1) ) {
							if( (to[j].getNum()==(from[j].getNum()-1)) && (to[i].getNum()==(from[i].getNum()+1)) ) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkRsRdCondition2(BbsState[] from, BbsState[] to) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==1) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if( ((block[i]==0) && (from[i].getNum()<server[i]))) {
							if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
								for(int x=0; x<(to[i].getNsSize()); x++) {
									if( (to[i].getNS(x) == (from[i].getNS(x)+1)) ) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}		
		return false;
	}
}
