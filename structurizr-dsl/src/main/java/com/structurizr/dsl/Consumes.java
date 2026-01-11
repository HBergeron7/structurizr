package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class Consumes {
    private StaticStructureElement element;
    private String identifier;
    private String technology;
    private List<String> filters;
    private String description;
    private List<String> tags;

    Provides(StaticStructureElement element, String identifier, String technology, List<String> filters, String description, List<String> tags) {
        this.element = element;
        this.identifier = identifier;
        this.technology = technology;
        this.filters = filters;
        this.description = description;
        this.tags = tags;
    }

    StaticStructureElement getElement() {
        return element;
    }

    String getIdentifier() {
       return identifier;
    }


    String getTechnology() {
       return technology;
    }

    List<String> getFilters() {
       return Collections.unmodifiableList(filters);
    }

    String getDescription() {
       return description;
    }

    List<String> getTags() {
       return Collections.unmodifiableList(tags);
    }
}

