package life;

import java.awt.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Simulation live statistics!
 * @author jj@lindenlab.com
 */
public class LifeMonitor extends JFrame 
{
	/** Default */
	private static final long serialVersionUID = 1L;

	/**
	 * Entity population that is not dead estimation label.
	 */
	private Label entityLivingPopulation = new Label();

	/**
	 * Entity population that is dead estimation label.
	 */
	private Label entityDeadPopulation = new Label();

	/**
	 * Oldest living entity label.
	 */
	private Label oldestLivingEntity = new Label();

	/**
	 * Storage for the oldest entity recorded. 
	 */
	private int recordedOldestEntity = 0;
	
	/**
	 * Oldest entity ever label.
	 */
	private Label oldestEntityEver = new Label();
	
	/**
	 * Setup the new life monitor.
	 */
	public LifeMonitor()
	{
		// Setup window.
		setTitle("Simulating survival");
		setSize( 150, 400 );
		setPreferredSize( getSize() );
		setResizable( false );
		setDefaultCloseOperation( EXIT_ON_CLOSE );

		JPanel monitorPanel = new JPanel();
		monitorPanel.setSize( getSize() );
		monitorPanel.setPreferredSize( monitorPanel.getSize() );
		setContentPane( monitorPanel );
		
		// Setup entity population estimation UI.
		entityLivingPopulation.setSize( 130, 80 );
		entityLivingPopulation.setPreferredSize( entityLivingPopulation.getSize() );
		entityLivingPopulation.setLocation( 10, 20 );
		monitorPanel.add( entityLivingPopulation );
		
		// Setup dead entity estimation UI.
		entityDeadPopulation.setSize( 130, 80 );
		entityDeadPopulation.setPreferredSize( entityDeadPopulation.getSize() );
		entityDeadPopulation.setLocation( 10, 120 );
		monitorPanel.add( entityDeadPopulation );
		
		// Setup oldest living entity UI.
		oldestLivingEntity.setSize( 130, 80 );
		oldestLivingEntity.setPreferredSize( oldestLivingEntity.getSize() );
		oldestLivingEntity.setLocation( 10, 220 );
		monitorPanel.add( oldestLivingEntity );

		// Setup oldest entity ever UI.
		oldestEntityEver.setSize( 130, 80 );
		oldestEntityEver.setPreferredSize( oldestEntityEver.getSize() );
		oldestEntityEver.setLocation( 10, 320 );
		monitorPanel.add( oldestEntityEver );
	}
	
	/**
	 * Report the estimated count on entities in the land.
	 * @param living Number of entities not marked dead.
	 * @param dead Number of entities marked dead.
	 */
	public void reportEntityPopulation( int living, int dead )
	{
		entityLivingPopulation.setText( "Estimated living\n" + living );
		entityDeadPopulation.setText( "Estimated dead\n" + dead );
	}
	
	/**
	 * Record the oldest living entity.
	 * @param oldest The oldest living entity at the time of record.
	 */
	public void recordOldestLivingEntity( int oldest )
	{
		if( oldest > recordedOldestEntity )
		{
			recordedOldestEntity = oldest;
		}
		oldestLivingEntity.setText( "Oldest living\n" + oldest );
		oldestEntityEver.setText( "Oldest ever\n" + recordedOldestEntity );
	}
}
