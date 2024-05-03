package es.eduardo.gymtracker.utils;

public class LanguageItem {
    private String language;
    private int flag;

    public LanguageItem(String language, int flag) {
        this.language = language;
        this.flag = flag;
    }

    public String getLanguage() {
        return language;
    }

    public int getFlag() {
        return flag;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
