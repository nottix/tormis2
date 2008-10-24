package mis2.markov;

import java.util.Vector;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import mis2.states.BbsState;
import mis2.util.ParametersContainer;
import mis2.util.*;

public class QMatrixGenerator extends Thread {

	private int[] block;
	private int[] capacity;
	private int[] server;
	private double[] ts;
	private double[] serviceRate;
	private Vector<BbsState[]> states;
	private Matrix qMatrix;
	private Matrix routingMatrix;
	private int i, j;
	private int startRow, startCol;
	private int endRow, endCol;
	private int cond1 = 0;
	private int cond2 = 0;
	private int cond3 = 0;
	private int cond4 = 0;
	private int cond5 = 0;
	private int condDiag = 0;
	private int condZero = 0;
	private static int counter = 0;

	public QMatrixGenerator(Vector<BbsState[]> states, Matrix routingMatrix, Matrix qMatrix, int startRow, int startCol, int endRow, int endCol) {
		this.states = states;
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
		this.server = ParametersContainer.getServer();
		this.ts = ParametersContainer.getTs();
		this.serviceRate = ParametersContainer.getServiceRate();
		this.routingMatrix = routingMatrix;
		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;
		this.qMatrix = qMatrix;
		this.setName(counter+": QMatrixGenerator("+startRow+", "+endRow+")");
		QMatrixGenerator.counter++;
		this.setDaemon(true);
		this.setPriority(Thread.MAX_PRIORITY);
		this.start();
	}

	private int getDelta(int n) {
		if(n>0)
			return 1;
		else
			return 0;
	}

