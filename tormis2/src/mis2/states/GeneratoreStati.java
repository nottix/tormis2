package mis2.states;
/**
 * @(#)GeneratoreStati.java
 *
 * PROGETTO MIS2 application
 *
 * @author Pace Francesco, Sabbatini Andrea, Tavernese Lorenzo Fortunato
 * @version 1.2 19/02/2007 
 */

import java.util.Vector;

/**
 * La classe ha il compito di prevedere alcune routine che permettono il calcolo
 * degli stati Markoviani senza blocco.
 * @author Pace Francesco, Sabbatini Andrea, Tavernese Lorenzo Fortunato
 */
public class GeneratoreStati {
    
    /**
     * Funzione ricorsiva del fattoriale.
     * @param n Valore di cui ci vuole calcolare il fattoriale.
     * @return fattoriale del numero
     */
    private static double factorial(double n){
            if(n==0.0) return 1.0;
            else return n * factorial(n-1);
    }
    
    /**
     * Calcola la binomiale per ottenere il numero di stati ammissibili della rete.
     * N.B. Senza blocco!!
     * @param N Numero di job nella rete
     * @param M Numero di centri(nodi) della rete
     * @return Il numero di stati globali della rete senza blocco.
     */
    private static Double calcola_num_stati(double N, double M){
            double numeratore = factorial(N+M-1.0);
            double denominatore = factorial(M-1.0) * factorial(N); 
            Double ret = new Double(numeratore / denominatore);
            if(ret-ret.intValue() > 0.5){ret = ret+1.0;}
            return ret;
    }

    /**
     * Metodo che fornisce il numero di stati della rete. Questo metodo è stato
     * scelto di implementarlo con molti parametri per consentirne un riuso futuro
     * dell'applicazione, in quanto, qualunque sia la tipologia di blocco, questo metodo
     * rimane trasparente all'utente.
     * @param num_stati numero di stati calcolati con la binomiale.
     * @param num_centri numero di centri della rete
     * @param num_job numero di job della rete
     * @return vettore con gli stati della rete.
     */
    private static Vector[] calcola_stati(int num_stati, int num_centri, int num_job){

            int i, j, k, num_sub_stati;

            Vector[] ret_matrix = new Vector[num_centri];
            for(int h=0; h<ret_matrix.length; h++){
                ret_matrix[h] = new Vector();
            }

            if(num_centri == 2){
                    for(i=0; i<num_stati; i++){
                            ret_matrix[0].add(num_job-i);
                            ret_matrix[1].add(i);
                    }
                    return ret_matrix; 
            }else if(num_centri >= 3){
                    for(j=0; j<num_centri; j++){
                               if(j==0) ret_matrix[j].add(num_job);
                               else ret_matrix[j].add(0);
                    }

                    for(i=1; i<=num_job; i++){
                            num_sub_stati = calcola_num_stati(new Double(i), new Double(num_centri-1)).intValue();
                            Vector[] tmp_matrix = new Vector[num_centri-1];
                            tmp_matrix = calcola_stati(num_sub_stati, num_centri-1, i);
                            
                            for(j=1; j<num_sub_stati+1; j++){
                                    ret_matrix[0].add(num_job-i);
                                    for(k=1; k<num_centri; k++){
                                            ret_matrix[k].add(tmp_matrix[k-1].elementAt(j-1));

                                    }
                            }
                    }
            }


            return ret_matrix;
    }
    
    /**
     * Semplice metodo che permette di gestire il flusso ad oggetti del calcolo degli stati. 
     * @param num_job numero di job della rete
     * @param num_centri numero di centri della rete
     * @return vettore con gli stati calcolati
     */
    public static Vector[] getStati(int num_job, int num_centri){

	int num_stati = calcola_num_stati((double)num_job, (double)num_centri).intValue();
        
        Vector[] matrix = new Vector[num_centri];

	matrix = calcola_stati(num_stati, num_centri, num_job);

        return matrix;
    }

}
