import java.util.Scanner;
public class Hmm3 {

    static int tranMatrixRow, tranMatrixCol, emisMatrixRow, emisMatrixCol, initialStateRow, initialStateCol = 0;
    static double[][] tranMatrix, emisMatrix, initialStateMatrix;
    static double[][] alpha, beta, gamma;
    static double[][][] diGamma;
    static double[] c;
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

    static void calculateAlpha() {
      //c0 for scaling
      c = new double[emissionSequence.length];
      c[0] = 0;
      alpha = new double[emissionSequence.length][tranMatrix.length];

      for(int i = 0; i < tranMatrix.length; i++) {
        alpha[0][i] = emisMatrix[i][emissionSequence[0]] * initialStateMatrix[0][i];
        c[0] += alpha[0][i];
      }

      c[0] = 1 / c[0];
      for(int i = 0; i < tranMatrix.length; i++) {
        alpha[0][i]*= c[0];
      }

      for(int t = 1; t < emissionSequence.length; t++) {
        c[t] = 0;
        for(int i = 0; i < tranMatrix.length; i++) {
          double b = emisMatrix[i][emissionSequence[t]];
          double sum = 0;

          for(int j = 0; j < tranMatrix.length; j++) {
            sum += tranMatrix[j][i]*alpha[t - 1][j];
          }
          alpha[t][i] = b * sum;
          c[t] += alpha[t][i];
        }

        c[t] = 1 / c[t];

        for(int i = 0; i < tranMatrix.length; i++) {
          alpha[t][i]*= c[t];
        }
      }
    }

    static void calculateBeta() {
      beta = new double[emissionSequence.length][tranMatrix.length];
      for(int i = 0; i < tranMatrix.length; i++) {
        beta[emissionSequence.length - 1][i] = c[emissionSequence.length - 1];
      }

      for(int t = emissionSequence.length - 2; t >= 0; t--) {
        for(int i = 0; i < tranMatrix.length; i++) {
          double sum = 0;
          for(int j = 0; j < tranMatrix.length; j++) {
            sum += beta[t + 1][j]*emisMatrix[j][emissionSequence[t + 1]]*tranMatrix[i][j];
          }
          beta[t][i] = c[t] * sum;
        }
      }
      //System.out.println(printMatrix(beta));
    }

    static void calculateDiGamma() {
      diGamma = new double[emissionSequence.length][tranMatrix.length][tranMatrix.length];

      //System.out.println(printMatrix(alpha));

      for(int t = 0; t < emissionSequence.length - 1; t++) {
        for(int i = 0; i < tranMatrix.length; i++) {
          for(int j = 0; j < tranMatrix.length; j++) {
            double sum = 0;
            for(int k = 0; k < tranMatrix.length; k++) {
              sum += alpha[emissionSequence.length - 1][k];
            }
            diGamma[t][i][j] = (alpha[t][i]*tranMatrix[i][j]*emisMatrix[j][emissionSequence[t + 1]] * beta[t + 1][j]) / sum;
          }
        }
      }
    }

    static void calculateGamma() {
    gamma = new double[emissionSequence.length][tranMatrix.length];
      for(int t = 0; t < emissionSequence.length; t++) {
        for(int i = 0; i < tranMatrix.length; i++) {
          double sum = 0;
          for(int j = 0; j < tranMatrix.length; j++) {
            sum += diGamma[t][i][j];
          }
          gamma[t][i] = sum;
        }
      }

    }

    static double[][] estimateTransitionMatrix() {
      double[][] transitionMatrix = new double[tranMatrix.length][tranMatrix[0].length];

      calculateAlpha();
      calculateBeta();
      calculateDiGamma();
      calculateGamma();

      for(int i = 0; i < transitionMatrix.length; i++) {
        for(int j = 0; j < transitionMatrix[0].length; j++) {
          double sum1 = 0;
          double sum2 = 0;
          for(int t = 0; t < emissionSequence.length - 1; t++) {
            sum1 += diGamma[t][i][j];
            sum2 += gamma[t][i];
          }
          transitionMatrix[i][j] = sum1/sum2;
        }
      }

      return transitionMatrix;
    }

    static double[][] estimateEmissionMatrix() {

      return new double[1][1];

    }

    public static void main(String args[]) {
        readInput();
        //double[][] test = matrixMult(initialStateMatrix, tranMatrix);
        //System.out.println(printMatrix(test));
        // double[][] result = matrixMult(test, emisMatrix);
        //System.out.println(result.length + " " + result[0].length + " " + printMatrix(result));
        System.out.println(printMatrix(estimateTransitionMatrix()));
    }
}
