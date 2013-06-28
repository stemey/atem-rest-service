/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.service.entity;

import java.util.Collection;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.entity.search.Sorting;


// TODO: Auto-generated Javadoc
/**
 * The Interface FindByTypeService.
 */
public interface FindByTypeService
{

	/**
	 * Gets the entities.
	 * 
	 * @param sorting the sorting
	 * @return the entities
	 */
	public <E> Object getEntities(EntityType<E> entityType, Query query, Sorting sorting, Paging paging,ListCallback<E> listCallback);
	
	public <E> Collection<String> getQueryableFields(EntityType<E> entityType);
}
