// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.geojson.common.ElementWriter;


/**
 * Renders a way node as xml.
 * 
 * @author Brett Henderson
 */
public class WayNodeWriter extends ElementWriter {

    private boolean firstWayNode = true;

    /**
     * Creates a new instance.
     *
     * @param indentLevel
     *            The indent level of the element.
     */
    public WayNodeWriter(int indentLevel, boolean prettyOutput) {
        super(indentLevel, prettyOutput);
    }


    /**
     * Writes the way node.
     *
     * @param wayNode
     *            The wayNode to be processed.
     */
    public void processWayNode(WayNode wayNode) {
        appendToList(wayNode.getNodeId(), firstWayNode);

        if (firstWayNode) {
            firstWayNode = false;
        }
    }


    public void reset() {
        firstWayNode = true;
    }
}
