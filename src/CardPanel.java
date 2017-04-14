import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//CardPanel inherits from JPanel and implements MouseListener and MouseMotionListener interfaces

public class CardPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener
{
	
	//deck and card items
	ImageIcon topOfDeck = new ImageIcon("c");
	ImageIcon table = new ImageIcon("../cards/tableTop.gif");
	ArrayList<Card> playerCards, aiCards;
	DeckOfCards gameDeck;
	
	//integers used to set location on panel
	int playerX = 129, playerY = 442;
	int aiX = 129, aiY = 52 ;
	int deckX = 49 , deckY = 225 ;
	int cardSpace = 120;
	int playerMoved = 1, aiMoved = 1;
	int playerWins = 0, aiWins = 0, draws = 0;
	
	//sum of hands
	int[] playerSum, aiSum;
	
	//booleans
	boolean playerTurn, playerWon,
			aiTurn, aiWon, gameOver;
	
	//initiate buttons
	JButton hit = new JButton("HIT");
	JButton stand = new JButton("STAND");
	JButton reset = new JButton("RESET");
	String outcome = "";
	CardPanel()
	{

		createGameSession();
		
		//set up the buttons
		add(hit);
		add(stand);
		add(reset);		
		hit.addActionListener(this);
		stand.addActionListener(this);
		reset.addActionListener(this);
		
		//actions
		hit.setActionCommand("hit");
		stand.setActionCommand("stand");
		reset.setActionCommand("reset");
		addMouseListener(this); //To detect "mouse clicked" events
		addMouseMotionListener(this); //to detect "mouse dragged" events
		setFocusable(true);
	}

	public void createGameSession()
	{
		//create a deck of cards to use
		gameDeck = new DeckOfCards();
		playerCards = new ArrayList<Card>();
		aiCards = new ArrayList<Card>();
		
		//set up the sum values, 2 to find either ace as 1 or 11 for both
		playerSum = new int[2];
		aiSum = new int[2];
		
		//set booleans
		gameOver = false;
		playerWon = false;
		aiWon = false;
		playerTurn = true;
		aiTurn = false;
		
		//explain what the buttons do
		hit.setToolTipText("Ask dealer to deal one more card");
		stand.setToolTipText("Stay with your current cards");
		reset.setToolTipText("Reset the game and try again");
		
		//draw cards into player/ai hands
		for(int i = 0; i<2; i++)
		{
			dealCardToHand(playerCards,gameDeck,'p');
			dealCardToHand(aiCards,gameDeck,'a');
		}
		
		for(int i = 0; i < playerCards.size(); i++) //flip all the cards the player has
		{
			playerCards.get(i).flip();
		}
		aiCards.get(0).flip(); //flip only one card the ai has
		getSumOfHand(playerCards, playerSum);
		getSumOfHand(aiCards, aiSum);
	}
	public void paintComponent(Graphics g)
	{
		//clear screen
		super.paintComponent(g);
		
		//display table
		table.paintIcon(this,g,0,0);
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
     
		g.setColor(Color.white);
    
		//display the sum of the hands for convenience
		g.drawString(String.format("Player's Sum = %s - %s",playerSum[0], playerSum[1]), playerX, playerY + 115);
		g.drawString(String.format("AI's Sum = %s - %s",aiSum[0], aiSum[1]), aiX, aiY + 115);
		g.drawString(String.format("--Score--  Player: %s   AI: %s   Draws: %s",playerWins, aiWins,draws), 275, 300);
		
		if(gameOver)
		{
			g.drawString(String.format("%s", outcome), deckX + 200, deckY + 150);
		}
		//paint the deck
		topOfDeck.paintIcon(this, g, deckX, deckY);
		
		//paint the cards
		for(int i = 0; i < playerCards.size(); i++)
		{
			playerCards.get(i).draw(g,this);
		}
		for(int i = 0; i < aiCards.size(); i++)
		{
			aiCards.get(i).draw(g,this);
		}
	}

	public void getSumOfHand(ArrayList<Card> hand, int[] theSum)
	{
		theSum[0]=0; theSum[1]=0;
		//System.out.println("Found sum of hand");
		for(int i=0; i<hand.size(); i++)
		{
			if(hand.get(i).isFaceUp())
			{
				//System.out.println("Card number: " + (i+1) + " is face up");
				if(hand.get(i).getNumber() == 1) //if it's an ace
				{
					//get the sum if counted as a '1'
					theSum[0] += 1;
						
					//get the sum if counted as an '11'
					theSum[1] += 11;
				}
				else if(hand.get(i).getNumber() > 10) //if it's a jack, queen, or king it's worth 10
				{
					theSum[0] += 10;
					theSum[1] += 10;
				}
				else //else it's a card with normal face value
				{
					theSum[0] += hand.get(i).getNumber();
					theSum[1] += hand.get(i).getNumber();
				}
			}
		}
	}
	public void dealCardToHand(ArrayList<Card> hand, DeckOfCards theDeck, char ch)
	{
		//shuffle the deck to randomize the cards
		theDeck.shuffle();
		int temp = 0;
		
		//deal a card
		//System.out.println("Hit hand with card");
		hand.add(theDeck.dealTopCard());
		
		
		switch(ch)	//set the location of the new card
		{
			case 'p': //player was delt 
				//update playerSum
			
				hand.get(hand.size()-1).setLocation(playerX +((hand.size()-playerMoved)*cardSpace), playerY);
				if(hand.size()>=3)
					hand.get(hand.size()-1).flip();
				getSumOfHand(hand, playerSum);
				break;
				
 			case 'a': //ai was delt
 				
 				hand.get(hand.size()-1).setLocation(aiX + ((hand.size()-aiMoved)*cardSpace), aiY);
 				if(hand.size()>=3)
					hand.get(hand.size()-1).flip();
				getSumOfHand(hand, aiSum);
 				break;
		}
	}

	
	public void aiPlays()
	{
		while(aiTurn)
		{	
			//flip the second card in ai's hand
			if(!(aiCards.get(1).isFaceUp()))
				aiCards.get(1).flip();
			//System.out.println("Flipped ai's card");
			getSumOfHand(aiCards,aiSum);
			
			
			//stand if ai has number >=18 or has a better hand than player
			if(greatestNonBustedNumber(aiSum) >= 18 || 
				greatestNonBustedNumber(aiSum) >= greatestNonBustedNumber(playerSum))
			{
				//stand
				System.out.println("Stand ai");
				aiTurn = false;
				checkWinner();
			}
			
			else// if(aiSum[1] <=17)	//hit
			{
				dealCardToHand(aiCards,gameDeck,'a');
				checkWinner();
			}
			
			
		}
	}
	
