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
		
		Matrix qMatrix = new DenseMatrix(states.size(), states.size());
		Double size = (double)states.size();
		
		QMatrixGenerator q = null;
		Vector<QMatrixGenerator> qVec = new Vector<QMatrixGenerator>();
		Double numThread = 3.0;
		for(Double i=0.0; i<size; i+=(size/numThread)) {
			int endRow = Double.valueOf(Math.ceil((i+(size/numThread)))).intValue();
			int sizeVal = Double.valueOf(Math.ceil(size)).intValue();
			//System.out.println("Size: "+sizeVal+", StartRow: "+i.intValue()+", StartCol: "+0+", EndRow: "+endRow+", EndCol: "+(sizeVal));
			q = new QMatrixGenerator(states, routing.getRoutingMatrix(), qMatrix, i.intValue(), 0, endRow, sizeVal);
			qVec.add(q);
		}
		
		try {
			for(int i=0; i<qVec.size(); i++) {
				qVec.get(i).join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		StateProbability prob = new StateProbability(qMatrix);
		DenseVector x = prob.calcPi();
		
		q.printZeroQMatrix(qMatrix);
		
		//q.printQMatrix();
		prob.printX(x);
		
		IndexCalculator index = new IndexCalculator(numJobs, states, 
                                                x, routing.getRoutingMatrix());
		System.out.println();
		double total = 0;
		for(int i=0; i<x.size(); i++) {
			total += x.get(i);
		}
		for(int i=0; i<M; i++) {
                        System.out.println();
			System.out.println("\tU"+i+": "+index.calcUtilizationOf(i));
			System.out.println("\tX"+i+": "+index.calcThroughputOf(i));
			System.out.println("\tL"+i+": "+index.calcMeanQueueOf(i));
			System.out.println("\tT"+i+": "+index.calcMeanResponseTimeOf(i));
		}
		System.out.println("\tTr: "+index.centerResponseTime(index.getTotT(), new Gauss(routing.getRoutingMatrix()).getRapVisite()));
		
		System.out.println("\n\ttotal: "+total);
                System.out.println();
                
        MVA mva = new MVA(numJobs, routing.getRoutingMatrix());
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoutingMatrixReader routing = new RoutingMatrixReader(RoutingMatrixReader.path);
		routing.printRoutingMatrix();
		ParametersContainer.loadParameters();
		SimulationController sim = null;
		for(int i=1/*ParametersContainer.getN()*/; i<=ParametersContainer.getN(); i++) {
			System.out.println("Job: "+i);
			sim = new SimulationController(i, routing);
		}
	}

}
