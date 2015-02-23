// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;
import org.openstreetmap.osmosis.geojson.common.geojsonTaskManagerFactory;
import org.openstreetmap.osmosis.geojson.common.CompressionMethod;

import java.io.File;


/**
 * The task manager factory for an xml writer.
 * 
 * @author Brett Henderson
 */
public class geojsonWriterFactory extends geojsonTaskManagerFactory {
	private static final String ARG_FILE_NAME = "file";
	private static final String DEFAULT_FILE_NAME = "dump.geojson";

    private static final String ARG_PRETTY_OUTPUT = "pretty";
    private static final boolean DEFAULT_PRETTY_OUTPUT = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
		String fileName;
		File file;
		geojsonWriter task;
        CompressionMethod compressionMethod;
		
		// Get the task arguments.
		fileName = getStringArgument(
			taskConfig,
			ARG_FILE_NAME,
			getDefaultStringArgument(taskConfig, DEFAULT_FILE_NAME)
		);
		compressionMethod = getCompressionMethodArgument(taskConfig, fileName);
		
		// Create a file object from the file name provided.
		file = new File(fileName);

        boolean prettyOutput = getBooleanArgument(taskConfig, ARG_PRETTY_OUTPUT, DEFAULT_PRETTY_OUTPUT);

		// Build the task object.
		task = new geojsonWriter(file, compressionMethod, prettyOutput);
		
		return new SinkManager(taskConfig.getId(), task, taskConfig.getPipeArgs());
	}
}
