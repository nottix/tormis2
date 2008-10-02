package mis2.simulation;

import mis2.util.*;

public class SimulationController {
	
	public SimulationController() {
		RoutingMatrixReader routing = new RoutingMatrixReader(RoutingMatrixReader.path);
		routing.printRoutingMatrix();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimulationController sim = new SimulationController();

	}

}
