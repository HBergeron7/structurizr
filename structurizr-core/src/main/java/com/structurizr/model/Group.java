package com.structurizr.model;

import com.structurizr.util.StringUtils;
import com.structurizr.util.TagUtils;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a group in the model.
 */
public final class Group implements Comparable<Group> {

    private String name;
    private final Set<String> tags = new LinkedHashSet<>();

    Group() {
    }

    Group(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("A group name must be specified.");
        }

        this.name = name.trim();
    }

    public String getTags() {
        return TagUtils.toString(tags);
    }

    public Set<String> getTagsAsSet() {
        return new LinkedHashSet<>(tags);
    }

    void setTags(String tags) {
        this.tags.clear();
        addTags(tags == null ? null : tags.split(","));
    }

    public void addTags(String... tags) {
        if (tags == null) {
            return;
        }

        for (String tag : tags) {
            if (tag != null) {
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    this.tags.add(trimmed);
                }
            }
        }
    }

    @Override
    public int compareTo(Group group) {
        return name.compareTo(group.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }

        Group group = (Group) o;
        return Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
