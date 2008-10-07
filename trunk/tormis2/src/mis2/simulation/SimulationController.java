package mis2.simulation;

import mis2.util.*;
import mis2.states.*;

public class SimulationController {
	
	public SimulationController(int numJobs, RoutingMatrixReader routing) {
		int N = ParametersContainer.getN();
		int M = ParametersContainer.getM();
		StatesGenerator states = new StatesGenerator(M, numJobs, routing);
		states.calcStates();
		states.printStatesDisp();
		//states.filterRsRd();
		System.out.println("Filter");
		states.printStates();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoutingMatrixReader routing = new RoutingMatrixReader(RoutingMatrixReader.path);
		routing.printRoutingMatrix();
		ParametersContainer.loadParameters();
		SimulationController sim;
		for(int i=1; i<=ParametersContainer.getN(); i++) {
			System.out.println("Job: "+i);
			sim = new SimulationController(i, routing);
		}
		

	}

}
