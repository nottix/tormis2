package mis2.markov;

import java.util.*;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

public class MemoryReclaimer extends Thread {

	private Vector<QMatrixGenerator> qVec;
	
	public MemoryReclaimer(Vector<QMatrixGenerator> qVec) {
		this.qVec = qVec;
		this.setDaemon(true);
		this.setPriority(Thread.MAX_PRIORITY);
		this.start();
	}
	
	public void run() {
		while(true) {
			System.out.println("Free Memory: "+Runtime.getRuntime().freeMemory());
			if(Runtime.getRuntime().freeMemory()<500000000) {
				System.out.println("Free Memory: "+Runtime.getRuntime().freeMemory());
				for(int i=0; i<qVec.size(); i++) {
					qVec.get(i).lock();
				}
				Matrix qMatrix = qVec.get(0).getQMatrix();
				((FlexCompRowMatrix)qMatrix).compact();
				System.gc();
				System.runFinalization();
				for(int i=0; i<qVec.size(); i++) {
					qVec.get(i).unlock();
				}
			}
			try {
				sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			yield();
		}
	}
	
}
