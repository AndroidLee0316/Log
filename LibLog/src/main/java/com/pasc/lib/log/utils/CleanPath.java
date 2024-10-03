package com.pasc.lib.log.utils;

public class CleanPath {
    private static boolean filterEnable = false;

    public static String cleanString(String content) {
        if (!filterEnable){
            return content;
        }
        if (content == null) return null;
        String cleanString = "";
        for (int i = 0; i < content.length(); ++i) {
            cleanString += cleanChar(content.charAt(i));
        }
        return cleanString;
    }

    private static char cleanChar(char aChar) {

        // 0 - 9
        for (int i = 48; i < 58; ++i) {
            if (aChar == i) return (char) i;
        }

        // 'A' - 'Z'
        for (int i = 65; i < 91; ++i) {
            if (aChar == i) return (char) i;
        }

        // 'a' - 'z'
        for (int i = 97; i < 123; ++i) {
            if (aChar == i) return (char) i;
        }

        // other valid characters
        switch (aChar) {
            case '/':
                return '/';
            case '.':
                return '.';
            case '-':
                return '-';
            case '_':
                return '_';
            case ' ':
                return ' ';
        }
        return '%';
    }
}
