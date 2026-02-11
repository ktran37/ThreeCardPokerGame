import java.util.ArrayList;

public class HeadlessTestClient {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 5000;
        String name = "Client";

        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }
        if (args.length >= 3) name = args[2];

        Client c = new Client(host, port);
        System.out.println(name + ": connecting to " + host + ":" + port);
        if (!c.connect()) {
            System.err.println(name + ": failed to connect");
            return;
        }

        try {
            // Place a small ante and no pair plus
            PokerInfo bet = new PokerInfo();
            bet.setGameAction("BET");
            bet.setAnteBet(1);
            bet.setPairPlusBet(0);

            System.out.println(name + ": sending BET");
            c.sendInfo(bet);

            PokerInfo deal = c.receiveInfo();
            System.out.println(name + ": received action=" + deal.getGameAction() + ", message='" + deal.getMessage() + "'");

            // Show player cards (text)
            ArrayList<Card> p = deal.getPlayerHand();
            System.out.println(name + ": player hand: " + p);

            // Now play: set a play bet and send PLAY
            PokerInfo play = new PokerInfo();
            play.setGameAction("PLAY");
            play.setAnteBet(deal.getAnteBet());
            play.setPairPlusBet(deal.getPairPlusBet());
            play.setPlayBet(1);
            play.setPlayerHand(deal.getPlayerHand());
            play.setDealerHand(deal.getDealerHand());

            System.out.println(name + ": sending PLAY");
            c.sendInfo(play);

            PokerInfo result = c.receiveInfo();
            System.out.println(name + ": received action=" + result.getGameAction() + ", message='" + result.getMessage() + "'");
            System.out.println(name + ": totalWinnings=" + result.getTotalWinnings());
            System.out.println(name + ": dealer hand: " + result.getDealerHand());

        } catch (Exception e) {
            System.err.println(name + ": error during play: " + e.getMessage());
            e.printStackTrace();
        } finally {
            c.disconnect();
            System.out.println(name + ": disconnected");
        }
    }
}
