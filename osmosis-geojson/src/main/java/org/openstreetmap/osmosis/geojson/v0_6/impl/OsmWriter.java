// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.OsmosisConstants;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.geojson.common.ElementWriter;

import java.io.Writer;


/**
 * Renders OSM data types as xml.
 *
 * @author Brett Henderson
 */
public class OsmWriter extends ElementWriter {

	private SubElementWriter subElementWriter;
	private boolean renderAttributes;


	/**
	 * Creates a new instance.
	 *
	 * @param indentLevel
	 *            The indent level of the element.
	 * @param renderAttributes
	 *            Specifies whether attributes of the top level element should
	 *            be rendered. This would typically be set to false if this
	 *            element is embedded within a higher level element (eg.
	 *            changesets)
	 *
	 */
	public OsmWriter(int indentLevel, boolean renderAttributes, boolean prettyOutput) {
		super(indentLevel, prettyOutput);
		
		this.renderAttributes = renderAttributes;
		
		// Create the sub-element writer which calls the appropriate element
		// writer based on data type.
		subElementWriter = new SubElementWriter(indentLevel + 1, prettyOutput);
	}
	
	/**
	 * Begins an element.
	 */
	public void begin() {
        startObject(true);

        if (renderAttributes) {

            addAttribute("version", XmlConstants.OSM_VERSION, true);
            addAttribute("generator", "Osmosis " + OsmosisConstants.VERSION, false);
        }
	}
	
	
	/**
	 * Ends an element.
	 */
	public void end() {
        if(subElementWriter.getState() != null) {
            endList();
        }

        endObject();
	}
	
	
	/**
	 * Writes the element in the container.
	 * 
	 * @param entityContainer
	 *            The container holding the entity.
	 */
	public void process(EntityContainer entityContainer) {
		entityContainer.process(subElementWriter);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWriter(final Writer writer) {
		super.setWriter(writer);
		
		// Tell the sub element writer that a new writer is available. This will
		// cause the underlying entity writing classes to be updated.
		subElementWriter.updateWriter(writer);
	}
	
	
	/**
	 * Directs data to the appropriate underlying element writer.
	 * 
	 * @author Brett Henderson
	 */
	private static class SubElementWriter extends ElementWriter implements EntityProcessor {
        private NodeWriter nodeWriter;
        private WayWriter wayWriter;
        private RelationWriter relationWriter;
        private BoundWriter boundWriter;
        private boolean boundWritten = false; // can't write a Bound twice
        private boolean entitiesWritten = false; // can't write a Bound after any Entities
        private State state = null;

        public enum State {
            BOUNDS, NODES, WAYS, RELATIONS;
        }

        public State getState() {
            return state;
        }

		/**
		 * Creates a new instance.
		 * 
		 * @param indentLevel
		 *            The indent level of the sub-elements.
		 */
        public SubElementWriter(int indentLevel, boolean prettyOutput) {
            super(indentLevel, prettyOutput);
            nodeWriter = new NodeWriter(indentLevel, prettyOutput);
            wayWriter = new WayWriter(indentLevel, prettyOutput);
            relationWriter = new RelationWriter(indentLevel, prettyOutput);
            boundWriter = new BoundWriter(indentLevel, prettyOutput);
        }


        /**
         * Updates the underlying writer.
         *
         * @param writer
         *            The writer to be used for all output xml.
         */
        public void updateWriter(final Writer writer) {
            super.setWriter(writer);
            nodeWriter.setWriter(writer);
            wayWriter.setWriter(writer);
            relationWriter.setWriter(writer);
            boundWriter.setWriter(writer);
            // reset the flags indicating which data has been written
            boundWritten = false;
            entitiesWritten = false;
            nodeWriter.reset();
            wayWriter.reset();
            relationWriter.reset();
        }


        /**
         * {@inheritDoc}
         */
        public void process(NodeContainer node) {
            if (state != State.NODES) {
                if(state != null) {
                    endList();
                }
                objectKey("nodes", false);
                startList();
                state = State.NODES;
            }
            nodeWriter.process(node.getEntity());
            entitiesWritten = true;
        }


        /**
         * {@inheritDoc}
         */
        public void process(WayContainer way) {
            if (state != State.WAYS) {
                if(state != null) {
                    endList();
                }
                objectKey("ways", false);
                startList();
                state = State.WAYS;
            }
            wayWriter.process(way.getEntity());
            entitiesWritten = true;
        }


        /**
         * {@inheritDoc}
         */
        public void process(RelationContainer relation) {
            if (state != State.RELATIONS) {
                if(state != null) {
                    endList();
                }
                objectKey("relations", false);
                startList();
                state = State.RELATIONS;
            }
            relationWriter.process(relation.getEntity());
            entitiesWritten = true;
        }


        /**
         * {@inheritDoc}
         */
        public void process(BoundContainer bound) {
            if (boundWritten) {
                throw new OsmosisRuntimeException("Bound element already written and only one allowed.");
            }
            if (entitiesWritten) {
                throw new OsmosisRuntimeException("Can't write bound element after other entities.");
            }
            boundWriter.process(bound.getEntity());
            boundWritten = true;
        }
	}
}
