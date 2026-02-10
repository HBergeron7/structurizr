package com.structurizr.inspection.model;

import com.structurizr.inspection.Inspector;
import com.structurizr.inspection.Violation;
import com.structurizr.model.StaticStructureElement;

public class StaticStructureElementConsumesInspection extends AbstractStaticStructureElementInspection {

    public StaticStructureElementConsumesInspection(Inspector inspector) {
        super(inspector);
    }

    @Override
    protected Violation inspect(StaticStructureElement staticStructureElement) {
        var consumesList = staticStructureElement.getConsumes();

        for (var consumes : consumesList) {
            if (consumes.getLinkedRelationshipIdList().isEmpty()) {
                return violation("The " + terminologyFor(staticStructureElement).toLowerCase() + " \"" + nameOf(staticStructureElement) + "\" has consumes without a link to a matching provides.");
            }
        }

        return noViolation();
    }

    @Override
    protected String getType() {
        return "model.staticstructure.consumes";
    }

}

