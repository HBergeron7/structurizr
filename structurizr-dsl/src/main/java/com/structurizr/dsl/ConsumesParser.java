package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class ConsumesParser extends AbstractParser {
    private static final String GRAMMAR = "consumes <key> [technology] [filters] [tags]";

    private final static int KEY_INDEX = 1;
    private final static int TECHNOLOGY_INDEX = 2;
    private final static int FILTERS_INDEX = 3;
    private final static int DESCRIPTION_INDEX = 4;
    private final static int TAGS_INDEX = 4;

    Consumes parse(ElementDslContext context, Tokens tokens) {
        StaticStructureElement element;
        String identifier;
        String technology = null;
        String description = null;
        List<String> filters = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        element = (StaticStructureElement)context.getElement();

        if (tokens.includes(KEY_INDEX)) {
            identifier = tokens.get(KEY_INDEX);
        } else {
            throw new RuntimeException("Must provide identifier, expected: " + GRAMMAR);
        }

        if (tokens.hasMoreThan(TAGS_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        if (tokens.includes(TECHNOLOGY_INDEX)) {
            technology = tokens.get(TECHNOLOGY_INDEX);
        }

        if (tokens.includes(FILTERS_INDEX)) {
            filters.addAll(Arrays.asList(tokens.get(FILTERS_INDEX).split(",")));
        }

        if (tokens.includes(DESCRIPTION_INDEX)) {
            description = tokens.get(DESCRIPTION_INDEX);
        }

        if (tokens.includes(TAGS_INDEX)) {
            tags.addAll(Arrays.asList(tokens.get(TAGS_INDEX).split(",")));
        }

        return new Consumes(element, identifier, technology, filters, description, tags);
    }
}
