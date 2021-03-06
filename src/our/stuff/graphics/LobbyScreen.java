package our.stuff.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.brackeen.javagamebook.tilegame.GameManager;

import our.stuff.eventlisteners.StartButtonListener;
import our.stuff.eventlisteners.BackButtonListener;
import our.stuff.eventlisteners.ConnectButtonListener;
import our.stuff.eventlisteners.HostButtonListener;
import our.stuff.eventlisteners.JoinButtonListener;
import our.stuff.networking.Client;
import our.stuff.networking.LobbyHostListener;
import our.stuff.networking.Server;

public class LobbyScreen extends JFrame
{
	
	private Server server;
	
	private JButton hostButton;
	private JButton joinButton;
	private JButton menuButton;
	private JButton sendButton;
	
	private JPanel buttonPanel;
	private JPanel screenContainer;
	private JPanel connectPanel;
	private JPanel hostPanel;
	private JPanel chatPanel;
	
	private ImageIcon icon;
	private JTextField ipBox;
	private JTextField chatBox;
	private JTextArea chatHistory;
	
	public LobbyScreen()
	{
		hostButton = new JButton("Host Game");
		hostButton.addActionListener(new HostButtonListener(this));
		joinButton = new JButton("Join Game");
		joinButton.addActionListener(new JoinButtonListener(this));
		menuButton = new JButton("Main Menu");
		menuButton.addActionListener(new BackButtonListener(this));
		sendButton = new JButton("Send Message");
		buttonPanel = new JPanel(new GridLayout(4, 1));
		buttonPanel.add(hostButton);
		buttonPanel.add(joinButton);
		buttonPanel.add(menuButton);
		
		chatHistory = new JTextArea(10,20);
		chatHistory.setMargin(new Insets(5, 5, 5, 5));
		chatHistory.setEditable(false);
		
		chatBox = new JTextField("Enter chat message here");
		chatBox.setDisabledTextColor(Color.GRAY);
		sendButton.setEnabled(false);
		chatBox.setEnabled(false);
		
		chatPanel = new JPanel(new BorderLayout());
		//chatPanel.add(new JLabel("Chat"), BorderLayout.PAGE_START);
		chatPanel.add(chatHistory, BorderLayout.NORTH);
		chatPanel.add(chatBox, BorderLayout.CENTER);
		chatPanel.add(sendButton, BorderLayout.SOUTH);
		
		screenContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		screenContainer.add(chatPanel);
		screenContainer.add(buttonPanel);
		this.setSize(800,600);
		this.add(screenContainer);
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	
    	icon = new ImageIcon("images/background.png");
    	 
		JPanel panel = new JPanel()
		{
			protected void paintComponent(Graphics g)
			{
				g.drawImage(icon.getImage(), 0, 0, null);
 				super.paintComponent(g);
			}
		};
		panel.setOpaque( false );
		panel.setPreferredSize( new Dimension(400, 400) );
 
		panel.add(screenContainer);
		this.add(panel);
	}
	
	private int state = 0;
	
	private static final int DEFAULT = 0;
	private static final int JOIN = 1;
	private static final int HOST = 2;
	
	/**
	 * Method the back button will trigger.
	 */
	public void back()
	{
		switch (state)
		{
		case DEFAULT:
			GameManager.getGameManagerInstance().setMultiScreen(false);
			state = DEFAULT;
			break;
		case JOIN:
			screenContainer.remove(connectPanel);
			buttonPanel.setVisible(true);
			state = DEFAULT;
			break;
		case HOST:
			screenContainer.remove(hostPanel);
			buttonPanel.setVisible(true);
			server.close();
			state = DEFAULT;
			break;
			
		}
		chatBox.setEnabled(false);
		sendButton.setEnabled(false);
	}
	
	/**
	 * Contains the logic for hosting a session
	 * 
	 */
	public void host()
	{
		chatBox.setEnabled(true);
		sendButton.setEnabled(true);
		state = HOST;
		buttonPanel.setVisible(false);
		hostPanel = new JPanel(new BorderLayout(2, 2));
		JButton backButton = new JButton("Back");
		JTextArea logText = new JTextArea(10, 20);
		logText.append("Hosting game");
		logText.setMargin(new Insets(5, 5, 5, 5));
		logText.setEditable(false);
		backButton.addActionListener(new BackButtonListener(this));
		hostPanel.add(new JLabel("Server Log"), BorderLayout.NORTH);
		hostPanel.add(logText, BorderLayout.CENTER);
		
		JRadioButton wave = new JRadioButton("Wave defense");
		JRadioButton race = new JRadioButton("Race");
		JRadioButton br = new JRadioButton("Battle Royale");
		JRadioButton coop = new JRadioButton("Co-op");
		
		wave.setSelected(true);
		
		ButtonGroup modes = new ButtonGroup();
		modes.add(wave);
		modes.add(race);
		modes.add(br);
		modes.add(coop);
		
		JPanel blist = new JPanel(new GridLayout(3, 2));
		
		blist.add(wave);
		blist.add(race);
		blist.add(br);
		blist.add(coop);
		
		JButton start = new JButton("Start Game");
		start.addActionListener(new StartButtonListener(this));
		
		blist.add(start);
		blist.add(backButton);
		
		hostPanel.add(blist, BorderLayout.SOUTH);
		
		screenContainer.add(hostPanel);
		
		server = new Server(25565, new LobbyHostListener(logText));
		server.start();
		logText.append("\nServer started on port: " + server.getPort());
	}
	/**
	 * Initiates logic for joining a lobby
	 */
	public void join()
	{
		chatBox.setEnabled(true);
		sendButton.setEnabled(true);
		state = JOIN;
		buttonPanel.setVisible(false);
		connectPanel = new JPanel();
		JButton connectButton = new JButton("Connect");
		JButton backButton = new JButton("Back");
		
		connectButton.addActionListener(new ConnectButtonListener(this));
		backButton.addActionListener(new BackButtonListener(this));
		
		ipBox = new JTextField("Enter IP here",15);
		
		connectPanel.add(backButton);
		connectPanel.add(ipBox);
		connectPanel.add(connectButton);
		screenContainer.add(connectPanel);
	}
	
	public void connect()
	{
		try
		{
			Client client = new Client(InetAddress.getByName(ipBox.getText()), 25565);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
