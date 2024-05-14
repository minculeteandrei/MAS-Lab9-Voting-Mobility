package platform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import agents.ElectionManagerAgent;
import agents.VoteCollectorAgent;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.AgentState;
import jade.wrapper.StaleProxyException;

/**
 * Launches a main container and associated agents.
 */
public class MainContainerLauncher {
	/**
	 * The voting agent containers.
	 */
	AgentContainer	containerCentralElection;
	/**
	 * The election manager agent.
	 */
	AgentController	electionMgrAgentCtrl;
	
	/**
	 * Configures and launches the central election container.
	 */
	void setupCentralElectionContainer() {
		Properties mainProps = new ExtendedProperties();
		mainProps.setProperty(Profile.GUI, "true"); // start the JADE GUI
		mainProps.setProperty(Profile.MAIN, "true"); // is main container
		mainProps.setProperty(Profile.CONTAINER_NAME, Constants.CENTRAL_ELECTION); // you can rename it
		// TODO: replace with actual IP of the current machine
		mainProps.setProperty(Profile.LOCAL_HOST, Constants.MAIN_HOST);
		mainProps.setProperty(Profile.LOCAL_PORT, "" + Constants.PORT);
		mainProps.setProperty(Profile.PLATFORM_ID, Constants.PLATFORM_ID);
		
		ProfileImpl mainProfile = new ProfileImpl(mainProps);
		containerCentralElection = Runtime.instance().createMainContainer(mainProfile);
	}
	
	/**
	 * Starts the agents assigned to the central election container.
	 */
	void startCentralElectionAgents() {
		String electionManagerName = "election_mgr";
		String voteCollectorName = "vote_collector";
		
		try {
			List<String> regionVoteNames = new ArrayList<>();
			for(int i = 1; i <= Constants.REGION_CONTAINERS.length; i++) {
				regionVoteNames.add("region" + i);
			}
			
			electionMgrAgentCtrl = containerCentralElection.createNewAgent(electionManagerName,
					ElectionManagerAgent.class.getName(),
					new Object[] { new AID(voteCollectorName, AID.ISLOCALNAME), regionVoteNames });
			electionMgrAgentCtrl.start();
			
			AgentController voteCollectorAgentCtrl = containerCentralElection.createNewAgent(voteCollectorName,
					VoteCollectorAgent.class.getName(), null);
			voteCollectorAgentCtrl.start();
		} catch(StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Verifies periodically if there are any remaining agents and, if not, shuts down the platform using the
	 * {@link MgrAgent}.
	 */
	protected void closeOnEnd() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				boolean alldone = true;
				try {
					if(electionMgrAgentCtrl.getState().getCode() != AgentState.cAGENT_STATE_DELETED)
						alldone = false;
				} catch(StaleProxyException e) {
					// agent is already out
				}
				if(alldone) {
					Log.log(null, "Attempting shutdown");
					try {
						containerCentralElection.createNewAgent("mgr", MgrAgent.class.getName(), null).start();
					} catch(StaleProxyException e) {
						e.printStackTrace();
					}
					timer.cancel();
				}
			}
		}, new Date(), 500);
	}
	
	/**
	 * Launches the main container.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args) {
		MainContainerLauncher launcher = new MainContainerLauncher();
		
		launcher.setupCentralElectionContainer();
		launcher.startCentralElectionAgents();
		if(Constants.CLOSE_ON_END)
			launcher.closeOnEnd();
	}
	
}
