/*******************************************************************************
 * Copyright (c) 2007 University of Illinois at Urbana-Champaign and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UIUC - Initial API and implementation
 *******************************************************************************/
package org.eclipse.photran.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.photran.core.vpg.PhotranVPG;

/**
 * Implements the Clear VPG Database action in the Refactor/(Debugging) menu
 * 
 * @author Jeff Overbey
 */
public class RefreshVPGDatabase extends FortranEditorActionDelegate
{
    public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException
    {
        try
        {
        	progressMonitor.beginTask("Waiting for background work to complete (Photran indexer)", IProgressMonitor.UNKNOWN);
            PhotranVPG.getInstance().queueJobToEnsureVPGIsUpToDate();
        }
        catch (Exception e)
        {
            throw new InvocationTargetException(e);
        }
        finally
        {
        	progressMonitor.done();
        }
    }
}