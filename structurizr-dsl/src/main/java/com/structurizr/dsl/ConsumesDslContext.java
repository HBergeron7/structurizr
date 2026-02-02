package com.structurizr.dsl;

import com.structurizr.model.Consumes;

final class ConsumesDslContext extends DslContext {

    private Consumes consumes;

    ConsumesDslContext(Consumes consumes) {
        super();
        this.consumes = consumes;
    }

    public Consumes getConsumes() {
        return consumes;
    }

    @Override
    protected String[] getPermittedTokens() {
        return new String[] {
                StructurizrDslTokens.DESCRIPTION_TOKEN,
                StructurizrDslTokens.TECHNOLOGY_TOKEN,
                StructurizrDslTokens.TAGS_TOKEN,
                StructurizrDslTokens.PROPERTIES_TOKEN
        };
    }

}
