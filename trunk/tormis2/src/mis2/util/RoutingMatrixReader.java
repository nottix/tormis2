package mis2.util;

import java.io.*;
import java.util.Vector;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;

public class RoutingMatrixReader {
	
	public static String path = "data/routing_matrix";
	
	private int M;
	
	private float[][] routingMatrixRaw;
	private Matrix routingMatrix;
	
	public RoutingMatrixReader(String path) {
		try {
			BufferedInputStream buf = new BufferedInputStream( new FileInputStream(path) );
			M = PropertiesReader.getIntValue("M");
			routingMatrixRaw = new float[M][M];
			routingMatrix = new FlexCompRowMatrix(M, M);
			char ch;
			int val;
			String num = "";
			int x=0, y=0;
			while((val=buf.read()) > 0) {
				ch = (char)val;
				if(ch=='\n') {
					routingMatrixRaw[x][y] = Float.parseFloat(num);
					routingMatrix.set(x, y, Double.parseDouble(num));
					num = "";
					y=0;
					x++;
				}
				else if(ch=='\t') {
					routingMatrixRaw[x][y] = Float.parseFloat(num);
					routingMatrix.set(x, y, Double.parseDouble(num));
					num = "";
					y++;
				}
				else {
					num += ch;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printRoutingMatrixRaw() {
		for(int i=0; i<M; i++) {
			for(int j=0; j<M; j++) {
				System.out.print(routingMatrixRaw[i][j]+"    ");
			}
			System.out.println();
		}
	}
	
	public void printRoutingMatrix() {
		for(int i=0; i<M; i++) {
			for(int j=0; j<M; j++) {
				System.out.print(routingMatrix.get(i, j)+"    ");
			}
			System.out.println();
		}
	}
	
	public Matrix getRoutingMatrix() {
		return this.routingMatrix;
	}
	
	public Vector<Integer> getDest(int i) {
		Vector<Integer> set = new Vector<Integer>();
		Double val = null;
		for(int j=0; j<this.routingMatrix.numColumns(); j++) {
			if((val=this.routingMatrix.get(i, j))>0)
				set.add((Integer)j);
		}
		return set;
	}
}
