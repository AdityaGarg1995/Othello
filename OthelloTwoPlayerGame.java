package Othello;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


/**
 *
 * @author Aditya Garg
 */


class Coordinates{ int x,y; }


public final class OthelloTwoPlayerGame extends JFrame implements ActionListener {
    /**
     * 
     */	
    private static final long serialVersionUID = 1L;

    // Strings that can be displayed by JTextArea display    
    String blackTurn = " Player Black's turn",
           whiteTurn = " Player White's turn",
           noValidWhiteMove = "Player White:NO VALID MOVE.Player Black's turn",
           noValidBlackMove = "Player Black:NO VALID MOVE.Player White's turn"; 
    
    // boolean value to determine which player's turn is this: true means Black Player's turn, false means White Player's turn
    boolean isPlayerBlackTurn;

    
    Color defaultColour = Color.orange,	/* Color that by deafult is the background color for all playButtons */
          flipColour;                   /* Color variable for temporarily storing Color values */  

    
    JPanel[] row = new JPanel[10];

    
    JButton button[][] = new JButton[8][8],
            exit       = new JButton("Exit"),     /* Exit Button */
            newGame    = new JButton("New Game"); /* New Game Button */

    
    int[] dimW = {300,70,150},
          dimH = {30,40,30};

    
    Dimension displayDimension = new Dimension(dimW[0], dimH[0]),
              ButtonDimension  = new Dimension(dimW[1], dimH[1]),
              newGameDimension = new Dimension(dimW[2], dimH[2]); 

    
    JTextArea display = new JTextArea(blackTurn, 1, 10);   /* Text Area to display which player's turn is this:
                                                              needed as players themselves may lose track of turns */      

    
    JTextField[] score = new JTextField[2];

    
    JLabel player[] = new JLabel[2];

    
    Font font1 = fontSetting(28),
         font2 = fontSetting(20);

    
    FlowLayout f1 = new FlowLayout(FlowLayout.CENTER),
               f2 = new FlowLayout(FlowLayout.CENTER, 1, 1);
    /* 1,1 in f2 sets horizontal & vertical gap between components as 1,1
     * while f1 sets the default gap of 5,5
     * hence f2 permits greater screen size reduction(if required) while preserving clarity  
     */

