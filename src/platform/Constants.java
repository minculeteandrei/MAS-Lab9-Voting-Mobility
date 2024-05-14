package platform;

/**
 * Constants.
 */
public class Constants {
	/**
	 * Name of the main container,
	 */
	static final String			CENTRAL_ELECTION			= "CentralElection";
	/**
	 * The list of containers.
	 */
	static final String[]		REGION_CONTAINERS			= { "VotingRegion1", "VotingRegion2", "VotingRegion3",
			"VotingRegion4" };
	/**
	 * Platform ID.
	 */
	static final String			PLATFORM_ID					= "voting-agents";
	/**
	 * Network settings.
	 */
	static final String			MAIN_HOST					= "localhost";
	/**
	 * Secondary container.
	 */
	static final String			SECONDARY_HOST				= "localhost";
	/**
	 * Network settings.
	 */
	static final int			PORT						= 1099;
	/**
	 * The type of service.
	 */
	public final static String	ELECTION_MANAGEMENT_SERVICE	= "election-management";
	/**
	 * Set to <code>true</code> if platform should shut down when there are no more running agents.
	 */
	public final static boolean	CLOSE_ON_END				= true;
}
