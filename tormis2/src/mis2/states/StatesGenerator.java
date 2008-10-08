package mis2.states;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import java.math.*;
import java.util.Vector;
import mis2.util.*;

public class StatesGenerator {
	
	private RoutingMatrixReader routing;
	private FlexCompRowMatrix statesMatrix;
	//private Vector[] statesDisp;
	private Vector<BbsState[]> states;
	
	private int numStates;
	private int M;
	private int numJobs;
	private int[] block;
	private int[] capacity;
	private int[] server;
	
	public StatesGenerator(int num_centri, int num_job, RoutingMatrixReader routing) {
		this.routing = routing;
		this.states = new Vector<BbsState[]>();
		this.M = num_centri;
		this.numJobs = num_job;
		this.numStates = this.calcNumStates(numJobs, M);
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
		this.server = ParametersContainer.getServer();
	}
	
	private long factorial(int n) {
		long result = n;
		while(n>=2) {
			result *= n-1;
			n--;
		}
		return result;
	}

	public int calcNumStates(int N, int M) {
		long num = factorial(N+M-1);
		long den = factorial(M-1) * factorial(N); 
		if(den<=0)
			return 1;
		int ret = (int)(num / den);
		ret = Math.round(ret);
		return ret;
	}

	/**
	 * Metodo che fornisce il numero di stati della rete. Questo metodo è stato
	 * scelto di implementarlo con molti parametri per consentirne un riuso futuro
	 * dell'applicazione, in quanto, qualunque sia la tipologia di blocco, questo metodo
	 * rimane trasparente all'utente.
	 * @param numStates numero di stati calcolati con la binomiale.
	 * @param numCenter numero di centri della rete
	 * @param numJobs numero di job della rete
	 * @return vettore con gli stati della rete.
	 */
	public Vector[] calcStatesDisp(int numStates, int numCenter, int numJobs){

		int i, j, k;
		int num_sub_stati;

		Vector[] ret_matrix = new Vector[numCenter];
		for(int h=0; h<ret_matrix.length; h++) {
			ret_matrix[h] = new Vector();
		}

		if(numCenter == 2) {
			for(i=0; i<numStates; i++) {
				ret_matrix[0].add(numJobs-i);
				ret_matrix[1].add(i);
			}
			//this.statesDisp = ret_matrix;
			return ret_matrix; 
		}
		else if(numCenter >= 3) {
			for(j=0; j<numCenter; j++){
				if(j==0) ret_matrix[j].add(numJobs);
				else ret_matrix[j].add(0);
			}

			for(i=1; i<=numJobs; i++) {
				num_sub_stati = calcNumStates(i, (numCenter-1));
				Vector[] tmp_matrix = new Vector[numCenter-1];
				tmp_matrix = calcStatesDisp(num_sub_stati, numCenter-1, i);

				for(j=1; j<num_sub_stati+1; j++) {
					ret_matrix[0].add(numJobs-i);
					for(k=1; k<numCenter; k++) {
						ret_matrix[k].add(tmp_matrix[k-1].elementAt(j-1));

					}
				}
			}
		}
		
		//this.statesDisp = ret_matrix;

		return ret_matrix;
	}
	
//	public void printStatesDisp() {
//		for(int i=0; i<this.statesDisp.length; i++) {
//			for(int j=0; j<this.statesDisp[i].size(); j++) {
//				System.out.print(this.statesDisp[i].get(j)+" ");
//			}
//			System.out.println();
//		}
//	}
	
	private int getMinPopulation(int index) {
		int totalCap = 0;
		for(int i=0; i<capacity.length; i++) {
			if(i!=index) {
				totalCap += capacity[i]; 
			}
		}
		totalCap = numJobs - totalCap;
		if(totalCap<0)
			return 0;
		else
			return totalCap;
	}
	
	
	public int calcBlockStates(Vector[] dispStates) {
		
		BbsState[] state;
		boolean valid = true;
		
		System.out.println("dispStates size: "+dispStates[0].size());
		for(int i=0; i<dispStates[0].size(); i++) {
			state = new BbsState[M];
			for(int j=0; j<dispStates.length; j++) {
				if(block[j]==0)
					state[j] = new BbsState((Integer)dispStates[j].get(i), routing.getDest(j));
				else
					state[j] = new BbsState((Integer)dispStates[j].get(i), null);
				//System.out.print("i: "+i+", state["+j+"]: "+state[j].getNum()+", ");
			}
			System.out.println();
			
			for(int k=0; k<block.length && valid; k++) {
				//if(block[k]==1) { //RS-RD
				if(capacity[k]>0) {
					//System.out.println("State val: "+state[k]+", pop: "+this.getMinPopulation(k)+", cap: "+capacity[k]);
					if((state[k].getNum() < this.getMinPopulation(k)) || (state[k].getNum() > capacity[k])) {
						valid=false;
					}
				}
			}
			if(valid) {
				this.states.addAll(this.genBbsStates(state));
			}
			else {
				System.out.println("State "+i);
				for(int y=0; y<state.length; y++) {
					System.out.print("Num: "+state[y].getNum()+", Ns: "+state[y].printNS()+" - ");
				}
				System.out.println("INVALID");
			}
			valid=true;
		}
		
		return 0;
	}
	
	private Vector<BbsState[]> genBbsStates(BbsState[] state) {
		Vector<BbsState[]> ret = new Vector<BbsState[]>();
		Vector[] states;
		BbsState[] clone;
		int numGen, min;
		boolean added = false;
		
		for(int i=0; i<state.length; i++) {
			if(block[i]==0 && state[i].getNum()>0) {
				min = Math.min(state[i].getNum(), server[i]);
				numGen = this.calcNumStates(min, state[i].getDest().size());
				System.out.println("Num: "+min+", NS: "+state[i].getDest().size()+", Comb: "+numGen);
				//this.statesDisp = null;
				states = this.calcStatesDisp(numGen, state[i].getDest().size(), min);
				System.out.println("States len: "+states.length);
				for(int k=0; k<states[0].size(); k++) {
					clone = state.clone();
					clone[i] = new BbsState(state[i].getNum(), state[i].getDest());
					for(int j=0; j<states.length; j++) {
						clone[i].addNS((Integer)states[j].get(k));
					}
					ret.add(clone);
					added = true;
				}
				//this.printStatesDisp();
				//this.printStates();
			}
		}
		if(!added)
			ret.add(state);
		
		for(int i=0; i<ret.size(); i++) {
			for(int j=0; j<((BbsState[])ret.get(i)).length; j++) {
				//System.out.println("j: "+j+", Num: "+((BbsState[])this.states.get(i))[j].getNum()+", NS: "+((BbsState[])this.states.get(i))[j].getNs());
				System.out.print("<"+((BbsState[])ret.get(i))[j].getNum()+", "+((BbsState[])ret.get(i))[j].printNS()+">   ");
			}
			System.out.println();
		}
		
		return ret;
	}
	
	public void printStates() {
		for(int i=0; i<this.states.size(); i++) {
			for(int j=0; j<((BbsState[])this.states.get(i)).length; j++) {
				//System.out.println("j: "+j+", Num: "+((BbsState[])this.states.get(i))[j].getNum()+", NS: "+((BbsState[])this.states.get(i))[j].getNs());
				System.out.print("<"+((BbsState[])this.states.get(i))[j].getNum()+", "+((BbsState[])this.states.get(i))[j].printNS()+">   ");
			}
			System.out.println();
		}
	}
	
	public void calcStates() {
		Vector[] vec = calcStatesDisp(numStates, M, numJobs);
		//this.printStatesDisp();
		calcBlockStates(vec);
	}
}
