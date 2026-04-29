package com.structurizr.model;

import com.structurizr.AbstractWorkspaceTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProvidesTests extends AbstractWorkspaceTestBase {

    @Test
    void construction() {
        StaticStructureElement element = model.addSoftwareSystem("Name", "Description");
        Provides provides = new Provides(element, "Key", "Action"); 
        assertEquals("Key", provides.getKey());
        assertEquals("Action", provides.getAction());
        assertEquals(element, provides.getElement());
    }

    @Test
    void match_one_to_one() {
        StaticStructureElement element = model.addSoftwareSystem("Name", "Description");
        Provides provides = new Provides(element, "Key", "Action"); 
        Consumes consumes = new Consumes(element, "Key");
    
        provides.addProperty("propKey", "propVal");
        consumes.addProperty("propKey", "propVal");

        assertTrue(provides.matches(consumes));
    }

}
