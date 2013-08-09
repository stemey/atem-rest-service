atem-rest-service
=================

A java library that helps creating rest services for entities and their meta data.


## General


Atem-api is a reflection api that provides a unified api to access the information needed by a lot of mapping libraries like jpa and others.
It also provides a unified api to access meta data like annotation, javadoc and others. Combining these atem-rest provides meta information in json to create forms 
for atem types. 

Atem-rest also provides a generic rest service for CRUD operations and others on atem entities. The type specific handling of these operations 
is defined in Atem services, which can be attached to atem types. 

Examples for Service interfaces:

* FindByIdService: find an entity by its id
* StatefulUpdateService: update an exisintg entity
* IdentityAttributeService: get the id of a given entity
* PersistenceService: persist a new entity
* DeletionService: delete an entity
* FindByTypeService: find entities of a certain type. You may also specify a query, paging and sorting.


These Interfaces are implemented for 

* Jpa Entities
* Spring Bean

