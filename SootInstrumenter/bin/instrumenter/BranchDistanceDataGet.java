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
import soot.jimple.toolkits.pointer.DependenceGraph;
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
import java.util.Map.Entry;

import com.sun.crypto.provider.DESCipher;

import Tutoriel.LoadAndGenerate;


public class BranchDistanceDataGet {			
	public static void main(String args[]){
	//Set classPath
		String exemplePath = "/Users/gamyot/Documents/workspace/Soot_Exemples/src/";
		String objectPath= "/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar";
		String tracePath = "/Users/gamyot/Documents/workspace/SootInstrumenter/src/";
		
		Scene.v().setSootClassPath(".:"+objectPath+":"+exemplePath+":"+ tracePath);
		
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		
		//Set up the class we’re working with
		SootClass c = Scene.v().loadClassAndSupport("TestExemples.StaticBlockVerification");
		c.setApplicationClass();
		
		SootMethod staticConstructor = getStaticConstructor(c);
		
		System.out.println(staticConstructor.toString());
	
		/*
		//Get the method to test
		SootMethod m = c.getMethodByName("doub");
		Body b = m.retrieveActiveBody();
		 */
		
		//getDecisionNodesAndTargets(c.getMethodByName("doub").retrieveActiveBody());
		
		/*
		System.out.println("____debug to signature ");
		for (SootMethod aMethod: c.getMethods())
			System.out.println(aMethod.getSignature().toString());
		
		*/
		//Body b2 =c.getMethodByName("<init>").retrieveActiveBody();
	
		
		
		//output the resulting code to binary
		try {
			LoadAndGenerate.outputClassToBinary(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * test
	 * */
private static SootMethod getStaticConstructor(SootClass c) {
		
		boolean  staticConstructorPresent = false;
		SootMethod staticConstructor = null;
		try{
		staticConstructor = c.getMethodByName("<clinit>");
		return staticConstructor;
		}
		catch(java.lang.RuntimeException e) {
			
			staticConstructor = new SootMethod("<clinit>",
	                Arrays.asList(new Type[] {}),
	                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
			
			return staticConstructor;
		}

	}
//créé un graphe de blocks de la méhtode et imprime chaque bock suivi de ses predecesseurs et successeurs 
	public static void getDecisionNodesAndTargets(Body b){
		ExceptionalBlockGraph graph = new ExceptionalBlockGraph(b);
		Iterator<Block> graphIt = graph.iterator();
		
		System.out.println("______________Block representation________________");
		
		//lists to store the  
		List<Block> decisionNodeList = new ArrayList<Block>();
		List<Block> targetList = new ArrayList<Block>();
		Map<Block,List<Block>> targetDependenciesMap = new HashMap<Block,List<Block>>();
		Stack<Block> decisioNodeStack = new Stack<Block>();
		
		//resolveDependencies
		while (graphIt.hasNext()){
		
			Block currentBlock = graphIt.next();

			//System.out.println(currentBlock.toString());
			System.out.println("--");

			//getting the decision nodes and the targets
			
			//all blocks are targets, including the decision nodes
			targetList.add(currentBlock);
			if (decisioNodeStack.size()>0){
				List<Block> targetDependenciesList = new ArrayList<Block>();
				for (Block dependencie: decisioNodeStack){
					targetDependenciesList.add(dependencie);
					//debug
					System.out.println("ND"+dependencie.getIndexInMethod()+": debug is a dependencie node");
				}
				
				targetDependenciesMap.put(currentBlock,targetDependenciesList);
				
				
			}
			//pop the decision node at end of scope
			if (isEndOfDecisionNodeScope(currentBlock)/*&&decisioNodeStack.peek()!=currentBlock*/){
				decisioNodeStack.pop();
				System.out.println("ND"+currentBlock.getIndexInMethod()+": end of a dependencie node scope");
				
			}
			//add the decision node to stack
			if (isDecisionNode(currentBlock)){
				decisionNodeList.add(currentBlock);
				decisioNodeStack.add(currentBlock);
				//debug
				System.out.println("ND"+currentBlock.getIndexInMethod()+": added to stack");
			}
			
			
			System.out.println();
				
		}
		debugGettDecisionNodes(decisionNodeList,targetList,targetDependenciesMap);
		
		
	}
	
	
	public static boolean isDecisionNode(Block b){
		return (b.getSuccs().size() >1);
	}
	
	private static boolean isEndOfDecisionNodeScope(Block b) {
		return (b.getPreds().size() >1);
	}

	private static void debugGettDecisionNodes(List<Block> decisionNodeList, List<Block> targetList, Map<Block, List<Block>> targetDependenciesMap){
		//debug print all the decision nodes and the targets with its dependencies 
				
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***decisionNodeList");
		for(Block blo: decisionNodeList)
			System.out.println("DN"+blo.getIndexInMethod()+" : "+blo.getTail().toString());	
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***targetList");
		for(Block targ: targetList)
			System.out.println("T"+targ.getIndexInMethod());
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***targetDependenciesMap");
		Set<Entry<Block, List<Block>>> entrySet = targetDependenciesMap.entrySet();
		
		for (Entry<Block, List<Block>> entry: entrySet){
			System.out.print("Target:"+entry.getKey().getIndexInMethod());
			System.out.print(" Dependencies: ");
			for (Block depend : entry.getValue())
			System.out.print(" D"+depend.getIndexInMethod());
		System.out.println();
		}
				
				
		
	}
	public static void debugGettDecisionNodesUnits(List<Unit> decisionNodeList, List<Block> targetList, Map<Block, List<Unit>> targetDependenciesMap){
		//debug print all the decision nodes and the targets with its dependencies 
				
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***decisionNodeList");
		for(Unit blo: decisionNodeList)
			System.out.println("DN "+blo.toString());	
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***targetList");
		for(Block targ: targetList)
			System.out.println("T"+targ.getIndexInMethod());
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***targetDependenciesMap");
		Set<Entry<Block, List<Unit>>> entrySet = targetDependenciesMap.entrySet();
		
		for (Entry<Block, List<Unit>> entry: entrySet){
			System.out.print("Target:"+entry.getKey().getIndexInMethod());
			System.out.print(" Dependencies: ");
			for (Unit depend : entry.getValue())
			System.out.print(" D"+depend.toString());
		System.out.println();
		}
				
				
		
	}
	
}


/*
  	Unit firstUnit = currentBlock.getHead();
	Unit lastUnit = currentBlock.getTail();
	System.out.println("______________-________________");
	System.out.println("--");

	System.out.println(currentBlock.toString());
	System.out.println("--");
	System.out.println("Premier et dernier units");
	System.out.println("-Head: " +firstUnit.toString()+" ("+ firstUnit.getClass()+")");
	System.out.println("-Tail: " +lastUnit.toString()+" ("+ lastUnit.getClass()+")");
		
	System.out.println("Predecesseurs:___________________________");
	List<Block> predsUnits= graph.getUnexceptionalPredsOf(currentBlock);
	Iterator predsIt = predsUnits.iterator();
	while(predsIt.hasNext())
		System.out.println("--- "+ predsIt.next().toString()+"--- ");
	System.out.println();
	
	System.out.println("Successeurs:___________________________");
	List<Block> succsUnits= graph.getUnexceptionalSuccsOf(currentBlock);
	Iterator succsIt = succsUnits.iterator();
	while(succsIt.hasNext())
		System.out.println("--- "+ succsIt.next().toString()+"--- ");
  
  */

//debug print all the decision nodes and the targets with its dependencies 
		/*
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***decisionNodeList");
		for(Block blo: decisionNodeList)
			System.out.println("DN"+blo.getIndexInMethod()+" : "+blo.getTail().toString());	
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***targetList");
		for(Block targ: targetList)
			System.out.println("T"+targ.getIndexInMethod());
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("*****");
		System.out.println("***targetDependenciesMap");
		Set<Entry<Block, List<Block>>> entrySet = targetDependenciesMap.entrySet();
		
		for (Entry<Block, List<Block>> entry: entrySet){
			System.out.print("Target:"+entry.getKey().getIndexInMethod());
			System.out.print(" Dependencies: ");
			for (Block depend : entry.getValue())
			System.out.print(" D"+depend.getIndexInMethod());
		System.out.println();
		}
		
		*/
