import java.util.Scanner;
public class Hmm0 {

    static int tranMatrixRow, tranMatrixCol, emisMatrixRow, emisMatrixCol, initialStateRow, initialStateCol = 0;
    static double[][] tranMatrix, emisMatrix, initialStateMatrix;
    static Scanner scanner = new Scanner(System.in);

    public static void readInput() {

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


    public static double[][] getTranMatrix() {
        return tranMatrix;
    }

    public static double[][] getEmisMatrix() {
        return emisMatrix;
    }

    public static double[][] getInitialStateMatrix() {
        return initialStateMatrix;
    }

    public static void main(String args[]) {
        readInput();
        double[][] test = matrixMult(getInitialStateMatrix(), getTranMatrix());
        //System.out.println(printMatrix(test));
        double[][] result = matrixMult(test, getEmisMatrix());
        System.out.println(result.length + " " + result[0].length + " " + printMatrix(result));
    }
}
