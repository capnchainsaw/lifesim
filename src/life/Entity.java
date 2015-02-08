package life;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.JPanel;

/**
 * Simulated being!
 * @author jj@lindenlab.com
 */
public class Entity extends JPanel 
{
	/** Default serial ID */
	private static final long serialVersionUID = 1L;

	/**
	 * Chance of death in the simulation.
	 */
	private static final float CHANCE_OF_DEATH = 0.01f; //0.005f;
	
	/**
	 * Food needed to split into two entities.
	 */
	private static final int FOOD_FOR_MITOSIS = 10; //13;
	
	/**
	 * How long an entity grows before its body stops growing and its chance of death increases.
	 */
	private static final int PERIOD_OF_GROWTH = 3; //7;
	
	/**
	 * The lowest value the range of color variation can be.
	 */
	private static final int VARIATION_RANGE_MINIMUM = 1; //5;
	
	/**
	 * The last identifier given to an entity;
	 */
	private int lastIdentifier = 0;
	
	/**
	 * Identifier unique to this entity.
	 */
	private final int identifier;
	
	/**
	 * The age of this entity.
	 */
	private int age = 0;
	
	/**
	 * If this entity is dead.
	 */
	private boolean dead = false;
	
	/**
	 * The food this entity has. 
	 */
	private int food = 0;
	
	/**
	 * The location of this entity in the grid of lands.
	 */
	private Point location;
	
	/**
	 * The color of this entity.
	 */
	private Color color;
	
	/**
	 * The color of this entity and the new entity after mitosis.
	 */
	private Color splitColor;
	
	/**
	 * Create entity at the given location.
	 * @param x The starting x-coordinate of this entity in the grid of lands.
	 * @param y The starting y-coordinate of this entity in the grid of lands.
	 */
	public Entity( int x, int y )
	{
		lastIdentifier++;
		identifier = lastIdentifier;
		setSize( 3 * ( Life.landSize.width / 5 ), 3 * ( Life.landSize.height / 5 ) );
		setPreferredSize( getSize() );
		setLocation( Life.landSize.width / 5, Life.landSize.height / 5 );
		location = new Point( x, y );
		setOpaque( true );
		setEntityColor( 
			new Color( 
				80 + Life.rn.nextInt( x + y + 1 ), 
				80 + Life.rn.nextInt( x + 1 ), 
				80 + Life.rn.nextInt( y + 1 ) ) );
	}
	
	/**
	 * @return If this entity is dead.
	 */
	public boolean isDead()
	{
		return dead;
	}
	
	/**
	 * @return The age of this entity.
	 */
	public int getAge()
	{
		return age;
	}
	
	/**
	 * Set this entity's color to the given color.
	 * @param newColor New color for the entity.
	 */
	public void setEntityColor( Color newColor )
	{
		splitColor = color = newColor;
		setBackground( color );
	}
	
	/**
	 * Consume the given Entity.
	 * @param consumed The Component to be consumed if it is an Entity.
	 */
	public void consumeEntity( Component consumed )
	{
		if( Entity.class.isAssignableFrom( consumed.getClass() ) )
		{
			Entity entity = (Entity) consumed;
			
			// Gained food from consumed entity.
			food += 3;
			
			// Combine color into split color.
			// RED
			boolean neg = Life.rn.nextBoolean();
			int rDelta = entity.color.getRed() - splitColor.getRed();
			if( rDelta < 0 )
			{
				rDelta = rDelta * -1;
			}
			rDelta += VARIATION_RANGE_MINIMUM;
			if( rDelta == 0 ) rDelta = 1;
			
			int newRed = splitColor.getRed() + ( Life.rn.nextInt( rDelta ) * ( neg ? -1 : 1 ) );
			if( newRed > 245 ) newRed = 245;
			else if( newRed < 10 ) newRed = 10;
			
			// GREEN
			neg = Life.rn.nextBoolean();
			int gDelta = entity.color.getGreen() - splitColor.getGreen();
			if( gDelta < 0 )
			{
				gDelta = gDelta * -1;
			}
			gDelta += VARIATION_RANGE_MINIMUM;
			if( gDelta == 0 ) gDelta = 1;

			int newGreen = splitColor.getGreen() + ( Life.rn.nextInt( gDelta ) * ( neg ? -1 : 1 ) );
			if( newGreen > 245 ) newGreen = 245;
			else if( newGreen < 10 ) newGreen = 10;
			
			// BLUE
			neg = Life.rn.nextBoolean();
			int bDelta = entity.color.getBlue() - splitColor.getBlue();
			if( bDelta < 0 )
			{
				bDelta = bDelta * -1;
			}
			rDelta += VARIATION_RANGE_MINIMUM;
			if( bDelta == 0 ) bDelta = 1;

			int newBlue = splitColor.getBlue() + ( Life.rn.nextInt( bDelta ) * ( neg ? -1 : 1 ) );
			if( newBlue > 245 ) newBlue = 245;
			else if( newBlue < 10 ) newBlue = 10;
			
			// Create merged color and set as split color.
			Color mergedColor = 
				new Color( newRed, newGreen, newBlue );
			splitColor = mergedColor;
		}
	}
	
