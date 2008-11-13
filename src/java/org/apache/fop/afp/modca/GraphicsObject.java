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

package org.apache.fop.afp.modca;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.fop.afp.AFPDataObjectInfo;
import org.apache.fop.afp.AFPObjectAreaInfo;
import org.apache.fop.afp.Factory;
import org.apache.fop.afp.goca.GraphicsBox;
import org.apache.fop.afp.goca.GraphicsData;
import org.apache.fop.afp.goca.GraphicsFillet;
import org.apache.fop.afp.goca.GraphicsFilletRelative;
import org.apache.fop.afp.goca.GraphicsFullArc;
import org.apache.fop.afp.goca.GraphicsLine;
import org.apache.fop.afp.goca.GraphicsLineRelative;
import org.apache.fop.afp.goca.GraphicsSetArcParameters;
import org.apache.fop.afp.goca.GraphicsSetCharacterSet;
import org.apache.fop.afp.goca.GraphicsSetCurrentPosition;
import org.apache.fop.afp.goca.GraphicsSetLineType;
import org.apache.fop.afp.goca.GraphicsSetLineWidth;
import org.apache.fop.afp.goca.GraphicsSetPatternSymbol;
import org.apache.fop.afp.goca.GraphicsSetProcessColor;
import org.apache.fop.afp.goca.GraphicsString;

/**
 * Top-level GOCA graphics object.
 *
 * Acts as container and factory of all other graphic objects
 */
public class GraphicsObject extends AbstractDataObject {

    /** The graphics data */
    private GraphicsData currentGraphicsData = null;

    /** list of objects contained within this container */
    protected List/*<PreparedAFPObject>*/ objects
        = new java.util.ArrayList/*<PreparedAFPObject>*/();

    /**
     * Default constructor
     *
     * @param factory the object factory
     * @param name the name of graphics object
     */
    public GraphicsObject(Factory factory, String name) {
        super(factory, name);
    }

    /** {@inheritDoc} */
    public void setViewport(AFPDataObjectInfo dataObjectInfo) {
        super.setViewport(dataObjectInfo);

        AFPObjectAreaInfo objectAreaInfo = dataObjectInfo.getObjectAreaInfo();
        int width = objectAreaInfo.getWidth();
        int height = objectAreaInfo.getHeight();
        int widthRes = objectAreaInfo.getWidthRes();
        int heightRes = objectAreaInfo.getHeightRes();
        final int leftEdge = 0;
        final int topEdge = 0;
        GraphicsDataDescriptor graphicsDataDescriptor = factory.createGraphicsDataDescriptor(
                    leftEdge, width, topEdge, height, widthRes, heightRes);

        getObjectEnvironmentGroup().setDataDescriptor(graphicsDataDescriptor);
    }

    /** {@inheritDoc} */
    public void addObject(PreparedAFPObject drawingOrder) {
        if (currentGraphicsData == null
                || (currentGraphicsData.getDataLength() + drawingOrder.getDataLength())
                >= GraphicsData.MAX_DATA_LEN) {
            newData();
        }
        currentGraphicsData.addObject(drawingOrder);
    }

    /**
     * Gets the current graphics data, creating a new one if necessary
     *
     * @return the current graphics data
     */
    private GraphicsData getData() {
        if (this.currentGraphicsData == null) {
            return newData();
        }
        return this.currentGraphicsData;
    }

    /**
     * Creates a new graphics data
     *
     * @return a newly created graphics data
     */
    private GraphicsData newData() {
        this.currentGraphicsData = factory.createGraphicsData();
        objects.add(currentGraphicsData);
        return currentGraphicsData;
    }

    /**
     * Sets the current color
     *
     * @param color the active color to use
     */
    public void setColor(Color color) {
        addObject(new GraphicsSetProcessColor(color));
    }

    /**
     * Sets the current position
     *
     * @param coords the x and y coordinates of the current position
     */
    public void setCurrentPosition(int[] coords) {
        addObject(new GraphicsSetCurrentPosition(coords));
    }

    /**
     * Sets the line width
     *
     * @param multiplier the line width multiplier
     */
    public void setLineWidth(int multiplier) {
        GraphicsSetLineWidth graphicsSetLineWidth = new GraphicsSetLineWidth(multiplier);
        addObject(graphicsSetLineWidth);
    }

    /**
     * Sets the line type
     *
     * @param type the line type
     */
    public void setLineType(byte type) {
        GraphicsSetLineType graphicsSetLineType = new GraphicsSetLineType(type);
        addObject(graphicsSetLineType);
    }

