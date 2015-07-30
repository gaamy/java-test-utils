package MyExemples;

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JimpleLocal;
import soot.util.Chain;
import soot.util.HashChain;

public class tempBackUp {
	
	/*
	 * Generates the sequence of instructions needed to instrument ifStmtUnit 
	 * 
	 * @param ifStmtUnit: the unit to be instrumented
	 * @return A Chain of Units that represent the instrumentation of ifStmtUnit 
	 */
	private static Chain<Unit> TestingFunctionCallInt(Unit ifStmtUnit, Body b) {
	    
		AbstractBinopExpr expression = (AbstractBinopExpr) ((JIfStmt) ifStmtUnit).getCondition(); // implementation of AbstractBinopExpr
		String opoperator = expression.getClass().toString();
		Value operand1 = expression.getOp1();
		Value operand2 = expression.getOp2();
		
		//if we got non variabels
		JimpleLocal op1Local = null;
		JimpleLocal op2Local = null;
		if(operand1 instanceof JimpleLocal)
			op1Local = (JimpleLocal)operand1;
		if(operand2 instanceof JimpleLocal)
			op2Local = (JimpleLocal)operand2;
		
		//---- if there are constants -- not sure
		Object op1Const = null;
		Object op2Const = null;
		if (operand1 instanceof Constant)
			op1Const=gatherConstantContent(operand1);
		if (operand2 instanceof Constant)
			op2Const=gatherConstantContent(operand2);
		//----- not sure
		
		
		Chain<Unit> resultUnits = new HashChain<Unit>();
		
		//Gather method to call
		String actualClassPath = Scene.v().getSootClassPath();
		String pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		SootClass c = Scene.v().loadClassAndSupport("instrumenter.MyPrinter");
		
		//Creates a local string
		RefType stringType= RefType.v("java.lang.String");		//make reference to string type (RefType are usefull to use classe as Type)
		Local testingString = Jimple.v().newLocal("testString",stringType);
		b.getLocals().add(testingString);	
		
		//assign the right value to the Local we gona print testString = "someContent"
		AssignStmt addString = Jimple.v().newAssignStmt(testingString,StringConstant.v("_some_content_"));		
		resultUnits.add(addString);
		
		//Call the method
		{
			SootMethod methodToCall = c.getMethodByName("printString");
            resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(),testingString))); 				
        }
		
		/*
		//SootField fieldReference =  Scene.v().getMainClass().getFieldByName(op1Local.getName());
		//SootField fieldReference = new SootField("fieldReference", LongType.v(), Modifier.STATIC);
		//Scene.v().getMainClass().addField(fieldReference);
		
		//Creates a local string
		//RefType intType= RefType.v("int");		
		Local testingInt = Jimple.v().newLocal("testInt",IntType.v());
		b.getLocals().add(testingInt);	
		
		//assign the right value to the Local 
		//AssignStmt addInt = Jimple.v().newAssignStmt(testingString, Jimple.v().newStaticFieldRef(gotoCounter.makeRef()));		
		AssignStmt addInt = Jimple.v().newAssignStmt(testingInt, IntConstant.v(337733));		
		resultUnits.add(addInt);
		
		//Call the method
		{
			SootMethod methodToCall = c.getMethodByName("printInt");
            resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(),testingInt))); 				
        }		
	*/
		return resultUnits;
		
		
	}
}
