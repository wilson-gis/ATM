import java.util.Scanner ;
public class Main {
	public static void main(String[] args) {
		Scanner x = new Scanner(System.in) ;
		int a  ; 
		int b  ;
		
		System.out.print("Enter the value of a : ") ;
		a = x.nextInt() ;
		System.out.print("Entef the value of b : ") ;
		b = x.nextInt() ;
		
		int temp = a ;
		a = b ;
		b = temp ; 
		System.out.println("After Swapping the Numbers :") ;
		
		System.out.println("The new value of a = " + a) ;
		System.out.println("The new value of b = " + b) ;
	}
}