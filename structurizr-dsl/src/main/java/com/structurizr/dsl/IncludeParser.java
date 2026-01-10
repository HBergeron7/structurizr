package com.structurizr.dsl;

import com.structurizr.http.RemoteContent;
import com.structurizr.util.FeatureNotEnabledException;
import com.structurizr.util.Url;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class IncludeParser extends AbstractParser {

    private static final String GRAMMAR = "!include <file|directory|url> [model|view]";

    private static final int SOURCE_INDEX = 1;
    private static final int SECTION_INDEX = 2;

    List<IncludedFile> parse(DslContext context, File dslFile, Tokens tokens) {
        // !include <file|directory|url>

        if (!context.getFeatures().isEnabled(Features.INCLUDE)) {
            throw new FeatureNotEnabledException(Features.INCLUDE, "!include is not permitted");
        }

        List<IncludedFile> includedFiles = new ArrayList<>();

        if (tokens.hasMoreThan(SECTION_INDEX)) {
            throw new RuntimeException("Too many tokens, expected: " + GRAMMAR);
        }

        if (!tokens.includes(SOURCE_INDEX)) {
            throw new RuntimeException("Expected: " + GRAMMAR);
        }

        String source = tokens.get(SOURCE_INDEX);
        String section = null;
        if (tokens.includes(SECTION_INDEX)) {
            section = tokens.get(SECTION_INDEX);
        }

        if (Url.isHttpsUrl(source)) {
            if (context.getFeatures().isEnabled(Features.HTTPS)) {
                RemoteContent content = context.getHttpClient().get(source);
                List<String> lines = Arrays.asList(content.getContentAsString().split("\n"));
                includedFiles.add(new IncludedFile(dslFile, lines, section));
            } else {
                throw new FeatureNotEnabledException(Features.HTTPS, "Includes via HTTPS are not permitted");
            }
        } else if (Url.isHttpUrl(source)) {
            if (context.getFeatures().isEnabled(Features.HTTP)) {
                RemoteContent content = context.getHttpClient().get(source);
                List<String> lines = Arrays.asList(content.getContentAsString().split("\n"));
                includedFiles.add(new IncludedFile(dslFile, lines, section));
            } else {
                throw new FeatureNotEnabledException(Features.HTTP, "Includes via HTTP are not permitted");
            }
        } else {
            if (context.getFeatures().isEnabled(Features.FILE_SYSTEM)) {
                if (dslFile != null) {
                    File path = new File(dslFile.getParent(), source);

                    try {
                        if (!path.exists()) {
                            throw new RuntimeException(path.getCanonicalPath() + " could not be found");
                        }

                        includedFiles.addAll(readFiles(path, section));
                        context.setDslPortable(false);
                    } catch (IOException e) {
                        throw new RuntimeException("Error including " + path.getAbsolutePath() + ": " + e.getMessage());
                    }
                }
            } else {
                throw new FeatureNotEnabledException(Features.FILE_SYSTEM, "!include <file> is not permitted");
            }
        }

        return includedFiles;
    }

    private List<IncludedFile> readFiles(File path, String section) throws IOException {
        List<IncludedFile> includedFiles = new ArrayList<>();

        if (path.isHidden() || path.getName().startsWith(".")) {
            // ignore
            return includedFiles;
        }

        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                Arrays.sort(files);

                for (File file : files) {
                    includedFiles.addAll(readFiles(file, section));
                }
            }
        } else {
            try {
                includedFiles.add(new IncludedFile(path, Files.readAllLines(path.toPath(), StandardCharsets.UTF_8), section));
            } catch (IOException e) {
                throw new RuntimeException("Error reading file at " + path.getAbsolutePath() + ": " + e.getMessage());
            }
        }

        return includedFiles;
    }

}
