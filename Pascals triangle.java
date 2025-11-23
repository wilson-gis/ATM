import java.util.Scanner;

public class PascalsTriangle {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of rows: ");
        int rows = scanner.nextInt();
        scanner.close();

        int[][] triangle = new int[rows][];

        for (int i = 0; i < rows; i++) {
            triangle[i] = new int[i + 1];

            // Calculate the numbers in the row
            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i) {
                    triangle[i][j] = 1;
                } else {
                    triangle[i][j] = triangle[i - 1][j - 1] + triangle[i - 1][j];
                }
            }
        }

        // Print the triangle
        for (int i = 0; i < rows; i++) {
            // Print spaces before the numbers
            for (int j = 0; j < rows - i - 1; j++) {
                System.out.print(" ");
            }

            // Print the numbers in the row
            for (int j = 0; j <= i; j++) {
                System.out.print(triangle[i][j] + " ");
            }

            System.out.println();
        }
    }
}