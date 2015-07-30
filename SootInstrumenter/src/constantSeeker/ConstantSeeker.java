/**
 * Part of this code is based on ConstantFieldValueFinder
Look at CollectConstants to see if it's similar
 * 
 * 
 * 
 */

package constantSeeker;
import soot.*;
import soot.JastAddJ.ArrayAccess;
import soot.dava.DavaBody;
import soot.dava.DecompilationException;
import soot.dava.toolkits.base.AST.interProcedural.ConstantFieldValueFinder;
import soot.jbco.jimpleTransformations.CollectConstants;
import soot.jimple.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.AbstractJimpleBinopExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.typing.fast.Integer1Type;
import soot.options.Options;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.*;

import java.awt.List;
import java.io.*;
import java.util.*;

/**ConstantSeeker
	Search for constants on the source code of a list of classes (Map<className,classPath>).
	He look at the right hand of any assignment and comparison (including attributes declaration) 
	seeking for primitives that represent constant declaration.
	ConstantSeeker have being developed in a testing context. 
	The goal is to gather meaningful data from the source code.
 */
public class ConstantSeeker {
	private ArrayList<SootClass> classesToSearch;
	private Set<String> stringConstants;
	private Set<Integer> integerConstants;
	private Set<Long> longConstants;
	private Set<Double> doubleConstants;
	private Set<Float> floatConstants;
	private Set<Character> charConstants;	//not imlpemented
	
	/**Constructor
	 *  
	 * @param classMap: containing the information to find  the classes we want to seek into
	 * 			 	   	Key: class name 
	 * 					Value: class path
	 */
	public ConstantSeeker(Map<String, String> classMap){
		classesToSearch = new ArrayList<SootClass>();
		stringConstants = new HashSet<String>();
		integerConstants= new HashSet<Integer>();
		longConstants = new HashSet<Long>();
		doubleConstants = new HashSet<Double>();
		floatConstants = new HashSet<Float>();
		charConstants = new HashSet<Character>();	//not imlpemented
		
		loadClasses(classMap);
		for(SootClass c: classesToSearch)
			seekConstans(c);
	}
	
	//getters
	public Set<Integer> getIntegerConstants(){
		return integerConstants;
	}
	public Set<String> getStringConstants(){
		return stringConstants;
	}
	public Set<Long> getLongConstants(){
		return longConstants;
	}
	public Set<Double> getDoubleConstants(){
		return doubleConstants;
	}
	public Set<Float> getFloatConstants(){
		return floatConstants;
	}

	
	/**
	 * search for constants on the class
	 */
	private void seekConstans(SootClass sClass){
		Iterator<SootMethod> methodIterator = sClass.methodIterator();
		while (methodIterator.hasNext()){
			SootMethod m = methodIterator.next();
			Body body = m.retrieveActiveBody();
			PatchingChain<Unit> unitList = body.getUnits();
			//iterate the units
			Iterator<Unit> unitIt = unitList.iterator();
			while(unitIt.hasNext()){
				Unit tempUnit = unitIt.next();
				//if the unit is a if stmt
				if (tempUnit instanceof soot.jimple.internal.JIfStmt){
					AbstractBinopExpr expression = (AbstractBinopExpr) ((JIfStmt) tempUnit).getCondition();
					Value operand1 = expression.getOp1();
					Value operand2 = expression.getOp2();
					if (operand1 instanceof Constant)
						constantFound(operand1);
					if (operand2 instanceof Constant)
						constantFound(operand2);
				}
				//if the unit is a if asgmt
				else if (tempUnit instanceof soot.jimple.internal.JAssignStmt){
					JAssignStmt assgmtUnit = (JAssignStmt) tempUnit;
					Value rightOperand = assgmtUnit.getRightOp();
					if (rightOperand instanceof Constant){
						constantFound(rightOperand);
					}
					//if the right hand of the assigment is a comparison
					else if(rightOperand instanceof soot.jimple.internal.AbstractBinopExpr/*soot.jimple.parser.node.PBinop*/){
						AbstractBinopExpr comparison =  (AbstractBinopExpr)rightOperand;
						Value operand1 = comparison.getOp1();
						Value operand2 = comparison.getOp2();
						if (operand1 instanceof Constant)
							constantFound(operand1);
						if (operand2 instanceof Constant)
							constantFound(operand2);
						
					}
				}
			}//iterate units
		}//iterate methods
	}
	
