package com.euromoby.api.common;

import java.util.regex.Pattern;

public class UUIDValidator {
    private static Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public static boolean isValid(String s) {
        return p.matcher(s).matches();
    }
}
