package de.wi08e.myhome.statusmanager;

import java.util.Set;

import de.wi08e.myhome.exceptions.InvalidStatusValue;
import de.wi08e.myhome.model.Trigger;
import de.wi08e.myhome.model.datagram.BroadcastDatagram;
import de.wi08e.myhome.model.datagram.Datagram;

public interface SpecializedStatusManager {
	/**
	 * 
	 * @param senderDatabaseId
	 * @param datagram
	 * @return null when not handled, type as String when handled
	 */
	public String handleBroadcastDatagram(BroadcastDatagram datagram);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param sender
	 * @param receiver
	 * @return datagram when successfull, otherwise null
	 */
	public Datagram findDatagramForStatusChange(String key, String value, Trigger trigger, int[] receiverIds) throws InvalidStatusValue;

	/** 
	 * @return a list of types this status manager can handle
	 */
	public Set<String> getAllTypes();
}
