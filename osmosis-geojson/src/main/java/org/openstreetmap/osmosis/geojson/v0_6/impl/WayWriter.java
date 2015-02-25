// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.pgsnapshot.common.NodeLocation;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.*;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.WayGeometryBuilder;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Renders a way as xml.
 *
 * @author Brett Henderson
 */
public class WayWriter extends EntityWriter {
    /**
     * Write the ordered list of node-references of a way.
     */
    private WayNodeWriter wayNodeWriter;
    /**
     * Write the tags of a way.
     */
    private TagWriter tagWriter;

    private boolean wayNodeList;


    /**
     * Creates a new instance.
     *
     * @param indentLevel
     *            The indent level of the element.
     */
    public WayWriter(int indentLevel, boolean prettyOutput, boolean wayNodeList) {
        super(indentLevel, prettyOutput);

        tagWriter = new TagWriter(indentLevel + 1, prettyOutput);
        wayNodeWriter = new WayNodeWriter(indentLevel + 1, prettyOutput);

        this.wayNodeList = wayNodeList;
    }

    /**
     * Writes the way.
     *
     * @param way
     *            The way to be processed.
     */
    public void process(Way way, boolean first, WayGeometryBuilder wayGeometryBuilder) {
        OsmUser user;
        List<WayNode> wayNodes;
        Collection<Tag> tags;

        user = way.getUser();

        startObject(first);
        addAttribute("type", "feature", true);
        addAttribute("id", "way/" + way.getId(), false);

        objectKey("properties", false);
        startObject(true);
        addAttribute("type", "way", true);
        addAttribute("id", way.getId(), false);

        tags = way.getTags();
        if (tags.size() > 0)
        {
            objectKey("tags", false);
            startObject(true);
            for (Tag tag : tags) {
                tagWriter.process(tag);
            }
            tagWriter.reset();
            endObject(); // tags
        }

        wayNodes = way.getWayNodes();
        List<NodeLocation> locations = new ArrayList<NodeLocation>();

        if (wayNodeList)
        {
            objectKey("nodes", false);
            startList();


            for (WayNode wayNode : wayNodes) {
                wayNodeWriter.processWayNode(wayNode);
                locations.add(wayGeometryBuilder.getNodeLocation(wayNode.getNodeId()));
            }

            wayNodeWriter.reset();
            endList(); // nodes
        }
        else
        {
            for (WayNode wayNode : wayNodes) {
                locations.add(wayGeometryBuilder.getNodeLocation(wayNode.getNodeId()));
            }
        }

        objectKey("meta", false);
        startObject(true);
        addAttribute("timestamp", way.getFormattedTimestamp(getTimestampFormat()), true);
        addAttribute("version", way.getVersion(), false);

        if (way.getChangesetId() != 0) {
            addAttribute("changeset", way.getChangesetId(), false);
        }

        if (!user.equals(OsmUser.NONE)) {
            addAttribute("uid", user.getId(), false);
            addAttribute("user", user.getName(), false);
        }

        endObject(); // meta
        endObject(); // properties

        objectKey("geometry", false);
        startObject(true);
        addAttribute("type", "LineString", true);
        objectKey("coordinates", false);
        startList();

        boolean firstLocation = true;
        for (NodeLocation location : locations) {
            if (location.isValid())
            {
                if (!firstLocation)
                {
                    nextListElement();
                }

                startList();
                appendToList(location.getLongitude(), true);
                appendToList(location.getLatitude(), false);
                endList();

                if (firstLocation)
                {
                    firstLocation = false;
                }
            }
        }

        endList(); // coordinates
        endObject(); // geometry
        endObject();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setWriter(final Writer writer) {
        super.setWriter(writer);

        wayNodeWriter.setWriter(writer);
        tagWriter.setWriter(writer);
    }

    public void reset() {
    }
}
