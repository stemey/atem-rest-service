/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.service.entity;

import java.io.Serializable;

import org.atemsource.atem.api.type.EntityType;


/**
 * The Interface FindByIdService.
 */
public interface FindByIdService
{

	/**
	 * Find entity typed id.
	 * 
	 * @param entityType the entity type
	 * @param id the id
	 * @return the object
	 */
	public <O,T> T findById(EntityType<O> entityType, Serializable id, SingleCallback<O,T> singleCallback);
}
