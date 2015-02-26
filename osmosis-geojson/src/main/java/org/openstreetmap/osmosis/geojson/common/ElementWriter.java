// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.geojson.common;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.domain.common.TimestampFormat;

/**
 * Provides common functionality for all classes writing elements to json.
 *
 * @author Brett Henderson
 */
public class ElementWriter {

    /**
     * The number of spaces to indent per indent level.
     */
    private static final int INDENT_SPACES_PER_LEVEL = 2;

    /**
     * The output destination for writing all xml.
     */
    private Writer myWriter;

    /**
     * The indent level of the element.
     */
    private int myIndentLevel;

    private boolean prettyOutput;

    private final TimestampFormat myTimestampFormat;

    /**
     * Line separator string.  This is the value of the line.separator
     * property at the moment that the stream was created.
     */
    private String myLineSeparator;

    /**
     * Creates a new instance.
     *
     * @param anIndentionLevel The indent level of the element.
     */
    protected ElementWriter(final int anIndentionLevel, final boolean prettyOutput) {
        this.myIndentLevel = anIndentionLevel;
        this.prettyOutput = prettyOutput;

        myTimestampFormat = new geojsonTimestampFormat();
        this.myLineSeparator = System.getProperty("line.separator");
    }

    /**
     * Sets the writer used as the xml output destination.
     *
     * @param aWriter The writer.
     */
    public void setWriter(final Writer aWriter) {
        if (aWriter == null) {
            throw new IllegalArgumentException("null writer given");
        }
        this.myWriter = aWriter;
    }

    /**
     * Writes a series of spaces to indent the current line.
     *
     * @throws IOException if an error occurs.
     */
    private void writeIndent() throws IOException {
       writeIndent(0);
    }

    /**
     * Writes a series of spaces to indent the current line.
     *
     * @param addLevel Number of level to add
     * @throws IOException if an error occurs.
     */
    private void writeIndent(int addLevel) throws IOException {
        int indentSpaceCount;

        indentSpaceCount = (myIndentLevel + addLevel) * INDENT_SPACES_PER_LEVEL;

        for (int i = 0; i < indentSpaceCount; i++) {
            myWriter.append(' ');
        }
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     * @param s Input String
     * @return escaped string
     * Reference : https://code.google.com/p/json-simple/source/browse/trunk/src/main/java/org/json/simple/JSONValue.java
     */
    private String escapeString(String s) {

        if (s == null)
            return null;

        StringBuffer sb = new StringBuffer();

        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }

        return sb.toString();
    }

    /**
     * Returns a timestamp format suitable for xml files.
     *
     * @return The timestamp format.
     */
    protected TimestampFormat getTimestampFormat() {
        return myTimestampFormat;
    }

    /**
     * Writes the beginning of a JavaScript method call (for JSONP support).
     * @param name name
     */
    protected void startMethod(String name) {
        try {
            myWriter.append(name);
            myWriter.append("(");
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     * Writes the end of a JavaScript method call (for JSONP support).
     */
    protected void endMethod() {
        try {
            myWriter.append(")");
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     * Writes an element opening line without the final
     * closing portion of the tag.
     *
     * @param first first
     */
    protected void startObject(boolean first) {
        try {
            if(!first) {
                myWriter.append(',');

                if (prettyOutput)
                {
                    myWriter.append("\n");
                    writeIndent();
                }
            }

            myWriter.append("{");
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     *
     * @param objectKey objectKey
     * @param first first
     */
    protected void objectKey(String objectKey, boolean first) {
        try {
            if(!first) {
                myWriter.append(',');
            }

            if (prettyOutput)
            {
                myWriter.append("\n");
                writeIndent();
            }

            myWriter.append('"');
            myWriter.append(objectKey);
            myWriter.append("\": ");
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     *
     */
    protected void startList() {
        try {
            myWriter.append("[");
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     *
     * @param value value
     * @param first first
     */
    protected void appendToList(String value, boolean first) {
        try {
            if(!first) {
                myWriter.append(',');
            }

            if (prettyOutput)
            {
                myWriter.append("\n");
                writeIndent();
            }

            myWriter.append('"');
            myWriter.append(value);
            myWriter.append('"');

        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     *
     * @param value value
     * @param first first
     */
    protected void appendToList(Number value, boolean first) {
        try {
            if(!first) {
                myWriter.append(',');
            }

            if (prettyOutput)
            {
                myWriter.append("\n");
                writeIndent();
            }

            myWriter.append(value.toString());

        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     *
     */
    protected void nextListElement() {
        try {
            myWriter.append(',');
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     *
     */
    protected void endList() {
        try {
            myWriter.append("]");
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     * Adds an attribute to the element.
     *
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @param first first
     */
    protected void addAttribute(final String name, final String value, boolean first) {
        try {
            if(!first) {
                myWriter.append(',');
            }

            if (prettyOutput)
            {
                myWriter.append("\n");
                writeIndent(1);
            }

            myWriter.append('"');
            myWriter.append(escapeString(name));
            myWriter.append("\": \"");

            myWriter.append(escapeString(value));

            myWriter.append('"');
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     * Adds an attribute to the element.
     *
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @param first first
     */
    protected void addAttribute(final String name, final Number value, boolean first) {
        try {
            if(!first) {
                myWriter.append(',');
            }

            if (prettyOutput)
            {
                myWriter.append("\n");
                writeIndent(1);
            }

            myWriter.append('"');
            myWriter.append(name);
            myWriter.append("\": ");

            myWriter.append(value.toString());
        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }

    /**
     * Writes the closing tag of the element.
     */
    protected void endObject() {
        try {

            if (prettyOutput)
            {
                myWriter.append("\n");
                writeIndent();
            }

            myWriter.append("}");

        } catch (IOException e) {
            throw new OsmosisRuntimeException("Unable to write data.", e);
        }
    }
}