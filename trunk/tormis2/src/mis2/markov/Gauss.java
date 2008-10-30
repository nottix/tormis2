package mis2.markov;


import java.text.NumberFormat;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;


public class Gauss {
	private int num_scambi_righe;
	private Matrix matrix;
	private Matrix rapVisite;
	final int dim;

	public Gauss(Matrix matrix){
		this.dim = matrix.numColumns();

		this.matrix = matrix;
		rapVisite= new DenseMatrix(dim,dim);
//		matrix.set(0, 1, 1);
//		matrix.set(1, 2, 1);
//		matrix.set(2, 1, 0.7);
//		matrix.set(2, 0, 0.3);
		
//		matrix.set(0,1,0.25);
//		matrix.set(0,2,0.25);
//		matrix.set(0,3,0.25);
//		matrix.set(0,4,0.25);
//		matrix.set(0,5,0.25);
//		matrix.set(0,6,0.25);
//		matrix.set(0,7,0.25);
//		matrix.set(1,0,1);
//		matrix.set(1,1,1);
//		matrix.set(1,2,1);
//		matrix.set(1,3,1);
//		matrix.set(1,4,1);
//		matrix.set(1,5,1);
//		matrix.set(1,6,1);
//		matrix.set(1,7,1);
//		matrix.set(2,1,1);
//		matrix.set(2,2,1);
//		matrix.set(2,3,1);
//		matrix.set(2,4,1);
//		matrix.set(2,5,1);
//		matrix.set(2,6,1);
//		matrix.set(2,7,1);
//		matrix.set(3,0,1);
//		matrix.set(3,1,1);
//		matrix.set(3,2,1);
//		matrix.set(3,3,1);
//		matrix.set(3,4,1);
//		matrix.set(3,5,1);
//		matrix.set(3,6,1);
//		matrix.set(3,7,1);
//		matrix.set(4,0,1);
//		matrix.set(4,1,1);
//		matrix.set(4,2,1);
//		matrix.set(4,3,1);
//		matrix.set(4,4,1);
//		matrix.set(4,5,1);
//		matrix.set(4,6,1);
//		matrix.set(4,7,1);
//		matrix.set(5,0,1);
//		matrix.set(5,1,1);
//		matrix.set(5,2,1);
//		matrix.set(5,3,1);
//		matrix.set(5,4,1);
//		matrix.set(5,5,1);
//		matrix.set(5,6,1);
//		matrix.set(5,7,1);
//		matrix.set(6,0,1);
//		matrix.set(6,1,1);
//		matrix.set(6,2,1);
//		matrix.set(6,3,1);
//		matrix.set(6,4,1);
//		matrix.set(6,5,1);
//		matrix.set(6,6,1);
//		matrix.set(6,7,1);
//		matrix.set(7,0,1);	
//		matrix.set(7,1,1);	
//		matrix.set(7,2,1);	
//		matrix.set(7,3,1);	
//		matrix.set(7,4,1);	
//		matrix.set(7,5,1);	
//		matrix.set(7,6,1);	
//		matrix.set(7,7,1);
		calcola_rapporto_visite(this.matrix);
	}
	
	public Matrix getRapVisite() {
		return this.rapVisite;
	}

	//calcola la matrice che contiene il rapporto tra visite
	void calcola_rapporto_visite(Matrix matrix2){   
		int i, j; 
		double [] y = new double[dim]; 
		Matrix Pi= new DenseMatrix(dim,dim);
		Matrix backup= new DenseMatrix(dim,dim);
		backup= matrix2.copy();
		Pi=matrix2.copy();
		i = dim; 
		j = dim;
		y = calcola_pigreco(backup, Pi); 

//		System.out.println("RAPP:");
		for(i=0; i<dim; i++){
			for(j=0; j<dim; j++){
				rapVisite.set(i, j, y[i] / y[j]);
//				System.out.print(rapVisite.get(i, j)+" ");
			}
//			System.out.println();
		}
		//System.out.print("Matrice rapporti visite");
		
//		stampaAVideo(rapVisite);

	}

