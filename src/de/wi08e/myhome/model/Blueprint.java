/**
 * 
 */
package de.wi08e.myhome.model;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.wi08e.myhome.database.Database;


/**
 * 
 * @author marek_ventur
 *
 */
public class Blueprint {

	private int databseId;
	private String name;
	private int width;
	private int height;
	private Image image = null;
	private boolean primary = false;

	private List<BlueprintLink> blueprintLinks = Collections.synchronizedList(new ArrayList<BlueprintLink>());
	
	public Blueprint() {
		
	}
	/**
	 * 
	 * @param resultSet creates a node of resultset
	 * @throws SQLException
	 */
	public Blueprint(ResultSet resultSet) throws SQLException {
		databseId = resultSet.getInt("id");
		name = resultSet.getString("name");
		width = resultSet.getInt("width");
		height = resultSet.getInt("height");
		primary = (resultSet.getInt("primary") == 1);
		
		if (Database.columnExist(resultSet, "image")) {
		
			InputStream imageStream = resultSet.getBinaryStream("image");
			try { 
				image = ImageIO.read(imageStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * determining the fowlloing parameters
	 * @param databseId 
	 * @param name
	 * @param width
	 * @param height
	 * @param image
	 */
	public Blueprint(int databseId, String name, int width, int height,
			Image image, boolean primary) {
		super();
		this.databseId = databseId;
		this.name = name;
		this.width = width;
		this.height = height;
		this.image = image;
		this.primary = primary;
	}

	public int getDatabseId() {
		return databseId;
	}

	public void setDatabseId(int databseId) {
		this.databseId = databseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	

	public List<BlueprintLink> getBlueprintLinks() {
		return blueprintLinks;
	}

	/**
	 * This method can be removed after development!
	 */
	public void preview() {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setSize(width+20, height+60);
		
		
		
		for (BlueprintLink link: blueprintLinks) {
			JButton button = new JButton(link.getName());
			
			frame.getContentPane().add(button);
			
			button.setBounds((int)Math.round(getWidth()*link.getX())-10, 
					(int)Math.round(getHeight()*link.getY())-10,
					20, 
					20);
			
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(null, ((JButton)e.getSource()).getText());
				}
				
			});
			
		}
		
		JLabel label = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(label);
		label.setBounds(0, 0, width, height);
		
		
		frame.setVisible(true);
	}
	
	public boolean isPrimary() {
		return primary;
	}
	
	

}
