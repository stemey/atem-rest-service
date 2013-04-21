package org.atemsource.atem.service.entity;

import java.util.List;

import org.atemsource.atem.api.type.EntityType;

public interface SingletonService extends StatefulUpdateService{

	List<String> getIds(EntityType<?> originalType);

}
