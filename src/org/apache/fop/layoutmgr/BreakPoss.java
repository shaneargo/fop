/*
 * $Id$
 * Copyright (C) 2002 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 */
package org.apache.fop.layoutmgr;

import org.apache.fop.area.MinOptMax;
import org.apache.fop.traits.LayoutProps;

/**
 * Represents a break possibility for the layout manager.
 * Used to pass information between different levels of layout manager concerning
 * the break positions. In an inline context, before and after are interpreted as
 * start and end.
 * The m_position field is opaque represents where the break occurs. It is a
 * nested structure with one level for each layout manager involved in generating
 * the BreakPoss..
 * @author Karen Lease
 */
public class BreakPoss {


    /** Values for m_flags returned from lower level LM. */
    public static final int CAN_BREAK_AFTER = 0x01; // May break after
    public static final int ISLAST = 0x02; // Last area generated by FO
    public static final int ISFIRST = 0x04; // First area generated by FO
    public static final int FORCE = 0x08; // Forced break (ie LF)
    public static final int CAN_BREAK_BEFORE = 0x10;
    public static final int HAS_ANCHORS = 0x40;
    // Set this flag if all fo:character generated Areas would
    // suppressed at the end or beginning of a line
    public static final int ALL_ARE_SUPPRESS_AT_LB = 0x80;
    /** This break possibility is a hyphenation */
    public static final int HYPHENATED = 0x100;
    /** If this break possibility ends the line, all remaining characters
     * in the lowest level text LM will be suppressed.
     */
    public static final int REST_ARE_SUPPRESS_AT_LB = 0x200;

    /** The opaque position object used by m_lm to record its
     *  break position.
     */
    private Position m_position;

    /**
     * The size range in the stacking direction of the area which would be
     * generated if this BreakPoss were used.
     */
    private MinOptMax m_stackSize;

    /**
     * Max height above and below the baseline. These are cumulative.
     */
    private int m_iLead;
    // the max height of before and after alignment
    private int m_iTotal;
    // middle alignment height for after
    private int m_iMiddle;

    /** Size in the non-stacking direction (perpendicular). */
    private MinOptMax m_nonStackSize;

    private long m_flags = 0;
    private LayoutProps m_layoutProps = new LayoutProps();

    /** Store space-after (or end) and space-before (or start) to be
     * added if this break position is used.
     */
    private SpaceSpecifier m_spaceSpecTrailing;
    private SpaceSpecifier m_spaceSpecLeading;

    public BreakPoss(Position position) {
        this(position, 0);
    }

    public BreakPoss(Position position, long flags) {
        m_position = position;
        m_flags = flags;
    }

    /**
     * The top-level layout manager responsible for this break
     */
    public BPLayoutManager getLayoutManager() {
        return m_position.getLM();
    }

    //     public void setLayoutManager(BPLayoutManager lm) {
    //         m_lm = lm;
    //     }

    /**
     * An object representing the break position in this layout manager.
     */
    public Position getPosition() {
        return m_position;
    }

    public void setPosition(Position pos) {
        m_position = pos;
    }

    public void setStackingSize(MinOptMax size) {
        this.m_stackSize = size;
    }

    public MinOptMax getStackingSize() {
        return this.m_stackSize ;
    }

    public void setNonStackingSize(MinOptMax size) {
        this.m_nonStackSize = size;
    }

    public MinOptMax getNonStackingSize() {
        return this.m_nonStackSize ;
    }

    public long getFlags() {
        return m_flags;
    }

    public void setFlag(int flagBit) {
        setFlag(flagBit, true);
    }

    public void setFlag(int flagBit, boolean bSet) {
        if (bSet) {
            m_flags |= flagBit;
        } else {
            m_flags &= ~flagBit;
        }
    }

    public boolean isLastArea() {
        return ((m_flags & ISLAST) != 0);
    }

    public boolean isFirstArea() {
        return ((m_flags & ISFIRST) != 0);
    }

    public boolean canBreakAfter() {
        return ((m_flags & CAN_BREAK_AFTER) != 0);
    }

    public boolean canBreakBefore() {
        return ((m_flags & CAN_BREAK_BEFORE) != 0);
    }

    public boolean couldEndLine() {
        return ((m_flags & REST_ARE_SUPPRESS_AT_LB) != 0);
    }

    public boolean isForcedBreak() {
        return ((m_flags & FORCE) != 0);
    }

    public boolean isSuppressible() {
        return ((m_flags & ALL_ARE_SUPPRESS_AT_LB) != 0);
    }

    public SpaceSpecifier getLeadingSpace() {
        return m_spaceSpecLeading;
    }

    public MinOptMax resolveLeadingSpace() {
        if (m_spaceSpecLeading != null) {
            return m_spaceSpecLeading.resolve(false);
        } else
            return new MinOptMax(0);
    }

    public SpaceSpecifier getTrailingSpace() {
        return m_spaceSpecTrailing;
    }

    public MinOptMax resolveTrailingSpace(boolean bEndsRefArea) {
        if (m_spaceSpecTrailing != null) {
            return m_spaceSpecTrailing.resolve(bEndsRefArea);
        } else
            return new MinOptMax(0);
    }


    public void setLeadingSpace(SpaceSpecifier spaceSpecLeading) {
        m_spaceSpecLeading = spaceSpecLeading;
    }

    public void setTrailingSpace(SpaceSpecifier spaceSpecTrailing) {
        m_spaceSpecTrailing = spaceSpecTrailing;
    }

    public LayoutProps getLayoutProps() {
        return m_layoutProps;
    }

    public int getLead() {
        return m_iLead;
    }

    public int getTotal() {
        return m_iTotal;
    }

    public int getMiddle() {
        return m_iMiddle;
    }

    /**
     * set lead height of baseline positioned element
     */
    public void setLead(int ld) {
        m_iLead = ld;
    }

    /**
     * Set total height of top or bottom aligned element
     */
    public void setTotal(int t) {
        m_iTotal = t;
    }

    /**
     * Set distance below baseline of middle aligned element
     */
    public void setMiddle(int t) {
        m_iMiddle = t;
    }
}
