package webretrieval;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import webretrieval.database.DatabaseConstants;
import webretrieval.html.HTMLDoc;
import webretrieval.html.HTMLDocJdbc;
import webretrieval.html.HTMLImage;
import webretrieval.html.HTMLTags;
import webretrieval.tools.Global;
import webretrieval.tools.WordFinder;

/**
 * GUI of the class that takes all the components of this class and will put them together to work.
 * 
 * Needless to say, this class does not requires a JavaDoc, because it's a personal approach on how to use the api. It's
 * restricted to the creators idea. This calls should be be extended further because all methods are local to this class.
 * 
 * @author Samuel E. Sanchez
 *
 */
public class WebPageRetrievalGUI extends JFrame implements ActionListener, ItemListener{
	
	private static final long serialVersionUID = -1143518132589672260L;
	//GUI components
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private Container contentPane;
	private JTextField databaseURL;
	private JTextField database;
	private JTextField username;
	private JPasswordField password;
	private JTextField urlField;
	private JTextField keywordField;
	private JComboBox<String> searchOptions;
	private static final String[] searchOptionSites = {"Amazon", "Craigslist", "Database", "Google", "Other"};
	private JTextArea htmlTextArea;
	private String userNameTitle;
	private JComboBox<Object> googleList;
	private JPanel bodyPanel;
	private JFrame webPageFrame;
	private JEditorPane webPageViewer;
	private JPanel webPageViewButton;
	private JFrame imageFrame;
	private Container imgContainer;
	private static final String ABOUT = "CSCI 370 - Software Engineering\nAuthor : Samuel E. Sanchez\nProfessor: Robert Goldberg";
	private static final String INFO = "This product is still under development.\nFor inquiries please email: samuel.ed.sanchez@gmail.com";
	
	//Class Components
	private HTMLDocJdbc htmlDocJbdc;
	private WebPageRetrieval webPageRetrieval;
	private GoogleQuery googleQuery;
	private Object[] googleQueryList;
	
	public WebPageRetrievalGUI(){
		//Get Contain Pane in order to add Content to the rootFrame
		contentPane = getContentPane();
		searchOptions = new JComboBox<String>(searchOptionSites);
		searchOptions.setEditable(false);
		webPageRetrieval = new WebPageRetrieval();
		googleQuery = new GoogleQuery();
		webPageFrame = new JFrame();
		webPageViewer = new JEditorPane();
		webPageViewer.setEditable(false);
		webPageFrame.add(new JScrollPane(webPageViewer));
		imageFrame = new JFrame();
		imgContainer = imageFrame.getContentPane();
	}
	
	public void startGUI(){
		createWindow();
		logInPanel();
	}
	
