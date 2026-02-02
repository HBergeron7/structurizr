package com.structurizr.model;

/**
 * This strategy creates implied relationships between all valid combinations of the parent elements,
 * unless the same relationship already exists between them.
 */
public class CreateImpliedRelationshipsCombinedByTechnologyStrategy extends AbstractImpliedRelationshipsStrategy {

    @Override
    public void createImpliedRelationships(Relationship relationship) {
        Element source = relationship.getSource();
        Element destination = relationship.getDestination().getParent();

        while (source != null) {
            while (destination != null) {
                if (impliedRelationshipIsAllowed(source, destination)) {
                    // Get existing relationship with matching technology
                    Relationship curRelationship = source.getEfferentRelationshipByTechnologyWith(destination, relationship.getTechnology());

                    if (curRelationship == null) {
                        curRelationship = createImpliedRelationship(relationship, source, destination);
                        curRelationship.setDetailedDescription(relationship.getDetailedDescription()); 
                    } else {
                        if (!curRelationship.getDescription().contains(relationship.getDescription())) {
                            String description = curRelationship.getDescription() + "/" + relationship.getDescription();
                            curRelationship.setDescription(description);
                        }
                        curRelationship.setDetailedDescription(curRelationship.getDetailedDescription() + relationship.getDetailedDescription()); 
                        curRelationship.addLinkedRelationshipId(relationship.getId());
                    }

                }

                destination = destination.getParent();
            }

            destination = relationship.getDestination();
            source = source.getParent();
        }
    }

}
