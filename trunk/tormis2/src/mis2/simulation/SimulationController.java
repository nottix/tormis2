package mis2.simulation;

import java.util.Vector;

import mis2.util.*;
import mis2.states.*;
import mis2.markov.*;

public class SimulationController {
	
	public SimulationController(int numJobs, RoutingMatrixReader routing) {
		int N = ParametersContainer.getN();
		int M = ParametersContainer.getM();
		StatesGenerator statesG = new StatesGenerator(M, numJobs, routing);
//		states.printStatesDisp();
		//states.filterRsRd();
		Vector<BbsState[]> states = statesG.calcStates();
		System.out.println("Filter");
		statesG.printStates(states);
		QMatrixGenerator q = new QMatrixGenerator(states, routing.getRoutingMatrix());
		q.calcQMatrix();
		q.printQMatrix();
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
