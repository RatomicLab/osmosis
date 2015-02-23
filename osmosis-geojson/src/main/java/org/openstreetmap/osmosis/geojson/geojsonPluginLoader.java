// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;
import org.openstreetmap.osmosis.geojson.v0_6.geojsonWriterFactory;

/**
 * The plugin loader for the geojson tasks.
 * 
 * @author Brett Henderson
 */
public class geojsonPluginLoader implements PluginLoader {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, TaskManagerFactory> loadTaskFactories() {
		Map<String, TaskManagerFactory> factoryMap;
		
		factoryMap = new HashMap<String, TaskManagerFactory>();

		factoryMap.put("write-geojson", new geojsonWriterFactory());
        factoryMap.put("wg", new geojsonWriterFactory());
		
		return factoryMap;
	}
}
