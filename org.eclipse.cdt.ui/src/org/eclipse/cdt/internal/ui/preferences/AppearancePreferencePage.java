/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Anton Leherbauer (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.preferences;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.PreferenceConstants;

import org.eclipse.cdt.internal.ui.ICHelpContextIds;
import org.eclipse.cdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.cdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.Separator;

public class AppearancePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private SelectionButtonDialogField fShowTUChildren;
	private SelectionButtonDialogField fOutlineGroupIncludes;
	private SelectionButtonDialogField fOutlineGroupNamespaces;
	private SelectionButtonDialogField fCViewGroupIncludes;
	private SelectionButtonDialogField fCViewSeparateHeaderAndSource;
	
	public AppearancePreferencePage() {
		setPreferenceStore(PreferenceConstants.getPreferenceStore());
		setDescription(PreferencesMessages.AppearancePreferencePage_description); 
	
		IDialogFieldListener listener= new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				doDialogFieldChanged(field);
			}
		};

		fShowTUChildren= new SelectionButtonDialogField(SWT.CHECK);
		fShowTUChildren.setDialogFieldListener(listener);
		fShowTUChildren.setLabelText(PreferencesMessages.AppearancePreferencePage_showTUChildren_label); 

		fOutlineGroupIncludes= new SelectionButtonDialogField(SWT.CHECK);
		fOutlineGroupIncludes.setDialogFieldListener(listener);
		fOutlineGroupIncludes.setLabelText(PreferencesMessages.AppearancePreferencePage_outlineGroupIncludes_label); 

		fOutlineGroupNamespaces= new SelectionButtonDialogField(SWT.CHECK);
		fOutlineGroupNamespaces.setDialogFieldListener(listener);
		fOutlineGroupNamespaces.setLabelText(PreferencesMessages.AppearancePreferencePage_outlineGroupNamespaces_label); 

		fCViewGroupIncludes= new SelectionButtonDialogField(SWT.CHECK);
		fCViewGroupIncludes.setDialogFieldListener(listener);
		fCViewGroupIncludes.setLabelText(PreferencesMessages.AppearancePreferencePage_cviewGroupIncludes_label); 
		
		fCViewSeparateHeaderAndSource= new SelectionButtonDialogField(SWT.CHECK);
		fCViewSeparateHeaderAndSource.setDialogFieldListener(listener);
		fCViewSeparateHeaderAndSource.setLabelText(PreferencesMessages.AppearancePreferencePage_cviewSeparateHeaderAndSource_label); 
	}

	private void initFields() {
		IPreferenceStore prefs= getPreferenceStore();
		fShowTUChildren.setSelection(prefs.getBoolean(PreferenceConstants.PREF_SHOW_CU_CHILDREN));
		fCViewGroupIncludes.setSelection(prefs.getBoolean(PreferenceConstants.CVIEW_GROUP_INCLUDES));
		fCViewSeparateHeaderAndSource.setSelection(prefs.getBoolean(PreferenceConstants.CVIEW_SEPARATE_HEADER_AND_SOURCE));
		fOutlineGroupIncludes.setSelection(prefs.getBoolean(PreferenceConstants.OUTLINE_GROUP_INCLUDES));
		fOutlineGroupNamespaces.setSelection(prefs.getBoolean(PreferenceConstants.OUTLINE_GROUP_NAMESPACES));
	}
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), ICHelpContextIds.APPEARANCE_PREFERENCE_PAGE);
	}	

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		int nColumns= 1;
				
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.numColumns= nColumns;
		result.setLayout(layout);
				
		fShowTUChildren.doFillIntoGrid(result, nColumns);
		fCViewGroupIncludes.doFillIntoGrid(result, nColumns);
		fOutlineGroupIncludes.doFillIntoGrid(result, nColumns);
		fOutlineGroupNamespaces.doFillIntoGrid(result, nColumns);

		new Separator().doFillIntoGrid(result, nColumns);
		
		fCViewSeparateHeaderAndSource.doFillIntoGrid(result, nColumns);
		
		String noteTitle= PreferencesMessages.AppearancePreferencePage_note;
		String noteMessage= PreferencesMessages.AppearancePreferencePage_preferenceOnlyForNewViews; 
		Composite noteControl= createNoteComposite(JFaceResources.getDialogFont(), result, noteTitle, noteMessage);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		noteControl.setLayoutData(gd);
		
		initFields();
		
		Dialog.applyDialogFont(result);
		return result;
	}
	
	void doDialogFieldChanged(DialogField field) {	
		updateStatus(getValidationStatus());
	}
	
	private IStatus getValidationStatus(){
		return new StatusInfo();
	}
	
	private void updateStatus(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}		
	
	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	/*
	 * @see IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		IPreferenceStore prefs= getPreferenceStore();
		prefs.setValue(PreferenceConstants.PREF_SHOW_CU_CHILDREN, fShowTUChildren.isSelected());
		prefs.setValue(PreferenceConstants.CVIEW_GROUP_INCLUDES, fCViewGroupIncludes.isSelected());
		prefs.setValue(PreferenceConstants.CVIEW_SEPARATE_HEADER_AND_SOURCE, fCViewSeparateHeaderAndSource.isSelected());
		prefs.setValue(PreferenceConstants.OUTLINE_GROUP_INCLUDES, fOutlineGroupIncludes.isSelected());
		prefs.setValue(PreferenceConstants.OUTLINE_GROUP_NAMESPACES, fOutlineGroupNamespaces.isSelected());
		CUIPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}	
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		IPreferenceStore prefs= getPreferenceStore();
		fShowTUChildren.setSelection(prefs.getDefaultBoolean(PreferenceConstants.PREF_SHOW_CU_CHILDREN));
		fCViewGroupIncludes.setSelection(prefs.getDefaultBoolean(PreferenceConstants.CVIEW_GROUP_INCLUDES));
		fCViewSeparateHeaderAndSource.setSelection(prefs.getDefaultBoolean(PreferenceConstants.CVIEW_SEPARATE_HEADER_AND_SOURCE));
		fOutlineGroupIncludes.setSelection(prefs.getDefaultBoolean(PreferenceConstants.OUTLINE_GROUP_INCLUDES));
		fOutlineGroupNamespaces.setSelection(prefs.getDefaultBoolean(PreferenceConstants.OUTLINE_GROUP_NAMESPACES));
		super.performDefaults();
	}
}

