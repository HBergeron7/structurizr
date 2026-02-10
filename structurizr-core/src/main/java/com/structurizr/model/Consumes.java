package com.structurizr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.structurizr.PropertyHolder;
import java.util.*;

public class Consumes implements PropertyHolder {
    private StaticStructureElement element;
    private String elementId;
    private String key;
    private String technology;
    private String description;
    private Map<String, String> properties = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private Set<String> linkedRelationshipIdList = new TreeSet<>();

    Consumes(){
    }

    Consumes(StaticStructureElement element, String key) {
        this.element = element;
        this.elementId = element.getId();
        this.key = key;
        this.technology = technology;
        this.description = "";
    }
    
    Consumes(StaticStructureElement element, String key, String technology, String description, List<String> tags) {
        this.element = element;
        this.elementId = element.getId();
        this.key = key;
        this.technology = technology;
        this.description = description;
        this.tags = tags;
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

    public String getDescription() {
       return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void addLinkedRelationshipId(String relationshipId) {
        this.linkedRelationshipIdList.add(relationshipId); 
    }

    public Set<String> getLinkedRelationshipIdList() {
        return new TreeSet<>(this.linkedRelationshipIdList);
    }

    void setLinkedRelationshipIdList(Set<String> linkedRelationshipIdList) {
        if (linkedRelationshipIdList != null) {
            this.linkedRelationshipIdList = new TreeSet<>(linkedRelationshipIdList);
        }
    }
}
