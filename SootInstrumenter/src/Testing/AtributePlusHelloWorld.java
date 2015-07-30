package Testing;

import java.io.IOException;
import java.util.Iterator;

import Tutoriel.LoadAndGenerate;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Body;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.util.Chain;

public class AtributePlusHelloWorld {

	
	public static void main(String[] args) {
		//Set classPath
		Scene.v().setSootClassPath(".:/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar:/Users/gamyot/Documents/workspace/Soot_Exemples/src/");
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		
		//Set up the class we’re working with
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.Exemple2");
		c.setApplicationClass();
		//test ajout d'un attribut a la classe
	    SootField gotoCounter = new SootField("gotoCount", LongType.v(), Modifier.STATIC);
	  	c.addField(gotoCounter);
		//Get methods
		Iterator<SootMethod> methodIterator = c.methodIterator();
		while (methodIterator.hasNext()){
			Body body = methodIterator.next().retrieveActiveBody();
			
			Local tmpRef;
			
		    // Add locals, java.io.printStream tmpRef
	         tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
	         body.getLocals().add(tmpRef);
	           
	         // add "tmpRef = java.lang.System.out"
	         body.getUnits().add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
	             Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
	     
	         // insert "tmpRef.println("Hello world!")"
	         {
	         SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
	         body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!"))));
	         }
	         
	         
	      // insert "return"
	         body.getUnits().add(Jimple.v().newReturnVoidStmt());   
			
			
		}
			
         try {
 			LoadAndGenerate.outputClassToBinary(c);
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
	    
	}

}
