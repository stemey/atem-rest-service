/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.service.entity;

import java.util.Collection;
import java.util.List;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.EntityRestService.Result;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.entity.search.Sorting;

/**
 * The Interface FindByTypeService.
 */
public interface FindByTypeService {

	/**
	 * get entities
	 * 
	 * @param entityType
	 * @param query
	 * @param sorting
	 * @param paging
	 * @param listCallback
	 * @return
	 */
	public <O> Result getEntities(EntityType<O> entityType, Query query,
			Sorting sorting, Paging paging, ListCallback<O> listCallback);

	/**
	 * get the queryable fields
	 * 
	 * @param entityType
	 * @return
	 */
	public <E> Collection<String> getQueryableFields(EntityType<E> entityType);
}
