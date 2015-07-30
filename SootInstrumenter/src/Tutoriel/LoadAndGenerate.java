package Tutoriel;

import soot.*;
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



public class LoadAndGenerate {
	protected static List<SootMethod> methodList = new ArrayList<SootMethod>();
	
	public static void main(String args[]){
		//set classPath
		Scene.v().setSootClassPath(".:/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar:/Users/gamyot/Documents/workspace/Soot_Exemples/src/");
		//Scene.v().setSootClassPath(":/Users/gamyot/Documents/workspace/Soot_tutorial/src/");

		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		
		// Set up the class we’re working with
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.SampleSoot");
		c.setApplicationClass();
		
		//test ajout d'un attribut a la classe--------------
		SootField stringTest = new SootField("testField", RefType.v("java.lang.String"), Modifier.STATIC);
		Scene.v().getMainClass().addField(stringTest);
		//-------------------

		//Add some locals and a unit to a method
		//modify the body adding some locals and a unit
		SootMethod helloMethod= c.getMethodByName("graphTest");
		
		//Ajoute le helloWorld a la méthode
		addToBody(helloMethod.retrieveActiveBody());
				
		//Get methods "Obtenir les methodes de la classe dyamiquement (documentation de SootClass)"
		Iterator<SootMethod> methodIterator = c.methodIterator();
		while (methodIterator.hasNext()) {
			methodList.add(methodIterator.next());
		}
		//"	2.Obtenir info de chaque SootMethod dynamiquement."
		for(SootMethod m : methodList){
			String methodName = m.getName();
			Body b = m.retrieveActiveBody(); //get the body of the method(the body contains the method data)

			System.out.println("______________________________________________________");
			System.out.println("Method: \""+ methodName+"\"");
			
			//get the Locals and iterate through it to print each one
			printAllLocals(b);
			
			//get the Units and print each one
			printAllUnits(b);
			
			//get the Traps(exeptions) and iterate through it to print each one
			printAllTraps(b);	
		}
		
		//OutPut the Class to a binary file
		try {
			outputClassToBinary(c);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private static void addToBody(Body body) {
		 Chain units = body.getUnits();
         Local tmpRef;
       
         // Add locals, java.io.printStream tmpRef
             tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
             body.getLocals().addFirst(tmpRef);
               
         // add "tmpRef = java.lang.System.out"
             units.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
                 Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
         
         // insert "tmpRef.println("Hello world!")"
         {
             SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
             units.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!"))));
         }   
	}

	//OutPut the Class to a binary (.class) file
	public static void outputClassToBinary(SootClass sClass) throws IOException {
		/*//TODO:make this work (create the file )
		Class c = Class.forName(sClass.getClass().toString());
		String path = System.getProperty("user.dir")+"/sootOutput/"+c.getPackage()+"/"+c.getSimpleName()+".class";
		System.out.println("************"+path);
		File outputFile = new File(path);
		if (!outputFile.exists())
			outputFile.createNewFile();
		*/
		String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
        OutputStream streamOut = new JasminOutputStream( new FileOutputStream(fileName));
        PrintWriter writerOut = new PrintWriter( new OutputStreamWriter(streamOut));
        JasminClass jasminClass = new soot.jimple.JasminClass(sClass);
        jasminClass.print(writerOut);
        writerOut.flush();
        streamOut.close();
		
	}

	
	
	public static void printAllLocals(Body b) {
		Chain<Local> localVariables = b.getLocals();
		System.out.println("\n	Local Variables: \"Name(type)\"");
		Iterator<Local> localIt = localVariables.iterator();
		while(localIt.hasNext()){
			Local temp =localIt.next();
			System.out.println("		"+temp.getName()+"("+temp.getType()+")	");	
		}
		System.out.println("		__________	");
	}

	public static void printAllUnits(Body b) {
		PatchingChain<Unit> statements = b.getUnits();
		System.out.println("	Units: ");
		Iterator<Unit> unitIt = statements.snapshotIterator();
		while(unitIt.hasNext()){
			Unit tempUnit = unitIt.next();
			String toString = tempUnit.toString();
			tempUnit.getClass().toString();
			System.out.println("	"+toString);
			//System.out.println("Classe : "+ tempUnit.getClass().toString());
			
			//This block get the 2 operands and the operator 
			if (tempUnit.branches() == true && tempUnit instanceof soot.jimple.internal.JIfStmt){ // if we have a if statement
				//TODO: if we find a if statement, println() des operands op1 et op2 dans le .class 
				
				System.out.println("	--->Type d'expression :	"+ ((JIfStmt)tempUnit).getCondition().getClass().toString());
				//This block get the 2 operands and the binary operator
				//we know that the element are a JIfStmt and that is condition is represented by a value 
				AbstractBinopExpr expr = (AbstractBinopExpr) ((JIfStmt) tempUnit).getCondition(); // implementation of AbstractBinopExpr
				
				Value operand1 = expr.getOp1();
				Value operand2 = expr.getOp2();
				String operand1Value = operand1.getClass().toString();
				String operand2Value = operand2.getClass().toString();
				
				String operator = "";
				if (expr instanceof JGtExpr){
					JGtExpr greatherExpression = (JGtExpr)expr;
					operator = greatherExpression.getSymbol();
				}
				System.out.println("	Classe de op1:	"+ operand1Value);
				System.out.println("	Classe de op2:	"+ operand2Value );
				System.out.println("	Operator: " + operator);
			
				//Invoke  println() to show op1 and op2
				//instrumentExpressionOperands(expr, b);
				
			}
			
		}
		System.out.println("		__________	");
		
	}
	
	private static void instrumentExpressionOperands(AbstractBinopExpr expression, Body b) {
		//Extracting the exression fields
		Value operand1 = expression.getOp1();
		Value operand2 = expression.getOp2();
		String operator = expression.getClass().toString();
		
		Chain units = b.getUnits();
         Local  tmpRef;
		
         
		// Add locals, java.io.printStream tmpRef
        tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        b.getLocals().addFirst(tmpRef);
        
   
        // add "tmpRef = java.lang.System.out"
        units.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
            Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
    
        // insert "tmpRef.println("Hello world!")"
        {
        SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
        units.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Expression: "+ operator + " op1: "+ operand1+ " op2: "+ operand2))));
        }    
		
	}

	//print the traps
	private static void printAllTraps(Body b ) {
		Chain<Trap> trapExeptions = b.getTraps();
		System.out.println("\n	Traps: ");
		Iterator<Trap> trapIt = trapExeptions.iterator();
		while(trapIt.hasNext()){
			Trap temp = trapIt.next();
			String toString = temp.toString();
			//TODO: we need to test with traps to confirm that works
			System.out.println("____");
			System.out.println(toString);
		}
		System.out.println("		|__________	|");
	}

	//exemple de manipulation des  ValueBox
	public void foldAdds(Unit u)
	{
	    Iterator ubIt = u.getUseBoxes().iterator();
	    while (ubIt.hasNext())
	    {
	        ValueBox vb = (ValueBox) ubIt.next();
	        Value v = vb.getValue();
	        if (v instanceof AddExpr)
	        {
	            AddExpr ae = (AddExpr) v;
	            Value lo = ae.getOp1(), ro = ae.getOp2();
	            if (lo instanceof IntConstant && ro instanceof IntConstant)
	            {
	                IntConstant l = (IntConstant) lo,
	                      r = (IntConstant) ro;
	                int sum = l.value + r.value;
	                vb.setValue(IntConstant.v(sum));
	            }
	        }
	    }
	}
}