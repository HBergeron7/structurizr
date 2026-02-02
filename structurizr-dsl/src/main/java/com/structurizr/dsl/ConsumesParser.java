package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class ConsumesParser extends AbstractParser {
    private static final String GRAMMAR = "consumes <key> [technology] [description] [tags]";

    private final static int KEY_INDEX = 1;
    private final static int TECHNOLOGY_INDEX = 2;
    private final static int DESCRIPTION_INDEX = 3;
    private final static int TAGS_INDEX = 4;

    private final static int STANDALONE_ASSIGNMENT_INDEX = 1;

    Consumes parse(StaticStructureElementDslContext context, Tokens tokens) {
        StaticStructureElement element;
        String key;

        if (tokens.hasMoreThan(TAGS_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        element = context.getElement();

        if (tokens.includes(KEY_INDEX)) {
            key = tokens.get(KEY_INDEX);
        } else {
            throw new RuntimeException("Must provide key, expected: " + GRAMMAR);
        }

        Consumes consumes = element.addConsumes(key);

        if (tokens.includes(TECHNOLOGY_INDEX)) {
            consumes.setTechnology(tokens.get(TECHNOLOGY_INDEX));
        }

        if (tokens.includes(DESCRIPTION_INDEX)) {
            consumes.setDescription(tokens.get(DESCRIPTION_INDEX));
        }

        if (tokens.includes(TAGS_INDEX)) {
            consumes.setTags(Arrays.asList(tokens.get(TAGS_INDEX).split(",")));
        }

        return consumes;
    }

    void parseTags(ConsumesDslContext context, Tokens tokens) {
        // tags <tags> [tags]
        if (!tokens.includes(STANDALONE_ASSIGNMENT_INDEX)) {
            throw new RuntimeException("Expected: tags <tags> [tags]");
        }

        for (int i = STANDALONE_ASSIGNMENT_INDEX; i < tokens.size(); i++) {
            String tags = tokens.get(i);
            context.getConsumes().addTags(Arrays.asList(tags.split(",")));
        }
    }

    void parseDescription(ConsumesDslContext context, Tokens tokens) {
        // description <description>
        if (tokens.hasMoreThan(STANDALONE_ASSIGNMENT_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: description <description>");
        }

        if (!tokens.includes(STANDALONE_ASSIGNMENT_INDEX)) {
            throw new RuntimeException("Expected: description <description>");
        }

        String description = tokens.get(STANDALONE_ASSIGNMENT_INDEX);
        context.getConsumes().setDescription(description);
    }

    void parseTechnology(ConsumesDslContext context, Tokens tokens) {
        // technology <technology>
        if (tokens.hasMoreThan(STANDALONE_ASSIGNMENT_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: technology <technology>");
        }

        if (!tokens.includes(STANDALONE_ASSIGNMENT_INDEX)) {
            throw new RuntimeException("Expected: technology <technology>");
        }

        String technology = tokens.get(STANDALONE_ASSIGNMENT_INDEX);
        context.getConsumes().setTechnology(technology);
    }
}
