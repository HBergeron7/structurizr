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
}

