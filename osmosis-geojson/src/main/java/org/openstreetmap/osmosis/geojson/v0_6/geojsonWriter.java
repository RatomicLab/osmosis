// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.geojson.common.BaseGeojsonWriter;
import org.openstreetmap.osmosis.geojson.common.CompressionMethod;
import org.openstreetmap.osmosis.geojson.v0_6.impl.OsmWriter;

import java.io.BufferedWriter;
import java.io.File;


/**
 * An OSM data sink for storing all data to an xml file.
 * 
 * @author Brett Henderson
 */
public class geojsonWriter extends BaseGeojsonWriter implements Sink {
	
	private OsmWriter osmWriter;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param writer
	 *            The writer to send all data to.
	 */
	public geojsonWriter(BufferedWriter writer) {
		super(writer);
		
		osmWriter = new OsmWriter(0, true);
	}
	
	
	/**
	 * Creates a new instance.
	 * 
	 * @param file
	 *            The file to write.
	 * @param compressionMethod
	 *            Specifies the compression method to employ.
	 */
	public geojsonWriter(File file, CompressionMethod compressionMethod) {
		super(file, compressionMethod);
		
		osmWriter = new OsmWriter(0, true);
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void process(EntityContainer entityContainer) {
		initialize();
		
		osmWriter.process(entityContainer);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void beginElementWriter() {
		osmWriter.begin();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endElementWriter() {
		osmWriter.end();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setWriterOnElementWriter(BufferedWriter writer) {
		osmWriter.setWriter(writer);
	}
}
