package com.structurizr.dsl;

import com.structurizr.model.*;
import java.util.*;

final class RequiresParser extends AbstractParser {
    private static final String GRAMMAR = "requires <identifier> [tags]";

    private final static int IDENTIFIER_INDEX = 1;
    private final static int TAGS_INDEX = 2;

    private StaticStructureElement element;
    private String identifier;
    private String technology;
    List<String> tags = new ArrayList<>();

    void parse(ElementDslContext context, Tokens tokens) {
        element = (StaticStructureElement)context.getElement();

        if (tokens.includes(IDENTIFIER_INDEX)) {
            identifier = tokens.get(IDENTIFIER_INDEX);
        } else {
            throw new RuntimeException("Must provide identifier, expected: " + GRAMMAR);
        }

        if (tokens.hasMoreThan(TAGS_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        if (tokens.includes(TAGS_INDEX)) {
            tags.addAll(Arrays.asList(tokens.get(TAGS_INDEX).split(",")));
        }
    }

    StaticStructureElement getElement() {
        return element;
    }

    String getIdentifier() {
       return identifier;
    }

    List<String> getTags() {
       return Collections.unmodifiableList(tags);
    }
}
