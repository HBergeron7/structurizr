package com.structurizr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.*;

/**
 * A relationship between two elements.
 */
public final class Relationship extends ModelItem {

    private Model model;

    private Element source;
    private String sourceId;
    private Element destination;
    private String destinationId;
    private Set<String> description = new LinkedHashSet<>();
    private String technology;
    private InteractionStyle interactionStyle;

    private String linkedRelationshipId; // Keeping for backwards compatibility
    private Set<String> linkedRelationshipIdList = new TreeSet<>();

    Relationship() {
    }

    Relationship(Element source, Element destination, String description, String technology, InteractionStyle interactionStyle, String[] tags) {
        this();

        setSource(source);
        setDestination(destination);
        setDescription(description);
        setTechnology(technology);
        setInteractionStyle(interactionStyle);

        addTags(tags);
    }

    @JsonIgnore
    public Model getModel() {
        return this.model;
    }

    protected void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String getCanonicalName() {
        return new CanonicalNameGenerator().generate(this);
    }

    @JsonIgnore
    public Element getSource() {
        return source;
    }

    /**
     * Gets the ID of the source element.
     *
     * @return  the ID of the source element, as a String
     */
    public String getSourceId() {
        if (this.source != null) {
            return this.source.getId();
        } else {
            return this.sourceId;
        }
    }

    void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    void setSource(Element source) {
        this.source = source;
    }

    @JsonIgnore
    public Element getDestination() {
        return destination;
    }

    /**
     * Gets the ID of the destination element.
     *
     * @return  the ID of the destination element, as a String
     */
    public String getDestinationId() {
        if (this.destination != null) {
            return this.destination.getId();
        } else {
            return this.destinationId;
        }
    }

    void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    void setDestination(Element destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return this.description.isEmpty() ? "" : String.join("/", this.description);
    }

    void addDescription(String description) {
        this.description.add(description);
    }

    void setDescription(String description) {
        this.description = new LinkedHashSet<String>();
        this.description.addAll(Arrays.asList(description.split("/")));
    }

    /**
     * Gets the technology associated with this relationship (e.g. HTTPS, etc).
     *
     * @return  the technology as a String,
     *          or null if a technology is not specified
     */
    public String getTechnology() {
        return this.technology == null ? "" : this.technology;
    }

    public void setTechnology(String technology) {
        if (this.technology == null) {
            this.technology = technology;
        } else {
            if (source instanceof StaticStructureElementInstance && destination instanceof StaticStructureElementInstance) {
                // allow technology to be overridden for relationships in deployment environments (e.g. HTTP -> HTTPS)
                this.technology = technology;
            } else {
                throw new UnsupportedOperationException("Technology cannot be modified");
            }
        }
    }

    /**
     * Gets the interaction style (synchronous or asynchronous).
     *
     * @return  an InteractionStyle,
     *          or null if an interaction style has not been specified
     */
    public InteractionStyle getInteractionStyle() {
        return interactionStyle;
    }

    void setInteractionStyle(InteractionStyle interactionStyle) {
        this.interactionStyle = interactionStyle;
    }

    public String getLinkedRelationshipId() {
        return linkedRelationshipId;
    }

    void setLinkedRelationshipId(String baseRelationshipId) {
        this.linkedRelationshipId = baseRelationshipId;
    }

    void addLinkedRelationshipId(String baseRelationshipId) {
        this.linkedRelationshipIdList.add(baseRelationshipId); 
    }

    public Set<String> getLinkedRelationshipIdList() {
        return new TreeSet<>(this.linkedRelationshipIdList);
    }

    void setLinkedRelationshipIdList(Set<String> linkedRelationshipIdList) {
        if (linkedRelationshipIdList != null) {
            this.linkedRelationshipIdList = new TreeSet<>(linkedRelationshipIdList);
        }
    }

    @Override
    public Set<String> getDefaultTags() {
        if (linkedRelationshipId == null) {
            Set<String> tags = new LinkedHashSet<>();
            tags.add(Tags.RELATIONSHIP);

            if (technology != null && !technology.isBlank()) {
                tags.add(technology);
            }

            if (interactionStyle == InteractionStyle.Synchronous) {
                tags.add(Tags.SYNCHRONOUS);
            } else if (interactionStyle == InteractionStyle.Asynchronous) {
                tags.add(Tags.ASYNCHRONOUS);
            }

            return tags;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public String toString() {
        return source.toString() + " ---[" + description + "]---> " + destination.toString();
    }

}
