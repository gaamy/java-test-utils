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
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.*;
import traceOption2.TraceClass;

import java.io.*;
import java.util.*;

import Tutoriel.LoadAndGenerate;


public class IfstmtInstrumenter {
	protected static List<SootMethod> methodList = new ArrayList<SootMethod>();
	
	//Soot fields
	static SootClass TRACE_CLASS;
	
	//Soot methods
	static SootMethod PRINT_DOUBLE;
	static SootMethod TRACE_CLASS_CONTRUCTOR;
	static SootMethod TRACE_CLASS_ADD_TRACEMETHOD;
	static SootMethod TRACE_METHOD_VISIT_TARGET;
	static SootMethod TRACE_METHOD_VISIT_DECISION_NODE;
	
	/**
	 * initialize the tace methods to be used by soot as sootMethods
	 */
	private static void initialiseInvokeMethods(){
		
		/**
		 * Exemple (delete me)
		 */
		//Soot_Exemples.MyPrinter.PrintDouble
		String actualClassPath = Scene.v().getSootClassPath();
		String pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/Soot_Exemple/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		SootClass c = Scene.v().loadClassAndSupport("MyExemple.MyPrinter");
		PRINT_DOUBLE = c.getMethodByName("printDouble");
		
		/**
		 * traceClass
		 */
		//trace.TraceClass.TraceClass
		actualClassPath = Scene.v().getSootClassPath();
		pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		TRACE_CLASS = Scene.v().loadClassAndSupport("trace.TraceClass");
		TRACE_CLASS_CONTRUCTOR = c.getMethodByName("TraceClass");
		
		//trace.TraceClass.TraceClass
		actualClassPath = Scene.v().getSootClassPath();
		pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		c = Scene.v().loadClassAndSupport("trace.TraceClass");
		TRACE_CLASS_ADD_TRACEMETHOD = c.getMethodByName("addMethod");
		
		/**
		 * traceMethod
		 */
		
		//trace.TraceClass.TraceClass
		actualClassPath = Scene.v().getSootClassPath();
		pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		c = Scene.v().loadClassAndSupport("trace.TraceMethod");
		TRACE_METHOD_VISIT_TARGET = c.getMethodByName("visitTarget");

		//trace.TraceClass.TraceClass
		actualClassPath = Scene.v().getSootClassPath();
		pathToYourStaticMethodsClass = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		Scene.v().setSootClassPath(actualClassPath+":"+ pathToYourStaticMethodsClass);
		c = Scene.v().loadClassAndSupport("trace.TraceMethod");
		TRACE_METHOD_VISIT_DECISION_NODE = c.getMethodByName("visitDecisionNode");
		

	}
	
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
		
		/**
		 * Set up the trace data
		 */
		
		
		
		//creates the chain of units that represent the static block that declare the trace
		//which build the traceObjectand the traceMethod 
		//(it would be inserted at the begin of the class, perhaps i will need to copy it in each constructor)
		//Chain<Unit> staticBlock = new HashChain<Unit>();
		
		//Initialize trace methods as sootMethods
		initialiseInvokeMethods();
				
		//initialize TraceClass
		initialiseTraceClass(c.getName());
		
