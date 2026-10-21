package com.example.practicalfive;

public class TextProcessor {

    public String processMunicipality(String municipality) {
        if (municipality == null) {
            return null;
        }

        String input = municipality.trim();
        String[] names = {"Beijing", "Shanghai", "Chongqing", "Tianjin"};
        for (String name : names) {
            if (name.equalsIgnoreCase(input)) {
                return name;
            }
        }
        return null;
    }
}
