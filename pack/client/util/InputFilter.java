package pack.client.util;

public class InputFilter {
    String filterRegex;
    public InputFilter(String filterRegex) {
        this.filterRegex = filterRegex;
    }

    public boolean filter(String input) {
        return input.matches(filterRegex);
    }
}
