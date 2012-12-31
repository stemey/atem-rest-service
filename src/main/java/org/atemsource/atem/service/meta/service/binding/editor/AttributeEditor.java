package org.atemsource.atem.service.meta.service.binding.editor;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.utility.path.AttributePath;

public class AttributeEditor {
	private String code;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	private String editor;
}
