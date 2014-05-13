/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sergey Prigogin (Google)
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.text.c.hover;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;

import org.eclipse.ui.IEditorPart;

import org.eclipse.cdt.ui.text.c.hover.ICEditorTextHover;

/**
 * Aggregator of problem and doc hovers.
 * @since 5.0
 */
public class CTypeHover implements ICEditorTextHover, ITextHoverExtension, ITextHoverExtension2 {
	private AbstractCEditorTextHover fProblemHover;
	private AbstractCEditorTextHover fCDocHover;

	private AbstractCEditorTextHover fCurrentHover;

	public CTypeHover() {
		fProblemHover= new ProblemHover();
		fCDocHover= new CDocHover();
		fCurrentHover= null;
	}

	/*
	 * @see ICEditorTextHover#setEditor(IEditorPart)
	 */
	public void setEditor(IEditorPart editor) {
		fProblemHover.setEditor(editor);
		fCDocHover.setEditor(editor);
		fCurrentHover= null;
	}

	/*
	 * @see ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return fCDocHover.getHoverRegion(textViewer, offset);
	}

	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return String.valueOf(getHoverInfo2(textViewer, hoverRegion));
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		Object hoverInfo= fProblemHover.getHoverInfo2(textViewer, hoverRegion);
		if (hoverInfo != null) {
			fCurrentHover= fProblemHover;
			return hoverInfo;
		}

		fCurrentHover= fCDocHover;
		return fCDocHover.getHoverInfo2(textViewer, hoverRegion);
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return fCurrentHover == null ? null : fCurrentHover.getHoverControlCreator();
	}

	/*
	 * @see org.eclipse.jface.text.information.IInformationProviderExtension2#getInformationPresenterControlCreator()
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return fCurrentHover == null ? null : fCurrentHover.getInformationPresenterControlCreator();
	}
}