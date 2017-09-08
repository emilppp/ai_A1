import java.util.Scanner;
public class Hmm0 {

	public int tranMatrixRow, tranMatrixCol, emisMatrixRow, emisMatrixCol, initialStateRow, initialStateCol = 0;
	double[][] tranMatrix, emisMatrix, initialStateMatrix;
	Scanner scanner = new Scanner(System.in);
	
	public void readInput() {
		
		tranMatrixRow = scanner.nextInt();
		tranMatrixCol = scanner.nextInt();
		tranMatrix = generateMatrix(tranMatrixRow, tranMatrixCol);
		emisMatrixRow = scanner.nextInt();
		emisMatrixCol = scanner.nextInt();
		emisMatrix = generateMatrix(emisMatrixRow, emisMatrixCol);
		
		initialStateRow = scanner.nextInt();	
		initialStateCol = scanner.nextInt();
		initialStateMatrix = generateMatrix(initialStateRow, initialStateCol);
		 
	}
    

	public double[][] generateMatrix(int rows, int columns) {
		double[][] matrix = new double[rows][columns];
			
		for (int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				matrix[i][j] = scanner.nextDouble();
			}
		}
		return matrix;
	}

	public String printMatrix(double[][] matrix) {
		String result = "";
		for (int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				//System.out.print(matrix[i][j] + " ");
				result += matrix[i][j] + " ";
			}
			result += "\n";
			//System.out.print("\n");
		}
		return result;
	}
	
	
	public double[][] matrixMult(double[][] matrixA, double[][] matrixB) {
		double[][] matrix = new double[matrixA.length][matrixB[0].length];
		
		for(int i = 0; i < matrixA.length; i++) {
			for(int j = 0; j < matrixB[0].length; j++) {
				for(int k = 0; k < matrixA[0].length; k++) {
					matrix[i][j] += matrixA[i][k] * matrixB[k][j];
				}
				 
			}
		}
		return matrix;
	}
    

	public double[][] getTranMatrix() {
		return tranMatrix;
	}
	
	public double[][] getEmisMatrix() {
		return emisMatrix;
	}
	
	public double[][] getInitialStateMatrix() {
		return initialStateMatrix;
	}
	
	public static void main(String args[]) {
		Hmm0 hmm = new Hmm0();
		hmm.readInput();
		System.out.println(hmm.printMatrix(hmm.getTranMatrix()));
		System.out.println(" ");
		System.out.println(hmm.printMatrix(hmm.getEmisMatrix()));
		System.out.println(" ");
		System.out.println(hmm.printMatrix(hmm.getInitialStateMatrix()));	
		
		//double[][] matrixA = new double[2][2];
		//double[][] matrixB = new double[1][2];

		/*double[][] matrixB = new double[][]{
		{0.4, 0.2, 0.1, 0.3}
		};

		double[][] matrixA = new double[][]{
		{0.6, 0.2, 0.1, 0.1},
		{0.1, 0.4, 0.1, 0.4},
		{0.0, 0.0, 0.7, 0.3},
		{0.0, 0.0, 0.1, 0.9}
		}; */
		double[][] test = hmm.matrixMult(hmm.getInitialStateMatrix(), hmm.getTranMatrix());
		System.out.println(hmm.printMatrix(test));
		double[][] result = hmm.matrixMult(test, hmm.getEmisMatrix());
		System.out.println(result.length + " " + result[0].length + " " + hmm.printMatrix(result));
		
	}
}
