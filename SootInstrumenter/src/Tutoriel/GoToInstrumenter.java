package Tutoriel;

import soot.*;
import soot.baf.GotoInst;
import soot.jbco.jimpleTransformations.GotoInstrumenter;
import soot.jimple.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.AbstractJimpleBinopExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.toolkits.typing.fast.Integer1Type;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.*;

import java.io.*;
import java.util.*;

import Tutoriel.LoadAndGenerate;

public class GoToInstrumenter extends BodyTransformer{
	private SootField gotoCounter;
	private SootClass javaIoPrintStream;
	
	private static GotoInstrumenter instance = new GotoInstrumenter();
	private GoToInstrumenter(){	}
	
	public static GotoInstrumenter getInstance(){return instance;}
		
	public void main(String[] args){
		BodyTransformer goTo = new GotoInstrumenter();
		Pack jtp = PackManager.v().getPack("jtp");
		jtp.add(new Transform("jtp.instrumenter",goTo));
			//	"Tutoriel").add(new Transform("Tutoriel",GoToInstrumenter()));
		soot.Main.main(args);
	}
	
	public void internalTransform(Body body, String phaseName, Map options) {
		
		//Sanity check - find main() method
		if (!Scene.v().getMainClass().declaresMethod("void main(java.lang.String[])"))
		    throw new RuntimeException("couldn't find main() in mainClass");
		
		
		boolean gotoCounterAlredyOnClass = false;
		
		//Fetching or adding the field
			//Fetching field
		if (gotoCounterAlredyOnClass )
		    gotoCounter = Scene.v().getMainClass().getFieldByName("gotoCount");
			//Adding the field
		else{  // Add gotoCounter field
		    gotoCounter = new SootField("gotoCount", LongType.v(), Modifier.STATIC);
		    Scene.v().getMainClass().addField(gotoCounter);
		    
		 // Just in case, resolve the PrintStream SootClass.
		    Scene.v().loadClassAndSupport("java.io.PrintStream");
		    javaIoPrintStream = Scene.v().getSootClass("java.io.PrintStream");
		    gotoCounterAlredyOnClass = true;
		}
		    
		    
		//Add locals and statements
			//We first use the method's signature to check if it is a main method or not:
		boolean isMainMethod = body.getMethod().getSubSignature().equals("void main(java.lang.String[])");
		
		//adding a local
		Local tempLocal = Jimple.v().newLocal("tmp", LongType.v());
		body.getLocals().add(tempLocal);
		
		
		/*We can determine the statement type by checking its class with instanceof.
		Here, we are looking at four different statement types: GotoStmt, InvokeStmt,
		ReturnStmt and ReturnVoidStmt.*/
		//iterating through the Unit(Stmt) chain 
		Iterator stmtIt = body.getUnits().snapshotIterator();
		while(stmtIt.hasNext()){
			Stmt s = (Stmt) stmtIt.next();
			if(s instanceof GotoStmt){
				
				//insert profiling instructions here
				/*	Equivalent Jimple instructions:
					tmpLong = <classname: long gotoCount>;
					tmpLong = tmpLong + 1L;
			    		<classname: long gotoCount> = tmpLong;
				 */
				
				//je crois qu'il manque de quoi ici... pas sure
				AssignStmt toAdd1 = Jimple.v().newAssignStmt(tempLocal,Jimple.v().newStaticFieldRef(gotoCounter.makeRef()));
				body.getUnits().insertBefore(toAdd1, s);
				
			}
			else if (s instanceof InvokeStmt){
				//Check if it is a System.exit() statement.
				//If it is, inster print-out statement before s
				InvokeExpr iexpr = (InvokeExpr) ((InvokeStmt)s).getInvokeExpr();
				if (iexpr instanceof StaticInvokeExpr)
				{
				    SootMethod target = ((StaticInvokeExpr)iexpr).getMethod();
				    if (target.getSignature().equals  ("<java.lang.System: void exit(int)>"))
				    {
				    		/* Jimple equivalent of what we want
				    		  	tmpRef = <java.lang.System: java.io.PrintStream out>;
							tmpLong = <test: long gotoCount>;
							virtualinvoke tmpRef.<java.io.PrintStream: void println(long)>(tmpLong);
				    		 * */
				    	
				        //printing statement
				        Local  tmpRef;
						
				        tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
				        body.getLocals().add(tmpRef);
				        
				   
				        // add "tmpRef = java.lang.System.out"
				        body.getUnits().add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
				            Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
				    
				        // insert "tmpRef.println("Hello world!")"
				        {
				        SootMethod methodToCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
				        body.getUnits().insertBefore(
				        		Jimple.v().newInvokeStmt(
				        				Jimple.v().newVirtualInvokeExpr(tmpRef, methodToCall.makeRef(), tempLocal)),s);
				        }  
				    }
				}
			}
			else if(isMainMethod && (s instanceof ReturnStmt || s instanceof ReturnVoidStmt)){
				//In the main method, before the return statement, insert print-out statements
			}
				
		}

	}

}
