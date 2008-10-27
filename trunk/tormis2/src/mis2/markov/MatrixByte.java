/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mis2.markov;

/**
 *
 * @author gabriele
 */
public class MatrixByte {

        private int numRows;
        private int numCols;
        private byte[][] matrix;
        
        private final boolean test = true;
        
        public MatrixByte(int numRows, int numCols) {
            
            this.numRows = numRows;
            this.numCols = numCols;
            this.matrix = new byte[numRows * numCols][];
        }
        
        public void set(int i, int j, Double num) {
            
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
                    matrix[(i*this.numCols) + j][0] = sign;
                    if(sign == 1){
                        for(int n=1; n<len; n++){
                            if(n<strCrt.length){
                                this.matrix[(i*this.numCols) + j][n] = (byte)strCrt[n];
                            }
                            else{
                                this.matrix[(i*this.numCols) + j][n] = 0;
                            }
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
