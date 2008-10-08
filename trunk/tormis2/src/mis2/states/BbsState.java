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
	public BbsState(int n, Vector<Double> dest) {
		this.dest = dest;
//		System.out.println("Size: "+Math.min(ParametersContainer.getServer(i), dest.size()+1));
//		state = new int[Math.min(ParametersContainer.getServer(i), dest.size()+1)+1];
		//System.out.println("Size: "+(dest.size()+1));
		if(dest==null)
			state = new int[1];
		else
			state = new int[(dest.size()+1)];
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
		//System.out.println("Add: "+ns+" at "+index+", size: "+(state.length-1));
		this.state[index] = ns;
		index++;
	}
	
	public void reset() {
		for(int i=1; i<state.length; i++)
			state[i] = 0;
		index=1;
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
	
	public String printNS() {
		String out = "[";
		for(int i=1; i<state.length; i++) {
			out += state[i];
			if(i<state.length-1)
				out += ", ";
		}
		out += "]";
		return out;
	}
}
