// Liam Phelan 17451926
// Hugh McKeeney 17324636
// Hannah O'Dea 17405444

import java.util.concurrent.TimeUnit;

public class Backgammon {
    // This is the main class for the Backgammon game. It orchestrates the running of the game.

    public static final int NUM_PLAYERS = 2;

    private final Players players = new Players();
    private final Board board = new Board(players);
    private DoublingCube doublingCube = new DoublingCube();
    private final UI ui = new UI(board,players,doublingCube);

    private void getPlayerNames() {
        for (Player player : players) {
            ui.promptPlayerName();
            String name = ui.getString();
            ui.displayString("> " + name);
            player.setName(name);
            ui.displayPlayerColor(player);
        }
    }

    private void setMatchLength()
    {
        // Reset match length
        ui.displayString("Enter the number of points to play to:");
        do
        {
            String input = ui.getString();
            ui.displayString("> " + input);
            try
            {
                board.setMatchLength(Integer.parseInt(input));
                if (board.getMatchLength() < 1)
                    ui.displayString("Invalid number, try again:");
            }
            catch (NumberFormatException e)
            {
                if(input.toLowerCase().trim().equals("quit"))
                {
                    System.exit(0);
                }
                ui.displayString("Invalid input, try again:");
            }
        }
        while (board.getMatchLength() < 1);
    }

    private void rollToStart() throws InterruptedException
    {
        do {
            for (Player player : players) {
                player.getDice().rollDie();
                ui.displayRoll(player);
            }
            if (players.isEqualDice()) {
                ui.displayDiceEqual();
            }
        } while (players.isEqualDice());
        players.setCurrentAccordingToDieRoll();
        ui.displayDiceWinner(players.getCurrent());
        TimeUnit.SECONDS.sleep(2);
        ui.display();
    }

    public boolean checkDoublingLegal()
    {
        if(doublingCube.getOwner() == players.getNotCurrent().getId() || board.getMatchPoint()
        || board.getPlayersScore(players.getCurrent().getId()) + doublingCube.getDoubleValue() >= board.getMatchLength()
        || doublingCube.getDoubleValue() > 32)
            return false;
        else
            return true;
    }

