package com.structurizr.dsl;

final class ModelItemParser extends AbstractParser {

    private final static int DESCRIPTION_INDEX = 1;

    private final static int DETAIL_INDEX = 2;

    private final static int TAGS_INDEX = 1;

    private final static int URL_INDEX = 1;

    void parseTags(ModelItemDslContext context, Tokens tokens) {
        // tags <tags> [tags]
        if (!tokens.includes(TAGS_INDEX)) {
            throw new RuntimeException("Expected: tags <tags> [tags]");
        }

        for (int i = TAGS_INDEX; i < tokens.size(); i++) {
            String tags = tokens.get(i);
            context.getModelItem().addTags(tags.split(","));
        }
    }

    void parseDescription(ElementDslContext context, Tokens tokens) {
        // description <description>
        if (tokens.hasMoreThan(DESCRIPTION_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: description <description>");
        }

        if (!tokens.includes(DESCRIPTION_INDEX)) {
            throw new RuntimeException("Expected: description <description>");
        }

        String description = tokens.get(DESCRIPTION_INDEX);
        context.getElement().setDescription(description);
    }

    void parseDetail(ElementDslContext context, Tokens tokens) {
        // detailedDescription <description>
        if (tokens.hasMoreThan(DETAIL_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: detail <name> <detail>");
        }

        if (!tokens.includes(DETAIL_INDEX)) {
            throw new RuntimeException("Expected: detail <name> <detail>");
        }

        String name = tokens.get(1);
        String detail = tokens.get(2);
        context.getElement().putDetail(name, detail);
    }

    void parseUrl(ModelItemDslContext context, Tokens tokens) {
        // url <url>
        if (tokens.hasMoreThan(URL_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: url <url>");
        }

        if (!tokens.includes(URL_INDEX)) {
            throw new RuntimeException("Expected: url <url>");
        }

        String url = tokens.get(URL_INDEX);
        context.getModelItem().setUrl(url);
    }

}
