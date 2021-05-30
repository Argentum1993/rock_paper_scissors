public class Main {

    public static void main(String[] args) {
        Game game = Game.of(args);
        if (game != null)
            game.run();
    }
}
