/*******************************************************************************
 * Copyright (c) 2007 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intel Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.core.settings.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.settings.model.util.IKindBasedInfo;
import org.eclipse.cdt.core.settings.model.util.KindBasedStore;

class EntriesNamesContainer {
//	private String fLanguageSettingsId;
	private KindBasedStore fRemovedEntryNamesStore = new KindBasedStore();
	
//	EntriesNamesContainer(ICLanguageSetting setting) {
//		fLanguageSettingsId = setting.getId();
//	}
	
	public EntriesNamesContainer() {
		// TODO Auto-generated constructor stub
	}

	public EntriesNamesContainer(EntriesNamesContainer base) {
//		fLanguageSettingsId = base.fLanguageSettingsId;
		IKindBasedInfo infos[] = base.fRemovedEntryNamesStore.getContents();
		for(int i = 0; i < infos.length; i++){
			Set set = (Set)infos[i].getInfo();
			if(set != null)
				fRemovedEntryNamesStore.put(infos[i].getKind(), new HashSet(set));
		}
	}

//	public String getLanguageSettingId(){
//		return fLanguageSettingsId;
//o	}

	private Set getRemovedNamesSet(int kind, boolean create){
		Set set = (Set)fRemovedEntryNamesStore.get(kind);
		if(set == null && create){
			set = new HashSet();
			fRemovedEntryNamesStore.put(kind, set);
		}
		return set;
	}
	
	public void clear(){
		fRemovedEntryNamesStore.clear();
	}
	
	public void clear(int kind){
		fRemovedEntryNamesStore.put(kind, null);
	}
	
	public boolean contains(int kind, String name){
		Set set = getRemovedNamesSet(kind, false);
		if(set != null)
			return set.contains(name);
		return false;
	}
	
	public boolean add(int kind, String name){
		return getRemovedNamesSet(kind, true).add(name);
	}

	public boolean remove(int kind, String name){
		Set set = getRemovedNamesSet(kind, false);
		if(set != null)
			return set.remove(name);
		return false;
	}

	public void set(int kind, String names[]){
		if(names == null || names.length == 0) {
			clear(kind);
		} else {
			Set set = getRemovedNamesSet(kind, true);
			set.clear();
			add(set, names);
		}
	}

	private static void add(Set set, String names[]){
		for(int i = 0; i < names.length; i++){
			set.add(names[i]);
		}
	}

	public void add(int kind, String names[]){
		if(names == null || names.length == 0) {
			return;
		} else {
			Set set = getRemovedNamesSet(kind, true);
			add(set, names);
		}
	}

}
