package mis2.states;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import java.math.*;

public class StatesGenerator {


	private FlexCompRowMatrix statesMatrix;
	
	private long factorial(int n) {
		long result = n;
		while(n>=2) {
			result *= n-1;
			n--;
		}
		return result;
	}

	private double calcNumStates(int N, int M) {
		double num = factorial(N+M-1);
		double den = factorial(M-1) * factorial(N); 
		double ret = num / den;
		ret = Math.round(ret);
		return ret;
	}

	private Matrix calcStates(int numStates, int M, int numUsers){

		int i, j, k;
		double numSubStates;

		statesMatrix = new FlexCompRowMatrix(M, M); 
		
		
//		Vector[] ret_matrix = new Vector[num_centri];
//		for(int h=0; h<ret_matrix.length; h++){
//			ret_matrix[h] = new Vector();
//		}
		
//		for(j=0; j<M; j++){
//			if(j==0)
//				statesMatrix.set(j, 0, numUsers);
//			else
//				statesMatrix.set(j, 0, 0);
//		}
//
//		for(i=1; i<=numUsers; i++){
//			numSubStates = calcNumStates(i, M);
//			Matrix tmpMatrix = calcStates((int)numSubStates, M-1, i);
//
//			for(j=1; j<(int)numSubStates+1; j++){
//				statesMatrix.set(0, , arg2)[0].add(num_job-i);
//				for(k=1; k<num_centri; k++){
//					ret_matrix[k].add(tmp_matrix[k-1].elementAt(j-1));
//
//				}
//			}
//		}
//
//		return ret_matrix;
		return null;
	}
}
