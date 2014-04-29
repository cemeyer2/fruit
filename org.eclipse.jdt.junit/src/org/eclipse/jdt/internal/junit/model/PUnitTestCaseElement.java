package org.eclipse.jdt.internal.junit.model;

import org.eclipse.jdt.junit.model.ITestCaseElement;

public class PUnitTestCaseElement extends TestElement implements ITestCaseElement 
{
	
	public String moduleName;
	public String testName;
	
	public PUnitTestCaseElement(String testName, String moduleName)
	{
		super(null,testName, testName);
		this.moduleName = moduleName;
		this.testName = testName;
	}
	
	@Override
	public String getTestClassName() 
	{
		return moduleName;
	}

	@Override
	public String getTestMethodName() 
	{
		return testName;
	}

}
