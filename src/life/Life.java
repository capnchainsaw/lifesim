package life;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.*;

/**
 * Simulation! 
 * Mucking about on the train w/ no interwebs.
 * @author jj@lindenlab.com
 */
public class Life extends JFrame
{
	/** Default serial ID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The current running simulation.
	 */
	private static Life currentLife;
	
	/**
	 * The current monitor for the simulation.
	 */
	private static LifeMonitor currentMonitor = null;
	
	/**
	 * The thread for running the current simulation.
	 */
	private static Thread lifeThread;
	
	/**
	 * The randomizer for the simulation.
	 */
	public static Random rn = new Random();
	
	/**
	 * The age of this life.
	 */
	private int age = 0;
	
	/**
	 * The lands of this life
	 */
	private Land[][] lands = new Land[60][60];

	/**
	 * The size of a land in this life.
	 */
	public static Dimension landSize = new Dimension( 10, 10 );
	
	/**
	 * Start a new life simulation.
	 */
	public Life()
	{
		// Setup window.
		setTitle("Simulating survival");
		setSize( 
			lands.length * landSize.width, 
			lands[0].length * landSize.height );
		setPreferredSize( getSize() );
		setResizable( false );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		JPanel stage = new JPanel();
		stage.setSize( getSize() );
		stage.setPreferredSize( stage.getSize() );
		stage.setLayout( null );
		
		// Setup lands.
		int spawned = 0;
		for( int x = 0; x < lands.length; x++ )
		{
			for( int y = 0; y < lands.length; y++ )
			{
				lands[x][y] = new Land( x, y );
				
				// Create entities in land.
				int chance = rn.nextInt(500);
				if( chance < 10 )
				{
					lands[x][y].add( new Entity( x, y ) );
					spawned++;
				}
				
				stage.add( lands[x][y] );
			}
		}
		
		if( currentMonitor != null )
		{
			currentMonitor.reportEntityPopulation( spawned, 0 );
		}
		
		setContentPane( stage );
	}
	
	/**
	 * @return The age of this life.
	 */
	public int getAge()
	{
		return age;
	}
	
	/**
	 * Get the land at the given location in the grid of lands.
	 * @param x
	 * @param y
	 * @return The requested land.
	 * @throws Exception For invalid coordinates or non-existent land.
	 */
	public Land getLand( int x, int y ) 
		throws Exception
	{
		if( ( x >= lands.length ) || ( x < 0 ) )
			throw new Exception( "No land exists at " + x + ", " + y + "! X-coordinate out of bounds." );
		if( ( y >= lands[x].length ) || ( y < 0 ) )
			throw new Exception( "No land exists at " + x + ", " + y + "! Y-coordinate out of bounds." );
		if( lands[x][y] == null )
			throw new Exception( "No land exists at " + x + ", " + y + "! Land is null." );
		return lands[x][y];
	}
	
	/**
	 * @return The current running simulation.
	 */
	public static Life current()
	{
		return currentLife;
	}
	
	public static void main( String[] args )
	{
		// Setup simulation monitor.
		currentMonitor = new LifeMonitor();
		currentMonitor.setVisible( true );
		
		// Setup simulation.
		currentLife = new Life();
		currentLife.setVisible( true );
		
		// Move monitor to be next to the simulation.
		currentMonitor.setLocation( 
			currentLife.getLocation().x + currentLife.getSize().width, 
			currentLife.getLocation().y );
		
		// Setup simulation thread and start simulation.
		lifeThread = new Thread( new Runnable()
		{
			@Override
			public void run() 
			{
				try 
				{
					Thread.sleep( 100 );
					
					currentLife.age++;
					
					SwingUtilities.invokeAndWait( new Runnable()
					{
						@Override
						public void run() 
						{
							int living = 0;
							int dead = 0;
							int oldest = 0;
							// Cycle through lands and move entities.
							for( int x = 0; x < currentLife.lands.length; x++ )
							{
								for( int y = 0; y < currentLife.lands[x].length; y++ )
								{
									try
									{
										Land currentLand = currentLife.getLand( x, y );
										if( currentLand.getComponentCount() > 0 )
										{
											Component c = currentLand.getComponent( 0 );
											if( Entity.class.isAssignableFrom( c.getClass() ) )
											{
												Entity entity = (Entity) c;
												if( entity.isDead() )
												{
													dead++;
												}
												else
												{
													living++;
													if( entity.getAge() > oldest ) oldest = entity.getAge();
													entity.moveEntity();
												}
											}
										}
									}
									catch( Exception ex1 )
									{
										System.err.println( "Error moving entity: " + ex1.getMessage() );
									}
								}
							}
							// Refresh UI.
							currentLife.repaint();
							if( currentMonitor != null )
							{
								currentMonitor.reportEntityPopulation( living, dead );
								currentMonitor.recordOldestLivingEntity( oldest );
							}
						}
					});
				} 
				catch ( Exception e ) 
				{
					e.printStackTrace();
				}
				
				run();
			}
		});
		lifeThread.start();
	}
}
