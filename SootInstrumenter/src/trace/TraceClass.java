package trace;

import java.util.ArrayList;
import java.util.List;

public class TraceClass {
	static List<TraceMethod> traces;
	
	public TraceClass(){
		traces= new ArrayList<TraceMethod>();
	}
	
	public TraceMethod nextMethod(){
		TraceMethod newTraceMethod = new TraceMethod();
		traces.add(newTraceMethod);
		return traces.get(traces.size()-1);
	}
}
