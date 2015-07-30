package instrumenter;


import soot.*;
import soot.JastAddJ.ArrayAccess;
import soot.jbco.jimpleTransformations.CollectConstants;
import soot.jimple.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.AbstractJimpleBinopExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.typing.fast.Integer1Type;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.*;
import trace.TraceClass;

import java.io.*;
import java.util.*;

import Tutoriel.LoadAndGenerate;


public class IfstmtInstrumenter {
	protected static List<SootMethod> methodList = new ArrayList<SootMethod>();
	
	public static void main(String args[]){
		//Set classPath
		String exemplePath = "/Users/gamyot/Documents/workspace/Soot_Exemples/src/";
		String objectPath= "/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar";
		String tracePath = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		
		Scene.v().setSootClassPath(".:"+objectPath+":"+exemplePath+":"+ tracePath);
		
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		
		//Set up the class we’re working with
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.ExempleBasic");
		c.setApplicationClass();

		//Get methods
		Iterator<SootMethod> methodIterator = c.methodIterator();
		while (methodIterator.hasNext())
			methodList.add(methodIterator.next());
		//Iterate through the method list
		for(SootMethod m : methodList){
			Body body = m.retrieveActiveBody();
			PatchingChain<Unit> unitList = body.getUnits();
			//get the all the "if statements" Units 
			List<Unit> ifStmtList = searchIfStmts(body);

			//for each "if statement" unit, instrument and add the instrumentation code right after
			for(Unit ifStmtUnit: ifStmtList){
				//Chain<Unit> instrumentedChain = generateInstrumentationUnits(ifStmtUnit, body);
				//unitList.insertAfter(instrumentedChain, ifStmtUnit);
				Chain<Unit> testChain = generateInstrumentationUnits(ifStmtUnit, body);
				unitList.insertAfter(testChain, ifStmtUnit);
				
			}
			//Output all the units for this method on terminal
			String methodName = m.getName();
			System.out.println("____Method: \""+ methodName+"\"__________________________________________________");
			LoadAndGenerate.printAllUnits(body);
		}

		try {
			LoadAndGenerate.outputClassToBinary(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Generates the sequence of instructions needed to instrument ifStmtUnit 
	 * 
	 * @param ifStmtUnit: the unit to be instrumented
	 * @return A Chain of Units that represent the instrumentation of ifStmtUnit 
	 */
	private static Chain<Unit> generateInstrumentationUnits(Unit ifStmtUnit, Body b) {
	    
		AbstractBinopExpr expression = (AbstractBinopExpr) ((JIfStmt) ifStmtUnit).getCondition(); // implementation of AbstractBinopExpr
		Value operand1 = expression.getOp1();
		Value operand2 = expression.getOp2();
		
		
		JimpleLocal op1Local = (JimpleLocal)operand1;
		
	
		//Local localOperand = Jimple.v().newLocal("op1", operand1.getType());
		//b.getLocals().add(localOperand);
		
		
		//We need to use these operand as Locals or constants
		/**
		JimpleLocal test;
		if(operand1 instanceof soot.jimple.internal.JimpleLocal)		
			 test = (JimpleLocal)operand1;
		else
			test = null;
		*/
		
		String op = expression.getClass().toString();
		
		Chain<Unit> resultUnits = new HashChain<Unit>();
		 Local tmpRef;
		// Add locals directely on the top of the body, java.io.printStream tmpRef
		tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        b.getLocals().addFirst(tmpRef);
        
        // add "tmpRef = java.lang.System.out"
        resultUnits.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
            Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
        {
        SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
        resultUnits.add(
        		Jimple.v().newInvokeStmt(
        				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(),StringConstant.v("Operande 1: "))));
        resultUnits.add(
        		Jimple.v().newInvokeStmt(
        				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(),StringConstant.v(operand1.getClass().toString()))));
        				
        resultUnits.add(
        		Jimple.v().newInvokeStmt(
        				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(),StringConstant.v("Operande 2:"))));
       
        resultUnits.add(
        		Jimple.v().newInvokeStmt(
        				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(),StringConstant.v(operand2.getClass().toString()))));  
        
        resultUnits.add(
        		Jimple.v().newInvokeStmt(
        				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(),StringConstant.v("Operateur: "))));
        
        resultUnits.add(
        		Jimple.v().newInvokeStmt(
        				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(),StringConstant.v(op))));
       
        
        }    
		
        
		return resultUnits;
		
		
	}

	
	private static List<Unit> searchIfStmts(Body b) {
		List<Unit> searchResult = new ArrayList<Unit>();
		PatchingChain<Unit> statements = b.getUnits();
		Iterator<Unit> unitIt = statements.iterator();
		//iterate through the Units of the body
		while(unitIt.hasNext()){
			Unit tempUnit = unitIt.next();
			// if the unit is a "if statement"
			if (tempUnit instanceof soot.jimple.internal.JIfStmt)
				searchResult.add(tempUnit);
		}
		return searchResult;
	}
	

	/**Get the java.lang.object corresponding to the constant
	 * 
	 * 
	 * ----not sure----
	 * 
	 * @param val
	 * @return Object
	 */
	private static Object gatherConstantContent(Value val){
		//if the Value is not a numeric constant, should be a string constant 
		if(! (val instanceof NumericConstant)){
			if(val instanceof StringConstant){
				String tempVal = new String( ((StringConstant)val).value );
				return tempVal;
			}
		}
		else{
			NumericConstant numericVal = (NumericConstant)val;
			
			if(numericVal instanceof LongConstant){
				Long tempVal = new Long( ((LongConstant)numericVal).value );
				return tempVal;					
			}
			else if(numericVal instanceof DoubleConstant){
				Double tempVal = new Double( ((DoubleConstant)numericVal).value );
				return tempVal;
			}
			else if(numericVal instanceof FloatConstant){
				Float tempVal = new Float( ((FloatConstant)numericVal).value );
				return tempVal;
			}
			else if(numericVal instanceof IntConstant){
				Integer tempVal = new Integer( ((IntConstant)numericVal).value );
				return tempVal;	
			}
		}
		return null;
	}
}


/*		//works but not usefull
		{	
			SootMethod methodToCall = c.getMethodByName("printString");
			resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(),StringConstant.v(operand1.getClass().toString()))));
		}
 * */

	
//not tested
//-- variable test  
/*
Local  tmpRef;
//SootField testLocal = Scene.v().getMainClass().getFieldByName("gotoCount");

tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
b.getLocals().add(tmpRef);


// add "tmpRef = java.lang.System.out"
resultUnits.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
    Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
*/



// insert tmpRef.method
/*{
SootMethod methodToCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
resultUnits.add(	Jimple.v().newInvokeStmt(
				Jimple.v().newVirtualInvokeExpr(tmpRef, methodToCall.makeRef(), testLocal)));
}  */

//--end variable test