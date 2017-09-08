import java.util.Scanner;
public class Hmm1 {

    static int tranMatrixRow, tranMatrixCol, emisMatrixRow, emisMatrixCol, initialStateRow, initialStateCol = 0;
    static double[][] tranMatrix, emisMatrix, initialStateMatrix;
    static int[] emissionSequence;

    static Scanner scanner = new Scanner(System.in);

    public static void readInput() {

        tranMatrix = generateMatrix(scanner.nextInt(), scanner.nextInt());

        emisMatrix = generateMatrix(scanner.nextInt(), scanner.nextInt());

        initialStateMatrix = generateMatrix(scanner.nextInt(), scanner.nextInt());

        int emissionSequenceLength = scanner.nextInt();
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

    static double calculateAlpha() {
      double[][] alpha = new double[emissionSequence.length][tranMatrix.length];

      for(int i = 0; i < tranMatrix.length; i++) {
        alpha[0][i] = initialStateMatrix[0][i]*emisMatrix[i][emissionSequence[0]];
      }

      for(int t = 1; t < emissionSequence.length; t++) {
        for(int i = 0; i < tranMatrix.length; i++) {
          double b = emisMatrix[i][emissionSequence[t]];
          double sum = 0;

          for(int j = 0; j < tranMatrix.length; j++) {
            sum += tranMatrix[j][i]*alpha[t - 1][j];
          }
          alpha[t][i] = b * sum;
        }
      }

      double sum = 0;
      for(int i = 0; i < tranMatrix.length; i++) {
        sum += alpha[emissionSequence.length - 1][i];
        //System.out.println(alpha[i][alpha.length - 1]);
      }
      return sum;
    }

    public static void main(String args[]) {
        readInput();
        double[][] test = matrixMult(initialStateMatrix, tranMatrix);
        //System.out.println(printMatrix(test));
        // double[][] result = matrixMult(test, emisMatrix);
        //System.out.println(result.length + " " + result[0].length + " " + printMatrix(result));
        System.out.println(calculateAlpha());
    }
}
