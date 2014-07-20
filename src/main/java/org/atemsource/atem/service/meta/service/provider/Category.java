package org.atemsource.atem.service.meta.service.provider;

import java.util.LinkedList;
import java.util.List;

import org.atemsource.atem.service.meta.service.model.Service;

public class Category extends Group {
	
	private String basePath;
	
	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	private List<Group> groups;

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void add(ServiceGroup group) {
		if (groups==null) {
			groups= new LinkedList<Group>();
		}
		groups.add(group);
	}
	
	private List<Service> resources;

	public List<Service> getResources() {
		return resources;
	}

	public void setResources(List<Service> resources) {
		this.resources = resources;
	}
}
