/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.cdt.internal.core.indexer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.internal.core.index.IndexFileLocation;
import org.eclipse.cdt.internal.core.pdom.IndexerInputAdapter;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Provides information about the file paths represented as strings.
 * @since 5.0
 */
public class StandaloneIndexerInputAdapter extends IndexerInputAdapter {
	private HashMap<String, IIndexFileLocation> fIflCache= new HashMap<String, IIndexFileLocation>();

	private final StandaloneIndexer fIndexer;

	public StandaloneIndexerInputAdapter(StandaloneIndexer indexer) {
		fIndexer= indexer;
	}
	
	@Override
	public IScannerInfo getBuildConfiguration(int linkageID, Object tu) {
		return fIndexer.getScannerInfo();
	}

	@Override
	public long getLastModified(IIndexFileLocation location) {
		return new File(location.getFullPath()).lastModified();
	}

	@Override
	public boolean isSourceUnit(Object tu) {
		return isValidSourceUnitName((String) tu);
	}

	@Override
	public IIndexFileLocation resolveFile(Object tu) {
		return resolveASTPath((String) tu);
	}

	@Override
	public String getASTPath(IIndexFileLocation ifl) {
		return ifl.getFullPath();
	}

	@Override
	public IIndexFileLocation resolveASTPath(String astPath) {
		IIndexFileLocation result= fIflCache.get(astPath);
		if (result == null) {
			try {
				astPath = new File(astPath).getCanonicalPath();
			} catch (IOException e) {
				// use the original
			}
			//Standalone indexing stores the absolute paths of files being indexed
			result = new IndexFileLocation(URIUtil.toURI(astPath),astPath);
			fIflCache.put(astPath, result);
		}
		return result;
	}

	
	@Override
	public boolean doesIncludeFileExist(String includePath) {
		return new File(includePath).exists();
	}

	@Override
	public IIndexFileLocation resolveIncludeFile(String includePath) {		
		IIndexFileLocation result= fIflCache.get(includePath);
		if (result == null) {
			File file= new File(includePath);
			try {
				includePath = file.getCanonicalPath();
			} catch (IOException e) {
				// use the original
			}
			//Standalone indexing stores the absolute paths of files being indexed
			result = new IndexFileLocation(URIUtil.toURI(includePath),includePath);
			fIflCache.put(includePath, result);
		}
		return result;
	}

	@Override
	public boolean isFileBuildConfigured(Object tu) {
		return isValidSourceUnitName((String) tu);
	}

	@Override
	public boolean canBePartOfSDK(IIndexFileLocation ifl) {
		return false;
	}

	@Override
	public CodeReader getCodeReader(Object tu) {
		try {
			return new CodeReader((String) tu);
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public Object getInputFile(IIndexFileLocation location) {
		return location.getFullPath();
	}

	@Override
	public AbstractLanguage[] getLanguages(Object tu) {
		ILanguage language = fIndexer.getLanguageMapper().getLanguage((String) tu);
		if (language instanceof AbstractLanguage) {
			return new AbstractLanguage[] {(AbstractLanguage) language};
		}
		return new AbstractLanguage[0];
	}

	private boolean isValidSourceUnitName(String file) {
		IPath path = new Path(file);
		if (fIndexer.getValidSourceUnitNames() == null || fIndexer.getValidSourceUnitNames().size() == 0)
			return true;
		return fIndexer.getValidSourceUnitNames().contains(path.getFileExtension());
	}
}
