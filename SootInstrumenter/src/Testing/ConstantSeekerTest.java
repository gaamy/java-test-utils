package Testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import constantSeeker.ConstantSeeker;
import static org.junit.Assert.*;

import org.junit.Test;

import soot.jimple.Constant;

public class ConstantSeekerTest {

	public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ConstantSeekerTest.class);
    }
	
	//gatter all strings
	@Test
	public void test1() {			
		Map<String,String> testClasses= new  HashMap<String, String>();
		
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		
		//build expected set
		Set<String> expectedSet = new HashSet<String>();
		expectedSet.add("string");
		expectedSet.add("100string");
		expectedSet.add("yepGuys!");
		
		//Retrieve and compare
		Set<String> gatheredConstants = seeker.getStringConstants();
		for(String expectedString : expectedSet){
			String message = expectedString.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedString));
		}
	}
	
	//gatter a int 
	@Test
	public void test2() {			
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		Set<Integer> expectedSet = new HashSet<Integer>();
		expectedSet.add(1);
		expectedSet.add(0);
		expectedSet.add(1001);
		expectedSet.add(111111);
		expectedSet.add(2222222);
		expectedSet.add(99999);
		expectedSet.add(100000);
		expectedSet.add(31);
		expectedSet.add(32);
		Set<Integer> gatheredConstants = seeker.getIntegerConstants();
		for(Integer expectedInteger : expectedSet){
			String message = expectedInteger.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedInteger));
		}
	}

	//gatter a long 
	@Test
	public void test3() {			
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		Set<Long> expectedSet = new HashSet<Long>();
		expectedSet.add(new Long(22));
		expectedSet.add(new Long(10022));
		Set<Long> gatheredConstants = seeker.getLongConstants();
		for(Long expectedLong : expectedSet){
			String message = expectedLong.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedLong));
		}
	}
	
	//gatter a double 
	@Test
	public void test4() {			
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		Set<Double> expectedSet = new HashSet<Double>();
		expectedSet.add(new Double(444.4));
		expectedSet.add(new Double(100444.4));
		expectedSet.add(new Double(500000.561));
		expectedSet.add(new Double(50000.1));
		Set<Double> gatheredConstants = seeker.getDoubleConstants();
		for(Double expectedDouble : expectedSet){
			String message = "expectedDouble: "+ expectedDouble.toString()+" not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedDouble));
		}
	}
	
	//gatter a float 
	@Test
	public void test5() {			
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		Set<Float> expectedSet = new HashSet<Float>();
		expectedSet.add(new Float(100333));
		expectedSet.add(new Float(333));
		Set<Float> gatheredConstants = seeker.getFloatConstants();
		for(Float expectedFloat : expectedSet){
			String message = expectedFloat.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedFloat));
		}
	}
	
	/*
	we want
	final int CONST_ATT_INT = 1;
	final long CONST_ATT_LONG = 22;
	final float CONST_ATT_FLOAT = 333;
	final double CONST_ATT_DOUBLE = 444.4;
	final String CONST_ATT_STRING = "string";
	final boolean CONST_ATT_BOOL = true;
	
	*/
	//gatter all constants from Atributes
	@Test
	public void test6() {
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		
		//Set Expected set
		Set<Object> expectedSet = new HashSet<Object>();
		expectedSet.add(new Integer(1));
		expectedSet.add(new Long(22));
		expectedSet.add(new Float(333));
		expectedSet.add(new Double(444.4));
		expectedSet.add(new String("string"));
		expectedSet.add(new Integer(0));//false
		
		//Gather all constants found from ConstantSeeker
		Set<Object> gatheredConstants = new HashSet<Object>();
		gatheredConstants.addAll(seeker.getStringConstants());
		gatheredConstants.addAll(seeker.getIntegerConstants());
		gatheredConstants.addAll(seeker.getLongConstants());
		gatheredConstants.addAll(seeker.getDoubleConstants());
		gatheredConstants.addAll(seeker.getFloatConstants());
		
		//Check 
		for(Object expectedObject : expectedSet){
			String message = expectedObject.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedObject));
		}
	
				
	}
	
	/*
	 * we want
		int variableLocalInt = 0;
		final int CONST_LOCAL_INT= 1001;
		final long CONST_LOCAL_LONG = 10022;
		final float CONST_LOCAL_FLOAT = 100333;
		final double CONST_LOCAL_DOUBLE = 100444.4;
		final String CONST_LOCAL_STRING = "100string";
		final boolean CONST_LOCAL_BOOL = false; 
		
		variableLocalInt =111111;
		
		variableLocalInt = 2222222;
		
	 * */
	//gatter all constants from assigment statements
	@Test
	public void test7() {
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		
		//Set Expected set
		Set<Object> expectedSet = new HashSet<Object>();
		expectedSet.add(new Integer(1001));
		expectedSet.add(new Long(10022));
		expectedSet.add(new Float(100333));
		expectedSet.add(new Double(100444.4));
		expectedSet.add(new String("100string"));
		expectedSet.add(new Integer(0));//false
		expectedSet.add(new Integer(111111));
		expectedSet.add(new Integer(2222222));
		expectedSet.add(new Integer(99999));
		expectedSet.add(new Integer(32));
		expectedSet.add(new Integer(31));
		expectedSet.add(new Integer(1));
			
		//Gather all constants found from ConstantSeeker
		Set<Object> gatheredConstants = new HashSet<Object>();
		gatheredConstants.addAll(seeker.getStringConstants());
		gatheredConstants.addAll(seeker.getIntegerConstants());
		gatheredConstants.addAll(seeker.getLongConstants());
		gatheredConstants.addAll(seeker.getDoubleConstants());
		gatheredConstants.addAll(seeker.getFloatConstants());
		
		//Check 
		for(Object expectedObject : expectedSet){
			String message = expectedObject.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedObject));
		}
	}
		
	//gatter all constants from condition statements
	@Test
	public void test8(){			
		Map<String,String> testClasses= new  HashMap<String, String>();
		String currentDir = System.getProperty("user.dir")+"/src/";
		testClasses.put("TestExemples.ExempleConstants", currentDir);
		ConstantSeeker seeker = new ConstantSeeker(testClasses);
		
		//Set Expected set
		Set<Object> expectedSet = new HashSet<Object>();
		expectedSet.add(new Double(500000.561));
		expectedSet.add(new Double(50000.1));
		expectedSet.add(new String("yepGuys!"));
		expectedSet.add(new Integer(0));//false
		expectedSet.add(new Integer(111111));
		expectedSet.add(new Integer(100000));
		expectedSet.add(new Integer(2222222));
		
		//Gather all constants found from ConstantSeeker
		Set<Object> gatheredConstants = new HashSet<Object>();
		gatheredConstants.addAll(seeker.getStringConstants());
		gatheredConstants.addAll(seeker.getIntegerConstants());
		gatheredConstants.addAll(seeker.getLongConstants());
		gatheredConstants.addAll(seeker.getDoubleConstants());
		gatheredConstants.addAll(seeker.getFloatConstants());
		
		//Check 
		for(Object expectedObject : expectedSet){
			String message = expectedObject.toString()+" expected but not found on the gatheredConstants set. ";
			assertTrue(message,gatheredConstants.contains(expectedObject));
		}
	}

}
