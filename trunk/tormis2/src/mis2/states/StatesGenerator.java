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
	private int[] block;    /*  0 - BBS, 
                                    1 - RS_RD */
	private int[] capacity={0,5,5,5,5,5,5,5}; /* Capacità coda (0 - infinita)*/
	private int[] server;   /* Num servernti x centro */
	
	public StatesGenerator(int num_centri, int num_job, RoutingMatrixReader routing) {
		this.routing = routing;
		this.states = new Vector<BbsState[]>();
		this.M = num_centri;
		this.numJobs = num_job;
		this.numStates = this.calcNumStates((double)numJobs, (double)M).intValue();
		this.block = ParametersContainer.getBlock();
		//this.capacity = ParametersContainer.getCapacity();
		this.server = ParametersContainer.getServer();
	}
	
	private double factorial(double n){
            if(n==0.0) return 1.0;
            else return n * factorial(n-1);
        }

	private Double calcNumStates(double N, double M){
            double numeratore = factorial(N+M-1.0);
            double denominatore = factorial(M-1.0) * factorial(N); 
            Double ret = new Double(numeratore / denominatore);
            if(ret-ret.intValue() > 0.5){ret = ret+1.0;}
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
	private Vector[] calcStatesDisp(int num_stati, int num_centri, int num_job){

            int i, j, k, num_sub_stati;

            Vector[] ret_matrix = new Vector[num_centri];
            for(int h=0; h<ret_matrix.length; h++){
                ret_matrix[h] = new Vector();
            }

            if(num_centri == 2){
                    for(i=0; i<num_stati; i++){
                            ret_matrix[0].add(num_job-i);
                            ret_matrix[1].add(i);
                    }
                    return ret_matrix; 
            }else if(num_centri >= 3){
                    for(j=0; j<num_centri; j++){
                               if(j==0) ret_matrix[j].add(num_job);
                               else ret_matrix[j].add(0);
                    }

                    for(i=1; i<=num_job; i++){
                            num_sub_stati = calcNumStates(new Double(i), new Double(num_centri-1)).intValue();
                            Vector[] tmp_matrix = new Vector[num_centri-1];
                            tmp_matrix = calcStatesDisp(num_sub_stati, num_centri-1, i);
                            
                            for(j=1; j<num_sub_stati+1; j++){
                                    ret_matrix[0].add(num_job-i);
                                    for(k=1; k<num_centri; k++){
                                            ret_matrix[k].add(tmp_matrix[k-1].elementAt(j-1));

                                    }
                            }
                    }
            }


            return ret_matrix;
        }
	
	
//	private int getMinPopulation(int index) {
//		int totalCap = 0;
//		for(int i=0; i<capacity.length; i++) {
//			if(i!=index) {
//				totalCap += capacity[i]; 
//			}
//		}
//		totalCap = numJobs - totalCap;
//		if(totalCap<0)
//			return 0;
//		else
//			return totalCap;
//	}
	
	
//	public Vector<BbsState[]> calcBlockStates(Vector[] dispStates) {
//		
//		BbsState[] state;
//		boolean valid = true;
//		
//		System.out.println("dispStates size: "+dispStates[0].size());
//		for(int i=0; i<dispStates[0].size(); i++) {
//			state = new BbsState[M];
//			for(int j=0; j<dispStates.length; j++) {
//				if(block[j]==0)
//					state[j] = new BbsState((Integer)dispStates[j].get(i), routing.getDest(j), j);
//				else
//					state[j] = new BbsState((Integer)dispStates[j].get(i), routing.getDest(j), j);
//				//System.out.print("i: "+i+", state["+j+"]: "+state[j].getNum()+", ");
//			}
//			System.out.println();
//			
//			for(int k=0; k<block.length && valid; k++) {
//				//if(block[k]==1) { //RS-RD
//				if(capacity[k]>0) {
//					//System.out.println("State val: "+state[k]+", pop: "+this.getMinPopulation(k)+", cap: "+capacity[k]);
//					if((state[k].getNum() < this.getMinPopulation(k)) || (state[k].getNum() > capacity[k])) {
//						valid=false;
//					}
//				}
//			}
//			if(valid) {
//				this.states.addAll(this.genBbsStates(state));
//			}
//			else {
//				System.out.println("State "+i);
//				for(int y=0; y<state.length; y++) {
//					System.out.print("Num: "+state[y].getNum()+", Ns: "+state[y].printNS()+" - ");
//				}
//				System.out.println("INVALID");
//			}
//			valid=true;
//		}
//		
//		return this.states;
//	}
//	
//	private Vector<BbsState[]> genBbsStates(BbsState[] state) {
//		Vector<BbsState[]> ret = new Vector<BbsState[]>();
//		Vector[] states;
//		BbsState[] clone;
//		int numGen, min;
//		boolean added = false;
//		
//		for(int i=0; i<state.length; i++) {
//			if(block[i]==0 && state[i].getNum()>0) {
//				min = Math.min(state[i].getNum(), server[i]);
//				numGen = this.calcNumStates(min, state[i].getDest().size()).intValue();
//				//System.out.println("Num: "+min+", NS: "+state[i].getDest().size()+", Comb: "+numGen);
//				//this.statesDisp = null;
//				states = this.calcStatesDisp(numGen, state[i].getDest().size(), min);
//				//System.out.println("States len: "+states.length);
//				for(int k=0; k<states[0].size(); k++) {
//					clone = state.clone();
//					clone[i] = new BbsState(state[i].getNum(), state[i].getDest(), i);
//					for(int j=0; j<states.length; j++) {
//						clone[i].addNS((Integer)states[j].get(k));
//					}
//					ret.add(clone);
//					added = true;
//				}
//				//this.printStatesDisp();
//				//this.printStates();
//			}
//		}
//		if(!added)
//			ret.add(state);
//		
//		for(int i=0; i<ret.size(); i++) {
//			for(int j=0; j<((BbsState[])ret.get(i)).length; j++) {
//				//System.out.println("j: "+j+", Num: "+((BbsState[])this.states.get(i))[j].getNum()+", NS: "+((BbsState[])this.states.get(i))[j].getNs());
//				System.out.print("<"+((BbsState[])ret.get(i))[j].getNum()+", "+((BbsState[])ret.get(i))[j].printNS()+">   ");
//			}
//			System.out.println();
//		}
//		
//		return ret;
//	}
        
        private int[] getState(int index, Vector[] states) {
        
                int[] ret = new int[this.M];
                
                for(int i=0; i<this.M; i++){
                    ret[i] = (Integer)states[i].elementAt(index);
                }
                
                return ret;
        }
        
        private boolean checkOverB(int[] state) {
        
                boolean check = false;
                
                for(int i=0; i<this.M; i++){
                    if(state[i] > this.capacity[i] && this.capacity[i] != 0){
                        check = true;
                    }
                }
                
                return check;
        }
        
        private Vector[] filterB(Vector[] states) {
         
                Vector[] ret = new Vector[this.M];
                for(int r=0; r<this.M; r++)
                    ret[r] = new Vector();
                
                for(int i=0; i<states[0].size(); i++){
                
                    int tmp[] = this.getState(i, states);
                    if( !(this.checkOverB(tmp)) ){
                        for(int j=0; j<this.M; j++){
                            ret[j].add(states[j].elementAt(i));
                        }
                    }
                }
                
                return ret;
        }
        
        private boolean min(int a, int b) {
                return a<=b;
        }
        
        private BbsState[] initBBS() {
        
                BbsState[] bbs = new BbsState[this.M];
                for(int i=0; i<this.M; i++){
                    bbs[i] = new BbsState(-1, routing.getDest(i), i);
                }
                return bbs;
        }
        
        public Vector<BbsState[]> calcBlockStates(Vector[] states) {
        
                Vector<BbsState[]> ret = new Vector<BbsState[]>();
                Vector[] statesB = filterB(states);
                BbsState[] bbs = this.initBBS();
                
                for(int i=0; i<statesB[0].size(); i++) {
                    
                }
                
                return ret;
        }
        
        public void printStatesDisp(Vector[] state) {
		for(int i=0; i<state[0].size(); i++) {
			for(int j=0; j<state.length; j++) {
				System.out.print(state[j].get(i)+" ");
			}
			System.out.println();
		}
	}
	
	public void printStates(Vector<BbsState[]> states) {
		for(int i=0; i<states.size(); i++) {
			for(int j=0; j<((BbsState[])states.get(i)).length; j++) {
				//System.out.println("j: "+j+", Num: "+((BbsState[])this.states.get(i))[j].getNum()+", NS: "+((BbsState[])this.states.get(i))[j].getNs());
				System.out.print("<"+((BbsState[])states.get(i))[j].getNum()+", "+((BbsState[])states.get(i))[j].printNS()+">   ");
			}
			System.out.println();
		}
	}
	
	public Vector<BbsState[]> calcStates() {
		Vector[] vec = calcStatesDisp(numStates, M, 30);//numJobs);
		this.printStatesDisp(vec);
		return calcBlockStates(vec);
	}
        
        public static void main(String args[]) {
        
                int jobs = 30;
                int nodes = 8;
                StatesGenerator sg = new StatesGenerator(nodes, jobs, null);
                int n_states = sg.calcNumStates(jobs, nodes).intValue();
                Vector[] states = sg.calcStatesDisp(n_states, nodes, jobs);
                System.out.println("Dim states: " + n_states);
                Vector[] statesB = sg.filterB(states);
                System.out.println("Dim statesB: " + statesB[0].size());
                sg.printStatesDisp(statesB);
        }
}
