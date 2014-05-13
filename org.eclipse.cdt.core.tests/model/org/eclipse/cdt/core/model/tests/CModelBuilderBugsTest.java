/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Anton Leherbauer (Wind River Systems) - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.core.model.tests;

import junit.framework.Test;

import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.INamespace;
import org.eclipse.cdt.core.model.IStructure;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.testplugin.CProjectHelper;
import org.eclipse.cdt.core.testplugin.CTestPlugin;
import org.eclipse.cdt.core.testplugin.util.BaseTestCase;

/**
 * Tests for C model builder bugs.
 */
public class CModelBuilderBugsTest extends BaseTestCase {

	public static Test suite() {
		return suite(CModelBuilderBugsTest.class, "_");
	}

	private ICProject fCProject;
	private ITranslationUnit fTU;		
		
	public CModelBuilderBugsTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		fCProject= CProjectHelper.createCProject(getName(), null, IPDOMManager.ID_FAST_INDEXER);
		assertNotNull(fCProject);
		CProjectHelper.importSourcesFromPlugin(fCProject, CTestPlugin.getDefault().getBundle(), "/resources/cmodel");
		fTU= (ITranslationUnit) CProjectHelper.findElement(fCProject, "CModelBuilderTest.cpp");
		assertNotNull(fTU);
	}

	protected void tearDown() throws Exception {
		  CProjectHelper.delete(fCProject);
		  super.tearDown();
	}	
	
	public void testModelBuilderBug222398() throws Exception {
		IStructure clazz= (IStructure) fTU.getElement("Test");
		assertNotNull(clazz);
		ICElement[] methods= clazz.getChildren();
		assertEquals(2, methods.length);
		assertEquals("inlined", methods[0].getElementName());
		assertEquals("decl", methods[1].getElementName());
		
		INamespace ns= (INamespace) fTU.getElement("nsTest");
		ICElement[] functions= ns.getChildren();
		assertEquals(2, functions.length);
		assertEquals("inlined", functions[0].getElementName());
		assertEquals("decl", functions[1].getElementName());
	}

}