		//Get methods
		Iterator<SootMethod> methodIterator = c.methodIterator();
		while (methodIterator.hasNext())
			methodList.add(methodIterator.next());
		//Iterate through the method list
		for(SootMethod m : methodList){
			
			//TODO: Build a new TraceMethod
			initialiseTraceMethod(m);
			
			
			Body body = m.retrieveActiveBody();
			PatchingChain<Unit> unitList = body.getUnits();
			//get the all the "if statements" Units 
			List<Unit> ifStmtList = searchIfStmts(body);

			//for each "if statement" unit, instrument and add the instrumentation code right after
			for(Unit ifStmtUnit: ifStmtList){
				
				
				//TODO:insertafter the then statement: DN reached + then target reached	
			
				//TODO:insertafter the else statement: DN reached + else target reached
					

				
				//Chain<Unit> instrumentedChain = generateInstrumentationUnits(ifStmtUnit, body);
				//unitList.insertAfter(instrumentedChain, ifStmtUnit);			
				
				
			}
			//Debug
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
	
	
	private static void initialiseTraceMethod(SootMethod currentMethod) {
		String methodSignature = currentMethod.getSignature();
		Body currentBody = currentMethod.getActiveBody();
		
		//CfgMeticsExtractor = new(currentBody);
		
		//invoke TRACE_CLASS_ADD_TRACEMETHOD 
		//whit params(String methodSignature,int targetAmount, int decisionNodeAmount, List<List<Integer>> newDependenciesList)
		
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private static void initialiseTraceClass(String className) {
		Chain<Unit> taceDeclarationUnitChain = new HashChain<Unit>();
		
		//get the class
		SootClass c= Scene.v().getMainClass();
		//Declare the SootField from TraceClass type
		SootField trace = new SootField("trace", RefType.v(TRACE_CLASS));
		//add the field to actual main class
		c.addField(trace);
		
		SootMethod staticConstructor = getStaticConstructor(c);
		Body staticConstructorBody = staticConstructor.getActiveBody();
		
		
		/*jimple code
		 	temp$0 = "aName";
	        <TestExemples.StaticBlockVerification: java.lang.String className> = temp$0;
	        temp$1 = new trace.TraceClass;
	        temp$2 = <TestExemples.StaticBlockVerification: java.lang.String className>;
	        specialinvoke temp$1.<trace.TraceClass: void <init>(java.lang.String)>(temp$2);
	        <TestExemples.StaticBlockVerification: trace.TraceClass trace> = temp$1;
	        return;
		*/
		
		//invoke the TraceClass constructor with the className as parameter
		taceDeclarationUnitChain.add(
	    		Jimple.v().newInvokeStmt(
	    				Jimple.v().newStaticInvokeExpr(TRACE_CLASS_CONTRUCTOR.makeRef(),StringConstant.v(className))));

		
		staticConstructorBody.getUnits().addAll(taceDeclarationUnitChain);
	}
	
	/**
	 * check if the current class have a static block/constructor
	 * we want to initialize the traceClass and all the traceMethods in this block
	 * @param c
	 * @return
	 */
	private static SootMethod getStaticConstructor(SootClass c) {
		
		boolean  staticConstructorPresent = false;
		SootMethod staticConstructor = null;
		try{
		staticConstructor = c.getMethodByName("<clinit>");
		return staticConstructor;
		}
		catch(java.lang.RuntimeException e) {
			//creates a method 
			staticConstructor = new SootMethod("<clinit>",
	                Arrays.asList(new Type[] {}),
	                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
			// create empty body
			JimpleBody body = Jimple.v().newBody(staticConstructor);
			staticConstructor.setActiveBody(body);
			return staticConstructor;
		}

	}
	

	
	

	/**
	 * Generates the sequence of instructions needed to instrument ifStmtUnit 
	 * 
	 * @param ifStmtUnit: the unit to be instrumented
	 * @return A Chain of Units that represent the instrumentation of ifStmtUnit 
	 */
	private static Chain<Unit> buildMethoStructure(Unit ifStmtUnit, Body b) {
	    
		Chain<Unit> resultUnits = new HashChain<Unit>();
		
	    AbstractBinopExpr expression = (AbstractBinopExpr) ((JIfStmt) ifStmtUnit).getCondition(); // implementation of AbstractBinopExpr
		//get the operator
		String op = expression.getClass().toString();
		//get the operands
		Value op1 = expression.getOp1();
		Value op2 = expression.getOp2();
		Type op1Type = op1.getType();
		Type op2Type = op2.getType();
		

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
            				Jimple.v().newStaticInvokeExpr(PRINT_DOUBLE.makeRef(),doubleParam1))); 
		}
		
		if(op2Type instanceof IntegerType || op2Type instanceof LongType || op2Type instanceof FloatType || op2Type instanceof DoubleType) {

			resultUnits.add(Jimple.v().newAssignStmt(doubleParam2,
					(op2.getType() instanceof DoubleType) ? op2 : Jimple.v().newCastExpr(op2, DoubleType.v())));
			resultUnits.add(
            		Jimple.v().newInvokeStmt(
            				Jimple.v().newStaticInvokeExpr(PRINT_DOUBLE.makeRef(),doubleParam2)));
		}

		return resultUnits;				
	}

	
	
	
	

	/**
	 * returns a list with the if statements contained in the body b
	 * @param b
	 * @return List<Unit>
	 */
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
	 * ----not sure if this is usefull
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

	
