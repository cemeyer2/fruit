package edu.uiuc.cs427.nibbler.fruit.core;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.internal.resources.Workspace;

import org.eclipse.jface.viewers.*;
import org.eclipse.cdt.core.model.*;
import org.eclipse.ui.handlers.*;

public class RunFRUITTestHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
     * .ExecutionEvent) to why we are returning null.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelection( event );
		ITreeSelection treeSel = (ITreeSelection)sel;
		TreePath[] paths = treeSel.getPaths();
		
		FruitLaunchManager flm = new FruitLaunchManager(((ICProject)paths[0].getFirstSegment()).getProject());
		flm.updateAndLaunchDriver("run");
		
		return null;
    }
}