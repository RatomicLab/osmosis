// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;
import org.openstreetmap.osmosis.geojson.common.geojsonTaskManagerFactory;
import org.openstreetmap.osmosis.geojson.common.CompressionMethod;
import org.openstreetmap.osmosis.pgsnapshot.common.NodeLocationStoreType;

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

    private static final String ARG_NODE_LOCATION_STORE_TYPE = "nodeLocationStoreType";
    private static final String DEFAULT_NODE_LOCATION_STORE_TYPE = "CompactTempFile";

    private static final String ARG_WAYNODE_LIST = "wayNodeList";
    private static final boolean DEFAULT_WAYNODE_LIST = false;

    private static final String ARG_NODE_IGNORE_TAG = "nodeIgnoreTags"; // Ignore nodes with only those tags
    private static final String DEFAULT_NODE_IGNORE_TAG = "";
	
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
        boolean wayNodeList = getBooleanArgument(taskConfig, ARG_WAYNODE_LIST, DEFAULT_WAYNODE_LIST);

        String nodeIgnoreTags = getStringArgument(taskConfig, ARG_NODE_IGNORE_TAG, DEFAULT_NODE_IGNORE_TAG);

        NodeLocationStoreType storeType = Enum.valueOf(
                NodeLocationStoreType.class,
                getStringArgument(taskConfig, ARG_NODE_LOCATION_STORE_TYPE, DEFAULT_NODE_LOCATION_STORE_TYPE));

		// Build the task object.
		task = new geojsonWriter(file, compressionMethod, prettyOutput, storeType, wayNodeList, nodeIgnoreTags);
		
		return new SinkManager(taskConfig.getId(), task, taskConfig.getPipeArgs());
	}
}
