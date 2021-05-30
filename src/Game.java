import java.util.HashSet;
import java.util.ResourceBundle;

public class Game {
  private static final String SUCCESS_VALIDATION = "success";
  private static final String RESOURCE_PATH = "resources.";

  private GameItems items;

  private Game(String[] gameItems){
    this.items = new GameItems(gameItems);
  }

  public static Game of(String[] programArgs){
    String msg;

    if ((msg = validationArgs(programArgs)).equals(SUCCESS_VALIDATION)){
      return new Game(programArgs);
    }
    System.out.println(msg);
    return null;
  }

  private static String validationArgs(String[] programArgs){
    ResourceBundle res = ResourceBundle.getBundle(RESOURCE_PATH + "validation");

    if (programArgs.length < 3)
      return res.getString("lack.of.items");
    if ((programArgs.length & 1) == 0)
      return res.getString("even.number.items");
    HashSet<String> set = new HashSet<>();
    for (String arg : programArgs){
      if (set.contains(arg.toLowerCase())){
        return String.format(res.getString("contains.duplicates"), arg);
      }
      set.add(arg.toLowerCase());
    }
    return SUCCESS_VALIDATION;
  }
}
