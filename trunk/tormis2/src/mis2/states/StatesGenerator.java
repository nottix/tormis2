package mis2.states;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import java.math.*;
import java.util.Vector;
import mis2.util.*;

public class StatesGenerator {
	
	private FlexCompRowMatrix statesMatrix;
	private Vector[] statesDisp;
	private Vector<BbsState[]> states;
	
	private int numStates;
	private int M;
	private int numJobs;
	private int[] block;
	private int[] capacity;
	
	public StatesGenerator(int num_centri, int num_job) {
		this.states = new Vector();
		this.M = num_centri;
		this.numJobs = num_job;
		this.numStates = this.calcNumStates(numJobs, M);
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
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
	 * @param M numero di centri della rete
	 * @param numJobs numero di job della rete
	 * @return vettore con gli stati della rete.
	 */
	public Vector[] calcStatesDisp(int numStates, int M, int numJobs){

		int i, j, k;
		int num_sub_stati;

		Vector[] ret_matrix = new Vector[M];
		for(int h=0; h<ret_matrix.length; h++) {
			ret_matrix[h] = new Vector();
		}

		if(M == 2) {
			for(i=0; i<numStates; i++) {
				ret_matrix[0].add(numJobs-i);
				ret_matrix[1].add(i);
			}
			return ret_matrix; 
		}
		else if(M >= 3) {
			for(j=0; j<M; j++){
				if(j==0) ret_matrix[j].add(numJobs);
				else ret_matrix[j].add(0);
			}

			for(i=1; i<=numJobs; i++) {
				num_sub_stati = calcNumStates(i, (M-1));
				Vector[] tmp_matrix = new Vector[M-1];
				tmp_matrix = calcStatesDisp(num_sub_stati, M-1, i);

				for(j=1; j<num_sub_stati+1; j++) {
					ret_matrix[0].add(numJobs-i);
					for(k=1; k<M; k++) {
						ret_matrix[k].add(tmp_matrix[k-1].elementAt(j-1));

					}
				}
			}
		}
		
		this.statesDisp = ret_matrix;

		return ret_matrix;
	}
	
	public void printStatesDisp() {
		for(int i=0; i<this.statesDisp.length; i++) {
			for(int j=0; j<this.statesDisp[i].size(); j++) {
				System.out.print(this.statesDisp[i].get(j)+" ");
			}
			System.out.println();
		}
	}
	
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
	
	public int calcBlockStates() {
		
		BbsState[] state, state1, state2, state3;
		boolean valid = true;
		
		for(int i=0; i<this.statesDisp[0].size(); i++) {
			state = new BbsState[M];
			for(int j=0; j<this.statesDisp.length; j++) {
				state[j] = new BbsState((Integer)this.statesDisp[j].get(i), -1);
			}
			
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

				int k=0;
				if(state[k].getNum()>0) {
					state[k].setNs(2);
					this.states.add(state);

					state1 = state.clone();
					state1[k].setNs(3);
					this.states.add(state1);

					state2 = state.clone();
					state2[k].setNs(4);
					this.states.add(state2);

					state3 = state.clone();
					state3[k].setNs(5);
					this.states.add(state3);
				}
				else {
					this.states.add(state);
				}

			}
			else {
				System.out.println("State "+i);
				for(int y=0; y<state.length; y++) {
					System.out.print("Num: "+state[y].getNum()+", Ns: "+state[y].getNs()+" - ");
				}
				System.out.println("INVALID");
			}
			
			valid=true;
		}
		
		return 0;
	}
	
	public void printStates() {
		for(int i=0; i<this.states.size(); i++) {
			for(int j=0; j<((BbsState[])this.states.get(i)).length; j++) {
				//System.out.println("j: "+j+", Num: "+((BbsState[])this.states.get(i))[j].getNum()+", NS: "+((BbsState[])this.states.get(i))[j].getNs());
				System.out.print("<"+((BbsState[])this.states.get(i))[j].getNum()+", "+((BbsState[])this.states.get(i))[j].getNs()+">   ");
			}
			System.out.println();
		}
	}
	
	public void calcStates() {
		calcStatesDisp(numStates, M, numJobs);
		calcBlockStates();
	}
}
