/*******************************************************************************
 * Copyright (c) 2008 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.debug.core.tests;

import java.io.IOException;

import junit.framework.Test;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.ICDIFunctionLocation;
import org.eclipse.cdt.debug.core.cdi.ICDILocation;
import org.eclipse.cdt.debug.core.cdi.ICDILocator;
import org.eclipse.cdt.debug.core.cdi.model.ICDIBreakpoint;
import org.eclipse.cdt.debug.core.cdi.model.ICDIBreakpointManagement3;
import org.eclipse.cdt.debug.core.cdi.model.ICDIEventBreakpoint;
import org.eclipse.cdt.debug.core.model.ICBreakpointType;
import org.eclipse.cdt.debug.mi.core.MIException;
import org.eclipse.cdt.debug.mi.core.cdi.model.EventBreakpoint;

public class EventBreakpointTests extends AbstractDebugTest {
	public static Test suite() {
		return new DebugTestWrapper(EventBreakpointTests.class){};
	}

	protected String getProjectName() {
		return "catchpoints";
	}

	protected String getProjectZip() {
		return "resources/debugCxxTest.zip";
	}

	protected String getProjectBinary() {
		return "catchpoints.exe";
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createDebugSession();
		assertNotNull(currentTarget);
		currentTarget.deleteAllBreakpoints();
		pause();
	}

	void setBreakOnMain() throws CDIException {
		ICDILocation location = null;
		location = currentTarget.createFunctionLocation("", "main"); //$NON-NLS-1$	
		currentTarget.setFunctionBreakpoint(ICBreakpointType.TEMPORARY, (ICDIFunctionLocation) location, null, false);

	}

	public void testCatch() throws CModelException, IOException, MIException, CDIException {
		eventbreakpoints(EventBreakpoint.CATCH, "");
	}

	public void testThrow() throws CModelException, IOException, MIException, CDIException {
		eventbreakpoints(EventBreakpoint.THROW, "");
	}
	
	private void eventbreakpoints(String type, String arg) throws CModelException, IOException, MIException, CDIException {
		ICDIBreakpoint[] breakpoints;
		ICDIEventBreakpoint curbreak;

		setBreakOnMain();
		currentTarget.restart();
		waitSuspend(currentTarget);
		ICDILocator locator = currentTarget.getThreads()[0].getStackFrames()[0].getLocator();
		assertEquals("Debug should be stopped in function 'main' but it is stopped in: " + locator.getFunction(),
				"main", locator.getFunction());

		currentTarget.deleteAllBreakpoints();
		pause();
		assertTrue(currentTarget instanceof ICDIBreakpointManagement3);
		((ICDIBreakpointManagement3) currentTarget).setEventBreakpoint(type, arg, ICBreakpointType.REGULAR, null, false, true);
		pause();
		breakpoints = currentTarget.getBreakpoints();
		assertNotNull(breakpoints);
		assertEquals(1, breakpoints.length);
		if (breakpoints[0] instanceof ICDIEventBreakpoint) {
			curbreak = (ICDIEventBreakpoint) breakpoints[0];
		} else
			curbreak = null;
		assertNotNull("Found breakpoint is not an event breakpoint",curbreak);
		currentTarget.resume(false);
		waitSuspend(currentTarget);
		// it is stopped we are fine, it did hit breakpoint
	}

}
