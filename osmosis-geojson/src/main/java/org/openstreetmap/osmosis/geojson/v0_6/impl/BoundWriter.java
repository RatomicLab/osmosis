// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.v0_6.impl;

import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.geojson.common.ElementWriter;

import java.util.Locale;

/**
 * @author KNewman
 * @author Igor Podolskiy
 * 
 */
public class BoundWriter extends ElementWriter {

	/**
	 * Creates a new instance.
	 *
	 * @param indentLevel
	 *            The indent level of the element.
	 */
	public BoundWriter(int indentLevel, boolean prettyOutput) {
        super(indentLevel, prettyOutput);
	}


    /**
     * Writes the bound.
     *
     * @param bound
     *            The bound to be processed.
     */
    public void process(Bound bound) {

        // Only add the Bound if the origin string isn't empty
        if (bound.getOrigin() != "") {
            objectKey("bounds", false);
            startObject(true);
            // Write with the US locale (to force . instead of , as the decimal separator)
            // Use only 5 decimal places (~1.2 meter resolution should be sufficient for Bound)
            addAttribute("minlat", bound.getBottom(), true);
            addAttribute("minlon", bound.getLeft(), false);
            addAttribute("maxlat", bound.getTop(), false);
            addAttribute("maxlon", bound.getRight(), false);
            endObject();
        }
    }
}
