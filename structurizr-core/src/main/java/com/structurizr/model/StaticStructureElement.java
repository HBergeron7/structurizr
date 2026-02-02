package com.structurizr.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This is the superclass for model elements that describe the static structure
 * of a software system, namely Person, SoftwareSystem, Container and Component.
 */
public abstract class StaticStructureElement extends GroupableElement {

    private List<Consumes> consumes = new ArrayList<>();
    private List<Provides> provides = new ArrayList<>();

    StaticStructureElement() {
    }

    public List<Consumes> getConsumes() {
        return new ArrayList<>(consumes);
    }

    @Nonnull
    public Consumes addConsumes(@Nonnull String key) {
        Consumes consumes = new Consumes(this, key);
        this.consumes.add(consumes);
        return consumes;
    }

    void setConsumes(List<Consumes> consumes) {
        if (consumes != null) {
            this.consumes = new ArrayList<>(consumes);
        }
    }

    public List<Provides> getProvides() {
        return new ArrayList<>(provides);
    }

    @Nonnull
    public Provides addProvides(@Nonnull String key, @Nonnull String action) {
        Provides provides = new Provides(this, key, action);
        this.provides.add(provides);
        return provides;
    }

    void setProvides(List<Provides> provides) {
        if (provides != null) {
            this.provides = new ArrayList<>(provides);
        }
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and software system.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull SoftwareSystem destination, String description) {
        return uses(destination, description, null);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a software system.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology  the technology details (e.g. JSON/HTTPS)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull SoftwareSystem destination, String description, String technology) {
        return uses(destination, description, technology, null);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a software system.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull SoftwareSystem destination, String description, String technology, InteractionStyle interactionStyle) {
        return uses(destination, description, technology, interactionStyle, new String[0]);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a software system.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @param tags             an array of tags
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull SoftwareSystem destination, String description, String technology, InteractionStyle interactionStyle, String[] tags) {
        return uses((StaticStructureElement)destination, description, technology, interactionStyle, tags);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and container.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Container destination, String description) {
        return uses(destination, description, null);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a container.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology  the technology details (e.g. JSON/HTTPS)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Container destination, String description, String technology) {
        return uses(destination, description, technology, null);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a container.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Container destination, String description, String technology, InteractionStyle interactionStyle) {
        return uses(destination, description, technology, interactionStyle, new String[0]);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a container.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @param tags             an array of tags
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Container destination, String description, String technology, InteractionStyle interactionStyle, String[] tags) {
        return uses((StaticStructureElement)destination, description, technology, interactionStyle, tags);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and component.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Component destination, String description) {
        return uses(destination, description, null);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a component.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology  the technology details (e.g. JSON/HTTPS)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Component destination, String description, String technology) {
        return uses(destination, description, technology, null);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a component.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Component destination, String description, String technology, InteractionStyle interactionStyle) {
        return uses(destination, description, technology, interactionStyle, new String[0]);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and a component.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @param tags             an array of tags
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull Component destination, String description, String technology, InteractionStyle interactionStyle, String[] tags) {
        return uses((StaticStructureElement)destination, description, technology, interactionStyle, tags);
    }

    /**
     * Adds a unidirectional relationship between this element and a person.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "sends e-mail to")
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship delivers(@Nonnull Person destination, String description) {
        return delivers(destination, description, null);
    }

    /**
     * Adds a unidirectional relationship between this element and a person.
     *
     * @param destination the target of the relationship
     * @param description a description of the relationship (e.g. "sends e-mail to")
     * @param technology  the technology details (e.g. JSON/HTTPS)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship delivers(@Nonnull Person destination, String description, String technology) {
        return delivers(destination, description, technology, null);
    }

    /**
     * Adds a unidirectional relationship between this element and a person.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "sends e-mail to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship delivers(@Nonnull Person destination, String description, String technology, InteractionStyle interactionStyle) {
        return delivers(destination, description, technology, interactionStyle, new String[0]);
    }

    /**
     * Adds a unidirectional relationship between this element and a person.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "sends e-mail to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @param tags             an array of tags
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship delivers(@Nonnull Person destination, String description, String technology, InteractionStyle interactionStyle, String[] tags) {
        return getModel().addRelationship(this, destination, description, technology, interactionStyle, tags);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and the specified element.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull StaticStructureElement destination, String description, String technology, InteractionStyle interactionStyle) {
        return uses(destination, description, technology, interactionStyle, new String[0]);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and the specified element.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @param tags             an array of tags
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull StaticStructureElement destination, String description, String detailedDescription, String technology, InteractionStyle interactionStyle, String[] tags) {
        return getModel().addRelationship(this, destination, description, detailedDescription, technology, interactionStyle, tags);
    }

    /**
     * Adds a unidirectional "uses" style relationship between this element and the specified element.
     *
     * @param destination      the target of the relationship
     * @param description      a description of the relationship (e.g. "uses", "gets data from", "sends data to")
     * @param technology       the technology details (e.g. JSON/HTTPS)
     * @param interactionStyle the interaction style (sync vs async)
     * @param tags             an array of tags
     * @return the relationship that has just been created and added to the model
     */
    @Nullable
    public Relationship uses(@Nonnull StaticStructureElement destination, String description, String technology, InteractionStyle interactionStyle, String[] tags) {
        return getModel().addRelationship(this, destination, description, technology, interactionStyle, tags);
    }

}
