/*******************************************************************************
 * Copyright (c) 2006, 2007 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    QNX - Initial API and implementation
 *    Markus Schorn (Wind River Systems)
 *******************************************************************************/

package org.eclipse.cdt.internal.core.pdom.indexer;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.model.ITranslationUnit;

/**
 * Configures the abstract indexer to return tasks suitable for fast indexing.
 */
class PDOMFastIndexerTask extends PDOMIndexerTask {
	public PDOMFastIndexerTask(PDOMFastIndexer indexer, ITranslationUnit[] added, 
			ITranslationUnit[] changed, ITranslationUnit[] removed) {
		super(added, changed, removed, indexer, true);
	}

	@Override
	protected ICodeReaderFactory createReaderFactory() {
		return null;
	}
}
