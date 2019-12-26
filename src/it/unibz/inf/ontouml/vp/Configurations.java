package it.unibz.inf.ontouml.vp;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Configurations {
	
	@SerializedName("projects")
	@Expose()
	private List<ProjectConfigurations> projects;
	
	public Configurations() {
		this.projects = new ArrayList<ProjectConfigurations>();
	}
	
	public List<ProjectConfigurations> getProjectsList() {
		return this.projects;
	}
	
	public ProjectConfigurations getProject(String projectId) {
		final List<ProjectConfigurations> projectsList = getProjectsList();
		
		for (ProjectConfigurations project : projectsList) {
			if(project.getId().equals(projectId))
				return project;
		}
		
		return null;
	}
	
	public boolean addProject(ProjectConfigurations project) {
		return getProjectsList().add(project);
	}

}
