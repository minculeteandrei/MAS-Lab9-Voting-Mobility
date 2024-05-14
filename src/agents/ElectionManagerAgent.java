package agents;

import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import platform.Constants;
import platform.Log;
import platform.MgrAgent;

/**
 * ElectionManager agent.
 */
public class ElectionManagerAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3397689918969697329L;
	
	/**
	 * The AID of the VoteCollector agent used by this ElectionManager
	 */
	AID voteCollector;
	
	/**
	 * The names of the regions from which the ElectionManager awaits vote results collected by the VoteCollector.
	 */
	List<String> regionNames;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setup() {
		Log.log(this, "Hello");
		
		// get the AID of the vote collector agent
		voteCollector = (AID) getArguments()[0];
		Log.log(this, "My vote collector agent is: " + voteCollector);
		
		// get the list of region names (keys in the json file) from which the ElectionManager awaits votes
		regionNames = (List<String>) getArguments()[1];
		Log.log(this, "Awaiting votes from the following regions: " + regionNames);
		
		// Register the preference-agent service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		// TODO: register PreferenceAgent to DF with service type: preference-agent and service-name:
		// ambient-wake-up-call
		ServiceDescription sd = new ServiceDescription();
		sd.setType(Constants.ELECTION_MANAGEMENT_SERVICE);
		sd.setName("election-management");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		// TODO: move this to when the agent is actually done.
		MgrAgent.addTerminateBehavior(this, 3000);
	}
	
	@Override
	protected void takeDown() {
		// De-register from the yellow pages
		try {
			DFService.deregister(this);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		
		// Printout a dismissal message
		Log.log(this, "terminating.");
	}
}
