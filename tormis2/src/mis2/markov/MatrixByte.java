/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mis2.markov;

import no.uib.cipr.matrix.AbstractMatrix;

/**
 *
 * @author gabriele
 */
public class MatrixByte extends AbstractMatrix {

        private int numRows;
        private int numCols;
        private byte[][] matrix;
        
        private final boolean test = true;
        
        public MatrixByte(int numRows, int numCols) {
            super(numRows, numCols);
            this.numRows = numRows;
            this.numCols = numCols;
            this.matrix = new byte[numRows * numCols][];
        }
        
        public void set(int i, int j, double numD) {
            Double num = Double.valueOf(numD);
            String numStr = num.toString();
            char[] strCrt = numStr.toCharArray();
            byte sign = 0;
            String tmp = "";
            
            for(int k=0; k<strCrt.length; k++){
                if(k==0){
                    sign = (strCrt[0]=='-')? (byte)1 : (byte)0;
                }
                if(strCrt[k] == '.'){
                    
                    int len = k+5;
                    matrix[(i*this.numCols) + j] = new byte[len];
                    for(int n=0; n<len; n++){
                        if(n==0){
                            matrix[(i*this.numCols) + j][n] = sign;
                        }
                        else if(n<strCrt.length){
                            this.matrix[(i*this.numCols) + j][n] = (byte)strCrt[n-1+sign];
                        }
                        else{
                            this.matrix[(i*this.numCols) + j][n] = 0;
                        }
                    }
                    break;
                }
            }
            numStr = null;
            strCrt = null;
            tmp = null;
        }
        
        public double get(int i, int j) {
        
            double ret = 0.0;
            String retStr = "";
            
            if(this.matrix[(i*this.numCols) + j] == null){
                return 0.0;
            }
            
            for(int k=0; k<this.matrix[(i*this.numCols) + j].length; k++){
                if(k==0){
                    retStr += (this.matrix[(i*this.numCols) + j][k]==1)? "-" : "";
                }
                else{
                    retStr += (char)this.matrix[(i*this.numCols) + j][k];
                }
            }
            ret = Double.valueOf(retStr);
            retStr = null;
            
            return ret;
            
        }
        
        public int numRows() {
            return this.numRows;
        }
        
        public int numColumns() {
            return this.numCols;
        }
}
