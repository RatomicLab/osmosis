// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.geojson.common.ElementWriter;


/**
 * Renders a tag as xml.
 * 
 * @author Brett Henderson
 */
public class TagWriter extends ElementWriter {

    private boolean firstTag = true;

	/**
	 * Creates a new instance.
	 *
	 * @param indentLevel
	 *            The indent level of the element.
	 */
	public TagWriter(int indentLevel) {
        super(indentLevel);
	}

    /**
     * Writes the tag.
     *
     * @param tag
     *            The tag to be processed.
     */
    public void process(Tag tag) {
        addAttribute(tag.getKey(), tag.getValue(), firstTag);
        if (firstTag) {
            firstTag = false;
        }
    }

    public void reset() {
        firstTag = true;
    }
}
