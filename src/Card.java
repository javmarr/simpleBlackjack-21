import javax.swing.*;
import java.awt.*;
import java.util.*;

//The rectangle class gives the object x and y coordiante variables, width and height variables,
//with getWidth(), getHeight() methods, etc.  Also, there is a very useful 'contains( ... )' method
//to determine if a point lies within a given rectangle.
public class Card extends Rectangle
{

	//private attributes cannot be accessed outside of the class
	private String suit;
	private int number;

	//image for the front and back of the card
	private ImageIcon front;
	private ImageIcon back;

	//is the card face up or face down?
	private boolean faceup;

	//constructor
	Card()
	{
		suit = "joker";
		number = 0;
		faceup = false;
	}

	Card(int num, String s)
	{
		number = num;
		suit = s;
		faceup = false;
	}

	//flip card
	public void flip()
	{
		if(faceup)
		{
			faceup = false;

		}
		else
		{
			faceup = true;
		}
	}
	
	public boolean isFaceUp()
	{
		return faceup;
	}
	//assign a front image to card
	public void setImageIconFront(ImageIcon f)
	{
		front = f;
		width = f.getIconWidth();
		height = f.getIconHeight();
	}

	//assign a back image to card
	public void setImageIconBack(ImageIcon b)
	{
		back = b;
	}

	//move card to specified location
	public void setLocation(int newX, int newY)
	{
		x = newX;
		y = newY;
	}

	//public 'get' method to get a private attribute
	public String getSuit()
	{
		return suit;
	}

	//public 'get' method for number
	public int getNumber()
	{
		return number;
	}


	// 11, 12, 13, 1 should say jack, queen, king, ace respectively
	public String toString()
	{
		String output = "";

		if( number == 1 )
			output += "a";
		else if( number == 10)
			output += "t";
		else if( number == 11 )
			output += "j";
		else if( number == 12 )
			output += "q";
		else if( number == 13 )
			output += "k";
		else
			output += number;

		output += suit;

		return output;
	}

	//method that tells card how to draw itself
	public void draw(Graphics g, Component c)
	{
		if(faceup)
			g.drawImage(front.getImage(), x,y, c);
		else
			g.drawImage(back.getImage(), x,y, c);
	}

}


class DeckOfCards
{
	private ArrayList<Card> cardRay;

	DeckOfCards()
	{
		cardRay = new ArrayList<Card>();
		String[] temp = {"h", "s", "d", "c"};
		String str = "";
		for(int i = 1; i<14;i++)
		{
			for(String suit:temp)
			{
				Card newCard = new Card(i,suit);
				str = newCard.toString();
				newCard.setImageIconFront( new ImageIcon(String.format("../cards/%s.gif", str)));
				newCard.setImageIconBack(new ImageIcon("../cards/b.gif"));
				cardRay.add(newCard);
				//System.out.println("[Added] " + newCard);
			}
		}
	}

	DeckOfCards(int start, int end, ArrayList<String> strList)
	{
		cardRay = new ArrayList<Card>();

		String[] temp = new String[strList.size()];
		temp = strList.toArray(temp);
		for(int i = start; i<end+1;i++)
		{
			for(String suit:temp)
			{
				Card newCard = new Card(i,suit);
				cardRay.add(newCard);
				//System.out.println("[Added] " + newCard);
			}
		}

	}

	public void shuffle()
	{
		long seed = System.nanoTime();
		Collections.shuffle( cardRay, new Random(seed));
	}


	public Card dealTopCard()
	{
		Card topCard;
		topCard = cardRay.get(cardRay.size()-1);
		cardRay.remove(cardRay.size()-1);
		return topCard;

	}

	public String toString()
	{
		String output = "";
		for(Card c : cardRay)
		{
			System.out.println(c);
		}
		return output;
	}


}
