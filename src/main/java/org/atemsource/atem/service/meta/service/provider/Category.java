package org.atemsource.atem.service.meta.service.provider;

import java.util.LinkedList;
import java.util.List;

public class Category extends Group {
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
}
