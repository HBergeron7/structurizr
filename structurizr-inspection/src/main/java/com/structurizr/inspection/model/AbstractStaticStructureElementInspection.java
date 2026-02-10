package com.structurizr.inspection.model;

import com.structurizr.inspection.Inspector;
import com.structurizr.inspection.Violation;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

abstract class AbstractStaticStructureElementInspection extends AbstractElementInspection {

    public AbstractStaticStructureElementInspection(Inspector inspector) {
        super(inspector);
    }

    @Override
    protected final Violation inspect(Element element) {
        return inspect((StaticStructureElement)element);
    }

    protected abstract Violation inspect(StaticStructureElement staticStructureElement);

}
