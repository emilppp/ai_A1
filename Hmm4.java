import java.util.Scanner;
import java.lang.Math;

class Hmm4 {

  //Lambda = {A, B, pi}
  static double[][] A, B, pi, alpha, beta, gamma;

  static double[][][] digamma;

  static int[] O;

  static double[] c;

  static int N, M, T, iters;

  static double oldLogProb = -Double.POSITIVE_INFINITY;

  static Scanner scanner = new Scanner(System.in);

  public static void readInput() {

      N = scanner.nextInt();
      scanner.nextInt();
      A = generateMatrix(N, N);

      scanner.nextInt();
      M = scanner.nextInt();
      B = generateMatrix(N, M);

      scanner.nextInt();
      scanner.nextInt();
      pi = generateMatrix(1, N);

      T = scanner.nextInt();
      O = new int[T];

      for(int i = 0; i < T; i++) {
        O[i] = scanner.nextInt();
      }

      // printMatrix(A);
      // printMatrix(B);
      // printMatrix(pi);

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

  public static void printMatrix(double[][] matrix) {
      String result = "";
      System.out.print(matrix.length + " " + matrix[0].length +" ");
      for (int i = 0; i < matrix.length; i++) {
          for(int j = 0; j < matrix[0].length; j++) {
              //System.out.print(matrix[i][j] + " ");
              result += matrix[i][j] + " ";
          }
          //System.out.print("\n");
      }
      System.out.println(result);
  }

  public static void alpha_pass() {
    alpha = new double[T][N];
    c = new double[T];
    // compute alpha[0][i]
    c[0] = 0;
    for (int i = 0; i < N; i++) {
      alpha[0][i] = pi[0][i] * B[i][O[0]];
      c[0] += alpha[0][i];
    }

    // scale alpha[0][i]
    c[0] = 1.0 / c[0];
    for(int i = 0; i < N; i++) {
      alpha[0][i] = c[0]*alpha[0][i];
    }

    // compute alpha[t][i]
    for(int t = 1; t < T; t++) {
      c[t] = 0;
      for(int i = 0; i < N; i++) {
        alpha[t][i] = 0;
        for(int j = 0; j < N; j++) {
          alpha[t][i] = alpha[t][i] + alpha[t-1][j] * A[j][i];
        }
        alpha[t][i] = alpha[t][i] * B[i][O[t]];
        c[t] = c[t] + alpha[t][i];
      }
      // scale alpha[t][i]
      c[t] = 1.0 / c[t];
      for(int i = 0; i < N; i++) {
        alpha[t][i] = c[t] * alpha[t][i];
      }
    }
  }

  public static void beta_pass() {
    beta = new double[T][N];
    // scaled by c[t-1]
    for(int i = 0; i < N; i++) {
        beta[T-1][i] = c[T-1];
    }

    // beta_pass
    for(int t = T-2; t >= 0; t--) {
        for(int i = 0; i < N; i++) {
            beta[t][i] = 0;
            for (int j = 0; j < N ; j++) {
                beta[t][i] = beta[t][i] + A[i][j]*B[j][O[t+1]]*beta[t+1][j];
            }
            // scale beta with same factor
            beta[t][i] = c[t]*beta[t][i];
        }
    }
    //printMatrix(beta);

  }

  public static void gamma_pass() {
    digamma = new double[T][N][N];
    gamma = new double[T][N];

    for(int t = 0; t < T - 1; t++) {
      double denom = 0;
      for(int i = 0; i < N; i++) {
        for(int j = 0; j < N; j++) {
          denom = denom + alpha[t][i] * A[i][j] * B[j][O[t + 1]] * beta[t + 1][j];
        }
      }
      for(int i = 0; i < N; i++) {
        gamma[t][i] = 0;
        for(int j = 0; j < N; j++) {
          digamma[t][i][j] = (alpha[t][i] * A[i][j] * B[j][O[t + 1]] * beta[t + 1][j])/denom;
          gamma[t][i] = gamma[t][i] + digamma[t][i][j];
        }
      }
    }

    double denom = 0;
    for(int i = 0; i < N; i++) {
      denom = denom + alpha[T - 1][i];
    }
    for(int i = 0; i < N; i++) {
      gamma[T - 1][i] = alpha[T - 1][i] / denom;
    }

  }

  public static void reestimate() {
    //re estimate pi
    for(int i = 0; i < N; i++) {
      pi[0][i] = gamma[0][i];
    }

    //re estimate A
    for(int i = 0; i < N; i++) {
      for(int j = 0; j < N; j++) {
        double numer = 0.0;
        double denom = 0.0;
        for(int t = 0; t < T - 1; t++) {
          numer += digamma[t][i][j];
          denom += gamma[t][i];
        }
        A[i][j] = numer/denom;
      }
    }

    //re estimate B
    for(int i = 0; i < N; i++) {
      for(int j = 0; j < M; j++) {
        double numer = 0.0;
        double denom = 0.0;
        for(int t = 0; t < T; t++) {
          if(O[t] == j) {
            numer += gamma[t][i];
          }
          denom += gamma[t][i];
        }
        B[i][j] = numer/denom;
      }
    }
  }

  public static double computeLog() {
    double logProb = 0.0;
    for(int i = 0; i < T; i++) {
      logProb += Math.log(c[i]);
    }
    return -logProb;
  }

  public static void baum_welch() {
    while(iters < 200) {
      iters++;
      alpha_pass();
      beta_pass();
      gamma_pass();
      reestimate();
      double logProb = computeLog();
      if(logProb > oldLogProb) {
        oldLogProb = logProb;

        continue;
      }
      else {

        break;
      }
    }
  }

  public static void main(String[] args) {
    readInput();
    baum_welch();

    printMatrix(A);
    printMatrix(B);

  }
}
