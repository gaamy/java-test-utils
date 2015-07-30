/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package javafile.analyzer.visitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.print.attribute.standard.PrinterLocation;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;

import parser.wrapper.ExtendedASTVisitor;
import parser.wrapper.NamedCompilationUnit;

public class JavaFileAnalyzerVisitor extends ExtendedASTVisitor {
	//all the counter names
	public enum CounterEnum {
		tryStatement, ifStatement, forStatement, enhancedForStatement/*foreach*/, doStatement, whileStatement,  
		switchCase, switchStatement, superFieldAccess, synchronizedStatement, methodDeclaration,// (number of methods, number of public and private, number of static methods)
		privateMethod, publicMethod, staticMethod, continueStatement, numOfStatements,//node.getBody().getLength();==> to have the number of lines (of all methods in teh file)
		anonymousClassDeclaration, catchClause, assertStatement,  // otherClassType,//(superClass)inherited from a class different from Object
		stringParam,primitiveParam, otherParam, memberClasses, 
		nestedBlock, maxDept, easyCondition, mediumCondition, dificultCondition,
		methodDatabaseAcces, methodNetworkAcces, methodFileSystemAcces,
		attributeDatabaseAcces, attributeNetworkAcces, attributeFileSystemAcces,
		testCases
		
		//regrouper les statements qui causent des branchements dans le CFG(if,case,for,do,while,
		
		//further analysis
			//<String>
			//int typeParameterCounter; // (number of different types of parameters not in jdk 

	}
	//Enum map to store the counters
	private EnumMap<CounterEnum,Integer> currentClassCounterMap = new EnumMap<CounterEnum,Integer>(CounterEnum.class);
	//Contains the result for each class: Map<ClassName,ClassResult>
	private List<String> classResultList= new ArrayList<String>();
	//Contains the dependencies of each class : Map<className,List<dependencieClassName>>
	private Map<String,Set<String>> dependenciesMap = new HashMap<String, Set<String>>();
	//name of the actual class
	private String currentClassId="";
	//block embrication counter
	private int actualDepthLevel;
	
	public JavaFileAnalyzerVisitor() {
		initialiseCounterMap();
	}
	/**
	 * initialize all the counters at 0
	 */
	private void initialiseCounterMap(){
		currentClassCounterMap = new EnumMap<CounterEnum,Integer>(CounterEnum.class);
		for (CounterEnum i : CounterEnum.values())
			currentClassCounterMap.put(i, 0);	
	}
	
	/**
	 * fill the classResultList whit the classInfo data
	 * @param classInfo
	 * @return classResult
	 */
	public void saveClassResult(){
		StringBuilder classInfo = new StringBuilder();
		
		for (CounterEnum i : CounterEnum.values()){
			//if(i.name()==CounterEnum.numOfStatements.name()){
				
				int counterValue = currentClassCounterMap.get(i);
				classInfo.append(i.name()+"("+counterValue+");");
			//}	
		}
					
		classResultList.add(classInfo.toString());
		
		//TODO:debug
		System.out.println(classInfo.toString());
	}
	

	public void printCsvResults() {
		
		//build a string with the CounterEnum content
		StringBuilder infoHeader = new StringBuilder();
		infoHeader.append("Class name;");
		for (CounterEnum i : CounterEnum.values())
			infoHeader.append(i.name()+";");
		String title = infoHeader.toString();
		
		
		
		//For each class
			//print the counters (classResultList)
			//print the dependencies (dependenciesMap
			//void writeMapInCSV(Map<String, String[]> map, String path,String title, boolean hasTitle) {
		
	}
	public void generateListOfDependentClasses2(){
		generateListOfDependentClasses1(dependenciesMap);
		//append the result to the classResultMap
	}
	
	
	/**Author: Aminata
	 * @param mapOfClasses
	 */
	public  void  generateListOfDependentClasses1(
			Map<String, Set<String>> mapOfClasses) {

		Map<String, Set<String>> finalMap = new HashMap<String, Set<String>>();

		// loop on the map, for each class, add in the map its correspondance,
		// if the size before the loop and after the loop change, then the
		// boolean becomes true
		// otherwise, we are done with the changes

		boolean continueSearch = true;

		while (continueSearch) {

			continueSearch = false;
			for (Entry<String, Set<String>> entry : mapOfClasses.entrySet()) {

				String currentClass = entry.getKey();
				Set<String> dependentClasses = entry.getValue();

				int size = dependentClasses.size();
				for (String currentDependent : dependentClasses) {

					if (mapOfClasses.containsKey(currentDependent)) {
						dependentClasses.addAll(mapOfClasses
								.get(currentDependent));
					}
				}
				if (size != dependentClasses.size()) {
					continueSearch = true;
				}
			}
		}
		
		dependenciesMap =  finalMap;
	}
	
	
	@Override
	public void preVisit(ASTNode node) {
		// TODO Auto-generated method stub
		super.preVisit(node);
	}

