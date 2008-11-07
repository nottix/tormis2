package mis2.markov;

import java.util.Vector;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import mis2.states.BbsState;
import mis2.util.*;
import java.util.concurrent.Semaphore;;

public class QMatrixGenerator extends Thread {

	private int[] block;
	private int[] capacity;
	private int[] server;
	private double[] ts;
	private double[] serviceRate;
	private Vector<BbsState[]> states;
	private static Matrix qMatrix;
	private Matrix routingMatrix;
	private BbsState[] from, to;
	private int i, j, ii, jj, kk, ns;
	private int startRow, startCol;
	private int endRow, endCol;
	private int cond1 = 0;
	private int cond2 = 0;
	private int cond3 = 0;
	private int cond4 = 0;
	private int cond5 = 0;
	private int condDiag = 0;
	private int condZero = 0;
	public static int counter = 0;
	public Semaphore sem = null;
//	public static boolean ready = false;

	public QMatrixGenerator(Vector<BbsState[]> states, Matrix routingMatrix, Matrix qMatrix, int startRow, int startCol, int endRow, int endCol, int numThreads) {
//		if(QMatrixGenerator.sem==null)
//			sem = new Semaphore(numThreads, false);
		sem = new Semaphore(1, false);
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
	
	public void lock() {
		try {
			this.sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unlock() {
		try {
			this.sem.release();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void calcQMatrix() {
		int k;

		double diag = 0.0;
		for(j=this.startRow; j<states.size() && j<this.endRow; j++) {               // j - righe
			if((j%10)==0)
				System.out.println(this.getName()+" -> Status(size: "+states.size()+"): "+j+" of "+this.endRow+", "+(((double)(j-this.startRow)/(double)(this.endRow-this.startRow))*100)+"%");
			for(i=this.startCol; i<states.size() && i<this.endCol; i++) {       // i - colon

				if(i!=j/* && i==1 && j==0*/) {
					this.lock();
					from = states.get(j);
					to = states.get(i);
//					System.out.println("from: "+this.printState(states.get(j)));
//					System.out.println("to: "+this.printState(states.get(i)));
					if(this.checkRsRdCondition1Unb()) {
//						System.out.println("Condition RS-RD 1 " + " statoV: "+j+", statoN: "+i);
						cond1++;
					}
					else if(this.checkRsRdCondition2()) {
//						System.out.println("Condition RS-RD 2" + " statoV: "+j+", statoN: "+i);
						cond2++;
					}
					else if(this.checkBbsCondition1()) {
//						if(j==1)
//						System.out.println("INSERITO"+j+", "+i+", "+qMatrix.get(j, i));
//						System.out.println("Condition BBS 1" + " statoV: "+j+", statoN: "+i);
						cond3++;
					}
					else if(this.checkBbsCondition2()) {
//						if(j==1)
//						System.out.println("INSERITO2"+j+", "+i);
//						System.out.println("Condition BBS 2" + " statoV: "+j+", statoN: "+i);
						cond4++;
					}
					else if(this.checkBbsCondition3()) {
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

					this.unlock();
				}
			}
		}
		System.out.println("Total: "+states.size()*states.size()+", Cond1: "+cond1+", Cond2: "+cond2+", Cond3: "+cond3+", Cond4: "+cond4+", Cond5: "+cond5+", CondZero: "+condZero+", CondDiag: "+condDiag);
	}
	
	public Matrix getQMatrix() {
		return this.qMatrix;
	}
	
	private boolean checkRsRdCondition1Unb() {

		for(jj=0; jj<from.length; jj++) {
			if(from[jj].getNum()>0) {
				if(block[jj]==1 || (block[jj]==0 && from[jj].getNsSize()==0)) {
					if(from[jj].getDest() != null) {
						for(kk=0; kk<from[jj].getDest().size(); kk++) {
							ii = from[jj].getDestAt(kk);
							if( (((block[ii]==0) && (from[ii].getNum()>=server[ii])) || (block[ii]==1) || (block[ii]==0 && from[ii].getNsSize()==0)) && this.compareState() ) {
								if( (to[jj].getNum()==(from[jj].getNum()-1)) && (to[ii].getNum()==(from[ii].getNum()+1)) ) {
									qMatrix.set(j, i, this.getDelta(from[jj].getNum())*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, ii)*this.isAccepted(from[ii].getNum(), ii));
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
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

	private boolean checkRsRdCondition2() {

		for(jj=0; jj<from.length; jj++) {
			if(from[jj].getNum()>0) {
				if(block[jj]==1) {
					if(from[jj].getDest() != null) {
						for(kk=0; kk<from[jj].getDest().size(); kk++) {
							ii = from[jj].getDestAt(kk);
							if( ((block[ii]==0) && (from[ii].getNum()<server[ii])) && this.compareState()) {
								if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
									for(int x=0; x<(to[ii].getNsSize()) && x<from[ii].getNsSize(); x++) {
										if( (to[ii].getNS(x) == (from[ii].getNS(x)+1)) ) {
											qMatrix.set(j, i, this.getDelta(from[jj].getNum())*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, ii)*this.routingMatrix.get(ii, from[ii].getDestAt(x)));
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
		return false;
	}

	private boolean checkBbsCondition1() {

		for(jj=0; jj<from.length; jj++) {
			if(from[jj].getNum()>0) {
				if(block[jj]==0) { //Se BBS-SO
					if(from[jj].getDest() != null) {
						for(kk=0; kk<from[jj].getDest().size(); kk++) {
							ii = from[jj].getDestAt(kk);
							if(from[jj].getNum()<=server[jj]) {
								if( (((block[ii]==0) && (from[ii].getNum()>=server[ii])) || block[ii]==1) /*|| from[i].getNsSize()==0*/&& this.compareState()) {
//									System.out.println("Entra |j="+j+" |i="+i+" |numTo="+to[j].getNum()+" |numFrom="+from[j].getNum());
									/* Teorema di Notargiacomo */
									if(ii==jj && (to[jj].getNum()==from[jj].getNum()) && kk<from[jj].getNsSize()) {
										ns = from[jj].getNS(kk);
//										System.out.println("BBS1 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//										System.out.println("FROM: "+this.printState(from));
//										System.out.println("TO: "+this.printState(to));
//										System.out.println("ns: " + ns);
										if(ns>0) {
//											if(ns==0)
//											System.out.println("k: "+k+", "+from[j].toString());
//											System.out.println("ns: "+ns+", serv: "+this.serviceRate[j]+", f: "+this.calcF(from[j].getNum())+", acc: "+this.isAccepted(from[i].getNum(), i));
											qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.isAccepted(from[ii].getNum(), ii));
											return true;
										}
									}
									else if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) && kk<from[jj].getNsSize() ) {
										//int ns = from[j].getNSof(j, i);
										ns = from[jj].getNS(kk);
										if(ns>0) {
//											if(ns==0)
//											System.out.println("k: "+k+", "+from[j].toString());
//											System.out.println("ns: "+ns+", serv: "+this.serviceRate[j]+", f: "+this.calcF(from[j].getNum())+", acc: "+this.isAccepted(from[i].getNum(), i));
											qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.isAccepted(from[ii].getNum(), ii));
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
		return false;
	}


	private boolean checkBbsCondition2() {
//		System.out.println("checkBbsCondition2");
		for(jj=0; jj<from.length; jj++) {
			if(from[jj].getNum()>0) {
				if(block[jj]==0) {
					if(from[jj].getDest() != null) {
						for(kk=0; kk<from[jj].getDest().size(); kk++) {
							ii = from[jj].getDestAt(kk);
							if(from[jj].getNum()>server[jj]) {
//								System.out.println("Entra");
								if( (((block[ii]==0) && (from[ii].getNum()>=server[ii])) || block[ii]==1)/* || from[i].getNsSize()==0*/&& this.compareState()) {
//									System.out.println("Entra2");
//									System.out.println("Entra |j="+j+" |i="+i+" |numTo="+to[j].getNum()+" |numFrom="+from[j].getNum());
									if(ii==jj && (to[jj].getNum()==from[jj].getNum()) ) {
//										System.out.println("Entra3 IF: "+to[j].getNsSize());
										for(int x=0; x<(to[jj].getNsSize()) && x<from[jj].getNsSize(); x++) {
//											System.out.println("NS: "+to[j].getNS(x)+", "+(from[j].getNS(x)+1));
											if( (to[jj].getNS(x) == (from[jj].getNS(x)+1)) ) {
												ns = from[jj].getNSof(jj, ii);
												//int ns = from[j].getNS(x);
//												System.out.println("BBS2 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//												System.out.println("FROM: "+this.printState(from));
//												System.out.println("TO: "+this.printState(to));
//												System.out.println("stampa(non 0):"+ns);
//												System.out.println(this.serviceRate[j]);
//												System.out.println(this.calcF(from[j].getNum()));
//												System.out.println(this.routingMatrix.get(j, from[j].getDestAt(x)));
//												System.out.println(this.isAccepted(from[i].getNum(), i) );
												if(ns>0) {
													qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x))*this.isAccepted(from[ii].getNum(), ii));
													return true;
												}
											}
										}
									}
									else if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
//										System.out.println("Entra3 ELSE | size=" + to[j].getNsSize());
										for(int x=0; x<(to[jj].getNsSize()) && x<from[jj].getNsSize(); x++) {
											if( (to[jj].getNS(x) == (from[jj].getNS(x)+1)) ) {
//												System.out.println("Entra4 ELSE");
												ns = from[jj].getNSof(jj, ii);
//												System.out.println("BBS2 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//												System.out.println("FROM: "+this.printState(from));
//												System.out.println("TO: "+this.printState(to));
//												System.out.println("stampa(non 0):"+ns);
//												System.out.println(this.serviceRate[j]);
//												System.out.println(this.calcF(from[j].getNum()));
//												System.out.println(this.routingMatrix.get(j, from[j].getDestAt(x)));
//												System.out.println(this.isAccepted(from[i].getNum(), i) );

												if(ns>0) {
													qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x))*this.isAccepted(from[ii].getNum(), ii));
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
		}          
		return false;
	}

	private boolean checkBbsCondition3() {

		for(jj=0; jj<from.length; jj++) {
			if(from[jj].getNum()>0) {
				if(block[jj]==0) {
					if(from[jj].getDest() != null) {
						for(kk=0; kk<from[jj].getDest().size(); kk++) {
							ii = from[jj].getDestAt(kk);
							if(from[jj].getNum()<=server[jj]) {
//								System.out.println("Entra");
								if( ((block[ii]==0) && (from[ii].getNum()<server[ii])) && this.compareState() ) {
//									System.out.println("Entra2");
									if(ii==jj && (to[jj].getNum()==from[jj].getNum())) {
										for(int x=0; x<(to[ii].getNsSize()) && x<from[ii].getNsSize(); x++) {
											if( (to[ii].getNS(x) == (from[ii].getNS(x)+1)) ) {
												ns = from[jj].getNSof(jj, ii);
//												System.out.println("BBS3 -> x: "+xx+" y: "+yy+", j: "+j+" to k: "+k);
//												System.out.println("FROM: "+this.printState(from));
//												System.out.println("TO: "+this.printState(to));
												if(ns>0) {
													qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x)));
													return true;
												}
											}
										}
									}
									else if( (to[jj].getNum()==from[jj].getNum()-1) && (to[ii].getNum()==from[ii].getNum()+1) ) {
										for(int x=0; x<(to[ii].getNsSize()) && x<from[ii].getNsSize(); x++) {
											if( (to[ii].getNS(x) == (from[ii].getNS(x)+1)) ) {
												ns = from[jj].getNSof(jj, ii);
												if(ns>0) {
													qMatrix.set(j, i, ns*this.serviceRate[jj]*this.calcF(from[jj].getNum())*this.routingMatrix.get(jj, from[jj].getDestAt(x)));
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
		}              
		return false;
	}

	private boolean compareState() {
		if(ii==jj) {
			for(int i1=0; i1<from.length; i1++) {
				if(from[i1].getNum()!=to[i1].getNum())
					return false;
			}
			return true;
		}
		else {
			for(int i1=0; i1<from.length; i1++) {
				if(i1!=ii && i1!=jj && from[i1].getNum()!=to[i1].getNum())
					return false;
			}
			return true;
		}
	}

	public void printQMatrix() {
		int counter = 0;
		for(int j=0; j<qMatrix.numRows(); j++) {
			System.out.print(j + ": \t");
			for(int i=0; i<qMatrix.numColumns(); i++) {
				if(qMatrix.get(j, i)>0.0)
					counter++;
				System.out.print(qMatrix.get(j, i)+" ");
			}
			System.out.println();
		}
		System.out.println("\nNot null: "+counter+"\n\n");
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
