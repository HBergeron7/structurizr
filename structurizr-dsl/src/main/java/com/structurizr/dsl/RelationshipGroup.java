package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class RelationshipGroup {
    private StaticStructureElement consumer;
    private StaticStructureElement provider;
    private String technology;
    private List<String> filters;
    private String description;
    private String detailedDescription;
    private List<String> tags;
    private List<Consumes> linkedConsumes = new ArrayList<>();
    private List<Provides> linkedProvides = new ArrayList<>();

    RelationshipGroup(StaticStructureElement consumer, StaticStructureElement provider, String technology, String description, List<String> tags) {
        this.consumer = consumer;
        this.provider = provider;
        this.technology = technology;
        this.filters = filters;
        this.description = description;
        this.detailedDescription = "";
        this.tags = tags;
    }

    StaticStructureElement getConsumer() {
        return this.consumer;
    }

    StaticStructureElement getProvider() {
        return this.provider;
    }

    String getTechnology() {
       return this.technology;
    }

    String getDescription() {
       return this.description;
    }

    void appendDescription(String description) {
        if (!this.description.contains(description)) {
            this.description+="/" + description;
        }
    }

    String getDetailedDescription() {
        return this.detailedDescription;
    }

    void appendDetailedDescription(String detailedDescription) {
        this.detailedDescription += detailedDescription;
    }

    List<String> getTags() {
       return Collections.unmodifiableList(this.tags);
    }

    void addTags(List<String> tags) {
        this.tags.addAll(tags);
    }

    void addLinkedConsumes(Consumes consumes) {
        this.linkedConsumes.add(consumes);
    }

    void addLinkedProvides(Provides provides) {
        this.linkedProvides.add(provides);
    }

    void addLinkedRelationshipId(String relationshipId) {
        for (Consumes con : this.linkedConsumes) {
            con.addLinkedRelationshipId(relationshipId);
        }

        for (Provides pro : this.linkedProvides) {
            pro.addLinkedRelationshipId(relationshipId);
        }
    }
}

