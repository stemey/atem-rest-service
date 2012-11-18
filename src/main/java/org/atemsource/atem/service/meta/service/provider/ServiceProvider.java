package org.atemsource.atem.service.meta.service.provider;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.service.meta.service.model.Service;

public interface ServiceProvider<S extends Service> {

	Set<S> getServices();
}
