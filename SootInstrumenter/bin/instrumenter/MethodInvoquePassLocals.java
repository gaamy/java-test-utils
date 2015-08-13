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


	public class MethodInvoquePassLocals {
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
			SootClass c = Scene.v().loadClassAndSupport("MyExemples.ExempleDouble");
			c.setApplicationClass();

			//Get methods
			Iterator<SootMethod> methodIterator = c.methodIterator();
			while (methodIterator.hasNext()){
				SootMethod m = methodIterator.next();
				//Iterate through the method list
				Body body = m.retrieveActiveBody();
				PatchingChain<Unit> unitList = body.getUnits();
				//get the all the "if statements" Units 
				List<Unit> ifStmtList = searchIfStmts(body);

				//for each "if statement" unit, instrument and add the instrumentation code right after
				for(Unit ifStmtUnit: ifStmtList){
					//Chain<Unit> instrumentedChain = generateInstrumentationUnits(ifStmtUnit, body);
					//unitList.insertAfter(instrumentedChain, ifStmtUnit);
					Chain<Unit> testChain = TestingFunctionCallWithLocals(ifStmtUnit, body);
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
		
		private static Chain<Unit> TestingFunctionCallWithLocals(Unit ifStmtUnit, Body b) {
		    
			//Gather method to call do this outside!
			String actualClassPath = Scene.v().getSootClassPath();
			String pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/Soot_Exemples/src/";
			Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
			SootClass c = Scene.v().loadClassAndSupport("MyExemples.MyPrinter");
			SootMethod printDouble = c.getMethodByName("printDouble");
						
			Chain<Unit> resultUnits = new HashChain<Unit>();
			
		    AbstractBinopExpr expression = (AbstractBinopExpr) ((JIfStmt) ifStmtUnit).getCondition(); // implementation of AbstractBinopExpr
			//get the operator
			String op = expression.getClass().toString();
			//get the operands
			Value op1 = expression.getOp1();
			Value op2 = expression.getOp2();
			Type op1Type = op1.getType();
			Type op2Type = op2.getType();
			
			
			//--
			/**
			 * Inspired in testfull.coverage.whiteBox.WhiteInstrumenter
			 */
			//add temporary double locals 
			Local doubleParam1 = Jimple.v().newLocal("_double__param_1_", DoubleType.v());
			b.getLocals().add(doubleParam1);

			Local doubleParam2 = Jimple.v().newLocal("_double__param_2_", DoubleType.v());
			b.getLocals().add(doubleParam2);
			
			//si on a un type primitif
			if(op1Type instanceof IntegerType || op1Type instanceof LongType || op1Type instanceof FloatType || op1Type instanceof DoubleType) {

				resultUnits.add(Jimple.v().newAssignStmt(doubleParam1,
						(op1.getType() instanceof DoubleType) ? op1 : Jimple.v().newCastExpr(op1, DoubleType.v())));
				resultUnits.add(
	            		Jimple.v().newInvokeStmt(
	            				Jimple.v().newStaticInvokeExpr(printDouble.makeRef(),doubleParam1))); 
			}
			
			if(op2Type instanceof IntegerType || op2Type instanceof LongType || op2Type instanceof FloatType || op2Type instanceof DoubleType) {

				resultUnits.add(Jimple.v().newAssignStmt(doubleParam2,
						(op2.getType() instanceof DoubleType) ? op2 : Jimple.v().newCastExpr(op2, DoubleType.v())));
				resultUnits.add(
	            		Jimple.v().newInvokeStmt(
	            				Jimple.v().newStaticInvokeExpr(printDouble.makeRef(),doubleParam2)));
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
	}

	
	
	
//could be usefull to parse different types of operands
	/*// si on a un boolean
	} else if(op1Type instanceof BooleanType) { 

		if(config.getDataFlowCoverage().isPUse()) {
			boolean tracked = false;
			if(use1 != null) {
				Local localDef = getTrackingDef(resultUnits, op1, useStmt);
				if(localDef != null) {
					tracked = true;
					resultUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(localTracker, setConditionTargetDistance.makeRef(), DoubleConstant.v(1), localDef)));
				}
			}

			if(use2 != null) {
				Local localDef = getTrackingDef(resultUnits, op2, useStmt);
				if(localDef != null) {
					tracked = true;
					resultUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(localTracker, setConditionTargetDistance.makeRef(), DoubleConstant.v(1), localDef)));
				}
			}

			if(!tracked) {
				resultUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(localTracker, setConditionTargetDistance.makeRef(), DoubleConstant.v(1), NullConstant.v())));
			}

		} else {
			resultUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(localTracker, setConditionTargetDistance.makeRef(), DoubleConstant.v(1), NullConstant.v())));
		}
		

	} else if(op1Type instanceof RefLikeType) { //si on a un objet 
		
		// localTmpDouble1 = (op1 == null) ? 0 : 1;
		Unit isNull = Jimple.v().newNopStmt();
		resultUnits.add(Jimple.v().newAssignStmt(localTmpDouble1, DoubleConstant.v(0)));
		resultUnits.add(Jimple.v().newIfStmt(Jimple.v().newEqExpr(op1, NullConstant.v()), isNull));
		resultUnits.add(Jimple.v().newAssignStmt(localTmpDouble1, DoubleConstant.v(1)));
		resultUnits.add(isNull);

		// localTmpDouble2 = (op2 == null) ? 0 : 1;
		isNull = Jimple.v().newNopStmt();
		resultUnits.add(Jimple.v().newAssignStmt(localTmpDouble2, DoubleConstant.v(0)));
		resultUnits.add(Jimple.v().newIfStmt(Jimple.v().newEqExpr(op2, NullConstant.v()), isNull));
		resultUnits.add(Jimple.v().newAssignStmt(localTmpDouble2, DoubleConstant.v(1)));
		resultUnits.add(isNull);
		 
	}*/