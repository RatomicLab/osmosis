// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;

import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;


/**
 * Renders a node as xml.
 *
 * @author Brett Henderson
 */
public class NodeWriter extends EntityWriter {
    /**
     * Write the tags of a node.
     */
    private TagWriter tagWriter;
    private NumberFormat numberFormat;

	/**
	 * Creates a new instance.
	 *
	 * @param indentLevel
	 *            The indent level of the element.
	 */
	public NodeWriter(int indentLevel, boolean prettyOutput) {
		super(indentLevel, prettyOutput);
		
		tagWriter = new TagWriter(indentLevel + 1, prettyOutput);
		
		// Only write the first 7 decimal places.
		// Write in US locale so that a '.' is used as the decimal separator.
		numberFormat = new DecimalFormat(
			"0.#######;-0.#######",
			new DecimalFormatSymbols(Locale.US)
		);
	}


    /**
     * Writes the node.
     *
     * @param node
     *            The node to be processed.
     */
    public void process(Node node, boolean first) {
        OsmUser user;
        Collection<Tag> tags;

        user = node.getUser();

        startObject(first);
        addAttribute("type", "feature", true);
        addAttribute("id", "node/" + node.getId(), false);

        objectKey("properties", false);
        startObject(true);
        addAttribute("type", "node", true);
        addAttribute("id", node.getId(), false);

        tags = node.getTags();
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

        objectKey("meta", false);
        startObject(true);
        addAttribute("timestamp", node.getFormattedTimestamp(getTimestampFormat()), true);
        addAttribute("version", node.getVersion(), false);

        if (node.getChangesetId() != 0) {
            addAttribute("changeset", node.getChangesetId(), false);
        }

        if (!user.equals(OsmUser.NONE)) {
            addAttribute("uid", user.getId(), false);
            addAttribute("user", user.getName(), false);
        }

        endObject(); // meta
        endObject(); // properties

        objectKey("geometry", false);
        startObject(true);
        addAttribute("type", "Point", true);
        objectKey("coordinates", false);
        startList();
        appendToList(node.getLongitude(), true);
        appendToList(node.getLatitude(), false);
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

        tagWriter.setWriter(writer);
    }


    public void reset() {
    }
}
