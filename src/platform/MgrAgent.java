package platform;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;

/**
 * "Manager" agent that helps with shutting down the platform after everything is done.
 * 
 * @author Andrei Olaru
 */
public class MgrAgent extends Agent {
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -2190074402175042314L;
	
	@Override
	protected void setup() {
		super.setup();
		Log.log(this, "request shutdown");
		Codec codec = new SLCodec();
		Ontology jmo = JADEManagementOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(jmo);
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(getAMS());
		msg.setLanguage(codec.getName());
		msg.setOntology(jmo.getName());
		try {
			getContentManager().fillContent(msg, new Action(getAID(), new ShutdownPlatform()));
			send(msg);
		} catch(Exception e) {
			//
		}
		takeDown();
	}
	
	/**
	 * Can be used by an agent to add a termination behavior with a given timeout.
	 * 
	 * @param agent
	 *            - the agent to add the behavior to.
	 * @param timeout
	 *            - the timeout before termination.
	 */
	public static void addTerminateBehavior(Agent agent, long timeout) {
		// TODO remove this behavior:
		agent.addBehaviour(new WakerBehaviour(agent, timeout) {
			private static final long serialVersionUID = 2344280954807547170L;
			
			@Override
			protected void onWake() {
				myAgent.doDelete();
			}
		});
	}
}