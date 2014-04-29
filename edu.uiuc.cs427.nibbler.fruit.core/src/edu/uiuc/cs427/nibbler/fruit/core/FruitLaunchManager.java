package edu.uiuc.cs427.nibbler.fruit.core;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.cdt.core.model.*;
import org.eclipse.cdt.managedbuilder.core.*;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.ui.properties.Messages;
import org.eclipse.cdt.core.settings.model.*;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import edu.uiuc.cs427.nibbler.fruit.core.driverGenerator.FruitGen;

public class FruitLaunchManager {

	IProject project;
	
	public FruitLaunchManager( IProject project )
	{
		this.project = project;
	}
	
	public void generateSkeleton(IProgressMonitor monitor, IFile moduleUnderTestFile, String moduleUnderTestName)
	{	
		createSkeletonFiles( monitor, moduleUnderTestFile, moduleUnderTestName );

		updateFRUITConfiguration( ManagedBuildManager.getBuildInfo(project).getDefaultConfiguration() );
		updateBuildConfigurationsForNewTest( moduleUnderTestName );
	}
	
	private void createSkeletonFiles( IProgressMonitor monitor, IFile moduleUnderTestFile, String moduleUnderTestName )
	{
		IFile fruitTestFile = project.getFile( moduleUnderTestName + "_test.f90" );
		IFile fruitDriverFile = project.getFile( "fruit_test_driver.f90" );
		IFile fruitBasketFile = project.getFile( "fruit_basket_gen.f90" );
		
		try
		{
			InputStream stream1 = new ByteArrayInputStream ( testSkeleton(moduleUnderTestName).toString().getBytes());
			InputStream stream2 = new ByteArrayInputStream ( driverSkeleton().toString().getBytes());
			InputStream stream3 = new ByteArrayInputStream ( driverSkeleton().toString().getBytes());
			
			if ( !fruitTestFile.exists() )
			{
				fruitTestFile.create( stream1, true, monitor );
			}
			
			if ( !fruitDriverFile.exists() )
			{
				fruitDriverFile.create( stream2, true, monitor );
			}
			
			if ( !fruitBasketFile.exists() )
			{
				fruitBasketFile.create( stream3, true, monitor );
			}
		}
		catch ( CoreException e )
		{
			
		}
	}
		
	private StringBuffer testSkeleton(String moduleUnderTestName)
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( "! This file is an example FRUIT Test Case file, update with relevant tests !\n\n" );
		buf.append( "module " + moduleUnderTestName + "_test\n");
		buf.append( "   use fruit\n\n");
		buf.append( "contains\n\n");
		
		buf.append( "   ! setup_before_all\n");
		buf.append( "   ! setup = setup_before_each\n");
		buf.append( "   subroutine setup\n");
		buf.append( "   end subroutine setup\n\n");
		
		buf.append( "   ! teardown_before_all\n");
		buf.append( "   ! teardown = teardown_before_each\n");
		buf.append( "   subroutine teardown\n");
		buf.append( "   end subroutine teardown\n\n");
		  
		buf.append( "   subroutine test_example1\n");
		buf.append( "      use " + moduleUnderTestName + " only: function_name\n\n");
		buf.append( "   end subroutine test_example1\n\n");
		
		buf.append( "end module " + moduleUnderTestName + "_test\n");
		