    /**
     * Sets whether to fill the next shape
     *
     * @param fill whether to fill the next shape
     */
    public void setFill(boolean fill) {
        GraphicsSetPatternSymbol graphicsSetPattern = new GraphicsSetPatternSymbol(
                fill ? GraphicsSetPatternSymbol.SOLID_FILL
                     : GraphicsSetPatternSymbol.NO_FILL
        );
        addObject(graphicsSetPattern);
    }

    /**
     * Sets the character set to use
     *
     * @param fontReference the character set (font) reference
     */
    public void setCharacterSet(int fontReference) {
        addObject(new GraphicsSetCharacterSet(fontReference));
    }

    /**
     * Adds a line at the given x/y coordinates
     *
     * @param coords the x/y coordinates (can be a series)
     */
    public void addLine(int[] coords) {
        addLine(coords, false);
    }

    /**
     * Adds a line at the given x/y coordinates
     *
     * @param coords the x/y coordinates (can be a series)
     * @param relative relative true for a line at current position (relative to)
     */
    public void addLine(int[] coords, boolean relative) {
        if (relative) {
            addObject(new GraphicsLineRelative(coords));
        } else {
            addObject(new GraphicsLine(coords));
        }
    }

    /**
     * Adds a box at the given coordinates
     *
     * @param coords the x/y coordinates
     */
    public void addBox(int[] coords) {
        addObject(new GraphicsBox(coords));
    }

    /**
     * Adds a fillet (curve) at the given coordinates
     *
     * @param coords the x/y coordinates
     */
    public void addFillet(int[] coords) {
        addFillet(coords, false);
    }

    /**
     * Adds a fillet (curve) at the given coordinates
     *
     * @param coords the x/y coordinates
     * @param relative relative true for a fillet at current position (relative to)
     */
    public void addFillet(int[] coords, boolean relative) {
        if (relative) {
            addObject(new GraphicsFilletRelative(coords));
        } else {
            addObject(new GraphicsFillet(coords));
        }
    }

    /**
     * Sets the arc parameters
     *
     * @param xmaj the maximum value of the x coordinate
     * @param ymin the minimum value of the y coordinate
     * @param xmin the minimum value of the x coordinate
     * @param ymaj the maximum value of the y coordinate
     */
    public void setArcParams(int xmaj, int ymin, int xmin, int ymaj) {
        addObject(new GraphicsSetArcParameters(xmaj, ymin, xmin, ymaj));
    }

    /**
     * Adds an arc
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param mh the integer portion of the multiplier
     * @param mhr the fractional portion of the multiplier
     */
    public void addFullArc(int x, int y, int mh, int mhr) {
        addObject(new GraphicsFullArc(x, y, mh, mhr));
    }

//    /**
//     * Adds an image
//     *
//     * @param x the x coordinate
//     * @param y the y coordinate
//     * @param width the image width
//     * @param height the image height
//     * @param imgData the image data
//     */
//    public void addImage(int x, int y, int width, int height, byte[] imgData) {
//        addObject(new GraphicsImage(x, y, width, height, imgData));
//    }

    /**
     * Adds a string
     *
     * @param str the string
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void addString(String str, int x, int y) {
        addObject(new GraphicsString(str, x, y));
    }

    /**
     * Begins a graphics area (start of fill)
     */
    public void beginArea() {
        if (currentGraphicsData == null) {
            newData();
        }
        currentGraphicsData.beginArea();
    }

    /**
     * Ends a graphics area (end of fill)
     */
    public void endArea() {
        if (currentGraphicsData != null) {
            currentGraphicsData.endArea();
        }
    }

    /** {@inheritDoc} */
    public String toString() {
        return "GraphicsObject: " + getName();
    }

    /**
     * Creates a new graphics segment
     */
    public void newSegment() {
        getData().newSegment();
    }

    /** {@inheritDoc} */
    protected void writeStart(OutputStream os) throws IOException {
        byte[] data = new byte[17];
        copySF(data, Type.BEGIN, Category.GRAPHICS);
        os.write(data);
    }

    /** {@inheritDoc} */
    protected void writeContent(OutputStream os) throws IOException {
        super.writeContent(os);
        super.writeObjects(objects, os);
    }

    /** {@inheritDoc} */
    protected void writeEnd(OutputStream os) throws IOException {
        byte[] data = new byte[17];
        copySF(data, Type.END, Category.GRAPHICS);
        os.write(data);
    }

}