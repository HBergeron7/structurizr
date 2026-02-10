package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class ConsumesParser extends AbstractParser {
    private static final String GRAMMAR = "consumes <key> [technology] [description] [tags]";

    private final static int KEY_INDEX = 1;
    private final static int TECHNOLOGY_INDEX = 2;
    private final static int DESCRIPTION_INDEX = 3;
    private final static int TAGS_INDEX = 4;

    private final static int VALUE_INDEX = 1;
    private final static int NAME_INDEX = 1;

    Consumes parse(StaticStructureElementDslContext context, Tokens tokens, Archetype archetype) {
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

        String technology = archetype.getTechnology();
        if (tokens.includes(TECHNOLOGY_INDEX)) {
            technology = tokens.get(TECHNOLOGY_INDEX);
        }
        consumes.setTechnology(technology);

        String description = archetype.getDescription();
        if (tokens.includes(DESCRIPTION_INDEX)) {
            description = tokens.get(DESCRIPTION_INDEX);
        }
        consumes.setDescription(description);

        List<String> tags = new ArrayList<>(archetype.getTags());
        if (tokens.includes(TAGS_INDEX)) {
            tags.addAll(Arrays.asList(tokens.get(TAGS_INDEX).split(",")));
        }
        consumes.addTags(tags);

        return consumes;
    }

    void parseTags(ConsumesDslContext context, Tokens tokens) {
        // tags <tags> [tags]
        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: tags <tags> [tags]");
        }

        for (int i = NAME_INDEX; i < tokens.size(); i++) {
            String tags = tokens.get(i);
            context.getConsumes().addTags(Arrays.asList(tags.split(",")));
        }
    }

    void parseDescription(ConsumesDslContext context, Tokens tokens) {
        // description <description>
        if (tokens.hasMoreThan(VALUE_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: description <description>");
        }

        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: description <description>");
        }

        String description = tokens.get(VALUE_INDEX);
        context.getConsumes().setDescription(description);
    }

    void parseTechnology(ConsumesDslContext context, Tokens tokens) {
        // technology <technology>
        if (tokens.hasMoreThan(VALUE_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: technology <technology>");
        }

        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: technology <technology>");
        }

        String technology = tokens.get(VALUE_INDEX);
        context.getConsumes().setTechnology(technology);
    }
}
