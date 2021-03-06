/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Anton Leherbauer (Wind River Systems) - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.text.c.hover;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.cdt.internal.ui.text.AbstractSourceViewerInformationControl;

/**
 * Information control for macro expansion.
 *
 * @since 5.0
 */
public class CMacroExpansionControl extends AbstractSourceViewerInformationControl {

	private CMacroExpansionInput fInput;

	/**
	 * Creates a new control for use as a hover which does not take the focus.
	 * 
	 * @param parent  parent shell
	 * @param statusFieldText  text to be displayed in the status field, may be <code>null</code>
	 */
	public CMacroExpansionControl(Shell parent, String statusFieldText) {
		super(parent, PopupDialog.HOVER_SHELLSTYLE, SWT.NONE, false, false, false, statusFieldText);
		setTitleText(CHoverMessages.CMacroExpansionControl_title_macroExpansion);
	}

	/**
	 * Creates a new control for use as a hover which optionally takes the focus.
	 * 
	 * @param parent  parent shell
	 * @param takeFocus  whether this control should take the focus
	 */
	public CMacroExpansionControl(Shell parent, boolean takeFocus) {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, SWT.NONE, takeFocus, false, false, null);
		setTitleText(CHoverMessages.CMacroExpansionControl_title_macroExpansion);
	}

	/*
	 * @see org.eclipse.cdt.internal.ui.text.AbstractSourceViewerInformationControl#hasHeader()
	 */
	@Override
	protected boolean hasHeader() {
		return true;
	}

	/*
	 * @see org.eclipse.cdt.internal.ui.text.AbstractSourceViewerInformationControl#getId()
	 */
	@Override
	protected String getId() {
		return "org.eclipse.cdt.ui.text.hover.CMacroExpansion"; //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.cdt.internal.ui.text.AbstractSourceViewerInformationControl#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input instanceof CMacroExpansionInput) {
			CMacroExpansionInput macroExpansionInput= (CMacroExpansionInput) input;
			setInformation(macroExpansionInput.fExplorer.getFullExpansion().getCodeAfterStep());
			fInput= macroExpansionInput;
		} else {
			super.setInput(input);
		}
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#getInformationPresenterControlCreator()
	 */
	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				if (fInput != null && fInput.fExplorer.getExpansionStepCount() > 1) {
					return new CMacroExpansionExplorationControl(parent);
				} else {
					return new CMacroExpansionControl(parent, true);
				}
			}
		};
	}
}