	/* Formula di Pacini */
	private int isAccepted(int n, int i) {
		if(n<=capacity[i])
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

	public void run() {
		this.calcQMatrix();
	}

	public Matrix calcQMatrix() {
		boolean out = false;
		int rows = states.size();
		int columns = rows;
		int jj, ii, k, x, ns;
		BbsState[] from, to;
		//this.qMatrix = new FlexCompRowMatrix(rows, columns);

		double diag = 3;
		for(j=this.startRow; j<states.size() && j<this.endRow; j++) {               // j - righe
			if((j%10)==0)
				System.out.println(this.getName()+" -> Status(size: "+states.size()+"): "+j+" of "+this.endRow+", "+(((double)(j-this.startRow)/(double)(this.endRow-this.startRow))*100)+"%");
			for(i=this.startCol; i<states.size() && i<this.endCol; i++) {       // i - colon

				if(i!=j) {
					//System.out.println("X: "+j+", Y: "+i);
//					out = false;
//					from = states.get(j);
//					to = states.get(i);
//					if(!out) {
//					for(jj=0; jj<from.length; jj++) {
//					if(block[jj]==1) {
//					if(from[jj].getDest() != null) {
//					for(k=0; k<from[jj].getDest().size(); k++) {
//					ii = from[jj].getDestAt(k);
//					if( ((block[ii]==0) && (from[ii].getNum()>=server[ii])) || (block[ii]==1) ) {
//					if( (to[jj].getNum()==(from[jj].getNum()-1)) && (to[ii].getNum()==(from[ii].getNum()+1)) ) {

//					qMatrix.set(j, i, this.getDelta(from[jj].getNum())*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, ii)*this.isAccepted(from[jj].getNum(), ii));
//					out = true;
//					System.out.println("1");
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					if(!out) {
//					for(jj=0; jj<from.length; jj++) {
//					if(block[jj]==1) {
//					if(from[jj].getDest() != null) {
//					for(k=0; k<from[jj].getDest().size(); k++) {
//					ii = from[jj].getDestAt(k);
//					if( ((block[ii]==0) && (from[ii].getNum()<server[ii]))) {
//					if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
//					for(x=0; x<(to[ii].getNsSize()); x++) {
//					if( (to[ii].getNS(x) == (from[ii].getNS(x)+1)) ) {
//					qMatrix.set(j, i, this.getDelta(from[jj].getNum())*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, ii)*this.routingMatrix.get(ii, from[ii].getDestAt(x)));
//					out = true;
//					System.out.println("2");
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					if(!out) {
//					for(jj=0; jj<from.length; jj++) {
//					if(block[jj]==0) { //Se BBS-SO
//					if(from[jj].getDest() != null) {
//					for(k=0; k<from[jj].getDest().size(); k++) {
//					ii = from[jj].getDestAt(k);
//					if(from[jj].getNum()<=server[jj]) {
//					if( ((block[ii]==0) && (from[ii].getNum()>=server[ii])) || block[ii]==1) {
////					System.out.println("Entra |j="+j+" |i="+i+" |numTo="+to[j].getNum()+" |numFrom="+from[j].getNum());
//					/* Teorema di Notargiacomo */
//					if(ii==jj && (to[jj].getNum()==from[jj].getNum()) ) {
//					ns = from[jj].getNS(k);
////					System.out.println("BBS1 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
////					System.out.println("FROM: "+this.printState(from));
////					System.out.println("TO: "+this.printState(to));
////					System.out.println("ns: " + ns);
//					if(ns>0) {
////					if(ns==0)
////					System.out.println("k: "+k+", "+from[j].toString());
////					System.out.println("ns: "+ns+", serv: "+this.serviceRate[j]+", f: "+this.calcF(from[j].getNum())+", acc: "+this.isAccepted(from[i].getNum(), i));
//					qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.isAccepted(from[ii].getNum(), ii));
//					out = true;
//					System.out.println("3");
//					}
//					}
//					else if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
//					//int ns = from[j].getNSof(j, i);
//					ns = from[jj].getNS(k);
//					if(ns>0) {
////					if(ns==0)
////					System.out.println("k: "+k+", "+from[j].toString());
////					System.out.println("ns: "+ns+", serv: "+this.serviceRate[j]+", f: "+this.calcF(from[j].getNum())+", acc: "+this.isAccepted(from[i].getNum(), i));
//					qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.isAccepted(from[ii].getNum(), ii));
//					out = true;
//					System.out.println("4");
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}		
//					}
//					if(!out) {
//					for(jj=0; jj<from.length; jj++) {
//					if(block[jj]==0) {
//					if(from[jj].getDest() != null) {
//					for(k=0; k<from[jj].getDest().size(); k++) {
//					ii = from[jj].getDestAt(k);
//					if(from[jj].getNum()>server[jj]) {
////					System.out.println("Entra");
//					if( ((block[ii]==0) && (from[ii].getNum()>=server[ii])) || block[ii]==1) {
////					System.out.println("Entra2");
////					System.out.println("Entra |j="+j+" |i="+i+" |numTo="+to[j].getNum()+" |numFrom="+from[j].getNum());
//					if(ii==jj && (to[jj].getNum()==from[jj].getNum()) ) {
////					System.out.println("Entra3 IF");
//					for(x=0; x<(to[jj].getNsSize()); x++) {
//					if( (to[jj].getNS(x) == (from[jj].getNS(x)+1)) ) {
//					ns = from[jj].getNSof(jj, ii);
////					System.out.println("BBS2 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
////					System.out.println("FROM: "+this.printState(from));
////					System.out.println("TO: "+this.printState(to));
////					System.out.println("stampa(non 0):"+ns);
////					System.out.println(this.serviceRate[j]);
////					System.out.println(this.calcF(from[j].getNum()));
////					System.out.println(this.routingMatrix.get(j, from[j].getDestAt(x)));
////					System.out.println(this.isAccepted(from[i].getNum(), i) );
//					if(ns>0) {
//					qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x))*this.isAccepted(from[ii].getNum(), ii));
//					out = true;
//					System.out.println("5");
//					}
//					}
//					}
//					}
//					else if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
////					System.out.println("Entra3 ELSE | size=" + to[j].getNsSize());
//					for(x=0; x<(to[jj].getNsSize()); x++) {
//					if( (to[jj].getNS(x) == (from[jj].getNS(x)+1)) ) {
////					System.out.println("Entra4 ELSE");
//					ns = from[jj].getNSof(jj, ii);
//					if(ns>0) {
//					qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x))*this.isAccepted(from[ii].getNum(), ii));
//					out = true;
//					System.out.println("6");
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}	
//					}
//					if(!out) {
//					for(jj=0; jj<from.length; jj++) {
//					if(block[jj]==0) {
//					if(from[jj].getDest() != null) {
//					for(k=0; k<from[jj].getDest().size(); k++) {
//					ii = from[jj].getDestAt(k);
//					if(from[jj].getNum()<=server[jj]) {
////					System.out.println("Entra");
//					if( ((block[ii]==0) && (from[ii].getNum()<server[ii])) ) {
////					System.out.println("Entra2");
//					if(ii==jj && (to[jj].getNum()==from[jj].getNum()) ) {
//					for(x=0; x<(to[ii].getNsSize()); x++) {
//					if( (to[ii].getNS(x) == (from[ii].getNS(x)+1)) ) {
//					ns = from[jj].getNSof(jj, ii);
////					System.out.println("BBS3 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
////					System.out.println("FROM: "+this.printState(from));
////					System.out.println("TO: "+this.printState(to));
//					if(ns>0) {
//					qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x)));
//					out = true;
//					System.out.println("7");
//					}
//					}
//					}
//					}
//					else if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
//					for(x=0; x<(to[ii].getNsSize()); x++) {
//					if( (to[ii].getNS(x) == (from[ii].getNS(x)+1)) ) {
//					ns = from[jj].getNSof(jj, ii);
//					if(ns>0) {
//					qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x)));
//					out = true;
//					System.out.println("8");
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}
//					}		
//					}
//					if(!out) {
//					qMatrix.set(j, i, 0);
//					condZero++;
//					System.out.println("9");
//					}

					if(this.checkRsRdCondition1(states.get(j), states.get(i), j, i)) {
//						System.out.println("Condition RS-RD 1 " + " statoV: "+j+", statoN: "+i);
						cond1++;
					}
					else if(this.checkRsRdCondition2(states.get(j), states.get(i), j, i)) {
//						System.out.println("Condition RS-RD 2" + " statoV: "+j+", statoN: "+i);
						cond2++;
					}
					else if(this.checkBbsCondition1(states.get(j), states.get(i), j, i)) {
//						if(j==1)
//						System.out.println("INSERITO"+j+", "+i+", "+qMatrix.get(j, i));
//						System.out.println("Condition BBS 1" + " statoV: "+j+", statoN: "+i);
						cond3++;
					}
					else if(this.checkBbsCondition2(states.get(j), states.get(i), j, i)) {
//						if(j==1)
//						System.out.println("INSERITO2"+j+", "+i);
//						System.out.println("Condition BBS 2" + " statoV: "+j+", statoN: "+i);
						cond4++;
					}
					else if(this.checkBbsCondition3(states.get(j), states.get(i), j, i)) {
//						if(j==1)
//						System.out.println("INSERITO3"+j+", "+i);
//						System.out.println("Condition BBS 3" + " statoV: "+j+", statoN: "+i);
						cond5++;
					}
					else {
						qMatrix.set(j, i, 0);
						condZero++;
					}


					if(i==states.size()-1) {

						diag = 0;
						for(k=0; k<states.size(); k++) {
							if(k!=j) {
								diag += this.qMatrix.get(j, k);
							}
						}
						diag *= -1;
						this.qMatrix.set(j, j, diag);
						condDiag++;

					}


					//this.yield();
				}
			}
//			if(counter==0 || counter==1)
//			Runtime.getRuntime().gc();
		}
		System.out.println("Total: "+states.size()*states.size()+", Cond1: "+cond1+", Cond2: "+cond2+", Cond3: "+cond3+", Cond4: "+cond4+", Cond5: "+cond5+", CondZero: "+condZero+", CondDiag: "+condDiag);
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
								//System.out.println("acc: "+this.isAccepted(from[i].getNum(), i));
								qMatrix.set(x, y, this.getDelta(from[j].getNum())*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, i)*this.isAccepted(from[i].getNum(), i));
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
			if(block[j]==0) { //Se BBS-SO
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if(from[j].getNum()<=server[j]) {
							if( ((block[i]==0) && (from[i].getNum()>=server[i])) || block[i]==1) {
//								System.out.println("Entra |j="+j+" |i="+i+" |numTo="+to[j].getNum()+" |numFrom="+from[j].getNum());
								/* Teorema di Notargiacomo */
								if(i==j && (to[j].getNum()==from[j].getNum()) ) {
									int ns = from[j].getNS(k);
//									System.out.println("BBS1 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//									System.out.println("FROM: "+this.printState(from));
//									System.out.println("TO: "+this.printState(to));
//									System.out.println("ns: " + ns);
									if(ns>0) {
//										if(ns==0)
//										System.out.println("k: "+k+", "+from[j].toString());
//										System.out.println("ns: "+ns+", serv: "+this.serviceRate[j]+", f: "+this.calcF(from[j].getNum())+", acc: "+this.isAccepted(from[i].getNum(), i));
										qMatrix.set(xx, yy, ns*this.serviceRate[j]*this.calcF(from[j].getNum())*this.isAccepted(from[i].getNum(), i));
										return true;
									}
								}
								else if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
									//int ns = from[j].getNSof(j, i);
									int ns = from[j].getNS(k);
									if(ns>0) {
//										if(ns==0)
//										System.out.println("k: "+k+", "+from[j].toString());
//										System.out.println("ns: "+ns+", serv: "+this.serviceRate[j]+", f: "+this.calcF(from[j].getNum())+", acc: "+this.isAccepted(from[i].getNum(), i));
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
//		System.out.println("checkBbsCondition2");
		for(int j=0; j<from.length; j++) {
			if(block[j]==0) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if(from[j].getNum()>server[j]) {
//							System.out.println("Entra");
							if( ((block[i]==0) && (from[i].getNum()>=server[i])) || block[i]==1) {
//								System.out.println("Entra2");
//								System.out.println("Entra |j="+j+" |i="+i+" |numTo="+to[j].getNum()+" |numFrom="+from[j].getNum());
								if(i==j && (to[j].getNum()==from[j].getNum()) ) {
//									System.out.println("Entra3 IF: "+to[j].getNsSize());
									for(int x=0; x<(to[j].getNsSize()); x++) {
//										System.out.println("NS: "+to[j].getNS(x)+", "+(from[j].getNS(x)+1));
										if( (to[j].getNS(x) == (from[j].getNS(x)+1)) ) {
											int ns = from[j].getNSof(j, i);
											//int ns = from[j].getNS(x);
//											System.out.println("BBS2 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//											System.out.println("FROM: "+this.printState(from));
//											System.out.println("TO: "+this.printState(to));
//											System.out.println("stampa(non 0):"+ns);
//											System.out.println(this.serviceRate[j]);
//											System.out.println(this.calcF(from[j].getNum()));
//											System.out.println(this.routingMatrix.get(j, from[j].getDestAt(x)));
//											System.out.println(this.isAccepted(from[i].getNum(), i) );
											if(ns>0) {
												qMatrix.set(xx, yy, ns*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, from[j].getDestAt(x))*this.isAccepted(from[i].getNum(), i));
												return true;
											}
										}
									}
								}
								else if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
//									System.out.println("Entra3 ELSE | size=" + to[j].getNsSize());
									for(int x=0; x<(to[j].getNsSize()); x++) {
										if( (to[j].getNS(x) == (from[j].getNS(x)+1)) ) {
//											System.out.println("Entra4 ELSE");
											int ns = from[j].getNSof(j, i);
//											System.out.println("BBS2 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//											System.out.println("FROM: "+this.printState(from));
//											System.out.println("TO: "+this.printState(to));
//											System.out.println("stampa(non 0):"+ns);
//											System.out.println(this.serviceRate[j]);
//											System.out.println(this.calcF(from[j].getNum()));
//											System.out.println(this.routingMatrix.get(j, from[j].getDestAt(x)));
//											System.out.println(this.isAccepted(from[i].getNum(), i) );
											
											if(ns>0) {
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

	private boolean checkBbsCondition3(BbsState[] from, BbsState[] to, int xx, int yy) {

		for(int j=0; j<from.length; j++) {
			if(block[j]==0) {
				if(from[j].getDest() != null) {
					for(int k=0; k<from[j].getDest().size(); k++) {
						int i = from[j].getDestAt(k);
						if(from[j].getNum()<=server[j]) {
//							System.out.println("Entra");
							if( ((block[i]==0) && (from[i].getNum()<server[i])) ) {
//								System.out.println("Entra2");
								if(i==j && (to[j].getNum()==from[j].getNum()) ) {
									for(int x=0; x<(to[i].getNsSize()); x++) {
										if( (to[i].getNS(x) == (from[i].getNS(x)+1)) ) {
											int ns = from[j].getNSof(j, i);
//											System.out.println("BBS3 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//											System.out.println("FROM: "+this.printState(from));
//											System.out.println("TO: "+this.printState(to));
											if(ns>0) {
												qMatrix.set(xx, yy, ns*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, from[j].getDestAt(x)));
												return true;
											}
										}
									}
								}
								else if( (to[j].getNum()==from[j].getNum()-1) && (to[i].getNum()==from[i].getNum()+1) ) {
									for(int x=0; x<(to[i].getNsSize()); x++) {
										if( (to[i].getNS(x) == (from[i].getNS(x)+1)) ) {
											int ns = from[j].getNSof(j, i);
											if(ns>0) {
												qMatrix.set(xx, yy, ns*this.serviceRate[j]*this.calcF(from[j].getNum())*this.routingMatrix.get(j, from[j].getDestAt(x)));
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
			System.out.print(j + ": \t");
			for(int i=0; i<qMatrix.numColumns(); i++) {
				//if(qMatrix.get(j, i)!=0)
				System.out.print(qMatrix.get(j, i)+" ");
			}
			System.out.println();
		}
		System.out.println("\n\n");
	}

	public void printZeroQMatrix(Matrix matrix) {
		Vector empty = new Vector();
		boolean check = true;
		int counter = 0;
		for(int j=0; j<matrix.numRows(); j++) {
			for(int i=0; i<matrix.numColumns(); i++) {
				if(matrix.get(j, i)!=0){
					check = false;

				}
			}
			if(check) {
				empty.add(j);
				counter++;
//				System.out.println("Riga vuota: "+j);
			}
			check = true;
			//System.out.println();

		}
		System.out.println("Empty lines: "+counter);
		for(int i=0; i<empty.size(); i++){
			System.out.println(empty.get(i));
		}

	}

	public String printState(BbsState[] state) {
		String out = "";
		for(int i=0; i<state.length; i++) {
			out += "<"+state[i].getNum()+", "+state[i].toString()+"> ";
		}
		return out;
	}
}
