package com.structurizr.model;

import com.structurizr.util.StringUtils;

/**
 * Represents an element that can be included in a group.
 */
public abstract class GroupableElement extends Element {

    private String group;
    private String group_tags;

    GroupableElement() {
    }

    /**
     * Gets the name of the group in which this element should be included in.
     *
     * @return the group name, or null if not set
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the name of the group in which this element should be included in.
     *
     * @param group the group name
     */
    public void setGroup(String group) {
        if (group == null) {
            this.group = null;
        } else {
            this.group = group.trim();

            if (StringUtils.isNullOrEmpty(this.group)) {
                this.group = null;
            }
        }
    }

    public String getGroupTags() {
        return group_tags;
    }

    public void setGroupTags(String group_tags) {
        if (group_tags == null) {
            this.group_tags = "";
        } else {
            this.group_tags = group_tags.trim();

            if (StringUtils.isNullOrEmpty(this.group_tags)) {
                this.group = "";
            }
        }
    }

}
