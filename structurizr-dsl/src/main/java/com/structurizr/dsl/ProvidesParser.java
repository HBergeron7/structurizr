package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class ProvidesParser extends AbstractParser {
    private static final String GRAMMAR = "provides <key> <action> [technology] [properties] [description] [tags]";

    private final static int KEY_INDEX = 1;
    private final static int ACTION_INDEX = 2;
    private final static int TECHNOLOGY_INDEX = 3;
    private final static int PROPERTIES_INDEX = 4;
    private final static int DESCRIPTION_INDEX = 5;
    private final static int TAGS_INDEX = 6;

    Provides parse(ElementDslContext context, Tokens tokens) {
        StaticStructureElement element;
        String identifier;
        String action;
        String description = null;
        String technology = null;
        List<String> properties = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        element = (StaticStructureElement)context.getElement();

        if (tokens.includes(KEY_INDEX)) {
            identifier = tokens.get(KEY_INDEX);
        } else {
            throw new RuntimeException("Must provide key, expected: " + GRAMMAR);
        }

        if (tokens.includes(ACTION_INDEX)) {
            action = tokens.get(ACTION_INDEX);
        } else {
            throw new RuntimeException("Must provide action, expected: " + GRAMMAR);
        }

        if (tokens.hasMoreThan(TAGS_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        if (tokens.includes(TECHNOLOGY_INDEX)) {
            technology = tokens.get(TECHNOLOGY_INDEX);
        }

        if (tokens.includes(PROPERTIES_INDEX)) {
            properties.addAll(Arrays.asList(tokens.get(PROPERTIES_INDEX).split(",")));
        }

        if (tokens.includes(DESCRIPTION_INDEX)) {
            description = tokens.get(DESCRIPTION_INDEX);
        }

        if (tokens.includes(TAGS_INDEX)) {
            tags.addAll(Arrays.asList(tokens.get(TAGS_INDEX).split(",")));
        }

        return new Provides(element, identifier, action, technology, properties, description, tags);
    }
}
