package mis2.states;

public class BbsState {

	private int[] state;
	
	public BbsState(int n, int ns) {
		state = new int[2];
		state[0] = n;
		state[1] = ns;
	}
	
	public void setState(int n, int ns) {
		state[0] = n;
		state[1] = ns;
	}
	
	public void setNum(int num) {
		this.state[0] = num;
	}
	
	public void setNs(int ns) {
		this.state[1] = ns;
	}
	
	public int getNum() {
		return state[0];
	}
	
	public int getNs() {
		return state[1];
	}
}
