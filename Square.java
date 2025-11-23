import java.util.* ;
public class Main {
	public static int getSquare(int n){
		return n*n ;
	}
	public static void main(String[] args) {
		Scanner x = new Scanner(System.in) ;
		
		System.out.print("Enter the value of number you want the Squares of number up to that number : ") ;
		int m = x.nextInt() ;
		for (int k = 1 ; k <= m ; k++) {
			int result = getSquare(k) ;
			
			System.out.println(k+" * "+k+" = "+result) ;
		}
		
	}
}