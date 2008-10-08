package mis2.states;

import java.util.Vector;
import mis2.util.*;

public class BbsState {

	private int[] state;
	private int index;
	private Vector<Integer> dest;
	private int i;
	
	/**
	 * <n, <NSi1, NSi2, ..., NSik>>
	 * 
	 * @param n
	 * @param dim
	 */
	public BbsState(int n, Vector<Integer> dest, int i) {
		this.i = i;
		this.dest = dest;
//		System.out.println("Size: "+Math.min(ParametersContainer.getServer(i), dest.size()+1));
//		state = new int[Math.min(ParametersContainer.getServer(i), dest.size()+1)+1];
		//System.out.println("Size: "+(dest.size()+1));
		if(ParametersContainer.getBlock(i)==1)
			state = new int[1];
		else
			state = new int[(dest.size()+1)];
		state[0] = n;
		index = 1;
	}
	
	public Vector<Integer> getDest() {
		return this.dest;
	}
	
	public int getDestAt(int k) {
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
	public int getNsSize() {
		return this.state.length-1;
		//return index;
	}
	
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
		if(i+1>=state.length) {
			System.out.println("SFORATO i+1: "+(i+1)+", size: "+state.length);
			return -1;
		}
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
