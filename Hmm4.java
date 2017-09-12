import java.util.Scanner;

class Hmm4 {

  //Lambda = {A, B, pi}
  static double[][] A, B, pi, alpha, beta;

  static int[] O;

  static double[] c;

  static int N, M, T, iters;

  static double oldLogProb;

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

      int T = scanner.nextInt();
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
      for (int i = 0; i < matrix.length; i++) {
          for(int j = 0; j < matrix[0].length; j++) {
              //System.out.print(matrix[i][j] + " ");
              result += matrix[i][j] + " ";
          }
          result += "\n";
          //System.out.print("\n");
      }
      System.out.println(result);
  }

  public static void alpha_pass() {
    alpha = new double[T][N];

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
    // scaled by c[t-1]
    for(int i = 0; i < N; i++) {
        beta[T-1][i] = c[T-1];
    }

    // beta_pass
    for(int t = T-2; t <= 0; t--) {
        for(int i = 0; i < N; i++) {
            beta[t][i] = 0;
            for (int j = 0; j < N ; j++) {
                beta[t][i] = beta[t][i] + A[i][j]*B[j][O[t+1]]*beta[t+1][j];
            }
            // scale beta with same factor
            beta[t][i] = c[t]*B[t][i];
        }
    }
  }
  
  public static void main(String[] args) {
    readInput();
  }
}
