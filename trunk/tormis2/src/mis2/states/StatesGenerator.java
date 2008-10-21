package mis2.states;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import java.math.*;
import java.util.Vector;
import mis2.util.*;

public class StatesGenerator {

	private RoutingMatrixReader routing;
	private Vector<BbsState[]> states;
	private Vector[] statesDisp;

	private int numStates;
	private int M;
	private int numJobs;
	private int[] block;    /*  0 - BBS, 1 - RS_RD */
	private int[] capacity; /* Capacità coda (0 - infinita)*/
	private int[] server;   /* Num servernti x centro */


	public StatesGenerator(int numCentre, int numJobs, RoutingMatrixReader routingMatrix) {
		this.routing = routingMatrix;
		this.states = new Vector<BbsState[]>();
		this.M = numCentre;
		this.numJobs = numJobs;
		this.numStates = this.calcNumStates((double)numJobs, (double)M).intValue();
		this.block = ParametersContainer.getBlock();
		this.capacity = ParametersContainer.getCapacity();
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
	private Vector[] calcStatesDisp(int numStates, int numCentres, int numJob){

		int i, j, k, numSubStates;

		Vector[] ret_matrix = new Vector[numCentres];
		for(int h=0; h<ret_matrix.length; h++){
			ret_matrix[h] = new Vector();
		}

		if(numCentres == 2){
			for(i=0; i<numStates; i++){
				ret_matrix[0].add(numJob-i);
				ret_matrix[1].add(i);
			}
			return ret_matrix; 
		}else if(numCentres >= 3){
			for(j=0; j<numCentres; j++){
				if(j==0) ret_matrix[j].add(numJob);
				else ret_matrix[j].add(0);
			}

			for(i=1; i<=numJob; i++){
				numSubStates = calcNumStates(new Double(i), new Double(numCentres-1)).intValue();
				Vector[] tmp_matrix = new Vector[numCentres-1];
				tmp_matrix = calcStatesDisp(numSubStates, numCentres-1, i);

				for(j=1; j<numSubStates+1; j++){
					ret_matrix[0].add(numJob-i);
					for(k=1; k<numCentres; k++){
						ret_matrix[k].add(tmp_matrix[k-1].elementAt(j-1));

					}
				}
			}
		}


		return ret_matrix;
	}


//	private int getMinPopulation(int index) {
//	int totalCap = 0;
//	for(int i=0; i<capacity.length; i++) {
//	if(i!=index) {
//	totalCap += capacity[i]; 
//	}
//	}
//	totalCap = numJobs - totalCap;
//	if(totalCap<0)
//	return 0;
//	else
//	return totalCap;
//	}


//	public Vector<BbsState[]> calcBlockStates(Vector[] dispStates) {

//	BbsState[] state;
//	boolean valid = true;

//	System.out.println("dispStates size: "+dispStates[0].size());
//	for(int i=0; i<dispStates[0].size(); i++) {
//	state = new BbsState[M];
//	for(int j=0; j<dispStates.length; j++) {
//	if(block[j]==0)
//	state[j] = new BbsState((Integer)dispStates[j].get(i), routing.getDest(j), j);
//	else
//	state[j] = new BbsState((Integer)dispStates[j].get(i), routing.getDest(j), j);
//	//System.out.print("i: "+i+", state["+j+"]: "+state[j].getNum()+", ");
//	}
//	System.out.println();

//	for(int k=0; k<block.length && valid; k++) {
//	//if(block[k]==1) { //RS-RD
//	if(capacity[k]>0) {
//	//System.out.println("State val: "+state[k]+", pop: "+this.getMinPopulation(k)+", cap: "+capacity[k]);
//	if((state[k].getNum() < this.getMinPopulation(k)) || (state[k].getNum() > capacity[k])) {
//	valid=false;
//	}
//	}
//	}
//	if(valid) {
//	this.states.addAll(this.genBbsStates(state));
//	}
//	else {
//	System.out.println("State "+i);
//	for(int y=0; y<state.length; y++) {
//	System.out.print("Num: "+state[y].getNum()+", Ns: "+state[y].printNS()+" - ");
//	}
//	System.out.println("INVALID");
//	}
//	valid=true;
//	}

//	return this.states;
//	}

//	private Vector<BbsState[]> genBbsStates(BbsState[] state) {
//	Vector<BbsState[]> ret = new Vector<BbsState[]>();
//	Vector[] states;
//	BbsState[] clone;
//	int numGen, min;
//	boolean added = false;

//	for(int i=0; i<state.length; i++) {
//	if(block[i]==0 && state[i].getNum()>0) {
//	min = Math.min(state[i].getNum(), server[i]);
//	numGen = this.calcNumStates(min, state[i].getDest().size()).intValue();
//	//System.out.println("Num: "+min+", NS: "+state[i].getDest().size()+", Comb: "+numGen);
//	//this.statesDisp = null;
//	states = this.calcStatesDisp(numGen, state[i].getDest().size(), min);
//	//System.out.println("States len: "+states.length);
//	for(int k=0; k<states[0].size(); k++) {
//	clone = state.clone();
//	clone[i] = new BbsState(state[i].getNum(), state[i].getDest(), i);
//	for(int j=0; j<states.length; j++) {
//	clone[i].addNS((Integer)states[j].get(k));
//	}
//	ret.add(clone);
//	added = true;
//	}
//	//this.printStatesDisp();
//	//this.printStates();
//	}
//	}
//	if(!added)
//	ret.add(state);

//	for(int i=0; i<ret.size(); i++) {
//	for(int j=0; j<((BbsState[])ret.get(i)).length; j++) {
//	//System.out.println("j: "+j+", Num: "+((BbsState[])this.states.get(i))[j].getNum()+", NS: "+((BbsState[])this.states.get(i))[j].getNs());
//	System.out.print("<"+((BbsState[])ret.get(i))[j].getNum()+", "+((BbsState[])ret.get(i))[j].printNS()+">   ");
//	}
//	System.out.println();
//	}

//	return ret;
//	}

	private int[] getState(int index, Vector[] states) {

		int[] ret = new int[states.length];

		for(int i=0; i<states.length; i++){
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

	private BbsState[] createState() {

		BbsState[] bbs = new BbsState[this.M];
		for(int i=0; i<this.M; i++){
			bbs[i] = new BbsState(-1, routing.getDest(i), i);
		}
		return bbs;
	}

	private int[][] genBbsCombinations(int numServ, BbsState bbs) {

		int N_tmp = Math.min(numServ, bbs.getNum());
		int M_tmp = bbs.getDest().size();
                
		int numState = this.calcNumStates(new Double(N_tmp), 
				new Double(M_tmp)).intValue();
		Vector[] comb = this.calcStatesDisp(numState, M_tmp, N_tmp);

		int[][] ret = new int[comb[0].size()][M_tmp];
		for(int i=0; i<comb[0].size(); i++){
			ret[i] = this.getState(i, comb);
		}
		return ret;

	}

	private void addNewStates(Vector<BbsState[]> states, int iNode) {

		for(int i=0; i<states.size(); i++)
		{
			if(states.elementAt(i)[iNode].getNum() > 0){
				BbsState[] bbs = this.cloneState(states.elementAt(i));
				states.removeElementAt(i);
                                
				int[][] tmp = this.genBbsCombinations(this.server[iNode], bbs[iNode]);
                                
				for(int j=0; j<tmp.length; j++){
					BbsState[] bbsc = this.cloneState(bbs);
					for(int k=0; k<tmp[j].length; k++){
						bbsc[iNode].setNS(tmp[j][k], k+1);
					}
					states.add(i, bbsc);
					i++;
				}
				i--;
			}
		}
	}

	private BbsState[] cloneState(BbsState[] orig) {

		BbsState[] ret = new BbsState[orig.length];
		for(int i=0; i<ret.length; i++){
			ret[i] = new BbsState(orig[i].getNum(), 
                                (Vector<Integer>)orig[i].getDest().clone(), 
                                    orig[i].getINode());
			ret[i].setState(orig[i].getState());
		}
		return ret;
	}

	private Vector<BbsState[]> calcBlockStates(Vector[] states) {

		Vector<BbsState[]> ret = new Vector<BbsState[]>();
		Vector[] statesB = filterB(states);
		BbsState[] bbs = this.createState();

		for(int i=0; i<statesB[0].size(); i++) {
			BbsState[] bbsc = this.cloneState(bbs);
			int[] tmp = this.getState(i, states);

			for(int j=0; j<this.M; j++){
				bbsc[j].setNum(tmp[j]);
			}
			ret.add(bbsc);
		}
                
		for(int k=0; k<this.M; k++){
			if(this.block[k] == 0){ // Se il centro è BBS
				this.addNewStates(ret, k);
			}
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
				System.out.print("<"+((BbsState[])states.get(i))[j].getNum()+", "+((BbsState[])states.get(i))[j].toString()+">   ");
			}
			System.out.println();
		}
	}

	private void clearVectors(Vector[] v) {
		for(int i=0; i<this.M; i++)
			v[i].clear();
	}

	public Vector<BbsState[]> calcStates() {
		this.statesDisp = calcStatesDisp(numStates, M, numJobs);
//		this.printStatesDisp(this.statesDisp);
		this.states = calcBlockStates(this.statesDisp); 
		this.clearVectors(this.statesDisp);
		return this.states;
	}

//	public static void main(String args[]) {
//
//		int jobs = 30;
//		int nodes = 8;
//		ParametersContainer.loadParameters();
//		StatesGenerator sg = new StatesGenerator(nodes, jobs, null);
//		int n_states = sg.calcNumStates(jobs, nodes).intValue();
//		Vector[] states = sg.calcStatesDisp(n_states, nodes, jobs);
//		System.out.println("Dim states: " + n_states);
//		Vector[] statesB = sg.filterB(states);
//		sg.clearVectors(states);
//		System.out.println("Dim statesB: " + statesB[0].size());
//		//sg.printStatesDisp(statesB);
//		Vector<BbsState[]> vbbs = sg.calcBlockStates(statesB);
//		sg.clearVectors(statesB);
//		System.out.println("Dim statesBBS: " + vbbs.size());
//		sg.printStates(vbbs);
//	}
}
