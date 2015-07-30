package MyExemples;

public class Exemple1 {
	private static int attribut;
	
	Exemple1(){
		attribut=100;	
	}
	
	public static void main(String[] args){
		test(1,2,3);
	}
	public static void test(int a, int b, int c){
		
		
		if(a+b > c ){
			a=1;
		}
		else{
			c=2;
		}
		
		if(a == c){
			b=3;
		}
		boolean q = true;
		if(q){
			a=4;
		}
		
		boolean f = false;
		
		if(q==f){
			a=5;
		}
		
		
		if(!f){
			a=6;
		}
		if(attribut ==100){
			attribut --;
		}
	}
}
