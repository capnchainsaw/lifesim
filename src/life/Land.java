package life;

import java.awt.Color;
import javax.swing.JPanel;

/**
 * Simulated land!
 * @author jj@lindenlab.com
 */
public class Land extends JPanel 
{
	/** Default serial ID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create land at the given location.
	 * @param x The x-coordinate this land is in the grid of lands.
	 * @param y The y-coordinate this land is in the grid of lands.
	 */
	public Land( int x, int y )
	{
		setSize( Life.landSize );
		setPreferredSize( getSize() );
		setLocation( x * getWidth(), y * getHeight() );
		setOpaque( true );
		setBackground( new Color( x + x, y + y, x + y ) );
	}
}
