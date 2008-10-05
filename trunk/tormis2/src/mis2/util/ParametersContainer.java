package mis2.util;

public class ParametersContainer {

	private static int N = -1;
	private static int M = -1;
	private static double[] ts = null;
	private static int[] capacity = null;
	private static int[] server = null;
	private static int[] block = null;
	
	public static void loadParameters() {
		N = PropertiesReader.getIntValue("N");
		M = PropertiesReader.getIntValue("M");
		
		ts = new double[M];
		capacity = new int[M];
		server = new int[M];
		block = new int[M];
		for(int i=0; i<M; i++) {
			ts[i] = PropertiesReader.getFloatValue("Ts"+String.valueOf(i+1));
			capacity[i] = PropertiesReader.getIntValue("B"+String.valueOf(i+1));
			System.out.println("Cap["+i+"]: "+capacity[i]);
			server[i] = PropertiesReader.getIntValue("S"+String.valueOf(i+1));
			//1 se di tipo RS-RD
			if(PropertiesReader.getStringValue("C"+String.valueOf(i+1)).equals("RS-RD"))
				block[i] = 1;
			else
				block[i] = 0;
		}
	}
	
	public static int getN() {
		return N;
	}
	
	public static int getM() {
		return M;
	}
	
	public static double getTs(int i) {
		return ts[i];
	}
	
	
	public static int getCapacity(int i) {
		return capacity[i];
	}
	
	public static int getServer(int i) {
		return server[i];
	}
	
	public static int getBlock(int i) {
		return block[i];
	}
	
	public static double[] getTs() {
		return ts;
	}
	
	public static int[] getCapacity() {
		return capacity;
	}
	
	public static int[] getServer() {
		return server;
	}
	
	public static int[] getBlock() {
		return block;
	}
}
