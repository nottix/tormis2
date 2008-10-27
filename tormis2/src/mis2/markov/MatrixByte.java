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
        
            if(num >= 10.0){
                System.out.println("Errore double > 9.9 !!!!\n");
                return;
            }
            
            String numStr = num.toString();
            char[] strCrt = numStr.toCharArray();
            byte sign = 0;
            String tmp = "";
            
            System.out.println("Double: " + numStr);
            
            for(int k=0; k<strCrt.length; k++){
                if(k==0){
                    sign = (strCrt[0]=='-')? (byte)1 : (byte)0;
                }
                if(strCrt[k] == '.'){
                    int len = ( (strCrt.length - k)>5 )? 5 : (strCrt.length - k);
                    tmp = numStr.substring( ((sign==1)? 1 : 0), k);

                    matrix[(i*this.numCols) + j] = new byte[len + 1];
                    matrix[(i*this.numCols) + j][0] = sign;
                    matrix[(i*this.numCols) + j][1] = Byte.valueOf(tmp);

                    for(int iNum=1; iNum<len; iNum++){
                        tmp = numStr.substring(k+iNum, k+iNum+1);
                        matrix[(i*this.numCols) + j][iNum + 1] = Byte.valueOf(tmp);
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
            
            for(int k=0; k<this.matrix[(i*this.numCols) + j].length; k++){
                System.out.println(this.matrix[(i*this.numCols) + j][k]);
                if(k==0){
                    retStr += (this.matrix[(i*this.numCols) + j][k]==1)? "-" : "";
                }
                else if(k==2){
                    retStr += "." + this.matrix[(i*this.numCols) + j][k];
                }
                else{
                    retStr += this.matrix[(i*this.numCols) + j][k];
                }
            }
            
            System.out.println("get: " + retStr);
            ret = Double.valueOf(retStr);
            retStr = null;
            
            return ret;
            
        }
}
