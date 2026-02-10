package com.structurizr.dsl;

import com.structurizr.model.Provides;

final class ProvidesDslContext extends DslContext {

    private Provides provides;

    ProvidesDslContext(Provides provides) {
        super();
        this.provides = provides;
    }

    public Provides getProvides() {
        return provides;
    }

    @Override
    protected String[] getPermittedTokens() {
        return new String[] {
                StructurizrDslTokens.ACTION_TOKEN,
                StructurizrDslTokens.DESCRIPTION_TOKEN,
                StructurizrDslTokens.TECHNOLOGY_TOKEN,
                StructurizrDslTokens.TAGS_TOKEN,
                StructurizrDslTokens.PROPERTIES_TOKEN
        };
    }

}
