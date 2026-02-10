package com.structurizr.dsl;

final class ProvidesArchetypeDslContext extends ElementArchetypeDslContext {

    ProvidesArchetypeDslContext(Archetype archetype) {
        super(archetype);
    }

    @Override
    protected String[] getPermittedTokens() {
        return new String[] {
            StructurizrDslTokens.ACTION_TOKEN,
            StructurizrDslTokens.DESCRIPTION_TOKEN,
            StructurizrDslTokens.TECHNOLOGY_TOKEN,
            StructurizrDslTokens.TAG_TOKEN,
            StructurizrDslTokens.TAGS_TOKEN,
            StructurizrDslTokens.PROPERTIES_TOKEN
        };
    }

}
