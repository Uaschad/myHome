/**
 * 
 */
package de.wi08e.myhome.nodeplugins;

import java.awt.Image;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import de.wi08e.myhome.Config;
import de.wi08e.myhome.ConfigPlugin;
import de.wi08e.myhome.frontend.httpserver.HTTPServer;
import de.wi08e.myhome.model.Node;
import de.wi08e.myhome.model.datagram.Datagram;
import de.wi08e.myhome.nodemanager.NodeManager;
import de.wi08e.myhome.snapshotmanager.SnapshotManager;

/**
 * @author Marek_Ventur
 */

public class NodePluginManager implements Runnable {
	
	private final static Logger LOGGER = Logger.getLogger(HTTPServer.class.getName());
	private List<NodePluginRunnable> plugins;
	
	private BlockingQueue<MessageFromPluginQueueHolder> receivedMessages = new LinkedBlockingQueue<MessageFromPluginQueueHolder>();
	
	private NodeManager nodeManager = null;
	private boolean running = true;

	public void setNodeManager(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}
	
	public NodePluginManager() {
		
		
		/* Add all the plugins from a specific directory */
		// NodePluginLoader.addFile("nodeplugins/xyz.jar");
		// LOGGER.info("Added plugin xyz.jar");
		
		/* Create thread-safe plugin list (not sure, if needed) */
		plugins = Collections.synchronizedList(new ArrayList<NodePluginRunnable>());
		
		
		/* Loop plugin list */
		List<ConfigPlugin> configPlugins = Config.getNodePlugins();
		for (ConfigPlugin configPlugin: configPlugins) {
			
			Class<?> loadedClass;
			try {
				loadedClass = ClassLoader.getSystemClassLoader().loadClass(configPlugin.getNamespace()+".Main");
			
				Constructor<?> cs = loadedClass.getConstructor();
			
				NodePlugin plugin = (NodePlugin)cs.newInstance();
				NodePluginRunnable pluginRunnable = new NodePluginRunnable(plugin, configPlugin.getProperties(), configPlugin.getData(), receivedMessages);
				
				Thread pluginThread = new Thread(pluginRunnable);
				pluginThread.start();
				
				plugins.add(pluginRunnable);
				
				
			} catch (ClassNotFoundException e) {
				LOGGER.warning("Can't find "+configPlugin.getNamespace());
			} catch (SecurityException e) {
				LOGGER.warning("Security exception in "+configPlugin.getNamespace());
			} catch (NoSuchMethodException e) {
				LOGGER.warning("No constructor found in "+configPlugin.getNamespace());
			} catch (java.lang.ClassCastException e) {
				LOGGER.warning("Can't cast "+configPlugin.getNamespace());
			} catch (NodePluginException e) {
				LOGGER.warning(e.getMessage());
			} catch (Exception e) {
				LOGGER.warning("Other exception in "+configPlugin.getNamespace()+": "+e.getMessage());
			} 
		}
	}
	
	/**
	 * @param datagram sends the given datagram
	 */
	
	public void sendDatagram(Datagram datagram) {

		for (NodePluginRunnable pluginRunnable: plugins) 
			pluginRunnable.chainSendDatagramm(datagram);	

	}
	
	public Image getLastSnapshot(Node node) {
		for (NodePluginRunnable pluginRunnable: plugins) {
			Image image = pluginRunnable.getLastSnapshot(node);
			if (image != null)
				return image;
		}
		return null;
	}

	@Override
	public void run() {
		try {
			while (running) {
				MessageFromPluginQueueHolder message = receivedMessages.take();
				
				if (message.getType() == MessageFromPluginQueueHolder.Type.RECEIVED_DATAGRAM) {
					Datagram datagram = message.getDatagram();
				
					
					for (NodePluginRunnable pluginRunnable: plugins) 
						pluginRunnable.chainReceiveDatagram(datagram);	
					if (nodeManager != null)
						nodeManager.receiveDatagram(datagram);
				}
				
				if (message.getType() == MessageFromPluginQueueHolder.Type.SNAPSHOT) 
					nodeManager.storeSnapshot(message.getSnapshot());
			}
		} catch (InterruptedException e) {
		}		
	}
	
}
