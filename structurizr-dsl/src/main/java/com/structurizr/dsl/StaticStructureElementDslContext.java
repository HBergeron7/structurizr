package com.structurizr.dsl;

import com.structurizr.model.StaticStructureElement;

abstract class StaticStructureElementDslContext extends GroupableElementDslContext {

    StaticStructureElementDslContext() {
        super();
    }

    StaticStructureElementDslContext(ElementGroup group) {
        super(group);
    }

    abstract StaticStructureElement getElement();

}

