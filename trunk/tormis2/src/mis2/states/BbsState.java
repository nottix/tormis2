package mis2.states;

import java.util.Vector;
import mis2.util.*;

public class BbsState {

	private int[] state;
	private int index;
	private Vector<Double> dest;
	
	/**
	 * <n, <NSi1, NSi2, ..., NSik>>
	 * 
	 * @param n
	 * @param dim
	 */
	public BbsState(int n, int i, Vector<Double> dest) {
		this.dest = dest;
		state = new int[Math.min(ParametersContainer.getServer(i), dest.size()+1)];
		state[0] = n;
		index = 1;
	}
	
	public Vector<Double> getDest() {
		return this.dest;
	}
	
	public double getDestAt(int k) {
		return this.dest.get(k);
	}
	
	public void addNS(int ns) {
		this.state[index] = ns;
		index++;
	}
	
//	public void setState(int n, int ns) {
//		state[0] = n;
//		state[1] = ns;
//	}
//	
	public void setNum(int num) {
		this.state[0] = num;
	}
//	
//	public void setNs(int ns) {
//		this.state[1] = ns;
//	}
//	
	public int getNum() {
		return state[0];
	}
	
	public int getNS(int i) {
		return state[i+1];
	}
}
