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

import org.eclipse.core.internal.jobs.Counter;
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
import org.eclipse.jdt.core.dom.Name;
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

//import com.sun.xml.internal.bind.v2.model.core.ClassInfo;





import org.eclipse.jdt.internal.compiler.ast.CaseStatement;

import com.sun.tools.javac.code.Scope.ImportScope;

import parser.wrapper.ExtendedASTVisitor;
import parser.wrapper.NamedCompilationUnit;
import utils.io.CsvManager;

public class JavaFileAnalyzerVisitor extends ExtendedASTVisitor {
	
	// all the counter names
	public enum CounterEnum {
		tryStatement, ifStatement, forStatement, enhancedForStatement, doStatement,
		whileStatement, switchCase, switchStatement, catchClause, totalBranches, numOfStatements,
		FieldDeclaration, synchronizedStatement, methodDeclaration, privateMethod, publicMethod,
		staticMethod, anonymousClassDeclaration, nbMemberClasses, nbStringParams, nbPrimitiveParams,
		nbOtherTypesParams, nbOtherTypesParamsLibrary,parameterizedTypeParam, arrayTypeParam,javaImports, throwStatement,
		nestedBlock, maxDepth, maybeUsesExternal
		/* databaseImport, networkImport, ioImport, 
		easyCondition, mediumCondition, dificultCondition,
		testCases*/

	}

	// Enum map to store the counters
	private EnumMap<CounterEnum, Integer> countersMap = new EnumMap<CounterEnum, Integer>(CounterEnum.class);
	// Contains the result for each class: classId and all the values of the
	// counters in the order of the enum
	private Map<String,String> resultsMap = new HashMap<String,String>();

	// Contains the dependencies of each class :
	// Map<className,List<dependenciesClassName>>
	private Map<String, Set<String>> dependenciesMap = new HashMap<String, Set<String>>();

	//ImportSet
	private Set<String> importSet = new HashSet<String>();
	
	//ImportMap
	//private Map<String,List<String>> importMap =new HashMap<String,List<String>>();
	
	// id of the class bein visited
	private String currentClassId = "";
	
	//block embrication counter
	private int currentDepthLevel; 
	//private boolean intoNestedBlock;
	private boolean isNested ;
	
	

	public JavaFileAnalyzerVisitor() {
		initialiseEnumMap();
	}

	/**
	 *  new top level class
	 * @param node
	 */
	private void initialiseForNewCLass(TypeDeclaration node) {
		
		//initialize class name
		this.currentClassId = node.resolveBinding().getQualifiedName();
		// Initialize the dependencies list
		dependenciesMap.put(currentClassId, new HashSet<String>());
		
		
	}

	
	/**
	 * initialize all the counters at 0
	 */
	private void initialiseEnumMap() {
		countersMap = new EnumMap<CounterEnum, Integer>(CounterEnum.class);
		for (CounterEnum i : CounterEnum.values())
			countersMap.put(i, 0);
	}
	
	/**
	 * increment the concerned counterEnum
	 */
	private void incrementCounter(CounterEnum counter){
		countersMap.put(counter,countersMap.get(counter) + 1);
	}

	
	/**
	 * fill the classResult Map whit the current class information
	 */
	public void printClassInfos() {
		StringBuilder classInfo = new StringBuilder();
		StringBuilder classInfoSummary = new StringBuilder();

		classInfo.append(currentClassId + ";");
		//classInfoSummary.append(currentClassId + ";");

		for (CounterEnum i : CounterEnum.values()) {
			
			String counterName = i.name();
			int counterValue = countersMap.get(i);
			//Debug
			//if(CounterEnum.maxDepth.equals(i) ||CounterEnum.nestedBlock.equals(i)
			//	||CounterEnum.throwStatement.equals(i) || CounterEnum.parameterizedTypeParam.equals(i)
			//	||CounterEnum.arrayTypeParam.equals(i) ){
				
				classInfo.append(counterName + "(" + counterValue + ");");
				classInfoSummary.append(counterValue + ";");
			//}
		}

		this.resultsMap.put(currentClassId,classInfoSummary.toString());

		// TODO:debug
		System.out.println(classInfo.toString());
	}
	
