package it.unibz.inf.ontouml.vp.listeners;

import java.time.ZonedDateTime;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.controllers.GitHubAccessController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GitHubRelease;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class ProjectListener implements IProjectListener {

	private ProjectModelListener projectModelListener;
	private ProjectDiagramListener projectDiagramListener;

	public ProjectListener() {
		projectModelListener = new ProjectModelListener();
		projectDiagramListener = new ProjectDiagramListener();
		addListeners();
		StereotypeUtils.generate();
	}

	private void addListeners() {
		try {
			final IProject project = ApplicationManager.instance()
					.getProjectManager().getProject();

			project.addProjectModelListener(projectModelListener);
			project.addProjectDiagramListener(projectDiagramListener);

			projectModelListener.addListenersToModelElements();
			projectDiagramListener.addListenersToDiagrams();
		} catch (Exception e) {
			System.err.println("An error ocurred while adding listeners to the project.");
			e.printStackTrace();
		}
	}
	
	private void checkUpdates() {
		try {
			Configurations config = Configurations.getInstance();
			String lastUpdateCheck = config.getLastUpdatesCheck();
			ZonedDateTime lastCheck = ZonedDateTime.parse(lastUpdateCheck);
			
			// TODO: confirm plusDays() before PR
			if(lastCheck != null && lastCheck.plusDays(1).isBefore(ZonedDateTime.now())) {
				GitHubAccessController.lookupUpdates();
				List<GitHubRelease> releases = config.getReleases();
				GitHubRelease latestRelease = config.getLatestRelease();
				boolean upToDate = false;
				
				for (GitHubRelease release : releases) {
					if(
						release.getTagName().equals(OntoUMLPlugin.PLUGIN_VERSION_RELEASE)
						&& release.getCreatedAt().isBefore(latestRelease.getCreatedAt())
					) {
						upToDate = true;
					}
				}
				
				if(!upToDate) {
					System.out.println("New updates are available.");
					ViewUtils.simpleLog("New updates are available. Go to \"Update Plugin\" to get the latest version of the OntoUML Plugin for Visual Paradigm.");
				} else {
					System.out.println("No new updates available.");
					ViewUtils.simpleLog("Your OntoUML Plugin for Visual Paradigm is up to date with our latest release.");					
				}
			} else {
				System.out.println("Last check for updates was already performed in the last 24 hours.");
			}
			
			config.setLastUpdatesCheck(ZonedDateTime.now().toString());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void projectAfterOpened(IProject project) {
		try {
			if(project == null) { return ; }
			
			addListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void projectNewed(IProject project) {
		try {
			if(project == null) { return ; }
			
			addListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void projectOpened(IProject project) {
		checkUpdates();
	}

	@Override
	public void projectPreSave(IProject project) {}

	@Override
	public void projectRenamed(IProject project) {}

	@Override
	public void projectSaved(IProject project) {
	}

}
