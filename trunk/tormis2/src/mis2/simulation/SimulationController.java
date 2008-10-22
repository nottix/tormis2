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
//		double total2 = 0;
//		for(int j=0; j<states.size(); j++) {
//			total2 += states.get(j)[1].getNum();
//		}
//		System.out.println("Total: "+total2);
		statesGen.printStates(states);
		
		QMatrixGenerator q = new QMatrixGenerator(states, routing.getRoutingMatrix());
		Matrix qMatrix = q.calcQMatrix();
		
		StateProbability prob = new StateProbability(qMatrix);
		DenseVector x = prob.calcPi();
		
		q.printZeroQMatrix();
//		q.printQMatrix();
		prob.printX(x);
		
//		IndexCalculator index = new IndexCalculator(numJobs, states, 
//                                                x, routing.getRoutingMatrix());
//		System.out.println();
//		double total = 0;
//		for(int i=0; i<x.size(); i++) {
//			total += x.get(i);
//		}
//		for(int i=0; i<M; i++) {
//                        System.out.println();
//			System.out.println("\tU"+i+": "+index.calcUtilizationOf(i));
//			System.out.println("\tX"+i+": "+index.calcThroughputOf(i));
//			System.out.println("\tL"+i+": "+index.calcMeanQueueOf(i));
//			System.out.println("\tT"+i+": "+index.calcMeanResponseTimeOf(i));
//		}
//		System.out.println("\n\ttotal: "+tIndexCalculatootal);
//                System.out.println();
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoutingMatrixReader routing = new RoutingMatrixReader(RoutingMatrixReader.path);
		routing.printRoutingMatrix();
		ParametersContainer.loadParameters();
		SimulationController sim = null;
		for(int i=ParametersContainer.getN(); i<=ParametersContainer.getN(); i++) {
			System.out.println("Job: "+i);
			sim = new SimulationController(i, routing);
		}
	}

}
