// Liam Phelan 17451926
// Hugh McKeeney 17324636
// Hannah O'Dea 17405444

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.*;

class   BoardPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int FRAME_WIDTH = 752, FRAME_HEIGHT = 552;  // must be multiples of 4
    private static final int BORDER_TOP = 40, BORDER_BOTTOM = 75, BORDER_LEFT = 66, BORDER_RIGHT = 60;
    private static final int PIP_WIDTH = 47, BAR_WIDTH = 66;
    private static final int CHECKER_RADIUS = 16, CHECKER_DEPTH = 8, LINE_WIDTH = 2;   // must be even

    private Color[] checkerColors;
    private Board board;
    private Players players;
    private DoublingCube doublingCube;
    private BufferedImage boardImage;
    private Graphics2D g2;



    BoardPanel(Board board, Players players, DoublingCube doublingCube) {
        this.board = board;
        this.players = players;
        this.doublingCube = doublingCube;
        checkerColors = new Color[Players.NUM_PLAYERS];
        checkerColors[0] = players.get(0).getColor();
        checkerColors[1] = players.get(1).getColor();
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setBackground(Color.YELLOW);
        try {
            boardImage = ImageIO.read(this.getClass().getResource("board.jpg"));
        } catch (IOException ex) {
            System.out.println("Could not find the image file " + ex.toString());
        }
    }

    private void displayChecker (int player, int x, int y) {
        g2.setColor(Color.BLACK);
        Ellipse2D.Double ellipseBlack = new Ellipse2D.Double(x,y,2*CHECKER_RADIUS,2*CHECKER_RADIUS);
        g2.fill(ellipseBlack);
        Ellipse2D.Double ellipseColour = new Ellipse2D.Double(x+LINE_WIDTH,y+LINE_WIDTH,2*(CHECKER_RADIUS-LINE_WIDTH),2*(CHECKER_RADIUS-LINE_WIDTH));
        g2.setColor(checkerColors[player]);
        g2.fill(ellipseColour);
    }

    private void displayCheckerSide (int player, int x, int y) {
        g2.setColor(Color.BLACK);
        Rectangle2D.Double rectangleBlack = new Rectangle2D.Double(x,y,2*CHECKER_RADIUS,CHECKER_DEPTH);
        g2.fill(rectangleBlack);
        Rectangle2D.Double rectangleColour = new Rectangle2D.Double(x+LINE_WIDTH,y+LINE_WIDTH,2*(CHECKER_RADIUS-LINE_WIDTH),CHECKER_DEPTH-2*LINE_WIDTH);
        g2.setColor(checkerColors[player]);
        g2.fill(rectangleColour);
    }

    private void displayMatchScore(int matchLength, int currentScoreP1, int currentScoreP2)
    {
        g2.setColor(Color.MAGENTA);
        Rectangle2D.Double scoreBoard = new Rectangle2D.Double(50,255,305,40);
        g2.fill(scoreBoard);

        g2.setColor(Color.WHITE);
        String message;
        if (matchLength == 0)
        {
            message = "Match Length: x | Scores: ";
            g2.drawString("Match Length: x | Scores: ", 55, 280);
        }
        else
        {
            message = "Match Length: " + matchLength + " | Scores: ";
            g2.drawString("Match Length: " + matchLength + " | Scores: ", 55, 280);
        }
        g2.setColor(Color.RED);
        g2.drawString(String.valueOf(currentScoreP1), 100 + (message.length() * 8), 280);
        g2.setColor(Color.GREEN);
        g2.drawString(String.valueOf(currentScoreP2), 100 + (message.length() * 8) + 20, 280);
    }

    private void displayDoublingCube(int cubeValue, int currentPlayer)
    {
        int x = 362, y;
        if (currentPlayer == 0)
            y = 75;
        else if (currentPlayer == 1)
            y = 433;
        else
            y = 262;

        g2.setColor(Color.ORANGE);
        Rectangle2D.Double doublingCube = new Rectangle2D.Double(x, y, 30, 30);
        g2.fill(doublingCube);

        g2.setColor(Color.GRAY);
        Rectangle2D.Double ownerDisplay = new Rectangle2D.Double(400,255,150,40);
        g2.fill(ownerDisplay);

        g2.setColor(Color.CYAN);
        g2.drawString(String.valueOf(cubeValue), x + 11, y + 19);

        if (currentPlayer == 0)
            g2.drawString("DC Owner: Red", 405, 280);
        else if(currentPlayer == 1)
            g2.drawString("DC Owner: Green", 405, 280);
        else
            g2.drawString("DC Owner: None", 405, 280);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g2 = (Graphics2D) g;
        g2.drawImage(boardImage, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, this);
        for (int player=0; player<Backgammon.NUM_PLAYERS; player++)
        {
            int x,y;
            // Display Pip Numbers
            for (int pip=1; pip<=Board.NUM_PIPS; pip++)
            {
                if (pip>3*Board.NUM_PIPS/4)
                {
                    x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (pip-3*Board.NUM_PIPS/4-1)*PIP_WIDTH+PIP_WIDTH/4;
                }
                else if (pip>Board.NUM_PIPS/2)
                {
                    x = BORDER_LEFT + (pip-Board.NUM_PIPS/2-1)*PIP_WIDTH+PIP_WIDTH/4;
                }
                else if (pip>Board.NUM_PIPS/4)
                {
                    x = BORDER_LEFT + (Board.NUM_PIPS/2-pip)*PIP_WIDTH+PIP_WIDTH/4;
                }
                else
                {
                    x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (Board.NUM_PIPS/4-pip)*PIP_WIDTH+PIP_WIDTH/4;
                }
                if (pip>Board.NUM_PIPS/2)
                {
                    y = 3*BORDER_TOP/4;
                }
                else
                {
                    y = FRAME_HEIGHT-BORDER_BOTTOM/4;
                }
                g2.setColor(players.getCurrent().getColor());
                g2.setFont(new Font("Courier",Font.BOLD,16));
                if (players.getCurrent().getId()==0)
                {
                    g2.drawString(Integer.toString(pip), x, y);
                }
                else
                {
                    g2.drawString(Integer.toString(Board.NUM_PIPS-pip+1), x, y);
                }
            }
            // Display Bar
            for (int count=1; count<=board.getNumCheckers(player, Board.BAR); count++)
            {
                x = FRAME_WIDTH/2-CHECKER_RADIUS;
                if (player==0)
                {
                    y = FRAME_HEIGHT/4+(count-1)*CHECKER_RADIUS;
                }
                else
                {
                    y = 3*FRAME_HEIGHT/4-(count-1)*CHECKER_RADIUS;
                }
                displayChecker(player,x,y);
            }
            // Display Main Board
            for (int pip=1; pip<=Board.NUM_PIPS; pip++) {
                for (int count=1; count<=board.getNumCheckers(player,pip); count++) {
                    if (pip>3*Board.NUM_PIPS/4) {
                        x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (pip-3*Board.NUM_PIPS/4-1)*PIP_WIDTH;
                    } else if (pip>Board.NUM_PIPS/2) {
                        x = BORDER_LEFT + (pip-Board.NUM_PIPS/2-1)*PIP_WIDTH;
                    } else if (pip>Board.NUM_PIPS/4) {
                        x = BORDER_LEFT + (Board.NUM_PIPS/2-pip)*PIP_WIDTH;
                    } else {
                        x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (Board.NUM_PIPS/4-pip)*PIP_WIDTH;
                    }
                    if ( (player==0 && pip>Board.NUM_PIPS/2) || (player==1 && pip<Board.NUM_PIPS/2) ){
                        y = BORDER_TOP + (count-1)*2*CHECKER_RADIUS;
                    } else {
                        y = FRAME_HEIGHT - BORDER_BOTTOM - (count-1)*2*CHECKER_RADIUS;
                    }
                    displayChecker(player,x,y);
                }
            }
            // Display Bear Off
            for (int count=1; count<=board.getNumCheckers(player,Board.BEAR_OFF); count++) {
                x = FRAME_WIDTH - BORDER_RIGHT/2 - CHECKER_RADIUS;
                if (player==0) {
                    y = FRAME_HEIGHT - BORDER_BOTTOM - (count-1)*CHECKER_DEPTH;
                } else {
                    y = BORDER_TOP + (count-1)*CHECKER_DEPTH;
                }
                displayCheckerSide(player, x, y);
            }

            // Display Match Score
            displayMatchScore(board.getMatchLength(), board.getPlayer1Score(), board.getPlayer2Score());

            // Display Doubling Cube
            displayDoublingCube(doublingCube.getDoubleValue(), doublingCube.getOwner());
        }
    }

    public void refresh()
    {
        revalidate();
        repaint();
    }

}
