package org.atemsource.atem.service.meta.service.model.method;

import org.atemsource.atem.api.attribute.annotation.Association;

public class Param {

	private static final long serialVersionUID = -2129097756291034471L;

	private boolean array;

	private String code;

	private String description;

	private String editor;

	private String label;

	private boolean required;

	private String type;

	private boolean writable;

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getEditor() {
		return editor;
	}

	public String getLabel() {
		return label;
	}

	public boolean isArray() {
		return array;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public void setLabel(String name) {
		this.label = name;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public static class Values {
		@Association(targetType = String.class)
		private String[] elements;

		public String[] getElements() {
			return elements;
		}

		public void setElements(String[] elements) {
			this.elements = elements;
		}
	}
}
