/* (c) Copyright 2009 and following years, Aminata SABANE,
 * Ecole Polytechnique de Montr̩al.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import parser.input.SourceInputsHolder;
import parser.input.impl.FileSystemJavaProject;
import parser.wrapper.JavaParser;


public class JavaFileAnalyzerVistorCaller {

	private List<String> classStats = new ArrayList<String>();
	
	public static void main(String args[]) {

		// source of the java files in the well project structured style
		//String aSourcePath = args[0];
		
		 String aSourcePath="/Users/gamyot/Documents/workspace/TestClasses/src/";
	

		// using librairies? put the paths here
		// String aClassPath=args[1];
		String aClassPath = "";

		final String[] sourcePathEntries = new String[] { aSourcePath };

		final String[] classpathEntries = new String[] { aClassPath };

		try {
			SourceInputsHolder javaProject = new FileSystemJavaProject(
					Arrays.asList(classpathEntries),
					Arrays.asList(sourcePathEntries));

			final JavaParser eclipseSourceCodeParser = new JavaParser(javaProject);

			final JavaFileAnalyzerVisitor visitor = new JavaFileAnalyzerVisitor();
			eclipseSourceCodeParser.parse(visitor);
			
			//resolve dependencie between classes
			visitor.generateListOfDependentClasses2();
			visitor.printCsvResults();
		
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
