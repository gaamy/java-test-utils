package MyExemples;


public class SampleSoot {
	/*
	public static void main(String[] args){
		System.out.println("Running SampleSoot");
	}
	*/
	/*private int attribut;
	SampleSoot(){
		attribut=100;
	}*/
	
	public static void main(String[] args){
		System.out.println("Running SampleSoot");
		//try{
		graphTest(	Integer.valueOf(args[0]),
					Integer.valueOf(args[1]),
					Integer.valueOf(args[2]),
					Integer.valueOf(args[3]));
		//}
		//catch(java.lang.ArrayIndexOutOfBoundsException e){
		//	System.out.println("Erreur! Donnez 4 arguments(Exemple: 1 2 3 4)");
		//}
		
	}
	
	public static void graphTest(int a, int b, int c, int d){
		int resp;
		
		if(a<b){
			resp=11;
			if(b==c){
				resp=21;
				if(d>b){
					resp=31;
				}
				resp=32;
			}
			resp=22;	
		}
		resp=12;
		System.out.println(resp);
	}
	

}
