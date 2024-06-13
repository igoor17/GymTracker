package es.eduardo.gymtracker.utils;

/**
 * Represents a language item with its name and associated flag resource ID.
 */
public class LanguageItem {
    private String language; // The name of the language
    private int flag;       // The resource ID of the flag image

    /**
     * Constructs a new LanguageItem with the specified language name and flag resource ID.
     *
     * @param language The name of the language.
     * @param flag     The resource ID of the flag image.
     */
    public LanguageItem(String language, int flag) {
        this.language = language;
        this.flag = flag;
    }

    /**
     * Retrieves the name of the language.
     *
     * @return The language name.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Retrieves the resource ID of the flag image associated with the language.
     *
     * @return The flag image resource ID.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Sets the name of the language.
     *
     * @param language The new language name.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Sets the resource ID of the flag image associated with the language.
     *
     * @param flag The new flag image resource ID.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }
}