    // Coordinates variables for temporarily storing Coordinates values
    Coordinates temporarySourceButtonCoordinates = new Coordinates(),
                temporaryButtonCoordinates       = new Coordinates();

    
    static long startTime, endTime;
    
        
    OthelloTwoPlayerGame(){
        // Board's Design & Description
        
        super("Othello");
        setDesign();
        setSize(670, 600);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GridLayout grid = new GridLayout(10,10);
        setLayout(grid);

        startTime = System.currentTimeMillis();
        // Creation of new JPanels to add rows
        for(int i = 0; i < 10; i++) 
            row[i] = new JPanel();

        // Layout setting of row[0] & row[9]
        row[0].setLayout(f1);
        row[9].setLayout(f1);

        // Layout setting of remaining rows
        for(int i = 1; i < 9; i++)
            row[i].setLayout(f2);

        // Play Buttons
        for(int i = 0; i < 8; i++){ 
            for(int j = 0; j < 8; j++) {
                button[i][j] = new JButton();
                /*Simply "this" also works fine in addActionListener
                 "OthelloTwoPlayerGame.this" is Only used to prevent "Passing Suspicious Parameter" warning
                */
                button[i][j].addActionListener(OthelloTwoPlayerGame.this);
                button[i][j].setPreferredSize(ButtonDimension);
            }
        }

        // Exit Button: settings 
        buttonSetting(exit);
        // NewGame Button: settings
        buttonSetting(newGame);

        // JTextArea display: description
        display.setFont(font1);
        display.setEditable(false);
        display.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        display.setPreferredSize(displayDimension);

        // Addition of newGame, exit, display to row[0]
        row[0].add(newGame);
        row[0].add(display);
        row[0].add(exit);
        add(row[0]);

        // Addition of playButtons to rows 1 to 8
        for(int i = 8; i > 0; i--){		
            for(int j = 0; j < 8; j++) 
                row[i].add(button[i - 1][j]);
            add(row[i]);
        }

        // Score TextFields and Player Labels: description
        for(int i = 0; i < 2; i++){
            score[i] = new JTextField();
            score[i].setPreferredSize(ButtonDimension);
            score[i].setEditable(false);
            score[i].setFont(font2);
            score[i].setForeground(Color.red);
            score[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            player[i] = new JLabel();
            player[i].setPreferredSize(ButtonDimension);
            player[i].setFont(font2);
            player[i].setForeground(Color.red);
            player[i].setVisible(true);
        }

        // Player Labels and Score TextFields: Addition to row[9]
        row[9].add(player[0]);
        
        for(int i = 0; i < 2; i++)
            row[9].add(score[i]);
        
        row[9].add(player[1]);
        
        add(row[9]);

        score[0].setBackground(Color.black);
        score[1].setBackground(Color.white);
        
        player[0].setText("Black");
        player[1].setText("White");
        
        initialise();
    }

//    Settings for newGame and exit button
    public void buttonSetting(JButton setButton){
        setButton.setEnabled(true);
        setButton.setForeground(Color.red);
        setButton.setBackground(Color.gray);
        setButton.setFont(font2);
        /*Simply "this" also works fine in addActionListener
         "OthelloTwoPlayerGame.this" is ONLY used to prevent "PASSING SUSPICIOUS PARAMETER" warning
        */
        setButton.addActionListener(OthelloTwoPlayerGame.this);
        setButton.setPreferredSize(newGameDimension);
        setDesign2();
    }


    // These functions set the design
    public final void setDesign() {
        try { 
           UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
           System.err.println(e.getCause());
        }
    }

    public void setDesign2() {
        try {
           UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
           System.err.println(e.getCause());
        }
    }


    // This function sets the initial conditions for the board
    // Also used for new game
    public void initialise(){

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                button[i][j].setEnabled(false);
                // Foreground helps in identification of valid buttons
                button[i][j].setForeground(Color.gray);
                button[i][j].setBackground(defaultColour);
            }
        }

        isPlayerBlackTurn = true;
        display.setText(blackTurn);

        button[3][3].setEnabled(true);
        button[4][4].setEnabled(true);
        button[3][4].setEnabled(true);
        button[4][3].setEnabled(true);

        button[3][3].setBackground(Color.BLACK);
        button[4][4].setBackground(Color.BLACK);

        button[3][4].setBackground(Color.WHITE);
        button[4][3].setBackground(Color.WHITE);

        score[0].setText("" + 0);
        score[1].setText("" + 0);

        setVisible(true);
        showPossibleMoves(Color.BLACK);
    }

//     Neccessary helper functions    
//     Helper functions to create a confirm dialog box
    public int createConfirm(String message) { return JOptionPane.showConfirmDialog(this, message, "", JOptionPane.YES_NO_OPTION); }
    public int createConfirm(String message, String title){ return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION); }

//     Helper function to create a error message dialog box
    public void createErrorMessage(String message, String title){ JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE); }

//     Helper function to create a default message dialog box
    public void createMessage(String message){ JOptionPane.showMessageDialog(this, message); }
    
//    Helper Function to set font
    public Font fontSetting(int size){ return new Font("Times new Roman", Font.BOLD, size); }
    
