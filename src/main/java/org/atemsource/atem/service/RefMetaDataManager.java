package org.atemsource.atem.service;


public interface RefMetaDataManager {
	public String getIdAttribute(String originalTargetTypeCode);

	public String getSearchAttribute(String originalTargetTypeCode);

	public String getSearchUri(String originalTargetTypeCode);

	public String getSchemaUri(String originalTargetTypeCode);
}
