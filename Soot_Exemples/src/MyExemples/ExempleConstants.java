package MyExemples;

public class ExempleConstants {
	final int CONST_ATT_INT = 1;
	final long CONST_ATT_LONG = 22;
	final float CONST_ATT_FLOAT = 333;
	final double CONST_ATT_DOUBLE = 444.4;
	final String CONST_ATT_STRING = "string";
	final boolean CONST_ATT_BOOL = false;
	int variableAttInt;
	
	public int method(boolean lol, String lal, Double lel){
		int variableLocalInt = 0;
		final int CONST_LOCAL_INT= 1001;
		final long CONST_LOCAL_LONG = 10022;
		final float CONST_LOCAL_FLOAT = 100333;
		final double CONST_LOCAL_DOUBLE = 100444.4;
		final String CONST_LOCAL_STRING = "100string";
		final boolean CONST_LOCAL_BOOL = false; 
		
		if (lol == false)
			variableLocalInt = 111111;
		
		if(lal == "yepGuys!")
			variableLocalInt = 2222222;
		
		if(lel > 500000.561)
			variableLocalInt =+31;
		
		while(variableLocalInt <= 50000.1)
			variableLocalInt =+31;
		
		for (int i=99999; i>100000; i++){
			variableLocalInt =+32;
		}
		return variableLocalInt;
	}
}
