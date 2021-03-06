/*******************************************************************************
 * Copyright (c) 2000, 2008 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Sergey Prigogin, Google
 *     Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CCorePreferenceConstants;
import org.eclipse.cdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.cdt.core.parser.CodeReaderCache;
import org.eclipse.cdt.internal.core.model.CModelManager;
import org.eclipse.cdt.internal.core.pdom.indexer.IndexerPreferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;


public class CCorePreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
        HashSet optionNames = CModelManager.OptionNames;
    
		// Formatter settings
		Map defaultOptionsMap = DefaultCodeFormatterConstants.getDefaultSettings(); // code formatter defaults

		// Compiler settings
		defaultOptionsMap.put(CCorePreferenceConstants.TODO_TASK_TAGS, CCorePreferenceConstants.DEFAULT_TASK_TAG); 
		defaultOptionsMap.put(CCorePreferenceConstants.TODO_TASK_PRIORITIES, CCorePreferenceConstants.DEFAULT_TASK_PRIORITY); 
		defaultOptionsMap.put(CCorePreferenceConstants.TODO_TASK_CASE_SENSITIVE, CCorePreferenceConstants.DEFAULT_TASK_CASE_SENSITIVE); 
		defaultOptionsMap.put(CCorePreferenceConstants.CODE_FORMATTER, CCorePreferenceConstants.DEFAULT_CODE_FORMATTER); 
		defaultOptionsMap.put(CCorePreferenceConstants.INDEX_DB_CACHE_SIZE_PCT, CCorePreferenceConstants.DEFAULT_INDEX_DB_CACHE_SIZE_PCT); 
		defaultOptionsMap.put(CCorePreferenceConstants.MAX_INDEX_DB_CACHE_SIZE_MB, CCorePreferenceConstants.DEFAULT_MAX_INDEX_DB_CACHE_SIZE_MB); 
		defaultOptionsMap.put(CCorePreferenceConstants.WORKSPACE_LANGUAGE_MAPPINGS, CCorePreferenceConstants.DEFAULT_WORKSPACE_LANGUAGE_MAPPINGS);
		defaultOptionsMap.put(CodeReaderCache.CODE_READER_BUFFER, CodeReaderCache.DEFAULT_CACHE_SIZE_IN_MB_STRING);

		// Store default values to default preferences
	 	IEclipsePreferences defaultPreferences = ((IScopeContext) new DefaultScope()).getNode(CCorePlugin.PLUGIN_ID);
		for (Iterator iter = defaultOptionsMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			String optionName = (String) entry.getKey();
			defaultPreferences.put(optionName, (String)entry.getValue());
			optionNames.add(optionName);
		}

		defaultPreferences.putBoolean(CCorePreferenceConstants.SHOW_SOURCE_FILES_IN_BINARIES, true);
		defaultPreferences.putBoolean(CCorePlugin.PREF_USE_STRUCTURAL_PARSE_MODE, false);

		// indexer defaults
		IndexerPreferences.initializeDefaultPreferences(defaultPreferences);
	}
}