	/* Calcolo dei Pigreco(Si) attraverso il metodo di Perron */
	double[] calcola_pigreco(Matrix S, Matrix S1){
		int i, j;
		double det;
		int colonna_i_esima = 0;
		Matrix inversaTmp= new DenseMatrix(dim,dim);
		double [] pigreco = new double[dim];

		for(i=0; i<pigreco.length;i++){
			pigreco[i] = 0.0F;
		}    
		/* Dalla matrice S si sottrae 1 dalla diagonale principale per la proprietà di normalizzazione */
		for(i=0;i<dim;i++){
			S.set(i,i,S.get(i,i)-1.0);
			S1.set(i,i,S1.get(i,i)-1.0);
			/* Ogni volta si sostituisce alla colonna i-esima tutti 1.0, si calcola l'inversa e si prende il valore
			 * sulla diagonale principale corrispondente alla colonna i-esima che sarà il il π(Si) corrispondente */
		}
		while(colonna_i_esima < dim){
			for(i=0;i<dim;i++){
				S1.set(i,colonna_i_esima,1.0F);
			}
			det = calcola_determinante(S1);                 // Calcolo determinante di S'
//			System.out.printf("Porca puttana...");
			inversaTmp = calcola_inversa(S1, det);          // Calcolo inversa di S'
			pigreco[colonna_i_esima] = inversaTmp.get(colonna_i_esima,colonna_i_esima);  // Pigreco(Si) = S'_inv[i][i]
			S1=S.copy(); // Reset matrice S' !!!
			colonna_i_esima++;      // Incremento colonna
		}
//		System.out.println("Soluzione: ");
//		for(i=0;i<pigreco.length;i++){
//			System.out.print(pigreco[i]+" ");
//
//		}
//		System.out.println();
		return pigreco;	
	}

	/* Calcolo determinante matrice */
	double calcola_determinante(Matrix matrix){
		int i;
		double det = 1.0F;  
		num_scambi_righe = 0;
//		System.out.println("CALCDET: "+matrix.numColumns());
		Matrix ret_matrix = riduzione_gauss(matrix);
		for(i=0; i<matrix.numColumns(); i++){
			det *= ret_matrix.get(i,i);
		}

		return ( Math.pow((-1), num_scambi_righe) * det );
	}	

	//effettua la riduzione di gauss
	Matrix riduzione_gauss(Matrix matrix){
		int i, j, check, 
		iter = 1,
		numcheck = 0;    
//		System.out.println("RID MATRIX: "+matrix.numColumns());
		Matrix ret_matrix= new DenseMatrix(dim,dim);
		ret_matrix= matrix.copy();
		/* Itera finch non  stata ridotta completamente */
		while(iter == 1){
			numcheck++;
			for(i=0; i<matrix.numColumns()-1; i++){
				check = check_same_row(ret_matrix, i, i+1);
				for(j=0; j<matrix.numColumns(); j++){
					// Sottrae le righe solo se sono simili, cioè hanno il valore diverso da zero più a sx sulla stessa colonna
					if(check==1 && ret_matrix.get(i,j) != 0.0){
//						System.out.println("RID RET: "+ret_matrix.numColumns());
						ret_matrix = sottrai_riga(ret_matrix, i+1, i, (float)(ret_matrix.get(i+1,j)/ret_matrix.get(i,j)));
						j = dim;	// Come il break!! 	
					}	
				}
			}

			ret_matrix = ordina_righe(ret_matrix);
			iter = 0;
			for(i=0; i<matrix.numColumns()-1; i++){	// Controlla che ci siano tutti zero sotto la diagonale principale
				if(ret_matrix.get(i+1,i) != 0.0) iter = 1;
			}	

			if(numcheck > (matrix.numColumns()*matrix.numColumns())) // Controllo loop infinito!!!
				iter = 0;
		}

		return ret_matrix;	
	}
	/* Metodo fondamentale per la riduzione di Gauss. Sottrae dalla riga_da_sost la riga_da_sott moltimplicata per val_diff, 
	 * ossia riga_da_sost = riga_da_sost - (riga_da_sott * val_diff) */
	Matrix sottrai_riga(Matrix matrix, int riga_da_sost, int riga_da_sott, float val_diff){

//		System.out.println("Matrix1: "+matrix.numColumns());
		int i,j;
		float check_val_approx;
		Matrix ret_matrix= new DenseMatrix(dim,dim);
		ret_matrix=matrix.copy();

		for(i=0;i<matrix.numColumns();i++){
			for(j=0;j<matrix.numColumns();j++){  
				/* Calcola il nuovo valore della riga come: val = val - (val_diff * val_riga_da_sottrarre) */
				if(i == riga_da_sost){
					//System.out.println("Ret_Matrix1: "+ret_matrix.numColumns());
					check_val_approx = (float) ret_matrix.get(riga_da_sost,j) - (float)(val_diff * ret_matrix.get(riga_da_sott,j));
					if(check_val_approx > -0.0000001 && check_val_approx < 0.0000001){// Controllo di approssimazione!!!
						ret_matrix.set(riga_da_sost,j,0.0F);
					}else{
						ret_matrix.set(riga_da_sost,j,check_val_approx);
					}
				}
			}
		}
		return ret_matrix;
	} 
	/* Ordina le righe della matrice basandosi sul numero di zeri che ogni riga ha a sinistra */
	Matrix ordina_righe(Matrix matrix){
		int i, j, numcheck,
		flag = 1,
		max_check = matrix.numColumns() * matrix.numColumns();	// Per l'ordinamento riga a riga ci sono max O(n^2) confronti

		Matrix ret_matrix = matrix.copy(); 

		for(numcheck=0; numcheck<max_check; numcheck++){	
			for(i=0; i<matrix.numColumns()-1; i++){
				for(j=0; j<matrix.numColumns(); j++){
					/* Scambia le righe solo se il numero pi a sinistra  zero e quello della riga sotto diverso da zero */	
					if(flag == 1){
						if(ret_matrix.get(i,j) == 0.0 && ret_matrix.get(i+1,j) != 0.0){
							ret_matrix = scambia(ret_matrix, i, i+1);
							flag = 0; 
							num_scambi_righe++;
						}else if( (ret_matrix.get(i,j) == ret_matrix.get(i+1,j) && ret_matrix.get(i,j) != 0.0) ||
								(ret_matrix.get(i,j) != ret_matrix.get(i+1,j))){
							flag = 0;
						}
					}	
				}
				flag = 1; 
			}
		}

		return ret_matrix;	
	}
	/* Calcolo inversa matrice */
	Matrix calcola_inversa(Matrix matrix, double det){
		int i, j, count;
		double det_i;
		Matrix ret_matrix= new DenseMatrix(dim,dim);
		Matrix sott_matrix= new DenseMatrix(dim,dim);

		for(i=0; i<dim; i++){
			for(j=0; j<dim; j++){
				sott_matrix = calcola_sottomatrice(matrix, i, j);	// Calcolo la sottomatrice
				det_i = calcola_determinante(sott_matrix);		// Calcolo determinante delle sottomatrici
				/* Calcola i valori della matrice inversa come (-1)^(i+j) * (det_i / det) e facendo la trasposta */
				ret_matrix.set(j,i,(Math.pow((-1), (i+j)) * (det_i / det) ));
			}
		}
		return ret_matrix;
	}