	//sort the constant found into their respective set
	private void constantFound(Value val){
		
		//if the Value is not a numeric constant, should be a string constant 
		if(! (val instanceof NumericConstant)){
			if(val instanceof StringConstant){
				String tempVal = new String( ((StringConstant)val).value );
				stringConstants.add(tempVal);
			}
		}
		else{
			NumericConstant numericVal = (NumericConstant)val;
			
			if(numericVal instanceof LongConstant){
				Long tempVal = new Long( ((LongConstant)numericVal).value );
				longConstants.add(tempVal);					
			}
			else if(numericVal instanceof DoubleConstant){
				Double tempVal = new Double( ((DoubleConstant)numericVal).value );
				doubleConstants.add(tempVal);
			}
			else if(numericVal instanceof FloatConstant){
				Float tempVal = new Float( ((FloatConstant)numericVal).value );
				floatConstants.add(tempVal);
			}
			else if(numericVal instanceof IntConstant){
				Integer tempVal = new Integer( ((IntConstant)numericVal).value );
				integerConstants.add(tempVal);		
			}
		}

	}
	
	//classs loader
	private void loadClasses(Map<String, String> classMap){
		        
		String objectPath = "/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar";
		
		Set<String> a = classMap.keySet();
		Iterator<String> mapIt = a.iterator();
		while(mapIt.hasNext()){
			String className = mapIt.next();
			String classPath = classMap.get(className);
			
			Scene.v().setSootClassPath(".:"+objectPath+":"+classPath);
			
			Scene.v().loadClassAndSupport("java.lang.Object");
			Scene.v().loadClassAndSupport("java.lang.System");
			//Set up the class we’re working with
			
			try{
				SootClass c;
				c = Scene.v().loadClassAndSupport(className);
				c.setApplicationClass();
				try{
					classesToSearch.add(c);
				}
				catch(Exception e1){
					System.out.println(e1.toString());
					System.out.println(e1.getMessage());
					System.out.println("Error adding the class to the Chain:  "+ className);
				}
			}
			catch(Exception e2){
				System.out.println(e2.toString());
				System.out.println("Error Loading class "+ className);
				System.out.println("Check the name( "+className+" ) and path( "+classPath+" )");
			}
			
		}	
	}

	
	private void printList(Set constantsFound){
		Iterator listIt= constantsFound.iterator();
		while(listIt.hasNext()){	
			Object tempObj = listIt.next();
			System.out.println("	"+tempObj.toString()+" (" +tempObj.getClass()+") ");
		}
	}
	
	
	
	// print all gathered constants 
	public void printConstants(){
		System.out.println("Constantes:");
		System.out.println("-Strings: "+stringConstants.size());
		if (!stringConstants.isEmpty())
			printList(stringConstants);
		System.out.println("-Int: "+integerConstants.size());
		if (!integerConstants.isEmpty())
			printList(integerConstants);
		System.out.println("-Long: " +longConstants.size());
		if (!longConstants.isEmpty())
			printList(longConstants);
		System.out.println("-Double: " + doubleConstants.size());
		if (!doubleConstants.isEmpty())
			printList(doubleConstants);		
		System.out.println("-Float: " + floatConstants.size());
		if (!floatConstants.isEmpty())
			printList(floatConstants);		
		/*
		System.out.println("chars:");
		if (!charConstants.isEmpty()){
			System.out.println("char:");
			printList(charConstants);
		}
		*/
	}
}
