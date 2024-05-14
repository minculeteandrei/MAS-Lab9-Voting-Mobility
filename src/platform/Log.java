package platform;

import jade.core.AID;
import jade.core.Agent;

/**
 * Class helping with logging to standard output.
 * 
 * @author Andrei Olaru
 */
public class Log
{
	/**
	 * Prints to standard output a message emitted by the agent, formed of the objects in the output, in the context of
	 * a negotiation with the given partner (if any), for a given item (if any).
	 * 
	 * @param agent
	 *            - the agent logging the message.
	 * @param item
	 *            - the construction item this log message is about.
	 * @param partner
	 *            - the negotiation partner.
	 * @param output
	 *            - components of the output.
	 */
	protected static void logIntern(Agent agent, String item, AID partner, Object[] output)
	{
		String out = (agent != null ? agent.getLocalName() : "<no agent>") + ":\t ";
		if(item != null)
			out += "#" + item + "\t ";
		if(partner != null)
			out += "<" + partner.getLocalName() + ">\t";
		for(Object o : output)
			out += (o != null ? o.toString() : "<null>") + " ";
		System.out.println(out);
	}
	
	/**
	 * Prints to standard output a message emitted by the agent, formed of the objects in the output.
	 * 
	 * @param agent
	 *            - the agent logging the message.
	 * @param output
	 *            - components of the output.
	 */
	public static void log(Agent agent, Object... output)
	{
		logIntern(agent, null, null, output);
	}
	
	/**
	 * Prints to standard output a message emitted by the agent, formed of the objects in the output, in the context of
	 * a negotiation for a given item.
	 * 
	 * @param agent
	 *            - the agent logging the message.
	 * @param item
	 *            - the construction item this log message is about.
	 * @param output
	 *            - components of the output.
	 */
	public static void logI(Agent agent, String item, Object... output)
	{
		logIntern(agent, item, null, output);
	}
	
	/**
	 * Prints to standard output a message emitted by the agent, formed of the objects in the output, in the context of
	 * a negotiation with the given partner, for a given item.
	 * 
	 * @param agent
	 *            - the agent logging the message.
	 * @param item
	 *            - the construction item this log message is about.
	 * @param partner
	 *            - the negotiation partner.
	 * @param output
	 *            - components of the output.
	 */
	public static void logIP(Agent agent, String item, AID partner, Object... output)
	{
		logIntern(agent, item, partner, output);
	}
}
