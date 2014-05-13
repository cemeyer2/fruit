/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 * Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.managedbuilder.tests.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.managedbuilder.core.tests.BuildDescriptionModelTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildCoreTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildCoreTests20;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildCoreTests_SharedToolOptions;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildDependencyCalculatorTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildEnvironmentTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildMacrosTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedBuildTCSupportedTest;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedCommandLineGeneratorTest;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedProject21MakefileTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedProject30MakefileTests;
import org.eclipse.cdt.managedbuilder.core.tests.ManagedProjectUpdateTests;
import org.eclipse.cdt.managedbuilder.core.tests.MultiVersionSupportTests;
import org.eclipse.cdt.managedbuilder.core.tests.OptionEnablementTests;
import org.eclipse.cdt.managedbuilder.core.tests.PathConverterTest;
import org.eclipse.cdt.managedbuilder.core.tests.ResourceBuildCoreTests;
import org.eclipse.cdt.projectmodel.tests.BackwardCompatiblityTests;
import org.eclipse.cdt.projectmodel.tests.OptionStringListValueTests;
import org.eclipse.cdt.projectmodel.tests.ProjectModelTests;

/**
 *
 */
public class AllManagedBuildTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllManagedBuildTests.suite());
	}
	public static Test suite() {
		//  May/2005 Turning off all indexing for now because the "original" indexer causes hangs... 
		CCorePlugin.getDefault().getPluginPreferences().setValue(CCorePlugin.PREF_INDEXER, IPDOMManager.ID_NO_INDEXER);
		//  We could enable this later...
		//CCorePlugin.getDefault().getPluginPreferences().setValue(CCorePlugin.PREF_INDEXER, IPDOMManager.ID_FULL_INDEXER);

		TestSuite suite = new TestSuite(
				"Test for org.eclipse.cdt.managedbuild.core.tests");
		//$JUnit-BEGIN$
// TODO uncoment this
		suite.addTest(ManagedBuildCoreTests20.suite());
		suite.addTest(ManagedBuildCoreTests.suite());
		suite.addTest(ManagedProjectUpdateTests.suite());
		suite.addTest(ManagedCommandLineGeneratorTest.suite());
		suite.addTest(ResourceBuildCoreTests.suite());
		suite.addTest(ManagedProject21MakefileTests.suite());
		suite.addTest(ManagedProject30MakefileTests.suite());
		suite.addTest(ManagedBuildCoreTests_SharedToolOptions.suite());
		suite.addTest(ManagedBuildEnvironmentTests.suite());
		suite.addTest(ManagedBuildMacrosTests.suite());
		suite.addTest(ManagedBuildTCSupportedTest.suite());
		suite.addTest(MultiVersionSupportTests.suite());
		suite.addTest(OptionEnablementTests.suite());
		suite.addTest(ManagedBuildDependencyCalculatorTests.suite());

		suite.addTest(BuildDescriptionModelTests.suite());
		suite.addTest(PathConverterTest.suite());
		suite.addTest(ProjectModelTests.suite());
		suite.addTest(OptionStringListValueTests.suite());
		suite.addTest(BackwardCompatiblityTests.suite());
		//$JUnit-END$
		return suite;
	}
}