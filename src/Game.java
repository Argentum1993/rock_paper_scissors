import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Scanner;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

public class Game {
  private static final String SUCCESS_VALIDATION = "success";
  private static final String RESOURCE_PATH = "resources.";

  private final int LENGTH_ROW_KEY = 128 / 8;

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
    HashSet<String> set;
    ResourceBundle  res;

    set = new HashSet<>();
    res = ResourceBundle.getBundle(RESOURCE_PATH + "validation");
    if (programArgs.length < 3)
      return res.getString("lack.of.items");
    if ((programArgs.length & 1) == 0)
      return res.getString("even.number.items");
    for (String arg : programArgs){
      if (set.contains(arg.toLowerCase())){
        return String.format(res.getString("contains.duplicates"), arg);
      }
      set.add(arg.toLowerCase());
    }
    return SUCCESS_VALIDATION;
  }

  public void run(){
    String          computerTurn;
    String          playerTurn;
    String          resultFight;
    String          secretKey;
    ResourceBundle  res;

    res = ResourceBundle.getBundle(RESOURCE_PATH + "game");
    secretKey = generateSecretKey();
    computerTurn = computerTurn();
    System.out.println(res.getString("hmac"));
    System.out.println(generateHMAC(secretKey.getBytes(), computerTurn));
    playerTurn = playerTurn(res);
    if (playerTurn == null){
      System.out.println(res.getString("goodbye"));
      return;
    }
    System.out.println(String.format(res.getString("player.move.format"), playerTurn));
    System.out.println(String.format(res.getString("computer.move.format"), computerTurn));
    resultFight = items.fight(computerTurn, playerTurn);
    if (resultFight == null) {
      System.out.println(res.getString("draw.win"));
    } else if (resultFight.equals(playerTurn)){
      System.out.println(res.getString("player.win"));
    } else {
      System.out.println(res.getString("computer.win"));
    }
    System.out.println(String.format(res.getString("hmac.key.format"), secretKey));
  }

  private String generateHMAC(byte[] key, String data){
    Mac           sha256_HMAC;
    SecretKeySpec secret_key;
    String        hmac;

    secret_key = new SecretKeySpec(key, "HmacSHA256");
    hmac = null;
    try {
      sha256_HMAC = Mac.getInstance("HmacSHA256");
      sha256_HMAC.init(secret_key);
      byte[] mac = sha256_HMAC.doFinal(data.getBytes());
      hmac = Hex.encodeHexString(mac).toUpperCase();
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      e.printStackTrace();
    }
    return hmac;
  }

  private String generateSecretKey(){
    SecureRandom    random;
    byte[]          rowKey;

    random = new SecureRandom();
    rowKey = new byte[LENGTH_ROW_KEY];
    random.nextBytes(rowKey);
    return Hex.encodeHexString(rowKey).toUpperCase();
  }

  private String computerTurn(){
    return items.getRandomItem();
  }

  private String playerTurn(ResourceBundle res){
    int     turn;
    Scanner scanner;

    turn = -1;
    scanner = new Scanner(System.in);
    while (turn < 0 || turn > items.size()){
      printMenu(res);
      System.out.print(res.getString("enter.move"));
      try {
        turn = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e){
        turn = -1;
      }
    }
    return items.get(turn - 1);
  }

  private void printMenu(ResourceBundle res){
    String  section;

    System.out.println(res.getString("available.moves"));
    for (int i = 0; i < items.size(); i++) {
      section = String.format(res.getString("section.format"), i + 1, items.get(i));
      System.out.println(section);
    }
    section = String.format(res.getString("section.format"), 0, res.getString("exit"));
    System.out.println(section);
  }
}
