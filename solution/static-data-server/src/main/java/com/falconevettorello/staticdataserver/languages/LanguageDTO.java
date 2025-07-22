package com.falconevettorello.staticdataserver.languages;

public class LanguageDTO {
    private String language;
    private String type;

    public LanguageDTO() {}

    public LanguageDTO(String language, String type) {
        this.language = language;
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
