package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class Provides {
    private StaticStructureElement element;
    private String identifier;
    private String action;
    private String technology;
    private List<String> properties;
    private String description;
    private List<String> tags;

    Provides(StaticStructureElement element, String identifier, String action, String technology, List<String> properties, String description, List<String> tags) {
        this.element = element;
        this.identifier = identifier;
        this.action = action;
        this.description = description;
        this.technology = technology;
        this.properties = properties;
        this.tags = tags;
    }

    boolean matches(Consumes consumes) {
        boolean match = identifier == consumes.getIdentifier();
        
        if (match && consumes.getTechnology() != null) {
            match = technology == consumes.getTechnology();
        }

        if (match && !consumes.getFilters().isEmpty()) {
            for (String filter : consumes.getFilters()) {
                match = properties.contains(filter);
                if (!match) break;
            }
        }

        return match; 
    }

    StaticStructureElement getElement() {
        return element;
    }

    String getIdentifier() {
        return identifier;
    }

    String getAction() {
        return action;
    }

    String getDescription() {
       return description;
    }

    String getTechnology() {
       return technology;
    }

    List<String> getProperties() {
       return Collections.unmodifiableList(properties);
    }

    List<String> getTags() {
       return Collections.unmodifiableList(tags);
    }
}
