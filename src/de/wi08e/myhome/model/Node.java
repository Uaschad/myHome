package de.wi08e.myhome.model;

/**
 * This is a class to identify nodes (=external components). 
 * It is identified by a type, a manufacture and an id.
 * 
 * Examples for type: "enocean", "ip-camera", "cellphone", "email", ...
 * Exampled for manufacture: "generic", "simulator", "thermokon", "0x001", ...
 *  
 * If there is no known manufacture "generic" is used instead.
 * 
 * Type and manufacturer are lower case strings and therefore automatically converted.
 * 
 * Type, a manufacture and an id are not allowed to contain the character ":", because this is used as
 * delimiter for the descriptor
 * 
 * Nodes and all subclasses are (and should be) immutable.
 * 
 * @author Marek
 *
 */
public class Node {
	private String type;
	private String manufacturer;
	private String id;
	
	/**
	 * Returns the type in lower case letters 
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns the manufacturer in lower case letters 
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	
	/**
	 * Returns the id 
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * initiates the object from type, manufacturer and id
	 * @param type Type
	 * @param manufacturer Manufacturer
	 * @param id Id
	 * @throws IllegalArgumentException Is thrown when ":" is used in the parameters or any parameter is left blank
	 */
	public Node(String type, String manufacturer, String id) {
		super();
		
		if (type.contains(":") || 
				manufacturer.contains(":") || 
				id.contains(":") || 
				type.length() == 0 ||
				manufacturer.length() == 0 ||
				id.length() == 0)
			throw new IllegalArgumentException();
		
		this.type = type.toLowerCase();
		this.manufacturer = manufacturer.toLowerCase();
		this.id = id;
	}
	
	/**
	 * initiates the object from descriptor
	 * @param descriptor Descriptor, as generated by toString()
	 * @throws IllegalArgumentException Is thrown descriptor is not valid
	 */
	public Node(String descriptor) {
		super();
		
		String[] elements = descriptor.split(":");
		if (elements.length != 3 ||
			elements[0].length() == 0 ||
			elements[1].length() == 0 ||
			elements[2].length() == 0) 
			throw new IllegalArgumentException();
		
		
		
		this.type = elements[1].toLowerCase();
		this.manufacturer = elements[2].toLowerCase();
		this.id = elements[3];
	}
	
	/**
	 * Returns the descriptor for this Node as a single string
	 */
	public String toString() {
		return type+':'+manufacturer+':'+id;
	}
	
	
	
}