	@Override
	public boolean preVisit2(ASTNode node) {
		// TODO Auto-generated method stub
		return super.preVisit2(node);
	}

	@Override
	public void postVisit(ASTNode node) {
		// TODO Auto-generated method stub
		super.postVisit(node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		
		//return super.visit(node);
		return false;
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.anonymousClassDeclaration);
		currentClassCounterMap.put(CounterEnum.anonymousClassDeclaration, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		Integer temp = currentClassCounterMap.get(CounterEnum.assertStatement);
		currentClassCounterMap.put(CounterEnum.assertStatement,temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node) {
		//Block max dept counter
		if(node.getParent() instanceof MethodDeclaration)
			actualDepthLevel=0;
		else{
			actualDepthLevel++;
			if(actualDepthLevel > currentClassCounterMap.get(CounterEnum.maxDept))
				currentClassCounterMap.put(CounterEnum.maxDept,actualDepthLevel);
		}
		
		//Nested Blocks count
		if(actualDepthLevel>1){
			
		}
		
		
		return super.visit(node);
	}

	@Override
	public boolean visit(BlockComment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(BreakStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.catchClause);
		currentClassCounterMap.put(CounterEnum.catchClause, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	@Override
	public boolean visit(CompilationUnit node) {		
		return super.visit(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		//TODO:erase that if not working
		Expression expression= node.getExpression();
		if(false)
			return true;
		
		return super.visit(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.continueStatement);
		currentClassCounterMap.put(CounterEnum.continueStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.doStatement);
		currentClassCounterMap.put(CounterEnum.doStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(EmptyStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.enhancedForStatement);
		currentClassCounterMap.put(CounterEnum.enhancedForStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return false;
		//return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.forStatement);
		currentClassCounterMap.put(CounterEnum.forStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.ifStatement);
		currentClassCounterMap.put(CounterEnum.ifStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(Initializer node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(Javadoc node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(LabeledStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(LineComment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(MemberRef node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(MemberValuePair node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRef node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	/**
	 * call recurciveBlockStatementCounter to count recurcively the statements of each block
	 * @param methodStatementList
	 */
	private void blockStatementCount(List<Statement> methodStatementList){
		
		Iterator<Statement> it = methodStatementList.iterator();
		while(it.hasNext()){
			boolean blockFound = false;
			Statement currentStatement = (Statement)it.next();
			//check  if and else for blocks
			if(currentStatement instanceof IfStatement) {
				IfStatement currentIfStatement = (IfStatement)currentStatement;
				Statement thenStatement = currentIfStatement.getThenStatement();
				if(thenStatement instanceof Block)
					recurciveBlockStatementCounter((Block)thenStatement);
				else if(thenStatement != null)
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
				Statement elseStatement = currentIfStatement.getElseStatement();
				if(elseStatement instanceof Block)
					recurciveBlockStatementCounter((Block)elseStatement);
				else if(elseStatement != null)
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);

			}else if(currentStatement instanceof ForStatement){
				ForStatement currentForStatement = (ForStatement)currentStatement;
				Statement bodyStatement = currentForStatement.getBody();
				if(bodyStatement instanceof Block)
					recurciveBlockStatementCounter((Block)bodyStatement);	
				else
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
			}
			else if(currentStatement instanceof WhileStatement){
				WhileStatement currentWhileStatement = (WhileStatement) currentStatement;
				Statement bodyStatement = currentWhileStatement.getBody();
				if(bodyStatement instanceof Block)
					recurciveBlockStatementCounter((Block)bodyStatement);
				else
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
			}
			else if(currentStatement instanceof DoStatement){
				DoStatement doStatement = (DoStatement) currentStatement;
				Statement bodyStatement = doStatement.getBody();
				if(bodyStatement instanceof Block)
					recurciveBlockStatementCounter((Block)bodyStatement);
				else
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
			}
			else if(currentStatement instanceof SwitchStatement){
				//TODO: debug on a switch statement to analyse the situation	
			}
			//else if(currentStatement instanceof Statement){
				currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
				System.out.println("outer statement");
			//}
		}
	}
	
	/**
	 * its not working yet
	 * 
	 * @param blockStatement
	 */
	private void recurciveBlockStatementCounter(Block blockStatement){
		List<Statement> blockStatementList = blockStatement.statements();
		Iterator<Statement> it = blockStatementList.iterator();
		while(it.hasNext()){
			Statement currentStatement = (Statement)it.next();
			if(currentStatement instanceof IfStatement) {
				IfStatement currentIfStatement = (IfStatement)currentStatement;
				Statement thenStatement = currentIfStatement.getThenStatement();
				if(thenStatement instanceof Block)
					recurciveBlockStatementCounter((Block)thenStatement);
				else if(thenStatement != null)
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
				Statement elseStatement = currentIfStatement.getElseStatement();
				if(elseStatement instanceof Block)
					recurciveBlockStatementCounter((Block)elseStatement);
				else if(elseStatement != null)
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
			}else if(currentStatement instanceof ForStatement){
				ForStatement currentForStatement = (ForStatement)currentStatement;
				Statement bodyStatement = currentForStatement.getBody();
				if(bodyStatement instanceof Block)
					recurciveBlockStatementCounter((Block)bodyStatement);
				else
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
			}else if(currentStatement instanceof WhileStatement){
				WhileStatement currentWhileStatement = (WhileStatement) currentStatement;
				Statement bodyStatement = currentWhileStatement.getBody();
				if(bodyStatement instanceof Block)
					recurciveBlockStatementCounter((Block)bodyStatement);
				else
					currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);

			}
			//else if (currentStatement instanceof Statement){
				currentClassCounterMap.put(CounterEnum.numOfStatements, currentClassCounterMap.get(CounterEnum.numOfStatements)+1);
				System.out.println("inner statement");
			//}
		}
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		//method counter
		currentClassCounterMap.put(CounterEnum.methodDeclaration, currentClassCounterMap.get(CounterEnum.methodDeclaration)+1);
		
		//privateMethod, publicMethod and staticMethod counters
		if(Modifier.isPrivate(node.getModifiers()))
				currentClassCounterMap.put(CounterEnum.privateMethod, currentClassCounterMap.get(CounterEnum.privateMethod)+1);
		if(Modifier.isPublic(node.getModifiers()))
				currentClassCounterMap.put(CounterEnum.publicMethod, currentClassCounterMap.get(CounterEnum.publicMethod)+1);
		if(Modifier.isStatic(node.getModifiers()))
				currentClassCounterMap.put(CounterEnum.staticMethod, currentClassCounterMap.get(CounterEnum.staticMethod)+1);
					
		//numOfStatements counter
		List<Statement> methodStatementList = node.getBody().statements();
		blockStatementCount(methodStatementList);
		
		//paramCounter
		Iterator paramIt = node.parameters().iterator();
		//iterate trought the parameters
		while(paramIt.hasNext()){
			SingleVariableDeclaration currentParam = (SingleVariableDeclaration)paramIt.next();
			//if the current parameter is a Primitive
			if(currentParam.getType().isPrimitiveType())
				currentClassCounterMap.put(CounterEnum.primitiveParam,currentClassCounterMap.get(CounterEnum.primitiveParam) + 1);	
			//if the current parameter is a String 
			else if(currentParam.getType().resolveBinding().getQualifiedName().equals("java.lang.String"))
				currentClassCounterMap.put(CounterEnum.stringParam,currentClassCounterMap.get(CounterEnum.stringParam) + 1);
	
			else if (currentParam.getType().resolveBinding().getQualifiedName() != null){
				currentClassCounterMap.put(CounterEnum.otherParam,currentClassCounterMap.get(CounterEnum.otherParam) + 1);
				Set<String> fileDependenciesList = dependenciesMap.get(currentClassId);
				fileDependenciesList.add(currentParam.getType().resolveBinding().getQualifiedName());
				dependenciesMap.put(currentClassId, fileDependenciesList);	
				}	
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(Modifier node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(NullLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(NumberLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ParameterizedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(PrefixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(PrimitiveType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.superFieldAccess);
		currentClassCounterMap.put(CounterEnum.superFieldAccess, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.switchCase);
		currentClassCounterMap.put(CounterEnum.switchCase, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.switchStatement);
		currentClassCounterMap.put(CounterEnum.switchStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		Integer temp =currentClassCounterMap.get(CounterEnum.synchronizedStatement);
		currentClassCounterMap.put(CounterEnum.synchronizedStatement, temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(TagElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TextElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ThisExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		Integer temp = currentClassCounterMap.get(CounterEnum.tryStatement);
		currentClassCounterMap.put(CounterEnum.tryStatement,temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		//si ce n'est pas une interface, c'est une classe
		if(node.isInterface())
		 	return false;
		 else if(!node.isMemberTypeDeclaration()){
			
			//TODO:debug test
			 if(!this.currentClassId.isEmpty()){
				 System.out.println("Weird two top level classes");
			 }
			 
			 //new top level class			
			initialiseCounterMap();
			this.currentClassId=node.resolveBinding().getQualifiedName();
			//Initialize the dependencies list
			dependenciesMap.put(currentClassId,new HashSet<String>());
	 
			 
			 
			 
			 System.out.println(currentClassId+"...");
		 }else
			currentClassCounterMap.put(CounterEnum.memberClasses, currentClassCounterMap.get(CounterEnum.memberClasses)+1);
		
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeParameter node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		Integer temp = currentClassCounterMap.get(CounterEnum.whileStatement);
		currentClassCounterMap.put(CounterEnum.whileStatement,temp+1);
		return super.visit(node);
	}

	@Override
	public boolean visit(WildcardType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(AnnotationTypeMemberDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayAccess node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayCreation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(AssertStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(Assignment node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(Block node) {
		//depth level calculation
		actualDepthLevel--;
		
		super.endVisit(node);
	}

	@Override
	public void endVisit(BlockComment node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(BreakStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(CastExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(CatchClause node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(CharacterLiteral node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {

		//TODO Auto-generated stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ConditionalExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ConstructorInvocation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ContinueStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(DoStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(EmptyStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnhancedForStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnumConstantDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ExpressionStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(FieldAccess node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ForStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ImportDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(InfixExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(InstanceofExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(Initializer node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(Javadoc node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(LabeledStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(LineComment node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(MemberRef node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(MemberValuePair node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodRef node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodRefParameter node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		
		
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodInvocation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(Modifier node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(NormalAnnotation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(NullLiteral node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(NumberLiteral node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(PackageDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ParameterizedType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ParenthesizedExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(PostfixExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(PrefixExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(PrimitiveType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(QualifiedName node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(QualifiedType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ReturnStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SimpleName node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SimpleType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SingleVariableDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(StringLiteral node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SwitchCase node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SwitchStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(SynchronizedStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(TagElement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(TextElement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ThisExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(ThrowStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(TryStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		//Debug
		
		if(!node.isMemberTypeDeclaration()){
			//ecriture et reinitialisation
			
			saveClassResult();
			
			// forget that shit
			//Map<String,String> = new HashMap<String,String>();
			
 			//writeListInCSV(List<String> list,currentClassId,"results.txt", true);
 			
			
			PrintWriter writer;
			try {
				writer = new PrintWriter("results.txt", "UTF-8");
				for(String s :classResultList)
					writer.println(s);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
			
			
			//clear currentClassId
			currentClassId="";
		}
		
		
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeDeclarationStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeLiteral node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeParameter node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(VariableDeclarationExpression node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(WhileStatement node) {
		
		super.endVisit(node);
	}

	@Override
	public void endVisit(WildcardType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	
}
