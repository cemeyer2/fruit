package edu.uiuc.cs427.nibbler.punit.util;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.viewers.*;
import org.eclipse.cdt.core.model.*;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.*;

import edu.uiuc.cs427.nibbler.fruit.core.FruitLaunchManager;
import edu.uiuc.cs427.nibbler.punit.views.*;

/**
 * Handler for the "Run FRUIT Test" context menu entry
 */
public class RunFRUITTestHandler extends AbstractHandler implements IHandler {

    /**
     * Context menu handler for Run FRUIT Test.  This handler will update/build/execute
     * the fruit test via the FruitLaunchManager.  Then it will launch the PUnitView
     * which will be populated with data from the FruitLaunchManager upon completion.
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
		flm.updateAndLaunchDriver("debug");
		 
        try 
        {
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            PUnitView punitView = (PUnitView)activePage.showView("edu.uiuc.cs427.nibbler.punit.views.PUnitView");
            punitView.clearView();
            punitView.setFruitOutputFile(flm.getOutputFile());
            activePage.bringToTop(punitView);
            
        } 
        catch (CoreException e) 
        {
            e.printStackTrace();
        }
        
		return null;
    }
}