	/**
	 * Try to move the entity in the grid of lands.
	 * @return True if the move was successful, false if else.
	 */
	public boolean moveEntity()
	{
		try
		{
			// Nothing is done by dead entities.
			if( isDead() ) return false;
			
			// If already moved ahead in time then don't move.
			if( age >= Life.current().getAge() );
			age++;
			
			// Check if has food for mitosis.
			boolean mitosis = false;
			if( food >= FOOD_FOR_MITOSIS )
			{
				food -= FOOD_FOR_MITOSIS;
				mitosis = true;
			}
			
			// Check if dies.
			float increasedDeathChance = 0.0f;
			if( age > PERIOD_OF_GROWTH ) increasedDeathChance = 0.001f * ( age - PERIOD_OF_GROWTH );
			if( ( Life.rn.nextInt( 1001 ) / 1000.0f ) <= ( CHANCE_OF_DEATH + increasedDeathChance ) )
			{
				dead = true;
				setBackground( Color.white );
				// No movement unless dying during mitosis.
				if( !mitosis ) return false;
			}
			
			// Determine choice of action for entity / choice of split for mitosis.
			// 0 - do nothing, 1 - go north, 2 - go east, 3 - go south, 4 - go west
			int choice = ( mitosis ? Life.rn.nextInt( 4 ) + 1 : Life.rn.nextInt( 5 ) );
			if( choice > 0 )
			{
				Point target = new Point( location.x, location.y );
				switch( choice )
				{
				case 1: target.setLocation( target.x, target.y - 1 ); break;
				case 2: target.setLocation( target.x + 1, target.y ); break;
				case 3: target.setLocation( target.x, target.y + 1 ); break;
				case 4: target.setLocation( target.x - 1, target.y ); break;
				default: return false; // should be one of the above
				}
				try
				{
					Land origin = Life.current().getLand( location.x, location.y );
					Land expedition = Life.current().getLand( target.x, target.y );
					
					if( mitosis )
					{
						// Create new entity.
						Entity newEntity = new Entity( target.x, target.y );
						newEntity.setEntityColor( splitColor );
						setEntityColor( splitColor );
						// New entity consumes any previous inhabitants.
						while( expedition.getComponentCount() > 0 )
						{
							newEntity.consumeEntity( expedition.getComponent( 0 ) );
							expedition.remove( 0 );
						}
						// Move new entity to target land and change color to be origin lands color.
						expedition.add( newEntity );
					}
					else
					{
						// Take food from new lands.
						food++;
						// Consume any previous inhabitants.
						while( expedition.getComponentCount() > 0 )
						{
							consumeEntity( expedition.getComponent( 0 ) );
							expedition.remove( 0 );
						}
						// Move to target land and change color to be origin lands color.
						expedition.add( this );
						location = target;
					}
					expedition.setBackground( origin.getBackground() );
					return true;
				}
				catch ( Exception ex ) 
				{
					String message = ex.getMessage();
					if( !message.contains( "No land exists" ) )
					{
						System.err.println( 
							"Excepetion caught during transfer or mitosis of entity: " + ex.getMessage() );
						ex.printStackTrace();
					}
				}
			}
		}
		catch( Exception e )
		{
			System.err.println( "Exception moving entity: " + e.getMessage() );
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean equals( Object o )
	{
		if( Entity.class.isAssignableFrom( o.getClass() ) )
		{
			if( ((Entity) o).identifier == this.identifier ) return true;
		}
		return false;
	}
}