	private void createWindow(){
		setLocation(200,200);
		createMenu();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//Create the window with its menuBar
	public void createMenu(){
		//Create and add a Menu in the Menu_Bar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		fileMenu.add(createItem("Log In"));		//Component : 0
		fileMenu.add(createItem("Log Out"));	//Component : 1
		fileMenu.addSeparator();				//Component : 2
		fileMenu.add(createItem("Quit"));		//Component : 3
		fileMenu.getMenuComponent(1).setEnabled(false);	//Disable Log out
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(createItem("Info"));
		helpMenu.add(createItem("About"));
		
		//add the menu to its bar
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
	    setJMenuBar(menuBar);
	}//windowFrame
	
	private JMenuItem createItem(String itemName){
		JMenuItem item = new JMenuItem(itemName);
		item.addActionListener(this);
		return item;
	}
	
	//Get the item selected from JComboBox and perform an action
	public void itemStateChanged(ItemEvent event) {
		 if(event.getStateChange() == ItemEvent.DESELECTED) return;
		 
		 //Find out if this event came from our Search Option Sites List
		 String item = (((JComboBox<?>) event.getSource()).getSelectedItem()).toString();

		 if(Arrays.asList(searchOptionSites).contains(item)){
			int index = searchOptions.getSelectedIndex();
				
			switch(index){
			case 0://Amazon
				keywordField.setBorder(BorderFactory.createTitledBorder("ISBN-10 / ASIN"));
				break;
			case 1://Craiglist
				keywordField.setBorder(BorderFactory.createTitledBorder("Keyword"));
				break;
			case 2://Database
				keywordField.setBorder(BorderFactory.createTitledBorder("Tags"));
				break;
			case 3://Google
				keywordField.setBorder(BorderFactory.createTitledBorder("Terms"));
				break;
			case 4://Other
				keywordField.setBorder(BorderFactory.createTitledBorder("Keyword"));
			default:
				/*Do nothing*/
				break;
			}
		 }
		 else{
			String page = (String) googleQueryList[googleList.getSelectedIndex()];
			urlField.setText(page);
			if(page.contains("amazon")){
				 isAmazon(page, keywordField.getText().trim());
			}
			else if(page.contains("craigslist")){
				 isCraigslist(page, keywordField.getText().trim());
			}
			else{
				isOtherSite(page, keywordField.getText().trim());
			}
		 }
	}
	
	//Check which action has to be performed
	public void actionPerformed(ActionEvent event){
		String action = event.getActionCommand();
		if (action.equals("Quit")){
			if(htmlDocJbdc != null){
				try {
					htmlDocJbdc.Disconnect();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			System.exit(0);
		}//Quit
		else if (action.equals("Connect")){
			connectToDatabase();
		}//Open
		else if(action.equals("Log In")){
			logInPanel();
			return;
		}//Log In
		else if(action.equals("Log Out")){
			if(htmlDocJbdc != null){
				try {
					htmlDocJbdc.Disconnect();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			fileMenu.getMenuComponent(0).setEnabled(true); 	//Disable Log in
			fileMenu.getMenuComponent(1).setEnabled(false); //Enable Log out
			logInPanel();
		}//Log Out
		else if(action.equals("Info")){
			JOptionPane.showMessageDialog(null, INFO, "Information", JOptionPane.INFORMATION_MESSAGE);
		}//Info
		else if(action.equals("About")){
			JOptionPane.showMessageDialog(null, ABOUT, "About", JOptionPane.INFORMATION_MESSAGE);
		}//About
		else if(action.equals("Search")){
			//Remove the button to display the website if there was an unsuccessful connection
			if(webPageViewButton != null){
				bodyPanel.remove(webPageViewButton);
			}
			repaint();
			htmlTextArea.setText("");
			selectingWebSite();
		}//Search
		else if(action.equals("View Page")){
			webPageFrame.repaint();
			webPageFrame.pack();
			webPageFrame.setSize(700, 400);
			webPageFrame.setLocation(20, 20);
			webPageFrame.setVisible(true);
		}
		else {
			System.out.println("HERE?! - " + action);
		}
	}//actionPerformed
	
	private void logInPanel(){
		//Clear content pane
		contentPane.removeAll();
		contentPane.repaint();
		setTitle("Log In - Web Retrieval");
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());
		
		//Here we'll put the fields for the user to complete
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(4, 2));
		
		//Create Fields
		JLabel databaseURLLabel = new JLabel("MySQL URL:");
		JLabel databaseLabel = new JLabel("Database:");
		JLabel usernameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		databaseURL = new JTextField();
		database = new JTextField();
		username = new JTextField();
		password = new JPasswordField();
		password.setColumns(15);
		//Add fields to Input Panel
		inputPanel.add(databaseURLLabel);
		inputPanel.add(databaseURL);
		inputPanel.add(databaseLabel);
		inputPanel.add(database);
		inputPanel.add(usernameLabel);
		inputPanel.add(username);
		inputPanel.add(passwordLabel);
		inputPanel.add(password);
		
		//Add the Connect button
		JButton connect = new JButton("Connect");
		connect.addActionListener(this);
		JPanel connectPanel = new JPanel(new FlowLayout());
		connectPanel.add(connect);

		loginPanel.add(inputPanel, BorderLayout.CENTER);
		loginPanel.add(connectPanel, BorderLayout.SOUTH);
		contentPane.add(loginPanel);
		pack();
		setVisible(true);
	}
	
	//Display the interactive window
	private void webRetrievalPage(){
		//Clear content pane
		contentPane.removeAll();
		contentPane.repaint();
		setTitle(userNameTitle);
		
		//Create the containers for the searchers and displayer
		JPanel topLevel = new JPanel();
		JPanel searchPanel = new JPanel();
		bodyPanel = new JPanel();
		topLevel.setLayout(new BorderLayout(2,2));
		searchPanel.setLayout(new FlowLayout());
		bodyPanel.setLayout(new BorderLayout());
		
		//Create the search items
		urlField = new JTextField();
		urlField.setBorder(BorderFactory.createTitledBorder("URL"));
		urlField.setColumns(25);
		keywordField = new JTextField();
		keywordField.setBorder(BorderFactory.createTitledBorder("Keyword"));
		keywordField.setColumns(15);
		JButton search = new JButton("Search");
		search.setMnemonic('S');
		search.addActionListener(this);
		searchOptions.setSelectedIndex(1);	//By Default choose Craiglist
		searchOptions.addItemListener(this);
		
		//Create the body Items to be displayed
		htmlTextArea = new JTextArea();
		htmlTextArea.setEditable(false);
		htmlTextArea.setLineWrap(true);
		htmlTextArea.setWrapStyleWord(true);
		htmlTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
		JScrollPane htmlScroll = new JScrollPane(htmlTextArea);
		bodyPanel.add(htmlScroll, BorderLayout.CENTER);
		
		//Set ViewButton to display web-sites
		JButton viewSite = new JButton("View Page");
		viewSite.setMnemonic('V');
		viewSite.setMnemonic('P');
		viewSite.setToolTipText("Display the website");
		viewSite.addActionListener(this);
		webPageViewButton = new JPanel(new FlowLayout());
		webPageViewButton.add(viewSite);
		
		//Add Searchers to Layers
		searchPanel.add(urlField);
		searchPanel.add(keywordField);
		searchPanel.add(searchOptions);
		searchPanel.add(search);
		topLevel.add(searchPanel, BorderLayout.NORTH);
		topLevel.add(bodyPanel, BorderLayout.CENTER);
		
		contentPane.add(topLevel);
		pack();
		setVisible(true);
	}
		
	private void connectToDatabase(){
		String mysqlUrl = "jdbc:mysql://" + databaseURL.getText().trim() +
				DatabaseConstants.PORT + "/" + DatabaseConstants.MYSQL_DEFAULT_DB;
		char[] passwordChars = password.getPassword();
		StringBuffer passwordString = new StringBuffer();
		for(char c : passwordChars){
			passwordString.append(c);
		}
		userNameTitle = username.getText().trim();
		
		//Connecting to database
		try{
			htmlDocJbdc = new HTMLDocJdbc(mysqlUrl, userNameTitle, passwordString.toString());
			htmlDocJbdc.createUsePrimaryDatabase(database.getText().trim());
			fileMenu.getMenuComponent(0).setEnabled(false); //Disable Log in
			fileMenu.getMenuComponent(1).setEnabled(true); 	//Enable Log out
			setTitle(userNameTitle);
			webRetrievalPage();
		}catch(Exception e){
			htmlDocJbdc = null;
			userNameTitle = "";
			if(Global.DEBUG){
				System.out.println("Error : " + e.getMessage());
			}
		}
		//Clear Fields
		databaseURL.setText("");
		database.setText("");
		username.setText("");
		password.setText("");
	}
	
	private void selectingWebSite(){
		//Retrieve and clear search Information
		String url = urlField.getText().trim();
		String keyword = keywordField.getText().trim();
		int searchIndex = searchOptions.getSelectedIndex();
		
		//If there's nothing to search then return
		if(url.isEmpty() && keyword.isEmpty()) return;
		
		//Reset Google List every time the button search is clicked!
		if(googleList != null){
			bodyPanel.remove(googleList);
			googleQueryList = null;
			googleList = null;
			repaint();
		}
		
		//If Amazon | Craigslist | Database | Google | Other
		switch(searchIndex){
			case 0:	//Amazon
				//If we are looking for an ISBN then make sure it's correct
				if(url.isEmpty()){
					try{
						Integer.parseInt(keyword);
					}catch(NumberFormatException e){
						//It's not an ISBN then make google search this keyword at Amazon.com
						searchOptions.setSelectedIndex(3);
						isGoogleSite("www.amazon.com", keyword);	//This list will be redirected to isAmazon()
						break;
					}
					//Make the URL from the ISBN
					url = Global.AMAZON_PAGE + keyword;
				}
				//The user provided the Url or it was made from the ISBN
				isAmazon(url, keyword);
				break;
			case 1: //Craigslist
				//If there's not URL then let's look search Tag in Google
				if(url.isEmpty()){
					//Let's make google search in newyork.craiglist.org for keyword
					searchOptions.setSelectedIndex(3);
					isGoogleSite("newyork.craigslist.org", keyword);	//This list will be redirected to isCraiglist()
					break;
				}
				isCraigslist(url, keyword);
				break;
			case 2:	//Database
				isDatabase(url, keyword);
				break;
			case 3:	//Google
				if(url.isEmpty()){
					isGoogleQuery(keyword);
					break;
				}
				isGoogleSite(url, keyword);
				break;
			case 4: //Other
				if(url.isEmpty()){
					isGoogleQuery(keyword);
					break;
				}
				isOtherSite(url, keyword);
				break;
			default:
				break;
		}
	}
	
	private void isAmazon(String url, String keyword){
		//If the site is not amazon, then select other
		if(!url.contains("amazon.com")){
			isOtherSite(url, keyword);
			return;
		}
		searchOptions.setSelectedIndex(0);
		Set<String> wordsToFind = new HashSet<String>();
		wordsToFind.add("Book Description");
		wordsToFind.add("Product Details");
		getSite(url, keyword, wordsToFind, 0);
	}
	
	private void isCraigslist(String url, String keyword){
		//If the site is not craigslist, then select Other
		if(!url.contains("craigslist.org")){
			isOtherSite(url, keyword);
			return;
		}
		searchOptions.setSelectedIndex(1);
		Set<String> wordsToFind = new HashSet<String>();
		wordsToFind.add("Reply to");
		wordsToFind.add("Date");
		getSite(url, keyword, wordsToFind, 1);
	}
	
	public void isOtherSite(String url, String keyword){
		searchOptions.setSelectedIndex(4);
		Set<String> wordsToFind = new HashSet<String>();
		String[] keys = keyword.trim().split(",");
		for(String st : keys){
			if(st.trim().isEmpty()) continue;
			wordsToFind.add(st);
		}
		getSite(url, keyword, wordsToFind, 4);
	}
	
	private void getSite(String url, String keyword, Set<String> wordsToFind, int site){
		webPageRetrieval.Update(url);
		if(webPageRetrieval.isConnectionSuccessful()){
			if(urlField.getText().trim().isEmpty()){
				urlField.setText(url);
			}
			if(keywordField.getText().trim().isEmpty()){
				keywordField.setText(keyword);
			}
			
			HTMLDoc htmlDoc = webPageRetrieval.getHtmlDoc();
			
			//Make a list of the keywords
			String[] list = keyword.split(",");
			List<String> words = new LinkedList<String>();
			for(String word : list){
				words.add(word);
			}
			
			htmlDoc.setWordTags(words);
			try{//Store it in the database
				htmlDocJbdc.put(htmlDoc);
				htmlDocJbdc.executeBatch();
			}catch(Exception e){
				if(Global.DEBUG){ e.printStackTrace(); }
			}
			
			//Let's display the Title, description, isbn, prices and image
			String title = (String) htmlDoc.getTagList(HTMLTags.TITLE).toArray()[0];
			String body = (String) htmlDoc.getTagList(HTMLTags.BODY).toArray()[0];
			setTitle(title);
			StringBuffer buffer = new StringBuffer();
			
			if(wordsToFind != null && wordsToFind.size() != 0){
				Map<String, List<String>> matches = WordFinder.getParagraphs(wordsToFind, body);
				
				for(String key : matches.keySet()){
					buffer.append("\n\t[ " + key + " ] : \n");
					for(String m : matches.get(key)){
						buffer.append(m + "\n");
					}
				}
			}
			
			switch(site){
				case 0://Amazon
					//Get the main Picture - It will always display a picture
					HTMLImage mainImage = WordFinder.getBestMatchImage(htmlDoc.getTagList(HTMLTags.IMG), title);
					if(mainImage != null){
						BufferedImage imageUI;
						try {
							imgContainer.removeAll();
							imageUI = ImageIO.read(mainImage.image);
							if(imageUI != null){
								JLabel imageLabel = new JLabel(mainImage.alt, new ImageIcon(imageUI), SwingConstants.CENTER);
								imageFrame.add(imageLabel);
							}
							imageFrame.repaint();
							imageFrame.pack();
							imageFrame.setVisible(true);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					break;
				case 1://Craigslist
					buffer.append("\n\t[ City ] :\n");
					String host = htmlDoc.getHost();
					buffer.append(host.substring(0, host.indexOf(".")) + "\n");
					
					buffer.append("\n\t[ Title ] :\n");
					buffer.append(title + "\n");
					
					buffer.append("\n\t[ Body ] :\n");
					buffer.append(body + "\n");
					//Display All Pictures
					try {
						if(htmlDoc.getTagCount(HTMLTags.IMG) > 0){
							imgContainer.removeAll();
							imageFrame.setLayout(new FlowLayout());
							for(Object images : htmlDoc.getTagList(HTMLTags.IMG)){
								BufferedImage displayImg = ImageIO.read(((HTMLImage)images).image);
								if(displayImg == null) continue;
								imageFrame.add(new JLabel(new ImageIcon(displayImg)));
							}
							imageFrame.setSize(700, 500);
							imageFrame.repaint();
							imageFrame.setVisible(true);
						}
					} catch (IOException e1) {
						if(Global.DEBUG){
							e1.printStackTrace();
						}
					}
					break;
				case 3:
					/*Do Nothing*/
					break;
				case 4://Other
					buffer.append("\n\t[ Body ] :\n");
					buffer.append(body + "\n");
					
					buffer.append("\n\t[ Links to Images ] :\n");
					for(Object img : htmlDoc.getTagList(HTMLTags.IMG)){
						buffer.append( ((HTMLImage) img).src + "\n");
					}
					break;
				default:
					break;
			}
			
			htmlTextArea.setText(buffer.toString());
			setSize(700, 500);
			
			//Load the website into the JEditorPane
			try {
				webPageViewer.setPage(htmlDoc.getUrl());
				webPageFrame.setTitle(title);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Create the button that will display the website
			bodyPanel.add(webPageViewButton, BorderLayout.SOUTH);
			repaint();
		}
	}
	
	//Retrieve data from database - only retrieve by tags
	private void isDatabase(String url, String tags){
		String[] taglist = tags.trim().split(",");
		List<String> filters = new LinkedList<String>();
		
		for(String list : taglist){
			if(list.trim().isEmpty()) continue;
			filters.add(list);
		}
		try {
			List<HTMLDoc> docsRetrieved = htmlDocJbdc.getByTag(filters);
			StringBuffer buffer = new StringBuffer();
			
			if(docsRetrieved != null){
				//Put all the information retrieved from every page into the textArea
				for(HTMLDoc doc : docsRetrieved){
					buffer.append("\t[ Downloaded Date ]\n");
					buffer.append(doc.getDownloadedTime() + "\n");
					buffer.append("\t[ URL ]\n");
					buffer.append(doc.getUrl() + "\n");
					buffer.append("\t[ HOST ]\n");
					buffer.append(doc.getHost() + "\n");
					if(doc.getTagCount(HTMLTags.TITLE) > 0){
						buffer.append("\t[ TITLE ]\n");
						buffer.append(doc.getTagList(HTMLTags.TITLE).toArray()[0] + "\n");
					}
					if(doc.getTagCount(HTMLTags.BODY) > 0){
						buffer.append("\n\t[ BODY ]\n");
						buffer.append(doc.getTagList(HTMLTags.BODY).toString() + "\n");
					}
					buffer.append("\n\t[ LINKS ]\n");
					for(Object link : doc.getTagList(HTMLTags.A)){
						buffer.append(link + "\n");
					}
					buffer.append("\n\t[ TAGS ]\n");
					for(String tag : doc.getWordsTags()){
						buffer.append(tag + "\n");
					}
					buffer.append("\n---------------------------\n");
				}
			}
			else{
				buffer.append("NO MATCHES FOR : \n" + tags);
			}
			
			htmlTextArea.setText(buffer.toString());
			setSize(700, 500);
			repaint();
		} catch (SQLException e) {
			if(Global.DEBUG){
				e.printStackTrace();
			}
		}
	}
	
	public void isGoogleSite(String site, String query){
		isGoogleQuery(query + " site:" + site);
	}
	
	private void isGoogleQuery(String query){
		googleQuery.Update(query);
		int links = googleQuery.getTotalNumberOfLinks();
		if(links > 0){
			googleQueryList = googleQuery.getAllLinks().toArray();
			googleList = new JComboBox<Object>(googleQueryList);
			googleList.addItemListener(this);
			bodyPanel.add(googleList, BorderLayout.NORTH);
		}
		repaint();
		pack();
	}
}
