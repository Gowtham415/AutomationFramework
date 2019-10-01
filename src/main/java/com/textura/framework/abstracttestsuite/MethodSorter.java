package com.textura.framework.abstracttestsuite;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import com.textura.framework.annotations.Author;
import com.textura.framework.annotations.DontRunInProduction;

/**
 * This Listener can be used to manipulate the order or remove test cases from the current run. 
 *
 */
public class MethodSorter implements IMethodInterceptor {
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		if (context.getExcludedGroups().length>0 && methods.size()<2000) {
			//only use when there are excluded groups specified and methods length is <2000
			methods= removeExcludedGroups(context.getExcludedGroups(), methods);
		}
		
		methods = reorderMethodsSoDifferentSuitesRunInParallel(methods);
		return removeDontRunInProductionCases(methods);
		// return (sorter(methods));
	}
	
	public String getIMethodClass(IMethodInstance m) {
		return m.getMethod().getConstructorOrMethod().getDeclaringClass().getSimpleName();
	}
	
	public List<IMethodInstance> reorderMethodsSoDifferentSuitesRunInParallel(List<IMethodInstance> methods) {
		methods = new ArrayList<IMethodInstance>(methods);
		List<IMethodInstance> sortedList = new ArrayList<IMethodInstance>();
		String lastClass = "";
		boolean allSameClass = false;
		while(methods.size() > 0 && !allSameClass) {
			allSameClass = true;
			for(int i=0; i<methods.size(); i++) {
				String currentClass = getIMethodClass(methods.get(i));
				if(!lastClass.equals(currentClass)) {
					sortedList.add(methods.get(i));
					methods.remove(i);
					allSameClass = false;
				}
				lastClass = currentClass;
			}
		}
		if(methods.size() > 0) {
			sortedList.addAll(methods);
		}
		return sortedList;
	}

	public List<IMethodInstance> sorter(List<IMethodInstance> methods) {
		Comparator<IMethodInstance> comp = new Comparator<IMethodInstance>() {
			@Override
			public int compare(IMethodInstance arg0, IMethodInstance arg1) {
				return arg0.getMethod().getMethodName().compareTo(arg1.getMethod().getMethodName());
			}
		};
		Collections.sort(methods, comp);
		return methods;
	}

	public List<IMethodInstance> removeProductionIssueMethods(List<IMethodInstance> methods) {
		List<IMethodInstance> result = new ArrayList<IMethodInstance>();
		for (IMethodInstance m : methods) {
			boolean hasProductionIssue = false;
			for (String group : m.getMethod().getGroups()) {
				if (group.equals("ProductionIssues")) {
					hasProductionIssue = true;
				}
			}
			if (!hasProductionIssue) {
				result.add(m);
			}
		}
		return result;
	}

	public List<IMethodInstance> onlyProductionIssueMethods(List<IMethodInstance> methods) {
		List<IMethodInstance> result = new ArrayList<IMethodInstance>();
		for (IMethodInstance m : methods) {
			for (String group : m.getMethod().getGroups()) {
				if (group.equals("ProductionIssues")) {
					result.add(m);
				}
			}
		}
		return result;
	}

	public List<IMethodInstance> reverse(List<IMethodInstance> methods) {
		Collections.reverse(methods);
		return methods;
	}

	public List<IMethodInstance> removeDontRunInProductionCases(List<IMethodInstance> methods) {
		return removeCasesWithAnnotation(DontRunInProduction.class, methods);
	}

	public <T extends Annotation> List<IMethodInstance> removeCasesWithAnnotation(Class<T> c, List<IMethodInstance> methods) {
		List<IMethodInstance> result = new ArrayList<IMethodInstance>();
		for (IMethodInstance m : methods) {
			if (m.getMethod().getConstructorOrMethod().getMethod().getAnnotation(c) == null) {
				result.add(m);
			}
		}
		return result;
	}

	public <T extends Annotation> List<IMethodInstance> removeExcludedGroups(String[] groups,
			List<IMethodInstance> methods) {
		List<IMethodInstance> result = new ArrayList<IMethodInstance>();
		for (IMethodInstance m : methods) {
			List<String> excluded = Arrays.asList(groups);
			List<String> caseGroups = Arrays.asList(m.getMethod().getGroups());
			if (!caseGroups.stream().anyMatch(excluded::contains)) {
				result.add(m);
			} else {
				//System.out.println(caseGroups + " " + m.getMethod().getMethodName());
			}

		}
		return result;

	}
	

	public void authorScan(List<IMethodInstance> methods) {
		//Make sure retentionpolicy in the author class is set to 
		//@Retention(RUNTIME)
		//import static java.lang.annotation.RetentionPolicy.RUNTIME;
		for (IMethodInstance m : methods) {
			Method method = m.getMethod().getConstructorOrMethod().getMethod();
			Author a = method.getAnnotation(Author.class);
			if (a != null) {
				System.out.println(method.getName() + " Case author: " + a.name());
			}

		}
	}
}