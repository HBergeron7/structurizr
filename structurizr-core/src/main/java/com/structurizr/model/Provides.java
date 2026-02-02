package com.structurizr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.structurizr.PropertyHolder;
import java.util.*;

public class Provides implements PropertyHolder {
    private StaticStructureElement element;
    private String elementId;
    private String key;
    private String action;
    private String technology;
    private String description;
    private Map<String, String> properties = new HashMap<>();
    private List<String> tags = new ArrayList();

    Provides() {
    }

    Provides(StaticStructureElement element, String key, String action) {
        this.element = element;
        this.elementId = element.getId();
        this.key = key;
        this.action = action;
        this.description = "";
        this.technology = "";
    }

    Provides(StaticStructureElement element, String key, String action, String technology, String description, List<String> tags) {
        this.element = element;
        this.elementId = element.getId();
        this.key = key;
        this.action = action;
        this.description = description;
        this.technology = technology;
        this.tags = tags;
    }

    public boolean matches(Consumes consumes) {
        boolean match = key.equals(consumes.getKey());
        
        if (match && consumes.getTechnology() != null) {
            match = technology.equals(consumes.getTechnology());
        }

        if (match) {
            for (var filter : consumes.getProperties().entrySet()) {
                String key = filter.getKey();
                match = this.properties.containsKey(key) && this.properties.get(key).equals(filter.getValue());
                if (!match) break;
            }
        }

        return match; 
    }

    @JsonIgnore
    public StaticStructureElement getElement() {
        return element;
    }

    void setElement(StaticStructureElement element) {
        this.element = element;
        this.elementId = element.getId();
    }

    /**
     * Gets the ID of the source element.
     *
     * @return  the ID of the source element, as a String
     */
    public String getElementId() {
        if (this.element != null) {
            return this.element.getId();
        } else {
            return this.elementId;
        }
    }

    void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getKey() {
        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

    public String getAction() {
        return action;
    }

    void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
       return description;
    }

    public void setDescription(String description) {
        this.description = description;
    } 

    public String getTechnology() {
       return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public Map<String, String> getProperties() {
       return Collections.unmodifiableMap(properties);
    }

    void setProperties(Map<String, String> properties) {
        if (properties != null) {
            this.properties = new HashMap<>(properties);
        }
    }

    public void addProperty(String name, String value) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("A property name must be specified.");
        }

        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException("A property value must be specified.");
        }

        this.properties.put(name, value);
    }

    public List<String> getTags() {
       return Collections.unmodifiableList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public void addTags(List<String> tags) {
        this.tags.addAll(tags);
    }
}