    public void offerDoublingCube()
    {
        Player receivingPlayer = players.getNotCurrent();

        ui.displayString(receivingPlayer.getName() + " (" + receivingPlayer.getColorName() + "), the doubling cube has been offered to you, " +
                "would you like to accept the cube and up the stakes?");
        String command = ui.getString().toLowerCase().trim();
        ui.displayString("> " + command);

        while (!command.equals("yes") && !command.equals("no") && !command.equals("quit")) {
            ui.displayString("Invalid answer\nDo you want to accept the doubling cube?");
            ui.displayString("> " + command);
        }

        if(command.equals("quit"))
        {
            System.exit(0);
        }
        else if (command.equals("yes"))
        {
            //Pass the doubling cube to the next player & double the value
            doublingCube.doubleCube(receivingPlayer.getId());

        }
        else if (command.equals("no"))
        {
            //End the game and update the match score
            try {
                endOfGame(true);
            }catch ( InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void takeTurns() throws InterruptedException
    {
        Command command = new Command();
        boolean firstMove = true;

        do
        {
            Player currentPlayer = players.getCurrent();
            Dice currentDice;

            if( !firstMove)
            {
                boolean check = false;
                do
                {
                    ui.displayString(currentPlayer.getName() +
                            " enter 'double' to offer the doubling cube, otherwise enter roll");

                    String upStakes = ui.getString().toLowerCase().trim();

                    if (upStakes.equals("quit")) {
                        ui.displayString("> quit");
                        TimeUnit.SECONDS.sleep(1);
                        System.exit(0);
                        check = true;
                    } else if (upStakes.equals("double")) {
                        ui.displayString("> double");
                        if (checkDoublingLegal())
                            offerDoublingCube();
                        else {
                            ui.displayString("Doubling is not legal for you now, make your move.");
                            TimeUnit.SECONDS.sleep(1);
                        }
                        check = true;
                    } else if (upStakes.equals("roll")) {
                        ui.displayString("> roll");
                        check = true;
                    }
                    else if (upStakes.equals("cheat")) {
                        ui.displayString("> cheat");
                        board.cheat();
                        ui.display();
                    }else {
                        ui.displayString("Error: command not valid");
                    }
                }while(!check);
            }

            if (firstMove)
            {
                currentDice = new Dice(players.get(0).getDice().getDie(), players.get(1).getDice().getDie());
                firstMove = false;
            }
            else
            {
                currentPlayer.getDice().rollDice();
                ui.displayRoll(currentPlayer);
                currentDice = currentPlayer.getDice();
            }

            Plays possiblePlays = board.getPossiblePlays(currentPlayer, currentDice);

            if (possiblePlays.number() == 0)
            {
                ui.displayNoMove(currentPlayer);
            }
            else if (possiblePlays.number() == 1)
            {
                ui.displayForcedMove(currentPlayer);
                board.move(currentPlayer, possiblePlays.get(0));
            }
            else
            {
                ui.displayPlays(currentPlayer, possiblePlays);
                ui.promptCommand(currentPlayer);
                command = ui.getCommand(possiblePlays);
                ui.displayString("> " + command);
                if (command.isMove())
                {
                    board.move(currentPlayer, command.getPlay());
                }
                else if (command.isCheat())
                {
                    board.cheat();
                }
                else if( command.isQuit() )
                {
                    TimeUnit.SECONDS.sleep(1);
                    System.exit(0);
                }
            }

            ui.display();
            TimeUnit.SECONDS.sleep(2);
            players.advanceCurrentPlayer();
            ui.display();
            if (board.isGameOver())
                endOfGame(false);
        } while (!command.isQuit());
    }


    public void endOfMatch() throws InterruptedException
    {
        ui.displayGameWinner(board.getWinner());
        TimeUnit.SECONDS.sleep(3);

        ui.displayString("Do you want to play again?");
        String command = ui.getString().toLowerCase().trim();
        ui.displayString("> " + command);

        while(!command.equals("yes") && !command.equals("no") && !command.equals("quit"))
        {
            ui.displayString("Invalid answer\nDo you want to play again?");
            command = ui.getString().toLowerCase().trim();
            ui.displayString("> " + command);
        }

        if (command.equals("yes"))
        {
            board.matchReset();
            ui.displayString("-------------------");
            ui.displayStartOfGame();
            getPlayerNames();
            setMatchLength();
            ui.display();
        }
        else if (command.equals("no") || command.equals("quit"))
        {
            TimeUnit.SECONDS.sleep(1);
            System.exit(0);
        }
    }

    public void endOfGame(boolean dcRejected) throws InterruptedException {
        board.updateScore(dcRejected, doublingCube.getDoubleValue());
        ui.displayString(players.get(0).toString()+" score is: "+board.getPlayer1Score());
        ui.displayString(players.get(1).toString()+" score is: "+board.getPlayer2Score());
        if(board.getPlayer1Score() >= board.getMatchLength() || board.getPlayer2Score() >= board.getMatchLength() )
        {
            endOfMatch();
        }
        ui.displayString("Press enter to start the next game");
        if(ui.getString().toLowerCase().trim().equals("quit"))
        {
            System.exit(0);
        }
        //calling reset and then starting the new game
        //next game
        board.boardReset();
        doublingCube.setDoubleValue(1);
        doublingCube.setOwner(-1);
        ui.display();
        rollToStart();
        takeTurns();
    }

    private void play() throws InterruptedException {
        // board.setUI(ui);
        ui.display();
        ui.displayStartOfGame();
        getPlayerNames();
        setMatchLength();
        rollToStart();
        takeTurns();
    }

    public static void main(String[] args) throws InterruptedException
    {
        Backgammon game = new Backgammon();
        game.play();
        System.exit(0);
    }
}
