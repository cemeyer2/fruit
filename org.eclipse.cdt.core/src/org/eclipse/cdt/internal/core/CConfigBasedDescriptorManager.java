/*******************************************************************************
 * Copyright (c) 2007 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intel Corporation - Initial API and implementation
 * Anton Leherbauer (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CDescriptorEvent;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.ICDescriptorListener;
import org.eclipse.cdt.core.ICDescriptorManager;
import org.eclipse.cdt.core.ICDescriptorOperation;
import org.eclipse.cdt.core.settings.model.CProjectDescriptionEvent;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICDescriptionDelta;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionListener;
import org.eclipse.cdt.core.settings.model.ICSettingObject;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.internal.core.settings.model.CConfigurationDescriptionCache;
import org.eclipse.cdt.internal.core.settings.model.CConfigurationSpecSettings;
import org.eclipse.cdt.internal.core.settings.model.CProjectDescription;
import org.eclipse.cdt.internal.core.settings.model.CProjectDescriptionManager;
import org.eclipse.cdt.internal.core.settings.model.CStorage;
import org.eclipse.cdt.internal.core.settings.model.ExceptionFactory;
import org.eclipse.cdt.internal.core.settings.model.IInternalCCfgInfo;
import org.eclipse.cdt.internal.core.settings.model.InternalXmlStorageElement;
import org.eclipse.cdt.internal.core.settings.model.PathEntryConfigurationDataProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.w3c.dom.Element;

public class CConfigBasedDescriptorManager implements ICDescriptorManager {
	private static CConfigBasedDescriptorManager fInstance;
	public static final String NULL_OWNER_ID = ""; //$NON-NLS-1$
	private Map fOwnerConfigMap = null;
	private ICProjectDescriptionListener fDescriptionListener;
	
	private static final QualifiedName DESCRIPTOR_PROPERTY = new QualifiedName(CCorePlugin.PLUGIN_ID, "CDescriptor"); //$NON-NLS-1$

	private List fListeners = Collections.synchronizedList(new Vector());
//	private ThreadLocal fApplyingDescriptorMap = new ThreadLocal();
	private ThreadLocal fThreadInfo = new ThreadLocal();

	private class ThreadInfo {
		Map fApplyingDescriptorMap;
		Map fOperatingDescriptorMap;
		
		public Map getApplyingDescriptorMap(boolean create){
			if(fApplyingDescriptorMap == null && create){
				fApplyingDescriptorMap = new HashMap(1);
			}
			return fApplyingDescriptorMap;
		}

		public Map getOperatingDescriptorMap(boolean create){
			if(fOperatingDescriptorMap == null && create){
				fOperatingDescriptorMap = new HashMap(1);
			}
			return fOperatingDescriptorMap;
		}

	}

	private CConfigBasedDescriptorManager(){
	}
	
	public static CConfigBasedDescriptorManager getInstance(){
		if(fInstance == null){
			fInstance = getInstanceSynch();
		}
		return fInstance;
	}

	public static synchronized CConfigBasedDescriptorManager getInstanceSynch(){
		if(fInstance == null){
			fInstance = new CConfigBasedDescriptorManager();
		}
		return fInstance;
	}

	private static final COwnerConfiguration NULLCOwner = new COwnerConfiguration(NULL_OWNER_ID, 
			CCorePlugin.getResourceString("CDescriptorManager.internal_owner")); //$NON-NLS-1$

	public void configure(IProject project, String id) throws CoreException {
		CConfigBasedDescriptor dr;
		if (id.equals(NULLCOwner.getOwnerID())) {
			IStatus status = new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, -1,
					CCorePlugin.getResourceString("CDescriptorManager.exception.invalid_ownerID"), //$NON-NLS-1$
					(Throwable)null);
			throw new CoreException(status);
		}
		synchronized (CProjectDescriptionManager.getInstance()) {
			dr = findDescriptor(project, false);
			if (dr != null) {
				if (dr.getProjectOwner().getID().equals(NULLCOwner.getOwnerID())) {
					// non owned descriptors are simply configure to the new owner no questions ask!
					dr = updateDescriptor(project, dr, id);
					dr.apply(true);
				} else if (!dr.getProjectOwner().getID().equals(id)) {
					IStatus status = new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, CCorePlugin.STATUS_CDTPROJECT_EXISTS,
							CCorePlugin.getResourceString("CDescriptorManager.exception.alreadyConfigured"), //$NON-NLS-1$
							(Throwable)null);
					throw new CoreException(status);
				} else {
					return; // already configured with same owner.
				}
			} else {
//				try {
					dr = findDescriptor(project, true);
					dr = updateDescriptor(project, dr, id);
					dr.apply(true);
//				} catch (CoreException e) { // if .cdtproject already exists we'll use that
//					IStatus status = e.getStatus();
//					if (status.getCode() == CCorePlugin.STATUS_CDTPROJECT_EXISTS) {
//						descriptor = new CDescriptor(this, project);
//					} else
//						throw e;
//				}
			}
		}
		
		if(dr.isOperationStarted())
			dr.setOpEvent(new CDescriptorEvent(dr, CDescriptorEvent.CDTPROJECT_ADDED, 0));
	}
	
	private CConfigBasedDescriptor updateDescriptor(IProject project, CConfigBasedDescriptor dr, String ownerId) throws CoreException{
		ICConfigurationDescription cfgDes = dr.getConfigurationDescription();
		ICProjectDescription projDes = cfgDes.getProjectDescription();
		CConfigurationSpecSettings settings = ((IInternalCCfgInfo)cfgDes).getSpecSettings();
		settings.setCOwner(ownerId);
		COwner owner = settings.getCOwner();
		setLoaddedDescriptor(projDes, null);
		dr = findDescriptor((CProjectDescription)projDes);
		dr.setApplyOnChange(false);
		owner.configure(project, dr);
		dr.setApplyOnChange(true);

		return dr;
	}

	public void convert(IProject project, String id) throws CoreException {
		CConfigBasedDescriptor dr = findDescriptor(project, false);
		if(dr == null){
			throw ExceptionFactory.createCoreException(CCorePlugin.getResourceString("CConfigBasedDescriptorManager.0")); //$NON-NLS-1$
		}
		
		

//		boolean applyOnChange = dr.isApplyOnChange();
//		CProjectDescription des = (CProjectDescription)CProjectDescriptionManager.getInstance().getProjectDescription(project);
//		if(des == null){
//			throw ExceptionFactory.createCoreException("the project is not a CDT project");
//		}
//		ICConfigurationDescription cfgDes = des.getIndexConfiguration();
//		if(cfgDes == null){
//			throw ExceptionFactory.createCoreException("the projecty does not contain valid configurations");
//		}

		synchronized(CProjectDescriptionManager.getInstance()){
			dr = updateDescriptor(project, dr, id);
			dr.apply(true);
		}
		
		if(dr.isOperationStarted())
			dr.setOpEvent(new CDescriptorEvent(dr, CDescriptorEvent.CDTPROJECT_CHANGED, CDescriptorEvent.OWNER_CHANGED));
	}

	public ICDescriptor getDescriptor(IProject project) throws CoreException {
		return getDescriptor(project, true);
	}

	private ILock fInstanceLock = Job.getJobManager().newLock();

	public ICDescriptor getDescriptor(IProject project, boolean create)
			throws CoreException {
		if (create) {
			synchronized (CProjectDescriptionManager.getInstance()) {
				try {
					fInstanceLock.acquire();
					return findDescriptor(project, create);
				} finally {
					fInstanceLock.release();
		 		}
			}
		} else // no need to synchronize in this case.  
			return findDescriptor(project, create);
	}

	public void addDescriptorListener(ICDescriptorListener listener) {
		fListeners.add(listener);
	}

	public void removeDescriptorListener(ICDescriptorListener listener) {
		fListeners.remove(listener);
	}

	public void runDescriptorOperation(IProject project,
			ICDescriptorOperation op, IProgressMonitor monitor)
			throws CoreException {
		CConfigBasedDescriptor dr = findDescriptor(project, true);
		if (dr == null) {
			throw new CoreException(new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, -1, "Failed to create descriptor", null)); //$NON-NLS-1$
		}

		CDescriptorEvent event = null;
		synchronized (CProjectDescriptionManager.getInstance()) {
			boolean initialApplyOnChange = dr.isApplyOnChange();
			dr.setApplyOnChange(false);
			try {
				dr.operationStart();
				op.execute(dr, monitor);
			} finally {
				event = dr.operationStop();
				dr.setApplyOnChange(initialApplyOnChange);
			}

//			dr.apply(false);
		}
		
		if(event != null){
			CConfigBasedDescriptorManager.getInstance().notifyListeners(event);
		}
		
	}

	public void runDescriptorOperation(IProject project,
			ICProjectDescription des,
			ICDescriptorOperation op,
			IProgressMonitor monitor)
				throws CoreException {
		CConfigBasedDescriptor dr = getOperatingDescriptor(project);
		if(dr != null){
			throw new CoreException(new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, -1, CCorePlugin.getResourceString("CConfigBasedDescriptorManager.1"), null)); //$NON-NLS-1$
		}
		
		if(des.isReadOnly()){
			throw new CoreException(new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, -1, CCorePlugin.getResourceString("CConfigBasedDescriptorManager.2"), null)); //$NON-NLS-1$
		}
		
		//create a new descriptor
		dr = loadDescriptor((CProjectDescription)des);
		
		if (dr == null) {
			throw new CoreException(new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, -1, CCorePlugin.getResourceString("CConfigBasedDescriptorManager.3"), null)); //$NON-NLS-1$
		}
		
		setOperatingDescriptor(project, dr);

//		synchronized (CProjectDescriptionManager.getInstance()) {
			dr.setApplyOnChange(false);
			try {
				op.execute(dr, monitor);
				reconsile(dr, des);
			} finally {
				clearOperatingDescriptor(project);
			}
//		}
	}

	private CConfigBasedDescriptor getLoaddedDescriptor(ICProjectDescription des){
		return (CConfigBasedDescriptor)des.getSessionProperty(DESCRIPTOR_PROPERTY);
	}

	private void setLoaddedDescriptor(ICProjectDescription des, CConfigBasedDescriptor dr){
		des.setSessionProperty(DESCRIPTOR_PROPERTY, dr);
	}
	
	private CConfigBasedDescriptor findDescriptor(IProject project, boolean create) throws CoreException{
		CConfigBasedDescriptor dr = getOperatingDescriptor(project);
		if(dr != null)
			return dr;
		
		CProjectDescription des = (CProjectDescription)CProjectDescriptionManager.getInstance().getProjectDescription(project, false);

//		CConfigBasedDescriptor dr = null;
		if(des == null && create){
			des = createProjDescriptionForDescriptor(project);
		}
		if(des != null){
			dr = findDescriptor(des);
		} 
		return dr;
	}

	private CConfigBasedDescriptor findDescriptor(CProjectDescription des) throws CoreException{
		CConfigBasedDescriptor dr = getApplyingDescriptor(des.getProject());
		if(dr != null)
			return dr;
		
		if(des.isApplying()){
			dr = loadDescriptor(des);
			setApplyingDescriptor(des.getProject(), dr);
			return dr;
		}
		
		dr = getLoaddedDescriptor(des);
		if(dr == null){
			dr = loadDescriptor(des);
			if(dr != null){
				setLoaddedDescriptor(des, dr);
			}
		}
		return dr;
	}

	private CProjectDescription createProjDescriptionForDescriptor(final IProject project) throws CoreException{
		final CProjectDescriptionManager mngr = CProjectDescriptionManager.getInstance();
		final CProjectDescription des = (CProjectDescription)mngr.createProjectDescription(project, false, true);
			
		CConfigurationData data = mngr.createDefaultConfigData(project, PathEntryConfigurationDataProvider.getDataFactory());
		des.createConfiguration(CCorePlugin.DEFAULT_PROVIDER_ID, data);
		
//		mngr.runWspModification(new IWorkspaceRunnable() {
//
//			public void run(IProgressMonitor monitor) throws CoreException {
//				if(mngr.getProjectDescription(project, false) == null){
//					mngr.setProjectDescription(project, des);
//				}
//			}
//			
//		}, null);
		return des;
	}

	/*
	 * creates a new descriptor
	 */
	private CConfigBasedDescriptor loadDescriptor(CProjectDescription des) throws CoreException{
		CConfigBasedDescriptor dr = null;

		if(des != null){
			if(des.isReadOnly())
				des = (CProjectDescription)CProjectDescriptionManager.getInstance().getProjectDescription(des.getProject(), true);
			
			ICConfigurationDescription cfgDes = des.getDefaultSettingConfiguration();
			if(cfgDes instanceof CConfigurationDescriptionCache){
				des = (CProjectDescription)CProjectDescriptionManager.getInstance().getProjectDescription(des.getProject(), true);
				cfgDes = des.getDefaultSettingConfiguration();
			}
	
			
			if(cfgDes != null){
				if(cfgDes.isReadOnly())
					throw ExceptionFactory.createCoreException(CCorePlugin.getResourceString("CConfigBasedDescriptorManager.4")); //$NON-NLS-1$
					
				dr = new CConfigBasedDescriptor(cfgDes);
			} else if (!des.isCdtProjectCreating()){
				throw ExceptionFactory.createCoreException(CCorePlugin.getResourceString("CConfigBasedDescriptorManager.5")); //$NON-NLS-1$
			}
		}
		
		return dr;
	}

	public COwnerConfiguration getOwnerConfiguration(String id) {
		if (id.equals(NULLCOwner.getOwnerID())) {
			return NULLCOwner;
		}
		if (fOwnerConfigMap == null) {
			initializeOwnerConfiguration();
		}
		COwnerConfiguration config = (COwnerConfiguration)fOwnerConfigMap.get(id);
		if (config == null) { // no install owner, lets create place holder config for it.
			config = new COwnerConfiguration(id, CCorePlugin.getResourceString("CDescriptorManager.owner_not_Installed")); //$NON-NLS-1$
			fOwnerConfigMap.put(id, config);
		}
		return config;
	}

	private void initializeOwnerConfiguration() {
        IExtensionPoint extpoint = Platform.getExtensionRegistry().getExtensionPoint(CCorePlugin.PLUGIN_ID, "CProject"); //$NON-NLS-1$
		IExtension extension[] = extpoint.getExtensions();
		fOwnerConfigMap = new HashMap(extension.length);
		for (int i = 0; i < extension.length; i++) {
			IConfigurationElement element[] = extension[i].getConfigurationElements();
			for (int j = 0; j < element.length; j++) {
				if (element[j].getName().equalsIgnoreCase("cproject")) { //$NON-NLS-1$
					fOwnerConfigMap.put(extension[i].getUniqueIdentifier(), new COwnerConfiguration(element[j]));
					break;
				}
			}
		}
	}
	
	public void startup(){
		if(fDescriptionListener == null){
			fDescriptionListener = new ICProjectDescriptionListener(){

				public void handleEvent(CProjectDescriptionEvent event) {
					doHandleEvent(event);
				}
				
			};
			CProjectDescriptionManager.getInstance().addCProjectDescriptionListener(fDescriptionListener, CProjectDescriptionEvent.APPLIED | CProjectDescriptionEvent.LOADED | CProjectDescriptionEvent.DATA_APPLIED | CProjectDescriptionEvent.ABOUT_TO_APPLY);
		}
	}
	
	public void shutdown(){
		if(fDescriptionListener != null){
			CProjectDescriptionManager.getInstance().removeCProjectDescriptionListener(fDescriptionListener);
		}
	}
	
	private void doHandleEvent(CProjectDescriptionEvent event){
		try {
			switch(event.getEventType()){
			case CProjectDescriptionEvent.LOADED:{
					CProjectDescription des = (CProjectDescription)event.getNewCProjectDescription();
					CConfigBasedDescriptor dr = getLoaddedDescriptor(des);
					if(dr != null){
						//the descriptor was requested while load process
						des = (CProjectDescription)CProjectDescriptionManager.getInstance().getProjectDescription(des.getProject(), true);
						if(des != null){
							ICConfigurationDescription cfgDescription = des.getDefaultSettingConfiguration();
							if(cfgDescription != null){
								dr.updateConfiguration(cfgDescription);
								dr.setDirty(false);
							} else {
								setLoaddedDescriptor(des, null);
							}
						}
					}
				}
				break;
			case CProjectDescriptionEvent.ABOUT_TO_APPLY:{
					CProjectDescription des = (CProjectDescription)event.getNewCProjectDescription();
					if(des != null){
						CConfigBasedDescriptor dr = getLoaddedDescriptor(des);
						if(dr != null 
								&& dr.getConfigurationDescription().getProjectDescription() != des){
							reconsile(dr, des);
						}
					}
				}
				break;
			case CProjectDescriptionEvent.DATA_APPLIED:{
					CProjectDescription des = (CProjectDescription)event.getNewCProjectDescription();
					if(des != null){
						CConfigBasedDescriptor dr = clearApplyingDescriptor(event.getProject());
						if(dr != null){
							reconsile(dr, des);
						}
					}
				}
				break;
			case CProjectDescriptionEvent.APPLIED:
				CProjectDescription newDes = (CProjectDescription)event.getNewCProjectDescription();
				CProjectDescription oldDes = (CProjectDescription)event.getOldCProjectDescription();
				CDescriptorEvent desEvent = null;
				CConfigBasedDescriptor dr = null;
				ICConfigurationDescription updatedCfg = null;
				if(oldDes == null){
					dr = findDescriptor(newDes);
					updatedCfg = newDes.getDefaultSettingConfiguration();
					if(dr != null){
						desEvent = new CDescriptorEvent(dr, CDescriptorEvent.CDTPROJECT_ADDED, 0);
					}
				} else if(newDes == null) {
					dr = findDescriptor(oldDes);
					if(dr != null){
						desEvent = new CDescriptorEvent(dr, CDescriptorEvent.CDTPROJECT_REMOVED, 0);
					}
				} else {
					dr = findDescriptor(newDes);
					updatedCfg = newDes.getDefaultSettingConfiguration();
					if(dr != null){
						ICConfigurationDescription newCfg = newDes.getDefaultSettingConfiguration();
						ICConfigurationDescription oldCfg = oldDes.getDefaultSettingConfiguration();
						int flags = 0;
						if(oldCfg != null && newCfg != null){
							if(newCfg.getId().equals(oldCfg.getId())){
								ICDescriptionDelta cfgDelta = findCfgDelta(event.getProjectDelta(), newCfg.getId());
								if(cfgDelta != null){
									flags = cfgDelta.getChangeFlags() & (ICDescriptionDelta.EXT_REF | ICDescriptionDelta.OWNER);
								}
							} else {
								flags = CProjectDescriptionManager.getInstance().calculateDescriptorFlags(newCfg, oldCfg);
							}
						}
						
						int drEventFlags = descriptionFlagsToDescriptorFlags(flags);
//						if(drEventFlags != 0){
							desEvent = new CDescriptorEvent(dr, CDescriptorEvent.CDTPROJECT_CHANGED, drEventFlags);
//						}
					}
				}
				
				if(updatedCfg != null && dr != null){
					CProjectDescription writableDes = (CProjectDescription)CProjectDescriptionManager.getInstance().getProjectDescription(event.getProject(), true);
					ICConfigurationDescription indexCfg = writableDes.getDefaultSettingConfiguration();
					dr.updateConfiguration(indexCfg);
					dr.setDirty(false);
				}
				if(desEvent != null){
					notifyListeners(desEvent);
				}
				break;
		}
		} catch (CoreException e){
		}
	}
	
	private int descriptionFlagsToDescriptorFlags(int flags){
		int result = 0;
		if((flags & ICDescriptionDelta.EXT_REF) != 0){
			result |= CDescriptorEvent.EXTENSION_CHANGED;
		}
		if((flags & ICDescriptionDelta.OWNER) != 0){
			result |= CDescriptorEvent.OWNER_CHANGED;
		}
		return result;
	}
	private ICDescriptionDelta findCfgDelta(ICDescriptionDelta delta, String id){
		if(delta == null)
			return null;
		ICDescriptionDelta children[] = delta.getChildren();
		for(int i = 0; i < children.length; i++){
			ICSettingObject s = children[i].getNewSetting();
			if(s != null && id.equals(s.getId()))
				return children[i];
		}
		return null;
	}
	
	protected void notifyListeners(final CDescriptorEvent event) {
		final ICDescriptorListener[] listeners;
		synchronized (fListeners) {
			listeners = (ICDescriptorListener[])fListeners.toArray(new ICDescriptorListener[fListeners.size()]);
		}
		for (int i = 0; i < listeners.length; i++) {
			final int index = i;
			SafeRunner.run(new ISafeRunnable() {

				public void handleException(Throwable exception) {
					IStatus status = new Status(IStatus.ERROR, CCorePlugin.PLUGIN_ID, -1,
							CCorePlugin.getResourceString("CDescriptorManager.exception.listenerError"), exception); //$NON-NLS-1$
					CCorePlugin.log(status);
				}

				public void run() throws Exception {
					listeners[index].descriptorChanged(event);
				}
			});
		}
	}
	
	public boolean reconsile(CConfigBasedDescriptor descriptor, ICProjectDescription des){
		Map map = descriptor.getStorageDataElMap();
		boolean reconsiled = false;
		if(map.size() != 0){
			for(Iterator iter = map.entrySet().iterator(); iter.hasNext();){
				Map.Entry entry = (Map.Entry)iter.next();
				String id = (String)entry.getKey();
				Element el = (Element)entry.getValue();
				if(reconsile(id, el.getParentNode() != null ? el : null, des))
					reconsiled = true;
			}
		}
		return reconsiled;
	}
	
	private boolean reconsile(String id, Element el, ICProjectDescription des){
		ICConfigurationDescription cfgs[] = des.getConfigurations();
		boolean reconsiled = false;
		
		for(int i = 0; i < cfgs.length; i++){
			try {
				if(reconsile(id, el, cfgs[i]))
					reconsiled = true;
			} catch (CoreException e) {
				CCorePlugin.log(e);
			}
		}
		
		return reconsiled;
	}
	
	private boolean reconsile(String id, Element el, ICConfigurationDescription cfg) throws CoreException{
		CConfigurationSpecSettings setting = ((IInternalCCfgInfo)cfg).getSpecSettings();
		InternalXmlStorageElement storEl = (InternalXmlStorageElement)setting.getStorage(id, false);
		InternalXmlStorageElement newStorEl = el != null ? CStorage.createStorageElement(el, false) : null;

		boolean modified = false;

		if(storEl != null){
			if(newStorEl == null){
				setting.removeStorage(id);
				modified = true;
			} else {
				if(!newStorEl.matches(storEl)){
					setting.importStorage(id, newStorEl);
					modified = true;
				}
			}
		} else {
			if(newStorEl != null){
				setting.importStorage(id, newStorEl);
				modified = true;
			}
		}
		return modified;
	}
	
	private CConfigBasedDescriptor getApplyingDescriptor(IProject project){
		Map map = getApplyingDescriptorMap(false);
		if(map != null){
			return (CConfigBasedDescriptor)map.get(project);
		}
		return null;
	}

	private void setApplyingDescriptor(IProject project, CConfigBasedDescriptor dr){
		if(dr == null)
			clearApplyingDescriptor(project);
		else {
			Map map = getApplyingDescriptorMap(true);
			map.put(project, dr);
		}
	}

	private CConfigBasedDescriptor clearApplyingDescriptor(IProject project){
		Map map = getApplyingDescriptorMap(false);
		if(map != null){
			return (CConfigBasedDescriptor)map.remove(project);
		}
		return null;
	}

	private Map getApplyingDescriptorMap(boolean create){
		ThreadInfo info = getThreadInfo(create);
		if(info == null)
			return null;
		
		return info.getApplyingDescriptorMap(create);
//		Map map = (Map)fApplyingDescriptorMap.get();
//		if(map == null && create){
//			map = new HashMap(1);
//			fApplyingDescriptorMap.set(map);
//		}
//		return map;
	}
	
	private CConfigBasedDescriptor getOperatingDescriptor(IProject project){
		Map map = getOperatingDescriptorMap(false);
		if(map != null){
			return (CConfigBasedDescriptor)map.get(project);
		}
		return null;
	}

	private void setOperatingDescriptor(IProject project, CConfigBasedDescriptor dr){
		if(dr == null)
			clearOperatingDescriptor(project);
		else {
			Map map = getOperatingDescriptorMap(true);
			map.put(project, dr);
		}
	}

	private CConfigBasedDescriptor clearOperatingDescriptor(IProject project){
		Map map = getOperatingDescriptorMap(false);
		if(map != null){
			return (CConfigBasedDescriptor)map.remove(project);
		}
		return null;
	}
	
	private Map getOperatingDescriptorMap(boolean create){
		ThreadInfo info = getThreadInfo(create);
		if(info == null)
			return null;
		
		return info.getOperatingDescriptorMap(create);
	}

	private ThreadInfo getThreadInfo(boolean create){
		ThreadInfo info = (ThreadInfo)fThreadInfo.get();
		if(info == null && create){
			info = new ThreadInfo();
			fThreadInfo.set(info);
		}
		return info;
	}

}