		return buf;
	}
	
	private StringBuffer driverSkeleton()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( "! Warning, this file is generated automatically and should not be edited!\n\n" );
		
		return buf;
	}
	
	private void addExcludeFromBuildEntry( IConfiguration config, IPath path )
	{
		int numEntries;
		
		ICSourceEntry[] sourceEntries = config.getSourceEntries();

		numEntries = sourceEntries[0].getExclusionPatterns().length;
		IPath test[] = new IPath[numEntries + 1];
		
		if ( numEntries > 0 )
		{
			System.arraycopy(sourceEntries[0].getExclusionPatterns(), 0, test, 0, numEntries);
		}
		
		test[numEntries] = path;
		sourceEntries[0] = new CSourceEntry("", test, 24);
		config.setSourceEntries(sourceEntries);
	}
		
	private void updateBuildConfigurationsForNewTest(String moduleUnderTestName)
	{		
		IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
		IConfiguration activeConfig = buildInfo.getDefaultConfiguration();

		if ( activeConfig.getSourceEntries()[0].getExclusionPatterns().length == 0 )
		{
			// Remove driver files from other configs
			addExcludeFromBuildEntry( activeConfig,
					  project.getFile( "fruit_test_driver.f90" ).getProjectRelativePath() );
	
			addExcludeFromBuildEntry( activeConfig,
					  project.getFile( "fruit_basket_gen.f90" ).getProjectRelativePath() );
		}
		
		addExcludeFromBuildEntry( activeConfig,
								  project.getFile( moduleUnderTestName + "_test.f90" ).getProjectRelativePath() );
		
		// change things in the buildInfo
		ManagedBuildManager.saveBuildInfo(project, true);
	}
	
	private void updateFRUITConfiguration(IConfiguration parentConfig) {
		boolean configAlreadyExists = false;
		
		ICProjectDescription des = CoreModel.getDefault().getProjectDescription(project);
		ICConfigurationDescription[] allConfigs = des.getConfigurations();
		
		for ( int i = 0; i < allConfigs.length; i++ )
		{
			if ( allConfigs[i].getName().equals("FRUIT") )
			{
				configAlreadyExists = true;
			}
		}
		
		if ( configAlreadyExists == false )
		{			
			// Remove main.f90 from FRUIT Config
			Configuration cfg = (Configuration)parentConfig;
			String id = ManagedBuildManager.calculateChildId(cfg.getId(), null);
			ManagedProject mp = (ManagedProject)ManagedBuildManager.getBuildInfo(des.getProject()).getManagedProject();
			Configuration config = new Configuration(mp, cfg, id, true, false);
			config.setName("FRUIT");
			
			String target = config.getArtifactName();
			if (target == null || target.length() == 0)
				config.setArtifactName(mp.getDefaultArtifactName());
	
			CConfigurationData data = config.getConfigurationData();
			
			IFile mainFile = project.getFile("main.f90");
			ICSourceEntry[] sourceEntries = data.getSourceEntries();
			IPath test[] = new IPath[1];
			test[0] = mainFile.getProjectRelativePath();
			sourceEntries[0] = new CSourceEntry("", test, 24);
			data.setSourceEntries(sourceEntries);
			
			try {
				des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
			} catch (CoreException e) {
				System.out.println(Messages.getString("NewBuildConfigurationDialog.0")); //$NON-NLS-1$
				System.out.println(Messages.getString("NewBuildConfigurationDialog.1") + e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	private void updateDriver()
	{
		FruitGen fruitGenerator = new FruitGen();
		
		fruitGenerator.generateFruitDriver(project);		
	}
	
	public void updateAndLaunchDriver(String mode) 
	{
		updateDriver();
		
		ICConfigurationDescription active = makeConfigurationActive("FRUIT");
		
		buildConfiguration(active);

		ILaunchConfigurationType cType = getFortranLaunchConfigType();
		addFRUITLaunchConfiguration(cType);
				
		launchConfiguration(cType, "FRUIT", "run");
	}

	private void launchConfiguration(ILaunchConfigurationType cType, String name, String mode) 
	{
		try
		{
			ILaunchConfiguration[] config = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(cType);
		
			for ( int i = 0; i < config.length; i++ )
			{
				if ( config[i].getName().contains(name))
				{
					DebugUITools.launch(config[i], mode);					
					break;
				}
			}
		}
		catch (CoreException e)
		{
		}
		
	}

	private ILaunchConfigurationType getFortranLaunchConfigType() 
	{
		ILaunchConfigurationType cType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.photran.launch.localCLaunch");
		return cType;
	}

	private void addFRUITLaunchConfiguration(ILaunchConfigurationType cType) 
	{
		try
		{
			ILaunchConfigurationWorkingCopy wc = cType.newInstance(project, "FRUIT Tests");
			wc.setAttribute("org.eclipse.cdt.launch.PROGRAM_NAME", "FRUIT/" + project.getName());
			wc.setAttribute("org.eclipse.cdt.launch.PROJECT_ATTR", project.getName());
			wc.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE", "${workspace_loc:/Test2/FRUIT/fruit_output.log}");
			wc.doSave();
			
			IFile fruitOutputFile = project.getFile( "FRUIT/fruit_output.log" );
		
			if ( !fruitOutputFile.exists() )
			{
				fruitOutputFile.create( null, true, new NullProgressMonitor());
			}
		}
		catch ( Exception e )
		{
		}
	}

	private void buildConfiguration(ICConfigurationDescription config)
	{
		try
		{
			IConfiguration[] configs = new IConfiguration[1];
			configs[0] = ManagedBuildManager.getConfigurationForDescription(config);
	
			ManagedBuildManager.buildConfigurations(configs, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			
		}
	}

	private ICConfigurationDescription makeConfigurationActive(String name) {
		ICProjectDescription des = CoreModel.getDefault().getProjectDescription(project);
		ICConfigurationDescription desc[] = des.getConfigurations();
		ICConfigurationDescription newActive = desc[0];
		
		for ( int i = 0; i < desc.length; i++ )
		{
			if (desc[i].getConfigurationData().getName().equals(name))
			{
				des.setActiveConfiguration(desc[i]);
				newActive = desc[i];
			}
		}
		return newActive;
	}
}