	public int greatestNonBustedNumber(int[] theSum)
	{
		int gNBV = 0;
		
		if(theSum[1] <= 21) //sum[1] is always better than [0] if it's <= 21
			gNBV = theSum[1];
		else
			gNBV = theSum[0];
		
		return gNBV;
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{ 
		//"hit" and "stand" can only be pressed if the game isn't over
		if(playerTurn && !gameOver)
		{
			if ("hit".equals(e.getActionCommand()))
			{
				System.out.println("Hit me");
				dealCardToHand(playerCards, gameDeck, 'p');
				checkWinner();
			}
			else if("stand".equals(e.getActionCommand()))
			{
				System.out.println("Stand");
				playerTurn = false;
				aiTurn = true;
				aiPlays();
			}
			repaint();
		}
		//let the user reset after winning/losing/draw
		else if("reset".equals(e.getActionCommand()))
		{
			createGameSession();
			repaint();
		}
	}
	
	public void checkWinner()
	{
		
		//System.out.println("Check for a winner1");
		
		if(playerTurn) //player busted
		{
			if(playerSum[0] > 21)
			{
				System.out.println("Player busted");
				playerTurn = false;
				aiTurn = false;
				aiWon = true;
				gameOver = true;
			}
		}
		
		else if(aiTurn && !aiWon) //ai busted
		{
			if(aiSum[0] > 21)
			{
				System.out.println("AI busted");
				aiTurn = false;
				playerWon = true;
				gameOver = true;
			}
		}
		
		else if(!playerTurn && !aiTurn) //player and ai stand
		{
			
			//check values and size of hand to determine winner
			
			if(greatestNonBustedNumber(playerSum) > greatestNonBustedNumber(aiSum)) //player number is greater
			{
				playerWon = true;
			}
			else if(greatestNonBustedNumber(playerSum) < greatestNonBustedNumber(aiSum)) //ai number is greater
			{
				aiWon = true;
			}
			else //numbers are equal
			{
				if(playerCards.size() >= aiCards.size()) //player got same value with equal or more cards
					playerWon = true;
				
				if(playerCards.size() <= aiCards.size()) //ai got same value with equal or more cards
					aiWon = true;
			}
			
			gameOver = true;
			
		}
		
		if(gameOver)
		{
			if(playerWon && aiWon)
			{
				outcome = "DRAW";
				draws++;
			}
			else if(playerWon)
			{
				outcome = "YOU WIN";
				playerWins++;
			}
			else
			{
				outcome = "YOU LOSE";
				aiWins++;
			}
		}

		
	}
	
	
	/////////MouseListener methods///////////////////
	public void mouseClicked(MouseEvent me)
	{
		/*
		//check if click occurred on one of the cards, if so flip card
		int x = me.getX();
		int y = me.getY();
		//System.out.println("Click " + x + ", " + y);
		
		for(int i = 0; i<playerCards.size(); i++)
		{
			if(playerCards.get(i).contains(x,y))
			{
				playerCards.get(i).flip();
				System.out.println("Flipped the card with number: " + playerCards.get(i).getNumber());
			}
		}
		
		for(int i = 0; i<aiCards.size(); i++)
		{
			if(aiCards.get(i).contains(x,y))
			{
				aiCards.get(i).flip();
				System.out.println("Flipped the card with number: " + aiCards.get(i).getNumber());
			}
		}
		*/
		repaint();
	}

	public void mouseEntered(MouseEvent me)
	{

	}

	public void mouseExited(MouseEvent me)
	{

	}

	public void mousePressed(MouseEvent me)
	{

	}

	public void mouseReleased(MouseEvent me)
	{

	}



	//////////MouseMotionListener methods////////////////
	public void mouseDragged(MouseEvent me)
	{
//		//check if you are over a card.  If so, move the card to your mouse position
//		int x = me.getX();
//		int y = me.getY();
//		System.out.println("Drag");
//		if(jack.contains(x,y))
//		{
//			jack.setLocation(x,y);
//			System.out.println("Jack moved");
//		}
//		else if(queen.contains(x,y))
//		{
//			queen.setLocation(x,y);
//			System.out.println("Queen moved");
//		}
//
//		else if(king.contains(x,y))
//		{
//			king.setLocation(x,y);
//			System.out.println("King moved");
//		}
//
//		repaint();
	}

	public void mouseMoved(MouseEvent me)
	{
	}


}