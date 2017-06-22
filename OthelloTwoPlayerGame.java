package Othello;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


class Coordinates{ int x,y; }


public final class OthelloTwoPlayerGame extends JFrame implements ActionListener {
    /**
     * 
     */	
    private static final long serialVersionUID = 1L;


    boolean isPlayerBlackTurn;

    Color defaultColour = Color.orange,	
          flipColour;

    JPanel[] row = new JPanel[10];

    JButton button[][] = new JButton[8][8],
            exit       = new JButton("Exit"),
            newGame    = new JButton("New Game");

    int[] dimW = {300,70,150},
          dimH = {30,40,30};

    Dimension displayDimension = new Dimension(dimW[0], dimH[0]),
              ButtonDimension  = new Dimension(dimW[1], dimH[1]),
              newGameDimension = new Dimension(dimW[2], dimH[2]); 

    JTextArea display = new JTextArea(" Player Black's turn", 1, 10);

    JTextField[] score = new JTextField[2];

    JLabel player[] = new JLabel[2];

    Font font1 = new Font("Times new Roman", Font.BOLD, 28),
         font2 = new Font("Times new Roman", Font.BOLD, 20);

    FlowLayout f1 = new FlowLayout(FlowLayout.CENTER),
               f2 = new FlowLayout(FlowLayout.CENTER,1,1);
    /* 1,1 in f2 sets horizontal & vertical gap between components as 1,1
     * while f1 sets the default gap of 5,5
     * hence f2 permits greater screen size reduction(if required) while preserving clarity  
     */

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
                button[i][j].addActionListener(OthelloTwoPlayerGame.this);
                button[i][j].setPreferredSize(ButtonDimension);
            }
        }

        // Exit Button 
        buttonSetting(exit);

        // NewGame Button 
        buttonSetting(newGame);

        // Display TextArea Description
        display.setFont(font1);
        display.setEditable(false);
        display.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        display.setPreferredSize(displayDimension);

        // Addition of newGame, Exit, Display to row[0]
        row[0].add(newGame);
        row[0].add(display);
        row[0].add(exit);
        add(row[0]);

        // Addition of play buttons to rows 1 to 8
        for(int i = 8; i > 0; i--){		
            for(int j = 0; j < 8; j++) 
                row[i].add(button[i - 1][j]);
            add(row[i]);
        }

        // Score TextFields and Player Labels Description and addition to row[9]
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

        // Addition of Player Labels and Score TextFields to row[9]
        
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

    public void buttonSetting(JButton setButton){
        setButton.setEnabled(true);
        setButton.setForeground(Color.red);
        setButton.setBackground(Color.gray);
        setButton.setFont(font2);
        setButton.addActionListener(this);
        setButton.setPreferredSize(newGameDimension);
        setDesign2();
    }


    // This function sets the design
    public final void setDesign() {
        try { 
           UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        }         catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
    }

    public void setDesign2() {
        try {
           UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
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
        display.setText(" Player Black's turn");

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
                && !(button[left.x][left.y].getBackground().equals(defaultColour))){

            while((left.x >= 0) 
                    && (button[left.x][left.y].getBackground().equals(flipColour)))
                left.x--;

            if((left.x == -1) 
                    || (button[left.x][left.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

            else for(int i = (left.x + 1); i <= buttonCoordinates.x; i++)
                button[i][left.y].setBackground(currentPlayerColor);
        }


        Coordinates right = temporaryButtonCoordinates;
        //for converting pieces in right(East) of clicked Button
        right.x = buttonCoordinates.x + 1;
        right.y = buttonCoordinates.y;

        if((right.x <= 7) 
                && !(button[right.x][right.y].getBackground().equals(defaultColour))){

            while((right.x <= 7) 
                    && (button[right.x][right.y].getBackground().equals(flipColour)))
                right.x++;

            if((right.x == 8)
                    || (button[right.x][right.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

            else for(int i = (right.x - 1); i >= buttonCoordinates.x; i--)
                button[i][right.y].setBackground(currentPlayerColor);
        }


        Coordinates north = temporaryButtonCoordinates;
        //for converting pieces in north of clicked Button
        north.x = buttonCoordinates.x;
        north.y = buttonCoordinates.y + 1;

        if((north.y <= 7) 
                && !(button[north.x][north.y].getBackground().equals(defaultColour))){

            while((north.y <= 7) 
                    && (button[north.x][north.y].getBackground().equals(flipColour)))
                north.y++;

            if((north.y == 8) 
                    || (button[north.x][north.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

            else for(int i = (north.y - 1); i >= buttonCoordinates.y; i--)
                button[north.x][i].setBackground(currentPlayerColor);
        }


        Coordinates south = temporaryButtonCoordinates;
        //for converting pieces in south of clicked Button		
        south.x = buttonCoordinates.x;
        south.y = buttonCoordinates.y - 1;

        if((south.y >= 0) 
                && !(button[south.x][south.y].getBackground().equals(defaultColour))){

            while((south.y >= 0) 
                    && (button[south.x][south.y].getBackground().equals(flipColour)))
                south.y--;

            if((south.y == -1) 
                    || (button[south.x][south.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

            else for(int i = (south.y + 1); i <= buttonCoordinates.y; i++)
                button[south.x][i].setBackground(currentPlayerColor);
        }


        Coordinates northWest = temporaryButtonCoordinates;
        //for converting pieces in northWest of clicked Button 	
        northWest.x = buttonCoordinates.x - 1;
        northWest.y = buttonCoordinates.y + 1;

        if((northWest.x >= 0) && (northWest.y <= 7) 
            && !(button[northWest.x][northWest.y].getBackground().equals(defaultColour))){

            while((northWest.x >= 0) && (northWest.y <= 7) 
                && (button[northWest.x][northWest.y].getBackground().equals(flipColour))){
                northWest.y++;
                northWest.x--;
            }

            if((northWest.x == -1) || (northWest.y == 8) 
                || (button[northWest.x][northWest.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

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
            && !(button[northEast.x][northEast.y].getBackground().equals(defaultColour))){

            while((northEast.x <= 7) && (northEast.y <= 7) 
                  && (button[northEast.x][northEast.y].getBackground().equals(flipColour))){
                northEast.y++;
                northEast.x++;
            }

            if((northEast.x == 8) || (northEast.y == 8) 
                || (button[northEast.x][northEast.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

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
            && !(button[southEast.x][southEast.y].getBackground().equals(defaultColour))){

            while((southEast.x <= 7) && (southEast.y >= 0) 
                  && (button[southEast.x][southEast.y].getBackground().equals(flipColour))){
                southEast.y--;
                southEast.x++;
            }

            if((southEast.x == 8) || (southEast.y == -1) 
                || (button[southEast.x][southEast.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

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
            && !(button[southWest.x][southWest.y].getBackground().equals(defaultColour))){

            while((southWest.x >= 0) && (southWest.y >= 0) 
                && (button[southWest.x][southWest.y].getBackground().equals(flipColour))){
                southWest.x--;
                southWest.y--;
            }

            if((southWest.x == -1) || (southWest.y == -1) 
                || (button[southWest.x][southWest.y].getBackground().equals(defaultColour))){ /*do nothing*/ }

            else for(int i = (southWest.y + 1); i <= buttonCoordinates.y; i++){
                southWest.x++;
                button[southWest.x][i].setBackground(currentPlayerColor);
            }
        }

        // Sets all invalid buttons disabled		
        for(int i = 0; i < 8;i++){
            for(int j = 0; j < 8; j++){	
                // Foreground is used to identify valid buttons
                button[i][j].setForeground(Color.gray);
                if((button[i][j].getBackground().equals(Color.orange)))
                    button[i][j].setEnabled(false);
            }
        }
    }	


    //This function shows if there are any possible moves
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
                && !(button[left.x][left.y].getBackground().equals(defaultColour))){

            while((left.x >= 0)
                    && (button[left.x][left.y].getBackground().equals(flipColour)))
                left.x--;

            if((left.x == -1) 
                    || (button[left.x][left.y].getBackground().equals(defaultColour))){
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
                && !(button[right.x][right.y].getBackground().equals(defaultColour))){

            while((right.x <= 7) 
                    && (button[right.x][right.y].getBackground().equals(flipColour)))
                right.x++;

            if((right.x == 8) 
                    || (button[right.x][right.y].getBackground().equals(defaultColour))){
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
                && !(button[north.x][north.y].getBackground().equals(defaultColour))){

            while((north.y <= 7) 
                    && (button[north.x][north.y].getBackground().equals(flipColour)))
                north.y++;

            if((north.y == 8) 
                    || (button[north.x][north.y].getBackground().equals(defaultColour))){
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
                && !(button[south.x][south.y].getBackground().equals(defaultColour))){

            while((south.y >= 0)
                    && (button[south.x][south.y].getBackground().equals(flipColour)))
                south.y--;

            if((south.y == -1)
                    || (button[south.x][south.y].getBackground().equals(defaultColour))){
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
           && !(button[northWest.x][northWest.y].getBackground().equals(defaultColour))){

            while((northWest.x >= 0) && (northWest.y <= 7) 
                && (button[northWest.x][northWest.y].getBackground().equals(flipColour))){
                northWest.y++;
                northWest.x--;
            }

            if((northWest.x == -1) || (northWest.y == 8) 
              || (button[northWest.x][northWest.y].getBackground().equals(defaultColour))){
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
           && !(button[northEast.x][northEast.y].getBackground().equals(defaultColour))){

            while((northEast.x <= 7) && (northEast.y <= 7) 
                   && (button[northEast.x][northEast.y].getBackground().equals(flipColour))){
                northEast.y++;
                northEast.x++;
            }

            if((northEast.x == 8) || (northEast.y == 8) 
               || (button[northEast.x][northEast.y].getBackground().equals(defaultColour))){
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
           && !(button[southEast.x][southEast.y].getBackground().equals(defaultColour))){

            while((southEast.x <= 7) && (southEast.y >= 0) 
                && (button[southEast.x][southEast.y].getBackground().equals(flipColour))){
                southEast.y--;
                southEast.x++;
            }

            if((southEast.x == 8) || (southEast.y == -1) 
               || (button[southEast.x][southEast.y].getBackground().equals(defaultColour))){
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
           && !(button[southWest.x][southWest.y].getBackground().equals(defaultColour))){

            while((southWest.x >= 0) && (southWest.y >= 0)
                && (button[southWest.x][southWest.y].getBackground().equals(flipColour))){
                southWest.y--;
                southWest.x--;
            }

            if((southWest.x == -1) || (southWest.y == -1)
                || (button[southWest.x][southWest.y].getBackground().equals(defaultColour))){
                //do nothing
            }
            else for(int i = (southWest.y + 1); i <= (buttonCoordinates.y - 1); i++){
                southWest.x++;
                Flippable = true;
            }
        }
        return Flippable;
    }


    // This function shows possible moves for current player if any 
    public void showPossibleMoves(Color nextPlayerColour){

        JButton nextPossibleMove;
        
        Coordinates buttonCoordinates = temporarySourceButtonCoordinates; 
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){

                nextPossibleMove = button[i][j];
                buttonCoordinates.x = i;
                buttonCoordinates.y = j;

                if(nextPossibleMove.getBackground().equals(defaultColour)){
                    // Checking if any possible move exists
//                     boolean anyPossibleMove = anyPossibleMoves(buttonCoordinates, nextPlayerColour);

                    if(anyPossibleMoves(buttonCoordinates, nextPlayerColour)) {
                        nextPossibleMove.setForeground(Color.RED);
                        nextPossibleMove.setEnabled(true);
                        // Red foreground can't be seen by players but can be seen by computer.
                        // Red foreground helps computer to identify valid buttons. 
                    } 
                }
            }
        }
    }

 
    // This function displays Current Player and intermediate & final result of game
    public void getGameStatus(Color playerColour){

        Color nextPlayerColour,
              currentPlayerColour;
        
//        int countPossibleMovesForNextPlayer = 0,
//            countPossibleMovesForCurrentPlayer = 0;
        
        int blackScore = 0,
            whiteScore = 0;
    
        boolean currentPlayerHasMoves = false,
                nextplayerHasMoves = false;

        // Score Calculation
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8;j++){
                if (button[i][j].getBackground().equals(Color.BLACK))
                    blackScore++;
                else if (button[i][j].getBackground().equals(Color.WHITE))
                    whiteScore++;
            }
        }
        
        score[0].setText( "" + blackScore);
        score[1].setText( "" + whiteScore);


        // Obtaining status of game
        isPlayerBlackTurn = !isPlayerBlackTurn;	

        if (isPlayerBlackTurn)
           nextPlayerColour = Color.BLACK;

        else  nextPlayerColour = Color.WHITE;			

        showPossibleMoves(nextPlayerColour);

        
        // Counting possible moves for next player
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(button[i][j].getForeground().equals(Color.RED)){
                    // Red foreground was set using showPossibleMoves()
                    // countPossibleMovesForNextPlayer++;
                    nextplayerHasMoves = true;
                    break;
                }
            }
        }

        if(!nextplayerHasMoves/*countPossibleMovesForNextPlayer == 0*/){
                // i.e. next player has no valid move

            isPlayerBlackTurn = !isPlayerBlackTurn;	

            if(isPlayerBlackTurn)
               currentPlayerColour = Color.BLACK;
            else  currentPlayerColour = Color.WHITE;

            showPossibleMoves(currentPlayerColour);

            // Counting possible moves for current player
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(button[i][j].getForeground().equals(Color.RED)){
                        // Red foreground was set using showPossibleMoves()
                        // countPossibleMovesForCurrentPlayer++;
                        currentPlayerHasMoves = true;
                        break;
                    }
                }
            }

            if(!currentPlayerHasMoves/*countPossibleMovesForCurrentPlayer == 0*/){
                // i.e. current player also has no valid move
                // implying no player has any valid move left i.e. the board is full
                display.setText(" ");

                if(blackScore == whiteScore)
                    JOptionPane.showMessageDialog(this, "THIS MATCH IS A DRAW!");
                else if(blackScore > whiteScore)
                    JOptionPane.showMessageDialog(this, "CONGRATULATIONS \n PLAYER BLACK WINS!");

                else JOptionPane.showMessageDialog(this, "CONGRATULATIONS  \n PLAYER WHITE WINS!");

                
                int n = JOptionPane.showConfirmDialog(this, "Play Again ?", "Yes to play. No to exit", JOptionPane.YES_NO_OPTION);
                if (n == 0)  {
                    try{
                        System.out.println("Played Again");
                        new OthelloTwoPlayerGame();
                    } catch(Exception e){ System.err.println(e.getCause()); } 
                }
                else if(n == 1){
                    System.out.println("Exited Game");
                    System.exit(0);
                }
            }

            else{
                if(isPlayerBlackTurn){
                    display.setText("Player White:NO VALID MOVE.Player Black's turn ");
                    playerColour = Color.BLACK;
                }
                else {
                    display.setText("Player Black:NO VALID MOVE.Player White's turn");
                    playerColour = Color.WHITE;
                }
            }
        }

        else {
            if(isPlayerBlackTurn){
                display.setText(" Player Black's turn");
                playerColour = Color.BLACK;
            }
            else {
                display.setText(" Player White's turn");
                playerColour = Color.WHITE;
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent ae) {

        JButton clickedButton = (JButton)ae.getSource();
        Color playerColour;
        Coordinates buttonCoordinates = temporarySourceButtonCoordinates;

        if(clickedButton.getBackground().equals(defaultColour)){

            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                    if(clickedButton == button[i][j]){
                        buttonCoordinates.x = i;
                        buttonCoordinates.y = j;
                    }
            

            if(isPlayerBlackTurn)
                playerColour = Color.BLACK;
            // clickedButton.setBackground(Color.BLACK);

            else  playerColour = Color.WHITE;
            //clickedButton.setBackground(Color.WHITE);

            // Clicked Button's background need not be changed here as it is changed in changeColour
            changeColour( buttonCoordinates, playerColour );
            getGameStatus( playerColour );
        }

        else if(clickedButton == exit){
            if(JOptionPane.showConfirmDialog(this,"EXIT CURRENT GAME?", "",JOptionPane.YES_NO_OPTION) == 0){
                System.out.println("Exited Game");
                System.exit(0);
            }
        }
        
        else if(clickedButton == newGame){
            if(JOptionPane.showConfirmDialog(this,"START NEW GAME?","",JOptionPane.YES_NO_OPTION) == 0)  {
                System.out.println("New Game");
                initialise();
            }
        }
        
        else {
            JOptionPane.showMessageDialog(this, "INVALID MOVE!", "ERROR!",JOptionPane.ERROR_MESSAGE);
        }        
    }

    public static void play(){

        JFrame frame = new JFrame();

        if(JOptionPane.showConfirmDialog(frame, "   Click OK to play", "  PLAY OTHELLO",JOptionPane.DEFAULT_OPTION) == 0){
            try{
                System.out.println("Game Started");
                new OthelloTwoPlayerGame();
            } catch(Exception e){ System.err.println(e.getCause()); }
        }
        else  System.exit(0);
    }

    public static void main(String[] args) {
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        play();
    }
}
