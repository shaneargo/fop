/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.fonts;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * FontInfo contains meta information on fonts (where is the metrics file etc.)
 * TODO: We need to remove this class and think about more intelligent design patterns
 * (Data classes => Procedural code)
 */
public class EmbedFontInfo implements Serializable {

    /** Serialization Version UID */
    private static final long serialVersionUID = 8755432068669997369L;

    protected final URI metricsURI;
    protected final URI embedURI;
    /** false, to disable kerning */
    protected final boolean kerning;
    /** false, to disable advanced typographic features */
    protected final boolean advanced;
    /** the requested encoding mode for the font */
    private final EncodingMode encodingMode;
    private final EmbeddingMode embeddingMode;

    /** the PostScript name of the font */
    protected String postScriptName;
    /** the sub-fontname of the font (used for TrueType Collections, null otherwise) */
    protected String subFontName;

    /** the list of associated font triplets */
    private List<FontTriplet> fontTriplets;

    private transient boolean embedded = true;

    /**
     * Main constructor
     * @param metricsURI the URI of the XML resource containing font metrics
     * @param kerning True if kerning should be enabled
     * @param advanced true if advanced typography features should be enabled
     * @param fontTriplets List of font triplets to associate with this font
     * @param embedURI Path to the embeddable font file (may be null)
     * @param subFontName the sub-fontname used for TrueType Collections (null otherwise)
     * @param encodingMode the encoding mode to use for this font
     * @param embeddingMode the embedding mode for this font
     */
    public EmbedFontInfo(URI metricsURI, boolean kerning, boolean advanced,
            List<FontTriplet> fontTriplets, URI embedURI, String subFontName,
            EncodingMode encodingMode, EmbeddingMode embeddingMode) {
        this.metricsURI = metricsURI;
        this.embedURI = embedURI;
        this.kerning = kerning;
        this.advanced = advanced;
        this.fontTriplets = fontTriplets;
        this.subFontName = subFontName;
        this.encodingMode = encodingMode;
        this.embeddingMode = embeddingMode;
    }

    /**
     * Returns the URI of the metrics XML resource
     *
     * @return the metrics file path
     */
    public URI getMetricsURI() {
        return metricsURI;
    }

    /**
     * Returns the URI to the embeddable font resource
     *
     * @return the font resource URI
     */
    public URI getEmbedURI() {
        return embedURI;
    }

    /**
     * Determines if kerning is enabled
     * @return true if enabled
     */
    public boolean getKerning() {
        return kerning;
    }

    /**
     * Determines if advanced typographic features are enabled
     * @return true if enabled
     */
    public boolean getAdvanced() {
        return advanced;
    }

    /**
     * Returns the sub-font name of the font. This is primarily used for TrueType Collections
     * to select one of the sub-fonts. For all other fonts, this is always null.
     * @return the sub-font name (or null)
     */
    public String getSubFontName() {
        return this.subFontName;
    }

    /**
     * Returns the PostScript name of the font.
     * @return the PostScript name
     */
    public String getPostScriptName() {
        return postScriptName;
    }

    /**
     * Sets the PostScript name of the font
     * @param postScriptName the PostScript name
     */
    public void setPostScriptName(String postScriptName) {
        this.postScriptName = postScriptName;
    }

    /**
     * Returns the list of font triplets associated with this font.
     * @return List of font triplets
     */
    public List<FontTriplet> getFontTriplets() {
        return fontTriplets;
    }

    /**
     * Indicates whether the font is only referenced rather than embedded.
     * @return true if the font is embedded, false if it is referenced.
     */
    public boolean isEmbedded() {
        if (embedURI == null) {
            return false;
        } else {
            return this.embedded;
        }
    }

    /**
     * Returns the embedding mode for this font.
     * @return the embedding mode.
     */
    public EmbeddingMode getEmbeddingMode() {
       return embeddingMode;
    }

    /**
     * Defines whether the font is embedded or not.
     * @param value true to embed the font, false to reference it
     */
    public void setEmbedded(boolean value) {
        this.embedded = value;
    }

    /**
     * Returns the requested encoding mode for this font.
     * @return the encoding mode
     */
    public EncodingMode getEncodingMode() {
        return this.encodingMode;
    }

    private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.embedded = true;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "metrics-uri=" + metricsURI + ", embed-uri=" + embedURI
            + ", kerning=" + kerning
            + ", advanced=" + advanced
            + ", enc-mode=" + encodingMode
            + ", font-triplet=" + fontTriplets
            + (getSubFontName() != null ? ", sub-font=" + getSubFontName() : "")
            + (isEmbedded() ? "" : ", NOT embedded");
    }

}
