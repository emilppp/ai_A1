import java.util.Scanner;
public class Hmm2 {

    static int tranMatrixRow, tranMatrixCol, emisMatrixRow, emisMatrixCol, initialStateRow, initialStateCol = 0;
    static double[][] tranMatrix, emisMatrix, initialStateMatrix;
    static int[] emissionSequence, stateSequence;

    static Scanner scanner = new Scanner(System.in);

    public static void readInput() {

        tranMatrix = generateMatrix(scanner.nextInt(), scanner.nextInt());

        emisMatrix = generateMatrix(scanner.nextInt(), scanner.nextInt());

        initialStateMatrix = generateMatrix(scanner.nextInt(), scanner.nextInt());
        
        

       
       int emissionSequenceLength = scanner.nextInt();
       stateSequence = new int[emissionSequenceLength];
       emissionSequence = new int[emissionSequenceLength];
        for(int i = 0; i < emissionSequence.length; i++) {
          emissionSequence[i] = scanner.nextInt();
        }
    }

    public static double[][] generateMatrix(int rows, int columns) {
        double[][] matrix = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                matrix[i][j] = scanner.nextDouble();
            }
        }
        return matrix;
    }

    public static String printMatrix(double[][] matrix) {
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


    public static int[] viterbi() {
        double[][] delta = new double[emissionSequence.length][tranMatrix.length];
        double[][] delta_idx = new double[emissionSequence.length][tranMatrix.length];
        for(int i = 0; i < delta.length; i++) {
            delta[0][i] = emisMatrix[i][emissionSequence[0]] * initialStateMatrix[0][i];
        }

        for(int t = 0; t < emissionSequence.length; t++) {
            for(int i = 0; i < tranMatrix.length; i++) {
                double maximum = 0;
                for(int j = 0; j < tranMatrix.length; j++) {
                    double current = 0;
                    current = tranMatrix[j][i] * delta[t][j] * emisMatrix[i][emissionSequence[t]];
                    if(current > maximum) {
                        delta[t][i] = current;
                        maximum = current;
                        delta_idx[t][i] = j;
                    }
                }
            }        
        }

        double maximum = 0;
        for(int j = 0; j < tranMatrix.length; j++) {
            if(delta[tranMatrix.length-1][j] > maximum) {
                maximum = delta[tranMatrix.length-1][j];
                stateSequence[tranMatrix.length-1] = j;
            }
            for(int t = tranMatrix.length-2; t >= 0; t--) {
                stateSequence[t] = (int) delta_idx[t][stateSequence[t+1]];
            }
        }
        return stateSequence;
    }

    public static double[][] matrixMult(double[][] matrixA, double[][] matrixB) {
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



    public static void main(String args[]) {
        readInput();
        //double[][] test = matrixMult(initialStateMatrix, tranMatrix);
        //System.out.println(printMatrix(test));
        // double[][] result = matrixMult(test, emisMatrix);
        //System.out.println(result.length + " " + result[0].length + " " + printMatrix(result));
        //System.out.println(calculateAlpha());
        int[] X = viterbi();

        for(int i=0; i<X.length; i++) {
            System.out.print(X[i]+ " ");
        }
    }
}
