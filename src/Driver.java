import javax.swing.*;


public class Driver
{
	public static void main(String [] args)
	{
		//create a frame
		JFrame frame = new JFrame("Cheap 21");

		//create a panel, put panel in frame
		CardPanel panel = new CardPanel();
				
	    JButton button = new JButton();
	
		if(panel.gameOver)
		{
			button.setText("Click me to show dialog!");
			frame.add(button);
			frame.pack();
		}
		frame.getContentPane().add(panel);
		
		//make frame visible, set frame size
		frame.setVisible(true);
		frame.setSize(797,600);



	}
}