	/**
	 * calculate the depth level and count the nested blocks
	 * called on Visit: if,for,enhancedFor,do,while,case,catch
	 * @param node
	 */
	private void entersConditionalBlock(ASTNode node) {
		
		//System.out.println(currentDepthLevel+"__"+ node.toString()+"__");
		currentDepthLevel++;
		if (!isNested && currentDepthLevel >1){
			//System.out.println(currentDepthLevel+"__"+ node.toString()+"__");
			incrementCounter(CounterEnum.nestedBlock);
			isNested = true;
		}
		
		if(currentDepthLevel > countersMap.get(CounterEnum.maxDepth))
			countersMap.put(CounterEnum.maxDepth,currentDepthLevel);
		
		
	}
	/**
	 * used to increment the right conditionals counters:
	 * (easyCondition, mediumCondition, dificultCondition)
	 */
	private void countConditionals(){
		
	}
	
	/**
	 *  calculate the depth level and count the nested blocks
	 *  called on the endVisit: if,for,enhancedFor,do,while,case,catch
	 */
	private void exitConditionalBlock(ASTNode node) {
		currentDepthLevel--;		
		if(currentDepthLevel ==0){//node.getParent().getParent() instanceof MethodDeclaration){
			isNested = false;
			//currentDepthLevel =0;
		}
	}
	
	

	
	private void addDependnciesToClassResultMap(String name, Set<String> dependencies){
		Integer dependenciesCount = dependencies.size();
		String tempString = dependenciesCount.toString() +";" + resultsMap.get(name);
		resultsMap.put(name, tempString);
		
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, Set<String>> resolveClassDependencies() {
		Map<String, Set<String>> resolvedDependencies = resolveClassDependencies(this.dependenciesMap);
		
		Set<Entry<String, Set<String>>> entrySet = resolvedDependencies.entrySet();
		
		Iterator it = entrySet.iterator();
		while(it.hasNext()){
			Entry<String, Set<String>> classDependenciesEntry = (Entry<String,Set<String>>)it.next();
			String currentClassName = classDependenciesEntry.getKey();
			Set<String> classDependenciesSet =classDependenciesEntry.getValue();
			addDependnciesToClassResultMap(currentClassName, classDependenciesSet);
			}
		

		return resolvedDependencies;
	}

	/**
	 * @return
	 */
	public Map<String,String> getResultsMap() {
		return this.resultsMap;
	}

	/**
	 * Author: Aminata
	 * @param mapOfClasses
	 */
	public Map<String, Set<String>> resolveClassDependencies(
			Map<String, Set<String>> mapOfClasses) {

		Map<String, Set<String>> finalMap = new HashMap<String, Set<String>>(
				mapOfClasses);
		// loop on the map, for each class, add in the map its correspondance,
		// if the size before the loop and after the loop change, then the
		// boolean becomes true
		// otherwise, we are done with the changes

		boolean continueSearch = true;

		while (continueSearch) {

			continueSearch = false;
			Iterator<Entry<String, Set<String>>> it = finalMap.entrySet()
					.iterator();
			while (it.hasNext()) {

				Entry<String, Set<String>> entry = it.next();
				String currentClass = entry.getKey();
				Set<String> dependentClasses = entry.getValue();

				Set<String> newSet = new HashSet();
				newSet.addAll(dependentClasses);

				int size = dependentClasses.size();
				Iterator<String> iter = dependentClasses.iterator();
				while (iter.hasNext()) {
					String currentDependent = iter.next();
					if (finalMap.containsKey(currentDependent)) {
						newSet.addAll(finalMap.get(currentDependent));
					}
				}
				if (size != newSet.size()) {
					entry.setValue(newSet);
					continueSearch = true;
				}
			}
		}

		return finalMap;
	}
	
	public List<String> getImporList() {
		List<String> output = new ArrayList<String>();
		output.addAll(importSet);
		return output;
	}
	
	/*
	/**
	 * saves the current class imports into the 
	 
	private void saveImportsInList() {
		List<String> importList = new ArrayList<String>();
		importList.addAll(importSet);	
		importMap.put(currentClassId, importList);
	}
	*/
	
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
		// return super.visit(node);
		//System.out.println("Annotation"+node.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		incrementCounter(CounterEnum.anonymousClassDeclaration);
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
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node) {
		//Block max dept & nested blocks counter
		//initializedepthLevel and nested status when enters a method block
		/*
		if(node.getParent() instanceof MethodDeclaration){
			currentDepthLevel=0;
			isNested=false;
		}
		*/
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
		incrementCounter(CounterEnum.catchClause );
		entersConditionalBlock(node);
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
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		incrementCounter(CounterEnum.doStatement );
		incrementCounter(CounterEnum.numOfStatements);
		entersConditionalBlock(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(EmptyStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		incrementCounter(CounterEnum.enhancedForStatement );
		incrementCounter(CounterEnum.numOfStatements);
		entersConditionalBlock(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return false;
		// return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		incrementCounter(CounterEnum.FieldDeclaration );
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		incrementCounter(CounterEnum.forStatement );
		incrementCounter(CounterEnum.numOfStatements);
		entersConditionalBlock(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		incrementCounter(CounterEnum.ifStatement );
		incrementCounter(CounterEnum.numOfStatements);
		entersConditionalBlock(node);
	
		//Condition testing- may be abandoned
		//PREFIX_EXPRESSION = 38
		//int ex = node.getExpression().getNodeType();
		//ASTNode e;
		
		return super.visit(node);
	}

	
	@Override
	public boolean visit(ImportDeclaration node) {
		incrementCounter(CounterEnum.javaImports);
		//recuperer les import dans un set
		String importName = node.getName().toString();
		//filtre (on veux juste les .java)
		if(importName.contains("java."))
			importSet.add(importName);	
		if(importName.contains("java.io") || importName.contains("java.nio")
			||importName.contains("java.net")||importName.contains("java.sql"))
			incrementCounter(CounterEnum.maybeUsesExternal);

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
		incrementCounter(CounterEnum.numOfStatements);
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
	
	@Override
	public boolean visit(MethodDeclaration node) {
		
		isNested = false;
		
		// method counter
		incrementCounter(CounterEnum.methodDeclaration);
		// privateMethod, publicMethod and staticMethod counters
		if (Modifier.isPrivate(node.getModifiers()))
			incrementCounter(CounterEnum.privateMethod);
		if (Modifier.isPublic(node.getModifiers()))
			incrementCounter(CounterEnum.publicMethod );
		if (Modifier.isStatic(node.getModifiers()))
			incrementCounter(CounterEnum.staticMethod );

		
		// paramCounter
		Iterator paramIt = node.parameters().iterator();
		while (paramIt.hasNext()) {
			SingleVariableDeclaration currentParam = (SingleVariableDeclaration) paramIt.next();
			
			if (currentParam.getType().isPrimitiveType())
				incrementCounter(CounterEnum.nbPrimitiveParams);
			else if (currentParam.getType().resolveBinding() != null) {
				String paramType = currentParam.getType().resolveBinding().getQualifiedName();
				if (paramType.equals("java.lang.String")) {
					incrementCounter(CounterEnum.nbStringParams );
				} else {
					incrementCounter(CounterEnum.nbOtherTypesParams );
					
					if(currentParam.getType().isParameterizedType())
						incrementCounter(CounterEnum.parameterizedTypeParam);
					
					if(currentParam.getType().isArrayType())
						incrementCounter(CounterEnum.arrayTypeParam);
					
					// modifi� ci-dessous
					// Set<String> fileDependenciesList = mapOfDependencies
					// .get(currentClassId);
					// fileDependenciesList.add(currentParam.getType()
					// .resolveBinding().getQualifiedName());
					// mapOfDependencies.put(currentClassId,
					// fileDependenciesList);

					dependenciesMap.get(currentClassId).add(paramType);

					if (!currentParam.getType().resolveBinding().isFromSource()) {
						incrementCounter(CounterEnum.nbOtherTypesParamsLibrary );
					}
				}
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
		incrementCounter(CounterEnum.numOfStatements);
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
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		incrementCounter(CounterEnum.switchCase );
		incrementCounter(CounterEnum.numOfStatements);
		entersConditionalBlock(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		incrementCounter(CounterEnum.switchStatement);
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		incrementCounter(CounterEnum.synchronizedStatement);
		incrementCounter(CounterEnum.numOfStatements);
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
		incrementCounter(CounterEnum.throwStatement);
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		incrementCounter(CounterEnum.tryStatement );
		incrementCounter(CounterEnum.numOfStatements);
		return super.visit(node);
	}

	/*
	 * Type declaration represents a class or a interface declaration
	 * We start counting here if its a class
	 * */
	@Override
	public boolean visit(TypeDeclaration node) {
		// si ce n'est pas une interface, c'est une classe
		
		if (node.isInterface())
			return false;
		else if (!node.isMemberTypeDeclaration()) {
			initialiseForNewCLass(node);
		} else
			incrementCounter(CounterEnum.nbMemberClasses );

		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		incrementCounter(CounterEnum.numOfStatements);
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
		incrementCounter(CounterEnum.numOfStatements);
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
		incrementCounter(CounterEnum.whileStatement );
		incrementCounter(CounterEnum.numOfStatements);
		entersConditionalBlock(node);
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
		incrementCounter(CounterEnum.numOfStatements);
		super.endVisit(node);
	}

	@Override
	public void endVisit(Assignment node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(Block node) {
		/*
		if(node.getParent().getParent() instanceof MethodDeclaration){
			currentDepthLevel=0;
			isNested=false;
		}
		*/ 
		super.endVisit(node);
	}

	@Override
	public void endVisit(BlockComment node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(BreakStatement node) {
		incrementCounter(CounterEnum.numOfStatements);
		super.endVisit(node);
	}

	@Override
	public void endVisit(CastExpression node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(CatchClause node) {
		exitConditionalBlock(node);
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

		// TODO Auto-generated stub
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
		exitConditionalBlock(node);
		super.endVisit(node);
	}

	@Override
	public void endVisit(EmptyStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnhancedForStatement node) {
		exitConditionalBlock(node);
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
		exitConditionalBlock(node);
		
		super.endVisit(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		// TODO Auto-generated method stub
		//node.getExpression();

		exitConditionalBlock(node);
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
		exitConditionalBlock(node);
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

	/*
	 * Type declaration represents a class or a interface declaration
	 * We stop counting here and save the class results into the resultMap
	 * */
	@Override
	public void endVisit(TypeDeclaration node) {
		// Debug
		if (!node.isInterface()) {
			//stop counting if this is not a member class
			if (!node.isMemberTypeDeclaration()) {

				//compute totalBtanches
				int nbTotalBranches = this.countersMap
						.get(CounterEnum.doStatement)
						+ this.countersMap.get(CounterEnum.catchClause)
						+ this.countersMap.get(CounterEnum.ifStatement)
						+ this.countersMap.get(CounterEnum.switchCase)
						+ this.countersMap.get(CounterEnum.whileStatement)
						+ this.countersMap.get(CounterEnum.forStatement)
						+ this.countersMap.get(CounterEnum.enhancedForStatement);
				this.countersMap.put(CounterEnum.totalBranches, nbTotalBranches);
				
				//computes importExternal 
				if (countersMap.get(CounterEnum.maybeUsesExternal) >= 1)
					countersMap.put(CounterEnum.maybeUsesExternal,	1);
				
				//save the counters
				printClassInfos();
	
				//initialize all the counters at 0
				initialiseEnumMap();
				//clears the importSet
				//importSet =new HashSet<String>();
				
				// clear currentClassId
				currentClassId = "";
				
				
			}
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
		exitConditionalBlock(node);
		super.endVisit(node);
	}

	@Override
	public void endVisit(WildcardType node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}

}
