package analysis;

import java.util.TreeMap;

public class Language {

    private TreeMap<Integer, String> languageMap;

    public Language() {
        this.languageMap = new TreeMap<>();
    }

    public TreeMap<Integer, String> getLanguageMap() {
        return languageMap;
    }

    public void setLanguageMap(int key, String value) {
        this.languageMap.put(key, value);
    }

    @Override
    public String toString() {
        return "Languages: " + languageMap.toString();
    }
}
