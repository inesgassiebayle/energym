import com.google.common.base.Strings;


public class Aplication {
    public static void main(String[] args) {

    }

    private static String capitalized(String name) {
        return Strings.isNullOrEmpty(name) ? name : name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

}
