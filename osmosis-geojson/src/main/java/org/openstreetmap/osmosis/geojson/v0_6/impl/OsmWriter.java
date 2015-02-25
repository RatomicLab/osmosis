// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.OsmosisConstants;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.geojson.common.ElementWriter;
import org.openstreetmap.osmosis.pgsnapshot.common.NodeLocationStoreType;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.*;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.WayGeometryBuilder;

import java.io.Writer;
import java.util.HashSet;


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
	public OsmWriter(int indentLevel, boolean renderAttributes, boolean prettyOutput, NodeLocationStoreType storeType, boolean wayNodeList, String nodeIgnoreTags) {
		super(indentLevel, prettyOutput);
		
		this.renderAttributes = renderAttributes;
		
		// Create the sub-element writer which calls the appropriate element
		// writer based on data type.
		subElementWriter = new SubElementWriter(indentLevel + 1, prettyOutput, storeType, wayNodeList, nodeIgnoreTags);
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

        addAttribute("type", "FeatureCollection", false);
        objectKey("features", false);
        startList();
	}
	
	
	/**
	 * Ends an element.
	 */
	public void end() {
        endList();
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
        private org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.WayGeometryBuilder wayGeometryBuilder;
        private State state = null;
        private boolean first = true;
        private HashSet<String> nodeIgnoreTags;

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
        public SubElementWriter(int indentLevel, boolean prettyOutput, NodeLocationStoreType storeType, boolean wayNodeList, String nodeIgnoreTags) {
            super(indentLevel, prettyOutput);
            nodeWriter = new NodeWriter(indentLevel, prettyOutput);
            wayWriter = new WayWriter(indentLevel, prettyOutput, wayNodeList);
            relationWriter = new RelationWriter(indentLevel, prettyOutput);
            boundWriter = new BoundWriter(indentLevel, prettyOutput);
            wayGeometryBuilder = new WayGeometryBuilder(storeType);

            this.nodeIgnoreTags = new HashSet<String>();
            String[] tags = nodeIgnoreTags.split(",");
            for (int i = 0; i < tags.length; i++) {
                this.nodeIgnoreTags.add(tags[i]);
            }
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
            nodeWriter.reset();
            wayWriter.reset();
            relationWriter.reset();
            first = true;
        }


        /**
         * {@inheritDoc}
         */
        public void process(NodeContainer node) {

            // Store all nodes
            wayGeometryBuilder.addNodeLocation(node.getEntity());

            // Write only node with tags to the output (Other nodes are wayNodes)
            if (node.getEntity().getTags().size() > 0) {

                // Process node only if some tags are not in ignore list
                boolean ignoreTags = true;
                for (Tag tag : node.getEntity().getTags()) {
                    if (!nodeIgnoreTags.contains(tag.getKey())) {
                        ignoreTags = false;
                        break;
                    }
                }

                if (!ignoreTags)
                {
                    nodeWriter.process(node.getEntity(), first);

                    if (first) {
                        first = false;
                    }
                }
            }
        }


        /**
         * {@inheritDoc}
         */
        public void process(WayContainer way) {

            // Only write ways with 2 or more nodes
            if (way.getEntity().getWayNodes().size() > 1)
            {
                wayWriter.process(way.getEntity(), first, wayGeometryBuilder);

                if (first)
                {
                    first = false;
                }
            }
        }


        /**
         * {@inheritDoc}
         */
        public void process(RelationContainer relation) {

        }


        /**
         * {@inheritDoc}
         */
        public void process(BoundContainer bound) {

        }
	}
}