//    Helper function to check Background Color
    public boolean checkBackgoundColor(JButton button, Color color){ return button.getBackground().equals(color); }

    // This function causes actual change in color
    public void changeColour(Coordinates buttonCoordinates, Color currentPlayerColor){	

        if(isPlayerBlackTurn)
            flipColour = Color.WHITE;
        else  flipColour = Color.BLACK;


        Coordinates left = temporaryButtonCoordinates; 
        //for converting pieces in left(West) of clicked Button
        left.x = buttonCoordinates.x - 1;
        left.y = buttonCoordinates.y;

        if((left.x >= 0) 
                && !checkBackgoundColor(button[left.x][left.y], defaultColour)){

            while((left.x >= 0) 
                    && checkBackgoundColor(button[left.x][left.y], flipColour))
                left.x--;

            if((left.x == -1) 
                    || checkBackgoundColor(button[left.x][left.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (left.x + 1); i <= buttonCoordinates.x; i++)
                button[i][left.y].setBackground(currentPlayerColor);
        }


        Coordinates right = temporaryButtonCoordinates;
        //for converting pieces in right(East) of clicked Button
        right.x = buttonCoordinates.x + 1;
        right.y = buttonCoordinates.y;

        if((right.x <= 7) 
                && !checkBackgoundColor(button[right.x][right.y], defaultColour)){

            while((right.x <= 7) 
                    && checkBackgoundColor(button[right.x][right.y], flipColour))
                right.x++;

            if((right.x == 8)
                    || checkBackgoundColor(button[right.x][right.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (right.x - 1); i >= buttonCoordinates.x; i--)
                button[i][right.y].setBackground(currentPlayerColor);
        }


        Coordinates north = temporaryButtonCoordinates;
        //for converting pieces in north of clicked Button
        north.x = buttonCoordinates.x;
        north.y = buttonCoordinates.y + 1;

        if((north.y <= 7) 
                && !checkBackgoundColor(button[north.x][north.y], defaultColour)){

            while((north.y <= 7) 
                    && checkBackgoundColor(button[north.x][north.y],flipColour))
                north.y++;

            if((north.y == 8) 
                    || checkBackgoundColor(button[north.x][north.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (north.y - 1); i >= buttonCoordinates.y; i--)
                button[north.x][i].setBackground(currentPlayerColor);
        }


        Coordinates south = temporaryButtonCoordinates;
        //for converting pieces in south of clicked Button		
        south.x = buttonCoordinates.x;
        south.y = buttonCoordinates.y - 1;

        if((south.y >= 0) 
                && !checkBackgoundColor(button[south.x][south.y], defaultColour)){

            while((south.y >= 0) 
                    && checkBackgoundColor(button[south.x][south.y], flipColour))
                south.y--;

            if((south.y == -1) 
                    || checkBackgoundColor(button[south.x][south.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (south.y + 1); i <= buttonCoordinates.y; i++)
                button[south.x][i].setBackground(currentPlayerColor);
        }


        Coordinates northWest = temporaryButtonCoordinates;
        //for converting pieces in northWest of clicked Button 	
        northWest.x = buttonCoordinates.x - 1;
        northWest.y = buttonCoordinates.y + 1;

        if((northWest.x >= 0) && (northWest.y <= 7) 
            && !checkBackgoundColor(button[northWest.x][northWest.y], defaultColour)){

            while((northWest.x >= 0) && (northWest.y <= 7) 
                && checkBackgoundColor(button[northWest.x][northWest.y], flipColour)){
                northWest.y++;
                northWest.x--;
            }

            if((northWest.x == -1) || (northWest.y == 8) 
                || checkBackgoundColor(button[northWest.x][northWest.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (northWest.y - 1); i >= buttonCoordinates.y; i--){
                northWest.x++;
                button[northWest.x][i].setBackground(currentPlayerColor);
            }
        }


        Coordinates northEast = temporaryButtonCoordinates;
        //for converting pieces in northEast of clicked Button 	
        northEast.x = buttonCoordinates.x + 1;
        northEast.y = buttonCoordinates.y + 1;

        if((northEast.x <= 7) && (northEast.y <= 7) 
            && !checkBackgoundColor(button[northEast.x][northEast.y], defaultColour)){

            while((northEast.x <= 7) && (northEast.y <= 7) 
                  && checkBackgoundColor(button[northEast.x][northEast.y], flipColour)){
                northEast.y++;
                northEast.x++;
            }

            if((northEast.x == 8) || (northEast.y == 8) 
                || checkBackgoundColor(button[northEast.x][northEast.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (northEast.y - 1); i >= buttonCoordinates.y; i--){
                northEast.x--;
                button[northEast.x][i].setBackground(currentPlayerColor);
            }
        }


        Coordinates southEast = temporaryButtonCoordinates;
        //for converting pieces in southEast of clicked Button 
        southEast.x = buttonCoordinates.x + 1;
        southEast.y = buttonCoordinates.y - 1;

        if((southEast.x <= 7) && (southEast.y >= 0) 
            && !checkBackgoundColor(button[southEast.x][southEast.y], defaultColour)){

            while((southEast.x <= 7) && (southEast.y >= 0) 
                  && checkBackgoundColor(button[southEast.x][southEast.y], flipColour)){
                southEast.y--;
                southEast.x++;
            }

            if((southEast.x == 8) || (southEast.y == -1) 
                || checkBackgoundColor(button[southEast.x][southEast.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (southEast.y + 1); i <= buttonCoordinates.y; i++){
                southEast.x--;
                button[southEast.x][i].setBackground(currentPlayerColor);
            }
        }


        Coordinates southWest = temporaryButtonCoordinates;
        //for converting pieces in southWest of clicked Button  	
        southWest.x = buttonCoordinates.x - 1;
        southWest.y = buttonCoordinates.y - 1;

        if((southWest.x >= 0) && (southWest.y >= 0) 
            && !checkBackgoundColor(button[southWest.x][southWest.y], defaultColour)){

            while((southWest.x >= 0) && (southWest.y >= 0) 
                && checkBackgoundColor(button[southWest.x][southWest.y], flipColour)){
                southWest.x--;
                southWest.y--;
            }

            if((southWest.x == -1) || (southWest.y == -1) 
                || checkBackgoundColor(button[southWest.x][southWest.y], defaultColour)){ /*do nothing*/ }

            else for(int i = (southWest.y + 1); i <= buttonCoordinates.y; i++){
                southWest.x++;
                button[southWest.x][i].setBackground(currentPlayerColor);
            }
        }

        // Sets all invalid buttons disabled		
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){	
                // Foreground is used to identify valid buttons
                button[i][j].setForeground(Color.gray);
                if(checkBackgoundColor(button[i][j], defaultColour))
                    button[i][j].setEnabled(false);
            }
        }
    }	


    //This function shows if there are any possible moves for a button with coordinates equal to buttonCoordinates for given color of player
    public boolean anyPossibleMoves(Coordinates buttonCoordinates, Color currentPlayerColor){

        boolean Flippable = false;

        if (isPlayerBlackTurn)
            flipColour = Color.WHITE;

        else flipColour = Color.BLACK;


        Coordinates left = temporaryButtonCoordinates;
        //checking for converting pieces in left(West) of clicked Button
        left.x = buttonCoordinates.x - 1;
        left.y = buttonCoordinates.y;

        if((left.x >= 0)
                && !checkBackgoundColor(button[left.x][left.y], defaultColour)){

            while((left.x >= 0)
                    && checkBackgoundColor(button[left.x][left.y], flipColour))
                left.x--;

            if((left.x == -1) 
                    || checkBackgoundColor(button[left.x][left.y], defaultColour)){
                //do nothing
            }

            else for(int i = (left.x + 1); i <= (buttonCoordinates.x - 1); i++)
                Flippable = true;
        }


        Coordinates right = temporaryButtonCoordinates;
        //checking for converting pieces in right(East) of clicked Button
        right.x = buttonCoordinates.x + 1;
        right.y = buttonCoordinates.y;

        if((right.x <= 7) 
                && !checkBackgoundColor(button[right.x][right.y], defaultColour)){

            while((right.x <= 7) 
                    && checkBackgoundColor(button[right.x][right.y], flipColour))
                right.x++;

            if((right.x == 8) 
                    || checkBackgoundColor(button[right.x][right.y], defaultColour)){
                //do nothing
            }

            else for(int i = (right.x - 1); i >= (buttonCoordinates.x + 1); i--)
                Flippable = true;
        }


        Coordinates north = temporaryButtonCoordinates;
        //checking for converting pieces in north of clicked Button 
        north.x = buttonCoordinates.x;
        north.y = buttonCoordinates.y + 1;

        if((north.y <= 7) 
                && !checkBackgoundColor(button[north.x][north.y], defaultColour)){

            while((north.y <= 7) 
                    && checkBackgoundColor(button[north.x][north.y], flipColour))
                north.y++;

            if((north.y == 8) 
                    || checkBackgoundColor(button[north.x][north.y], defaultColour)){
                //do nothing
            }

            else for(int i = (north.y - 1); i >= (buttonCoordinates.y + 1); i--)
                Flippable = true;
        }


        Coordinates south = temporaryButtonCoordinates;
        //checking for converting pieces in south of clicked Button 		
        south.x = buttonCoordinates.x;
        south.y = buttonCoordinates.y - 1;

        if((south.y >= 0) 
                && !checkBackgoundColor(button[south.x][south.y], defaultColour)){

            while((south.y >= 0)
                    && checkBackgoundColor(button[south.x][south.y], flipColour))
                south.y--;

            if((south.y == -1)
                    || checkBackgoundColor(button[south.x][south.y], defaultColour)){
                //do nothing
            }
            else for(int i = (south.y + 1); i <= (buttonCoordinates.y - 1); i++)
                Flippable = true;
        }


        Coordinates northWest = temporaryButtonCoordinates;
        //checking for converting pieces in NW of clicked Button  
        northWest.x = buttonCoordinates.x - 1;
        northWest.y = buttonCoordinates.y + 1;

        if((northWest.x >= 0) && (northWest.y <= 7) 
           && !checkBackgoundColor(button[northWest.x][northWest.y], defaultColour)){

            while((northWest.x >= 0) && (northWest.y <= 7) 
                && checkBackgoundColor(button[northWest.x][northWest.y], flipColour)){
                northWest.y++;
                northWest.x--;
            }

            if((northWest.x == -1) || (northWest.y == 8) 
              || checkBackgoundColor(button[northWest.x][northWest.y], defaultColour)){
               //do nothing
            }
            else for(int i = (northWest.y - 1); i >= (buttonCoordinates.y + 1); i--){
                temporaryButtonCoordinates.x++;
                Flippable = true;
            }
        }


        Coordinates northEast = temporaryButtonCoordinates;
        //checking for converting pieces in NE of clicked Button  	
        northEast.x = buttonCoordinates.x + 1;
        northEast.y = buttonCoordinates.y + 1;

        if((northEast.x <= 7) && (northEast.y <= 7) 
           && !checkBackgoundColor(button[northEast.x][northEast.y], defaultColour)){

            while((northEast.x <= 7) && (northEast.y <= 7) 
                   && checkBackgoundColor(button[northEast.x][northEast.y], flipColour)){
                northEast.y++;
                northEast.x++;
            }

            if((northEast.x == 8) || (northEast.y == 8) 
               || checkBackgoundColor(button[northEast.x][northEast.y], defaultColour)){
                   //do nothing
            }
            else for(int i = (northEast.y - 1); i >= (buttonCoordinates.y + 1); i--){
                northEast.x--;
                Flippable = true;
            }
        }


        Coordinates southEast = temporaryButtonCoordinates;
        // checking for converting pieces in SE of clicked Button 
        southEast.x = buttonCoordinates.x + 1;
        southEast.y = buttonCoordinates.y - 1;

        if((southEast.x <= 7) && (southEast.y >= 0) 
           && !checkBackgoundColor(button[southEast.x][southEast.y], defaultColour)){

            while((southEast.x <= 7) && (southEast.y >= 0) 
                && checkBackgoundColor(button[southEast.x][southEast.y], flipColour)){
                southEast.y--;
                southEast.x++;
            }

            if((southEast.x == 8) || (southEast.y == -1) 
               || checkBackgoundColor(button[southEast.x][southEast.y], defaultColour)){
                //do nothing
            }

            else for(int i = (southEast.y + 1); i <= (buttonCoordinates.y - 1); i++){
                southEast.x--;
                Flippable = true;
            }
        }

        Coordinates southWest = temporaryButtonCoordinates;
        // checking for converting nodes in SW of clicked Button  	
        southWest.x = buttonCoordinates.x - 1;
        southWest.y = buttonCoordinates.y - 1;

        if((southWest.x >= 0) && (southWest.y >= 0) 
           && !checkBackgoundColor(button[southWest.x][southWest.y], defaultColour)){

            while((southWest.x >= 0) && (southWest.y >= 0)
                && checkBackgoundColor(button[southWest.x][southWest.y], flipColour)){
                southWest.y--;
                southWest.x--;
            }

            if((southWest.x == -1) || (southWest.y == -1)
                || checkBackgoundColor(button[southWest.x][southWest.y], defaultColour)){
                //do nothing
            }
            else for(int i = (southWest.y + 1); i <= (buttonCoordinates.y - 1); i++){
                southWest.x++;
                Flippable = true;
            }
        }
        return Flippable;
    }


    // This function shows if a player has any possible moves and shows them 
    public boolean showPossibleMoves(Color nextPlayerColour){

        boolean hasMoves = false;  /* boolean vaue to signify if there's any move for the player */
        JButton nextPossibleMove;
        
        Coordinates buttonCoordinates = temporarySourceButtonCoordinates; 
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){

                nextPossibleMove = button[i][j];
                buttonCoordinates.x = i;
                buttonCoordinates.y = j;

                if(checkBackgoundColor(nextPossibleMove, defaultColour)){
                    // Checking if any possible move exists

                    if(anyPossibleMoves(buttonCoordinates, nextPlayerColour)) {
                        /* if any vlaid moves exist for JButton nextPossibleMove, set its foreground to Color.RED, set it as Enabled */ 
                        nextPossibleMove.setForeground(Color.RED);
                        nextPossibleMove.setEnabled(true);
                        hasMoves = true;
                        /* Red foreground can't be seen by players but can be seen by computer.
                           Red foreground helps computer to identify valid buttons. */
                    } 
                }
            }
        }
        return hasMoves;
    }

    
//    Helper functions for getGameStatus() for funtions for new game and exit    
//    Game Exit function
    public void exitGame(){
        System.out.println("Exited Game");
        System.exit(0);
    }
//    New Game function
    public void newGameStart(String message){
        try{
            System.out.println(message);
            initialise();
        } catch(Exception e){ System.err.println(e.getCause()); }
    }
    
    // This function displays Current Player and intermediate & final result of game
    public void getGameStatus(Color playerColour){

        Color nextPlayerColour,
              currentPlayerColour;
        
        int blackScore = 0,
            whiteScore = 0;
    
        boolean currentPlayerHasMoves,
                nextplayerHasMoves;

        // Score Calculation
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8;j++){
                if (checkBackgoundColor(button[i][j], Color.BLACK))
                    blackScore++;
                else if (checkBackgoundColor(button[i][j], Color.WHITE))
                    whiteScore++;
            }
        }
        
        score[0].setText("" + blackScore);
        score[1].setText("" + whiteScore);


        // Obtaining status of game
        isPlayerBlackTurn = !isPlayerBlackTurn;	

        if (isPlayerBlackTurn)
           nextPlayerColour = Color.BLACK;

        else  nextPlayerColour = Color.WHITE;			

        // Counting possible moves for next player
        nextplayerHasMoves = showPossibleMoves(nextPlayerColour);

        if(!nextplayerHasMoves/*Count Of Possible Moves For Next Player = 0*/){
                              // i.e. next player has no valid move

            isPlayerBlackTurn = !isPlayerBlackTurn;	

            if(isPlayerBlackTurn)
               currentPlayerColour = Color.BLACK;
            else  currentPlayerColour = Color.WHITE;

            // Counting possible moves for current player
            currentPlayerHasMoves = showPossibleMoves(currentPlayerColour);

            if(!currentPlayerHasMoves/*Count of Possible Moves For CurrentPlayer = 0*/){
                                     // i.e. current player also has no valid move
                
                /* implying no player has any valid move left i.e. the board is full 
                   Time for displaying the result!!
                */
                display.setText(" ");

                if(blackScore == whiteScore)               /*Equal Scores*/
                    createMessage("THIS MATCH IS A DRAW!");
                else if(blackScore > whiteScore)           /*Black's Score > White's Score */
                    createMessage("CONGRATULATIONS \n PLAYER BLACK WINS!");

                else                                       /*White's Score > Black's Score */
                    createMessage("CONGRATULATIONS  \n PLAYER WHITE WINS!");

                
                switch(createConfirm("Play Again ?", "Yes to play. No to exit")){
                    case 0:
                        newGameStart("Playing Again \n New Game Started"); 
                        break;
                
                    case 1:
                        exitGame();
                        break;
                
                    default: System.err.println("Invalid Choice");
                }
            }
            
            // The nextPlayer does not have any moves left, so moving on to currentPlayer
            else{
                if(isPlayerBlackTurn){
                    // White player has no moves left
                    display.setText(noValidWhiteMove);
                    playerColour = Color.BLACK;
                }
                else {
                    // Black player has no moves left
                    display.setText(noValidBlackMove);
                    playerColour = Color.WHITE;
                }
            }
        }

        // The nextPlayer has moves
        else {
            if(isPlayerBlackTurn){
                display.setText(blackTurn);
                playerColour = Color.BLACK;
            }
            else {
                display.setText(whiteTurn);
                playerColour = Color.WHITE;
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent ae) {

        JButton clickedButton = (JButton)ae.getSource();
        Color playerColour;
        Coordinates buttonCoordinates = temporarySourceButtonCoordinates;

        if(checkBackgoundColor(clickedButton, defaultColour)){

            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                    if(clickedButton == button[i][j]){
                        buttonCoordinates.x = i;
                        buttonCoordinates.y = j;
                    }

            if(isPlayerBlackTurn)
                playerColour = Color.BLACK;

            else  playerColour = Color.WHITE;

            // Clicked Button's background need not be changed here as it is changed in changeColour
            changeColour(buttonCoordinates, playerColour);
            getGameStatus(playerColour);
        }

        else if(clickedButton == exit){
            if(createConfirm("EXIT CURRENT GAME?") == 0){
                exitGame();
            }
        }
        else if(clickedButton == newGame){
            if(createConfirm("START NEW GAME?") == 0){
                newGameStart("New Game Started");
            }
        }
        else {
            createErrorMessage("INVALID MOVE!", "ERROR!");
        }        
    }

    
/*  play() launches the game
    called in main so declared static
*/  
    public static void play(){

        // new JFrame() required as play() is a static method
        if(JOptionPane.showConfirmDialog(new JFrame(), "   Click OK to play", "  PLAY OTHELLO",
                                             JOptionPane.DEFAULT_OPTION) == 0){
                try{
                    System.out.println("Game Started");
                    new OthelloTwoPlayerGame();
                } catch(Exception e){ System.err.println(e.getCause()); }
        }                
        else  System.exit(0);
    }
    
    public static void main(String[] args) {
        endTime = System.currentTimeMillis();
        System.out.print(endTime - startTime);
        play();
    }
}
