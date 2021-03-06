/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.ui.wizards;

import org.eclipse.cdt.internal.ui.CPluginImages;
import org.eclipse.cdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.cdt.internal.ui.wizards.folderwizard.NewFolderWizardMessages;
import org.eclipse.cdt.internal.ui.wizards.folderwizard.NewSourceFolderWizardPage;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class NewSourceFolderCreationWizard extends NewElementWizard {

	private NewSourceFolderWizardPage fPage;

	public NewSourceFolderCreationWizard() {
		super();
		setDefaultPageImageDescriptor(CPluginImages.DESC_WIZBAN_NEWSRCFOLDR);
		setDialogSettings(CUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(NewFolderWizardMessages.getString("NewSourceFolderCreationWizard.title")); //$NON-NLS-1$
	}

	/*
	 * @see Wizard#addPages
	 */	
	@Override
	public void addPages() {
		super.addPages();
		fPage= new NewSourceFolderWizardPage();
		addPage(fPage);
		fPage.init(getSelection());
	}			

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createSourceRoot(monitor); // use the full progress monitor
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean res= super.performFinish();
		if (res) {
			selectAndReveal(fPage.getCorrespondingResource());
		}
		return res;
	}
		
}
