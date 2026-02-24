package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class ProvidesParser extends AbstractParser {
    private static final String GRAMMAR = "provides <key> [action] [technology] [description] [tags]";

    private final static int KEY_INDEX = 1;
    private final static int ACTION_INDEX = 2;
    private final static int TECHNOLOGY_INDEX = 3;
    private final static int DESCRIPTION_INDEX = 4;
    private final static int TAGS_INDEX = 5;

    private final static int VALUE_INDEX = 1;
    private final static int NAME_INDEX = 1;

    Provides parse(StaticStructureElementDslContext context, Tokens tokens, Archetype archetype) {
        StaticStructureElement element;
        String key;
        int curIndex = KEY_INDEX;

        if (tokens.hasMoreThan(TAGS_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        element = context.getElement();

        if (tokens.includes(curIndex)) {
            key = tokens.get(curIndex);
            curIndex++;
        } else {
            throw new RuntimeException("Must provide key, expected: " + GRAMMAR);
        }

        String action = archetype.getAction();
        if (action.isEmpty() && tokens.includes(curIndex)) {
            action = tokens.get(curIndex);
            curIndex++;
        }

        Provides provides = element.addProvides(key, action);

        String technology = archetype.getTechnology();
        if (technology.isEmpty() && tokens.includes(curIndex)) {
            technology = tokens.get(curIndex);
            curIndex++;
        }
        provides.setTechnology(technology);

        String description = archetype.getDescription();
        if (description.isEmpty() && tokens.includes(curIndex)) {
            description = tokens.get(curIndex);
            curIndex++;
        }
        provides.setDescription(description);

        List<String> tags = new ArrayList<>(archetype.getTags());
        if (tags.isEmpty() && tokens.includes(curIndex)) {
            tags.addAll(Arrays.asList(tokens.get(curIndex).split(",")));
            curIndex++;
        }
        provides.addTags(tags);

        return provides; 
    }

    void parseTags(ProvidesDslContext context, Tokens tokens) {
        // tags <tags> [tags]
        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: tags <tags> [tags]");
        }

        for (int i = NAME_INDEX; i < tokens.size(); i++) {
            String tags = tokens.get(i);
            context.getProvides().addTags(Arrays.asList(tags.split(",")));
        }
    }

    void parseDescription(ProvidesDslContext context, Tokens tokens) {
        // description <description>
        if (tokens.hasMoreThan(VALUE_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: description <description>");
        }

        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: description <description>");
        }

        String description = tokens.get(VALUE_INDEX);
        context.getProvides().setDescription(description);
    }

    void parseTechnology(ProvidesDslContext context, Tokens tokens) {
        // technology <technology>
        if (tokens.hasMoreThan(VALUE_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: technology <technology>");
        }

        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: technology <technology>");
        }

        String technology = tokens.get(VALUE_INDEX);
        context.getProvides().setTechnology(technology);
    }

    void parseAction(ProvidesDslContext context, Tokens tokens) {
        // action <action>
        if (tokens.hasMoreThan(VALUE_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: action <action>");
        }

        if (!tokens.includes(NAME_INDEX)) {
            throw new RuntimeException("Expected: action <action>");
        }

        String action = tokens.get(VALUE_INDEX);
        context.getProvides().setAction(action);
    }
}
