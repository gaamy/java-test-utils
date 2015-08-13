
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
import traceOption2.TraceClass;

import java.io.*;
import java.util.*;

import Tutoriel.LoadAndGenerate;


public class MethodInvoque {

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
		while (methodIterator.hasNext()){
			SootMethod m = methodIterator.next();
			Body body = m.retrieveActiveBody();
			PatchingChain<Unit> unitList = body.getUnits();
			
			Chain<Unit> testChain = TestingFunctionCallDoubleInt(body);
			
			unitList.insertBefore(testChain,unitList.getLast());
			//testChain = TestingFunctionCallString(body);
			//unitList.insertBefore(testChain,unitList.getLast());
			
			//Output all the units for this method on terminal
			String methodName = m.getName();
			System.out.println("____Method: \""+ methodName+"\"__________________________________________________");
			LoadAndGenerate.printAllUnits(body);
		}
		try{
			LoadAndGenerate.outputClassToBinary(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Chain<Unit> TestingFunctionCallString(Body b) {

		Chain<Unit> resultUnits = new HashChain<Unit>();
		
		//Gather class method to call
		String actualClassPath = Scene.v().getSootClassPath();
		String pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		SootClass c = Scene.v().loadClassAndSupport("instrumenter.MyPrinter");
		
		
		RefType stringType= RefType.v("java.lang.String");		//make reference to string type (RefType are usefull to use classe as Type)
		Local testingString = Jimple.v().newLocal("testString",stringType);
		b.getLocals().add(testingString);	
		
		// testString = "someContent"
		AssignStmt addString = Jimple.v().newAssignStmt(testingString,StringConstant.v("someContent"));		
		resultUnits.add(addString);
		{
			SootMethod methodToCall = c.getMethodByName("printString");
            resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(),testingString))); 				
        }
		return resultUnits;
	}

	private static Chain<Unit> TestingFunctionCallInt(Body b) {
	    	
		Chain<Unit> resultUnits = new HashChain<Unit>();
		
		//Gather method to call
		String actualClassPath = Scene.v().getSootClassPath();
		String pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.MyPrinter");
		
		//Creates a local int
		Local testingInt = Jimple.v().newLocal("testInt",IntType.v());
		b.getLocals().add(testingInt);	
		
		//assign the right value to the Local 		
		AssignStmt addInt = Jimple.v().newAssignStmt(testingInt, IntConstant.v(337733));		
		resultUnits.add(addInt);
		
		//Call the method
		{
			SootMethod methodToCall = c.getMethodByName("printInt");
            resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(),testingInt))); 				
        }		
		return resultUnits;
	}
	
	private static Chain<Unit> TestingFunctionCallDoubleInt(Body b) {
    	
		Chain<Unit> resultUnits = new HashChain<Unit>();
		
		//Gather method to call
		String actualClassPath = Scene.v().getSootClassPath();
		String pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/Soot_Exemples/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.MyPrinter");
		
		//Creates a local int
		Local testingInt = Jimple.v().newLocal("testInt",DoubleType.v());
		b.getLocals().add(testingInt);	
		
		//assign the right value to the Local 		
		AssignStmt addInt = Jimple.v().newAssignStmt(testingInt, DoubleConstant.v(337733));		
		resultUnits.add(addInt);
		
		//Call the method
		{
			SootMethod methodToCall = c.getMethodByName("printDouble");
            resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(),testingInt))); 				
        }		
		return resultUnits;
	}

}