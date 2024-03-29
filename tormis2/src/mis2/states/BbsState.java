package mis2.states;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import mis2.util.*;

public class BbsState {

	private int[] state;
	private int index;
	private Vector<Integer> dest;
	private int iNode;
	private int[] capacity;

	/**
	 * <n, [NSi1, NSi2, ..., NSiR]>
	 *  
	 *      R - min(Ki, dest.size()+1)
	 *      Ki - # server centro
	 *      Sum(Nsi) == min(n, Ki);
	 * 
	 * @param n
	 * @param dim
	 * 
	 */
	public BbsState(int n, Vector<Integer> dest, int iNode, int N) {
		this.capacity = ParametersContainer.getCapacity();
		this.iNode = iNode;
		this.dest = dest;
//		System.out.println("Size: "+Math.min(ParametersContainer.getServer(iNode), dest.size()+1));
//		state = new int[Math.min(ParametersContainer.getServer(iNode), dest.size()+1)+1];
		//System.out.println("Size: "+(dest.size()+1));
		if(ParametersContainer.getBlock(iNode)==1 || ParametersContainer.getCapacity(iNode)>=N || ParametersContainer.getCapacity(iNode)==0)
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
		this.state[index] = ns;
		index++;
	}

	public void setNS(int ns, int index) {
		if(index > this.state.length) {
			System.out.println("Errore - index setNS() errato!!");
			return;
		}
		this.state[index] = ns;
	}

	public void setState(int[] state) {
		this.state = state;
		this.index = state.length;
	}

	public int[] getState() {
		return this.state.clone();
	}

	public int getNSof(int from, int to) {
		for(int k=0; k<this.dest.size(); k++) {
			if(this.dest.get(k).equals(to))
				return this.getNS(k);
		}
		return -1;
	}

	public void reset() {
		for(int iNode=1; iNode<state.length; iNode++)
			state[iNode] = 0;
		index=1;
	}

	public int getNsSize() {
		return this.state.length-1;
	}

	public void setNum(int num) {
		this.state[0] = num;
	}

	public int getNum() {
		return state[0];
	}
	
	public int getZ() {
		int z = 0;
		for(int s=0; s<this.dest.size(); s++) {
			z += this.getDestAt(s);
		}
		return z;
	}

	public int getNS(int iNode) {
		if(iNode+1>=state.length) {
			System.out.println("SFORATO iNode+1: "+(iNode+1)+", size: "+state.length);
			return -1;
		}
		return state[iNode+1];
	}

	public boolean isBlocked() {
		for(int k=0; k<this.dest.size(); k++) {
			if(this.getNum() > capacity[this.dest.get(k)]) {
				return true;
			}
		}

		return false;
	}

	public int getINode() {
		return this.iNode;
	}

	public String toString() {
		String out = "[";
		for(int iNode=1; iNode<state.length; iNode++) {
			out += state[iNode];
			if(iNode<state.length-1)
				out += ", ";
		}
		out += "]";
		return out;
	}
}