	int check_same_row(Matrix matrix, int riga_i, int riga_j){
		int i, 
		count_0 = 0;
		for(i=0; i<matrix.numColumns(); i++){
			if(matrix.get(riga_i,i) == 0.0)	count_0++;
			else	break;
		}	

		for(i=0; i<matrix.numColumns(); i++){
			if(matrix.get(riga_j,i) == 0.0)	count_0--;
			else	break;
		}

		if(count_0 == 0)
			return 1;

		return 0;
	} 


	Matrix scambia(Matrix matrix, int riga_i, int riga_j){

		int i;
		double tmp_riga;

		for(i=0; i<matrix.numColumns(); i++){
			/* Scambio la riga_i con la riga_j */
			tmp_riga = matrix.get(riga_i,i);
			matrix.set(riga_i,i,matrix.get(riga_j,i));
			matrix.set(riga_j,i,tmp_riga);
		}

		return matrix;
	}
	/* Calcolo sotto-matrice della matrice principale rispetto a un elemento con metodo Laplace */
	Matrix calcola_sottomatrice(Matrix matrix, int x, int y){

		int count, i, j, 
		sott_i = 0, 
		sott_j = 0;
		Matrix ret_matrix= new DenseMatrix(dim-1,dim-1);

		for(i=0; i<dim; i++){
			if(i != x){
				for(j=0; j<dim; j++){
					if(j != y){ 
						ret_matrix.set(sott_i,sott_j++,matrix.get(i,j));
					}
				}
				sott_i++;
			}
			sott_j = 0;
		}

		return ret_matrix;
	}
	/**Stampa della matrice a video formattando i valori a 5 cifre decimali*/
	public void stampaAVideo(Matrix mat){
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(5);
		formatter.setMinimumFractionDigits(5);
		System.out.println("--------------------------------------------");
		for(int i=0;i<dim;i++)
		{
			for(int j=0;j<dim;j++)
			{

				System.out.print("[" + formatter.format(mat.get(i,j)) + "] ");
			}
			System.out.println("");
		}
		System.out.println("--------------------------------------------");
	}
	public static void main(String []args){
		//new Gauss();
	}
}