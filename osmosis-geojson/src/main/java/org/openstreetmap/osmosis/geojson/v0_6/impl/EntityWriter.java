// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.geojson.common.ElementWriter;

import java.util.Map.Entry;


/**
 * Provides common functionality for all classes writing OSM entities to xml.
 * 
 * @author Brett Henderson
 */
public class EntityWriter extends ElementWriter {

	/**
	 * Creates a new instance.
	 *
	 * @param indentionLevel
	 *            The indent level of the element.
	 */
	protected EntityWriter(int indentionLevel, boolean prettyOutput) {
		super(indentionLevel, prettyOutput);
	}
}
