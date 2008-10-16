package mis2.simulation;

import java.util.Vector;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.SparseVector;
import no.uib.cipr.matrix.sparse.BiCGstab;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import mis2.util.*;
import mis2.states.*;
import mis2.markov.*;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;

public class SimulationController {
	
	public SimulationController(int numJobs, RoutingMatrixReader routing) {
		int N = ParametersContainer.getN();
		int M = ParametersContainer.getM();
		
		StatesGenerator statesGen = new StatesGenerator(M, numJobs, routing);
		System.out.println("States:");
		Vector<BbsState[]> states = statesGen.calcStates();
		statesGen.printStates(states);
		
		QMatrixGenerator q = new QMatrixGenerator(states, routing.getRoutingMatrix());
		Matrix qMatrix = q.calcQMatrix();
		
		StateProbability prob = new StateProbability(qMatrix);
		DenseVector x = prob.calcPi();
		
		q.printQMatrix();
		prob.printX(x);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoutingMatrixReader routing = new RoutingMatrixReader(RoutingMatrixReader.path);
		routing.printRoutingMatrix();
		ParametersContainer.loadParameters();
		SimulationController sim = null;
		for(int i=1; i<=ParametersContainer.getN(); i++) {
			System.out.println("Job: "+i);
			sim = new SimulationController(i, routing);
		}
	}

}
