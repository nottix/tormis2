package mis2.markov;

import java.util.Vector;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import mis2.states.BbsState;
import mis2.util.ParametersContainer;
import mis2.util.*;

public class QMatrixGenerator {

	private int[] block;
	private int[] capacity;
	private int[] server;
	private double[] ts;
	private double[] serviceRate;
	private Vector<BbsState[]> states;
	private Matrix qMatrix;
	private Matrix routingMatrix;
	
	public QMatrixGenerator(Vector<BbsState[]> states, Matrix routingMatrix) {
		this.states = states;
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
		this.server = ParametersContainer.getServer();
		this.ts = ParametersContainer.getTs();
		this.serviceRate = ParametersContainer.getServiceRate();
		this.routingMatrix = routingMatrix;
	}
	
	private int getDelta(int n) {
		if(n>0)
			return 1;
		else
			return 0;
	}
	
	private int isAccepted(int n, int i) {
		if(n<capacity[i])
			return 1;
		else
			return 0;
	}
	
	private int calcF(int n) {
		if(n==0)
			return 0;
		else
			return 1;
	}
	
	public Matrix calcQMatrix() {
		int rows = states.size();
		int columns = rows;
		this.qMatrix = new FlexCompRowMatrix(rows, columns);
		int cond2 = 0;
		int cond1 = 0;
		int cond3 = 0;
		int cond4 = 0;
		int condDiag = 0;
		int condZero = 0;
		double diag = 0;
		for(int i=0; i<states.size(); i++) {
			for(int j=0; j<states.size(); j++) {
				if(i==j) {
					
				}
				else if(i==0) {
					diag = 0;
					if(j>0) {
						for(int k=0; k<states.size(); k++) {
							if(k!=(j-1)) {
								diag += this.qMatrix.get(j-1, k);
							}
						}
						diag *= -1;
						this.qMatrix.set(j-1, j-1, diag);
						condDiag++;
					}
				}
				else if(this.checkRsRdCondition1(states.get(j), states.get(i), j, i)) {
					cond1++;
				}
				else if(this.checkRsRdCondition2(states.get(j), states.get(i), j, i)) {
					cond2++;
				}
				else if(this.checkBbsCondition1(states.get(j), states.get(i), j, i)) {
					cond3++;
				}
				else if(this.checkBbsCondition2(states.get(j), states.get(i), j, i)) {
					cond4++;
				}
				else {
					qMatrix.set(j, i, 0);
					condZero++;
				}
			}
		}
		System.out.println("Total: "+states.size()*states.size()+", Cond1: "+cond1+", Cond2: "+cond2+", Cond3: "+cond3+", Cond4: "+cond4+", CondZero: "+condZero+", CondDiag: "+condDiag);
		return this.qMatrix;	
	}
	
	private boolean checkRsRdCondition1(BbsState[] from, BbsState[] to, int x, int y) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==1) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if( ((block[i]==0) && (from[i].getNum()>=server[i])) || (block[i]==1) ) {
							if( (to[j].getNum()==(from[j].getNum()-1)) && (to[i].getNum()==(from[i].getNum()+1)) ) {
								
								qMatrix.set(x, y, this.getDelta(from[j].getNum())*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, i)*this.isAccepted(from[j].getNum(), i));
								
								
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkRsRdCondition2(BbsState[] from, BbsState[] to, int xx, int yy) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==1) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if( ((block[i]==0) && (from[i].getNum()<server[i]))) {
							if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
								for(int x=0; x<(to[i].getNsSize()); x++) {
									if( (to[i].getNS(x) == (from[i].getNS(x)+1)) ) {
										qMatrix.set(xx, yy, this.getDelta(from[j].getNum())*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, i)*this.routingMatrix.get(i, from[i].getDestAt(x)));
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
	
	private boolean checkBbsCondition1(BbsState[] from, BbsState[] to, int xx, int yy) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==0) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if(from[j].getNum()<=server[j]) {
							if( ((block[i]==0) && (from[i].getNum()>=server[i])) || block[i]==1) {
								if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
									int ns = from[j].getNSof(j, i);
									if(ns>=0) {
										qMatrix.set(xx, yy, ns*this.serviceRate[j]*this.calcF(from[j].getNum())*this.isAccepted(from[i].getNum(), i));
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
	
	private boolean checkBbsCondition2(BbsState[] from, BbsState[] to, int xx, int yy) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==0) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if(from[j].getNum()>server[j]) {
							if( ((block[i]==0) && (from[i].getNum()>=server[i])) || block[i]==1) {
								if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
									for(int x=0; x<(to[i].getNsSize()); x++) {
										if( (to[j].getNS(x) == (from[j].getNS(x)+1)) ) {
											int ns = from[j].getNSof(j, i);
											if(ns>=0) {
												qMatrix.set(xx, yy, ns*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, from[j].getDestAt(x))*this.isAccepted(from[i].getNum(), i));
												return true;
											}
										}
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
	
	private boolean calcRowDiagValues(int row) {
		
		
		
		return false;
	}
	

	public void printQMatrix() {
		for(int j=0; j<qMatrix.numRows(); j++) {
			for(int i=0; i<qMatrix.numColumns(); i++) {
				//if(qMatrix.get(j, i)!=0)
					System.out.print(qMatrix.get(j, i)+" ");
			}
			System.out.println();

		}
			
	}
}
