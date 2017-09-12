import java.util.Scanner;
import java.lang.Math;

public class Hmm3 {

    static int tranMatrixRow, tranMatrixCol, emisMatrixRow, emisMatrixCol, initialStateRow, initialStateCol = 0;
    static double[][] tranMatrix, emisMatrix, initialStateMatrix;
    static double[][] alpha, beta, gamma;
    static double[][][] diGamma;
    static double[] c;
    static int[] emissionSequence;

    static double oldLogProb = -999999999;
    
    static int iters = 0;
    static int maxIters = 1000; 
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
        alpha[0][i] = initialStateMatrix[0][i] * emisMatrix[i][emissionSequence[0]];
        c[0] += alpha[0][i];
      }

      c[0] = 1.0 / c[0];
      for(int i = 0; i < tranMatrix.length; i++) {
        alpha[0][i] = c[0]*alpha[0][i];
      }

      for(int t = 1; t < emissionSequence.length; t++) {
        c[t] = 0;
        for(int i = 0; i < tranMatrix.length; i++) {
          alpha[t][i] = 0;
          for(int j = 0; j < tranMatrix.length; j++) {
            alpha[t][i] = alpha[t][i] + alpha[t-1][j]*tranMatrix[j][i];
          }
          alpha[t][i] = alpha[t][i]*emisMatrix[i][emissionSequence[t]];
          c[t] = c[t] + alpha[t][i];
        }
       
        c[t] = 1.0 / c[t];
        for(int i = 0; i < tranMatrix.length; i++) {
          alpha[t][i] = c[t]*alpha[t][i];
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
          beta[t][i] = 0;
          for(int j = 0; j < tranMatrix.length; j++) {
            beta[t][i] += tranMatrix[i][j]*emisMatrix[j][emissionSequence[t + 1]]*beta[t + 1][j];
          }
          //System.out.println(beta.length + " " + beta[t].length + " " + c.length);
          beta[t][i] = c[t]*beta[t][i];
        }
      }
    }

    static void calculateGammas() {
      gamma = new double[emissionSequence.length][tranMatrix.length];
      diGamma = new double[emissionSequence.length][tranMatrix.length][tranMatrix.length];

      for(int t = 0; t < emissionSequence.length - 1; t++) {
        double denom = 0.0;
        for(int i = 0; i < tranMatrix.length; i++) {
          for(int j = 0; j < tranMatrix.length; j++) {
            denom += alpha[t][i]*tranMatrix[i][j]*emisMatrix[j][emissionSequence[t + 1]]*beta[t + 1][j];
          }
        }
        for(int i = 0; i < tranMatrix.length; i++) {
          gamma[t][i] = 0.0;
          for(int j = 0; j < tranMatrix.length; j++) {
            if(denom == 0.0) {
                System.out.println("hahaha");
            }
            diGamma[t][i][j] = (alpha[t][i]*tranMatrix[i][j]*emisMatrix[j][emissionSequence[t + 1]]*beta[t + 1][j])/denom;
            gamma[t][i] += diGamma[t][i][j];
          }
        }
      }

      //spshl case
      double denom = 0;
      for(int i = 0; i < tranMatrix.length; i++) {
        denom += alpha[emissionSequence.length - 1][i];
      }
      for(int i = 0; i < tranMatrix.length; i++) {
        gamma[emissionSequence.length - 1][i] = (alpha[emissionSequence.length - 1][i]) / denom;
      }
    }

    static double calculateLog() {
      double logProb = 0;
      for(int i = 0; i < emissionSequence.length; i++) {
        logProb += Math.log(c[i]);
      }

      return -logProb;
    }

    static void baum_welch() {
      while(iters < maxIters) {
        iters++;
        calculateAlpha();
        calculateBeta();
        calculateGammas();
        reestimate();

        double logProb = calculateLog();

        if(logProb > oldLogProb) {
          oldLogProb = logProb;
          continue;
        }
        else {
          break;
        }


      }
     }

     static void reestimate() {

       // RE ESTIMATE PI
       for(int i = 0; i < tranMatrix.length; i++) {
         initialStateMatrix[0][i] = gamma[0][i];
       }
       // RE ESTIMATE A
       for(int i = 0; i < tranMatrix.length; i++) {
         for(int j = 0; j < tranMatrix.length; j++) {
           double numer = 0;
           double denom = 0;

           for(int t = 0; t < emissionSequence.length - 1; t++) {
             numer += diGamma[t][i][j];
             denom += gamma[t][i];
           }
           tranMatrix[i][j] = numer/denom;
         }
       }
       // RE ESTIMATE B
       for(int i = 0; i < tranMatrix.length; i++) {
         for(int j = 0; j < emisMatrix[0].length; j++) {
           double numer = 0;
           double denom = 0;
           for(int t = 0; t < tranMatrix.length; t++) {
             if(emissionSequence[t] == j) {
               numer += gamma[t][i];
             }
             denom += gamma[t][i];
           }
           emisMatrix[i][j] = numer/denom;
         }
       }
     }

    public static void main(String args[]) {
        readInput();
        //double[][] test = matrixMult(initialStateMatrix, tranMatrix);
        //System.out.println(printMatrix(test));
        // double[][] result = matrixMult(test, emisMatrix);
        //System.out.println(result.length + " " + result[0].length + " " + printMatrix(result));


        baum_welch();

        System.out.println(printMatrix(tranMatrix));
        System.out.println(printMatrix(emisMatrix));
    }
}
