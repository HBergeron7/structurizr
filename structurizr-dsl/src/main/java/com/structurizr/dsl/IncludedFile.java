package com.structurizr.dsl;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

final class IncludedFile {

    private final File file;
    private final List<String> lines;

    IncludedFile(File file, List<String> lines) {
        this.file = file;
        this.lines = lines;
    }

    IncludedFile(File file, List<String> lines, String section) {
        this.file = file;
        if (section != null) {
            this.lines = filterLines(lines, section);
        } else {
            this.lines = lines;
        }
    }

    List<String> filterLines(List<String> lines, String section) {
        List<String> sectionContent = new ArrayList<>();
        boolean insideSection = false;
        int braceCount = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            System.out.println("INCLUDE LINE: " + trimmed);
            
            if (!insideSection && trimmed.startsWith(section)) {
                insideSection = true;
                System.out.println("INSIDE SECTION");
                if (trimmed.contains("{")) {
                    braceCount=1;
                    continue;
                }
            }
            
            if (insideSection) {
                for (char c : trimmed.toCharArray()) {
                    if (c == '{') braceCount++;
                    if (c == '}') braceCount--;
                }
               
                System.out.println("BRACES: " + braceCount);
 
                if (braceCount == 0) {
                    break;
                }

                System.out.println("ADD LINE");
                sectionContent.add(line);
            }
        }

        if (braceCount > 0) {
            throw new RuntimeException("Error attempting to include section " + section);
        }
        
        return sectionContent;
    }

    List<String> getLines() {
        return lines;
    }

    File getFile() {
        return file;
    }

}
