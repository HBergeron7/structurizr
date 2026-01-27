package com.structurizr.dsl;

import com.structurizr.http.RemoteContent;
import com.structurizr.util.FeatureNotEnabledException;
import com.structurizr.util.Url;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ViewSet;
import com.structurizr.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MergeParser extends AbstractParser {

    private static final String GRAMMAR = "!merge <url>";

    private static final int SOURCE_INDEX = 1;

    void parse(DslContext context, ViewSet views, Tokens tokens) {
        // !merge <url>

        //if (!context.getFeatures().isEnabled(Features.INCLUDE)) {
        //    throw new FeatureNotEnabledException(Features.INCLUDE, "!include is not permitted");
        //}

        if (tokens.hasMoreThan(SOURCE_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        if (!tokens.includes(SOURCE_INDEX)) {
            throw new RuntimeException("Expected: " + GRAMMAR);
        }

        String source = tokens.get(SOURCE_INDEX);
        try {
            if (Url.isHttpsUrl(source) || Url.isHttpUrl(source)) {
                if (Url.isHttpsUrl(source) && !context.getFeatures().isEnabled(Features.HTTPS)) {
                    throw new FeatureNotEnabledException(Features.HTTPS, "Extends via HTTPS are not permitted");
                }
                if (Url.isHttpUrl(source) && !context.getFeatures().isEnabled(Features.HTTP)) {
                    throw new FeatureNotEnabledException(Features.HTTP, "Extends via HTTP are not permitted");
                }

                RemoteContent remoteContent = context.getHttpClient().get(source);

                String json = remoteContent.getContentAsString();
                Workspace workspaceWithLayout = WorkspaceUtils.fromJson(json);
                views.copyLayoutInformationFrom(workspaceWithLayout.getViews());
                views.getConfiguration().copyConfigurationFrom(workspaceWithLayout.getViews().getConfiguration());
            } else {
                //TODO Fix to proper exception
                throw new RuntimeException("Merge only supports urls");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}

