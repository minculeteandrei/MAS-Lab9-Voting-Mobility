package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.util.leap.Iterator;
import platform.Constants;
import platform.Log;
import votes.VoteReader;
import votes.VotingData;

/**
 * The Region Representative Agent.
 */
public class RegionRepAgent extends Agent {
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 2081456560111009192L;
	/**
	 * Known election manager agent
	 */
	AID							electionManagerAgent;
	/**
	 * key name for the region vote results from json input file
	 */
	String						regionVoteKey;
	/**
	 * The votes.
	 */
	VotingData					voteResult;
	
	@Override
	protected void setup() {
		Log.log(this, "Hello from RegionRepresantative: " + this.getLocalName());
		Log.log(this, "Adding DF subscribe behaviors");
		
		regionVoteKey = (String) getArguments()[0];
		
		AID dfAgent = getDefaultDF();
		Log.log(this, "Default DF Agent: " + dfAgent);
		
		// Create election service discovery behavior
		// Build the DFAgentDescription which holds the service descriptions for the the ambient-agent service
		
		VoteReader voteReader = new VoteReader();
		voteResult = voteReader.getVoteResult(regionVoteKey);
		
		DFAgentDescription DFDesc = new DFAgentDescription();
		ServiceDescription serviceDesc = new ServiceDescription();
		serviceDesc.setType(Constants.ELECTION_MANAGEMENT_SERVICE);
		DFDesc.addServices(serviceDesc);
		
		SearchConstraints cons = new SearchConstraints();
		cons.setMaxResults(Long.valueOf(1));
		
		// add sub behavior for ambient-agent service discovery
		this.addBehaviour(
				new SubscriptionInitiator(this, DFService.createSubscriptionMessage(this, dfAgent, DFDesc, cons)) {
					
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void handleInform(ACLMessage inform) {
						Log.log(myAgent, "Notification received from DF");
						
						try {
							DFAgentDescription[] results = DFService.decodeNotification(inform.getContent());
							if(results.length > 0)
								for(DFAgentDescription dfd : results) {
									AID provider = dfd.getName();
									// The same agent may provide several services; we are interested
									// in the election-management one
									for(Iterator it = dfd.getAllServices(); it.hasNext();) {
										ServiceDescription sd = (ServiceDescription) it.next();
										if(sd.getType().equals(Constants.ELECTION_MANAGEMENT_SERVICE)) {
											Log.log(myAgent, Constants.ELECTION_MANAGEMENT_SERVICE,
													"service found: Service \"", sd.getName(), "\" provided by agent ",
													provider.getName());
											addServiceAgent(Constants.ELECTION_MANAGEMENT_SERVICE, provider);
											
											// if we found the ElectionManager we can cancel the subscription
											cancel(inform.getSender(), true);
										}
									}
								}
						} catch(FIPAException fe) {
							fe.printStackTrace();
						}
					}
				});
	}
	
	/**
	 * This method will be called when all the needed agents have been discovered.
	 */
	protected void onDiscoveryCompleted() {
		// TODO: add the RequestInitiator behavior for asking the VoteCollectorAgent to come collect the results
	}
	
	/**
	 * Retains an agent provided a service.
	 * 
	 * @param serviceType
	 *            - the service type.
	 * @param agent
	 *            - the agent providing a service.
	 */
	public void addServiceAgent(String serviceType, AID agent) {
		
		if(serviceType.equals(Constants.ELECTION_MANAGEMENT_SERVICE)) {
			if(electionManagerAgent != null)
				Log.log(this, "Warning: a second preference agent found.");
			electionManagerAgent = agent;
		}
		
		if(electionManagerAgent != null)
			onDiscoveryCompleted();
	}
	
	@Override
	protected void takeDown() {
		// Printout a dismissal message
		Log.log(this, "terminating.");
	}
}
