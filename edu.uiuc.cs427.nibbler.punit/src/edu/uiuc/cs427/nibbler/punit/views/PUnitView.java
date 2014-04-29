package edu.uiuc.cs427.nibbler.punit.views;


import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import edu.uiuc.cs427.nibbler.fruit.core.parser.FruitAssertionMessage;
import edu.uiuc.cs427.nibbler.fruit.core.parser.FruitParser;
import edu.uiuc.cs427.nibbler.punit.model.ControlFruitAssertionMessages;
import edu.uiuc.cs427.nibbler.punit.provider.FruitAssertionMessageContentProvider;
import edu.uiuc.cs427.nibbler.punit.provider.FruitAssertionMessageLabelProvider;





/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class PUnitView extends ViewPart {
	private TableViewer viewer;
	private Action loadFileAction;
	private Action doubleClickAction;
	FruitAssertionMessageContentProvider contentProvider;
	PUnitProgressBar bar;
	CounterPanel fCounterPanel;
	Label totalTests, passedTests, failedTests;
	Composite parent;





	/**
	 * The constructor.
	 */
	public PUnitView() 
	{
	}




	/*	protected Composite createProgressCountPanel(Composite parent) {
	//	Composite composite= new Composite(parent, SWT.NONE);
	/	GridLayout layout= new GridLayout();
		composite.setLayout(layout);
		setCounterColumns(layout); 

		fCounterPanel = new CounterPanel(composite);
		fCounterPanel.setLayoutData(
			new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		fProgressBar = new JUnitProgressBar(composite);
		fProgressBar.setLayoutData(
				new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		return composite;
	}
	 */
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) 
	{
		this.parent = parent;
		GridData gridData;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		//
		fCounterPanel = new CounterPanel(parent);
		fCounterPanel.setLayoutData(
				new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		bar = new PUnitProgressBar(parent);
		bar.setLayoutData(
				new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		/*
			Composite comp = new Composite(parent,SWT.NO_BACKGROUND);
				comp.setLayout(new FillLayout());
			totalTests = new Label(comp,SWT.LEFT);
			passedTests = new Label(comp,SWT.CENTER);
			failedTests = new Label(comp,SWT.RIGHT);
			bar = new ProgressBar(parent, SWT.HORIZONTAL);
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalSpan = 1;
			gridData.grabExcessHorizontalSpace = true;
			bar.setLayoutData(gridData);
        */	
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		contentProvider = new FruitAssertionMessageContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.getTable().setLinesVisible(true);
		FruitAssertionMessageLabelProvider labelProvider = new FruitAssertionMessageLabelProvider();
		labelProvider.createColumns(viewer);
		viewer.setLabelProvider(labelProvider);

		viewer.setInput(getViewSite());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		viewer.getTable().setLayoutData(gridData);

		//viewer.setSorter(new NameSorter());
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "edu.uiuc.cs427.nibbler.punit.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		updateColoring();
		
		
		
	}

	public void addTest(FruitAssertionMessage test)
	{
		ControlFruitAssertionMessages.getInstance().addTest(test);
		viewer.refresh();
		parent.pack();
		parent.redraw();
		updateColoring();
	}


	private void updateColoring()
	{
		int startedCount;
		int ignoredCount;
		int totalCount;
		int errorCount = 0;
		int failureCount = 0;
		boolean hasErrorsOrFailures; // used to control color in progressBar
		boolean stopped = false;
		
		Table table = viewer.getTable();
		Display display = Display.getCurrent();
		int passedTests = 0;
		totalCount = startedCount = table.getItemCount();
		
		for(int i = 0; i < table.getItemCount(); i++)
		{

			TableItem item = table.getItem(i);
			FruitAssertionMessage message = (FruitAssertionMessage)contentProvider.getElements(null)[i];
			if(message.isFailure())
				item.setBackground(display.getSystemColor(SWT.COLOR_RED));
			else
			{
				item.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
				passedTests++;
			}
		}
		failureCount = table.getItemCount()- passedTests;
	    fCounterPanel.setTotal(totalCount);
		fCounterPanel.setRunValue(startedCount, 0);
		fCounterPanel.setErrorValue(errorCount);
		fCounterPanel.setFailureValue(failureCount);
		
		hasErrorsOrFailures = errorCount + failureCount > 0;
		
		bar.setMaximum(table.getItemCount());
		
		int ticksDone;
		if (startedCount == 0)
			ticksDone= 0;
		else if (startedCount == totalCount)// && ! fTestRunSession.isRunning())
			ticksDone= totalCount;
		else
			ticksDone= startedCount - 1;
		
		bar.reset(hasErrorsOrFailures, stopped, ticksDone, totalCount);	
	}
	
	private void updateColoring1()
	{
		Table table = viewer.getTable();
		Display display = Display.getCurrent();
		int passedTests = 0;
		for(int i = 0; i < table.getItemCount(); i++)
		{
			
			TableItem item = table.getItem(i);
			FruitAssertionMessage message = (FruitAssertionMessage)contentProvider.getElements(null)[i];
			if(message.isFailure())
				item.setBackground(display.getSystemColor(SWT.COLOR_RED));
			else
			{
				item.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
				passedTests++;
			}
		}
	//	bar.setMinimum(0);
		bar.setMaximum(table.getItemCount());
   //		bar.setSelection(passedTests);
		
		totalTests.setText("Total Tests Run: "+table.getItemCount()+"    ");
		this.passedTests.setText("Tests Passed: "+passedTests+"/"+table.getItemCount()+"    ");
		failedTests.setText("Tests Failed: "+(table.getItemCount()-passedTests)+"/"+table.getItemCount()+"    ");
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PUnitView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) 
	{
		manager.add(loadFileAction);
	}

	private void fillContextMenu(IMenuManager manager) 
	{
		manager.add(loadFileAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) 
	{
		//manager.add(loadFileAction);
	}

	private void makeActions() 
	{
		loadFileAction = new Action() 
		{
			public void run() 
			{
				FileDialog dialog = new FileDialog(viewer.getControl().getShell(),SWT.OPEN);
				String filePath = dialog.open();
				ControlFruitAssertionMessages.getInstance().clear();
				try
				{
					FruitParser parser = new FruitParser(new File(filePath));
					Map<String, FruitAssertionMessage> tests = parser.getAllTests();
					for(String key : tests.keySet())
						addTest(tests.get(key));
				}
				catch(IOException ioe)
				{
					showMessage("Error parsing file");
				}
				
			}
		};
		
		loadFileAction.setText("Load FRUIT Output Log");
		loadFileAction.setToolTipText("Load FRUIT Output Log");
		loadFileAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"PUnit View",
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}