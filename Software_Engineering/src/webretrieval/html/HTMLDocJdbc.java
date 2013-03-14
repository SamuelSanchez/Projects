package webretrieval.html;

import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
//import java.util.Scanner;

import webretrieval.database.DaoInterface;
import webretrieval.database.DatabaseConstants;
import webretrieval.database.JdbcConnection;
import webretrieval.tools.Global;

/**
 * This class stores HTMLDoc information into the database specified by the user. It also
 * retrieve information from the datbase and checks if there's a duplicate already.
 * 
 * 
 * @author Samuel E. Sanchez
 *
 */
//TODO: Some code could be placed into a function in order to reduce lines and perform re-usability
//Either createUsePrimaryDatabase() or usePrimaryDatabase() should be use after calling an instance of this class
public class HTMLDocJdbc extends JdbcConnection implements DaoInterface {

	//////////////////////////////////////////////////////////////////////////////////
	//  String to be used when creating, retrieving  or updating data from database //
	//////////////////////////////////////////////////////////////////////////////////
	
    private static final String CREATE_TABLE_PAGE_INFORMATION = 
			  "create table if not exists " + DatabaseConstants.PAGE_INFORMATION_TABLE + 
	       	  " (Downloaded_Date Date not null," +
	       	  "Host_URL  varchar(255) not null," +
	       	  "Host_Name varchar(255) not null," +
			  "Title     varchar(255) not null," +
			  "Body      blob         not null," +
			  "PRIMARY KEY (Host_URL, Title));"; //Body cannot be used as primary key because it's of 'blob' type TOO BAD!

    private static final String CREATE_TABLE_IMAGE = 
			  "create table if not exists " + DatabaseConstants.IMAGES_TABLE + 
	       	  " (Downloaded_Date Date not null," +
	       	  "Host_URL varchar(255) not null,"  +
	       	  "Image_URL varchar(255) not null," +
	       	  "Alt      varchar(255)," +
			  "Image    blob         not null,"  +
			  "PRIMARY KEY (Host_URL, Image_URL));";
    
    private static final String CREATE_TABLE_LINKS = 
			  "create table if not exists " + DatabaseConstants.LINKS_TABLE + 
	       	  " (Downloaded_Date Date not null,"  +
	       	  "Host_URL   varchar(255) not null," +
	       	  "Target_URL varchar(255) not null," +
			  "PRIMARY KEY (Host_URL, Target_URL));";
    
    private static final String CREATE_TABLE_TAGS = 
    		  "create table if not exists " + DatabaseConstants.TAGS_TABLE + 
    		  " (Tag   varchar(255) not null,"  +
	       	  "Host_URL   varchar(255) not null," +
			  "PRIMARY KEY (Tag, Host_URL));";
	
	private static final String INSERT_PAGE_INFORMATION = "INSERT INTO " + DatabaseConstants.PAGE_INFORMATION_TABLE +
			" (Downloaded_Date, Host_URL, Host_Name, Title, Body)" +
			" VALUES (?, ?, ?, ?, ?);";
	
	private static final String INSERT_IMAGE = "INSERT INTO " + DatabaseConstants.IMAGES_TABLE +
			" (Downloaded_Date, Host_URL, Image_URL, Alt, Image)" +
			" VALUES (?, ?, ?, ?, ?);";
	
	private static final String INSERT_LINKS = "INSERT INTO " + DatabaseConstants.LINKS_TABLE +
			" (Downloaded_Date, Host_URL, Target_URL)" +
			" VALUES (?, ?, ?);";
	
	private static final String INSERT_TAGS = "INSERT INTO " + DatabaseConstants.TAGS_TABLE +
			" (Tag, Host_URL)" +
			" VALUES (?, ?);";
	
	private static final String WHERE = " WHERE 1 = 1 ";
	
	public static final String SELECT_PAGE_INFORMATION = 
			"SELECT Downloaded_Date, Host_URL, Host_Name, Title, Body" +
			" FROM " + DatabaseConstants.PAGE_INFORMATION_TABLE + WHERE;
	
	
	public static final String SELECT_IMAGE = 
			"SELECT Downloaded_Date, Host_URL, Image_URL, Alt, Image" +
			 " FROM " + DatabaseConstants.IMAGES_TABLE + WHERE;
	
	public static final String SELECT_LINKS = 
			"SELECT Downloaded_Date, Host_URL, Target_URL" +
			 " FROM " + DatabaseConstants.LINKS_TABLE + WHERE;
	
	
	public static final String SELECT_TAGS = 
			"SELECT Tag, Host_URL" +
			 " FROM " + DatabaseConstants.TAGS_TABLE + WHERE;
	
	
	//Avoid SQL Injection and make programming easier - Use Prepared Statements to create queries
	private PreparedStatement insertImagesStatement;
	private PreparedStatement insertLinksStatement;
	private PreparedStatement insertTagsStatement;
	
	/**
	 * Creates a connection to the database
	 * @param url URL of the database, or localhost if run from the same computer
	 * @param username Username of the database
	 * @param password Password of the database
	 * @throws Exception Throws an Exception, if any
	 */
	public HTMLDocJdbc(String url, String username, String password) throws Exception {
		super(url,username,password);
		//Created predefined statements
		insertStatement = connection.prepareStatement(INSERT_PAGE_INFORMATION);
		insertImagesStatement = connection.prepareStatement(INSERT_IMAGE);
		insertLinksStatement = connection.prepareStatement(INSERT_LINKS);
		insertTagsStatement = connection.prepareStatement(INSERT_TAGS);
	}
	

	/**
	 * Creates a primary database and tables if they do not exists
	 */
	//Use in case that there's no database created
	public void createUsePrimaryDatabase(String database) throws SQLException {
		//Select Database and tables
	     statement.executeUpdate( DatabaseConstants.CREATE_DATABASE + database );
	     statement.executeUpdate( DatabaseConstants.USE_DATABASE + database );
		 statement.executeUpdate( CREATE_TABLE_PAGE_INFORMATION );
		 statement.executeUpdate( CREATE_TABLE_IMAGE );
		 statement.executeUpdate( CREATE_TABLE_LINKS );
		 statement.executeUpdate( CREATE_TABLE_TAGS );
	}
	
	/**
	 * Uses the primary database
	 */
	//Use when there's a database created already otherwise it will throw an error
	public void usePrimaryDatabase(String database) throws SQLException {
		statement.executeUpdate( DatabaseConstants.USE_DATABASE + database);
	}
	
	/**
	 * Disconnects all statements from the database
	 */
	public void Disconnect() throws SQLException{
		statement.close();
		if(insertStatement != null) insertStatement.close();
		if(insertImagesStatement != null) insertImagesStatement.close();
		if(insertLinksStatement != null) insertLinksStatement.close();
		if(insertTagsStatement != null) insertTagsStatement.close();
		connection.close();
	}	
	
	/**
	 * Executes all batches from the database
	 */
	public void executeBatch() throws SQLException{
		if(batchUpdatesSupported){
			statement.executeBatch();
			//Use multiple try/catch exceptions
			try{
				if(insertStatement != null)
					insertStatement.executeBatch();
			}catch(Exception e){
				if(Global.WARNING)
					System.out.println("Warning [Pages Info Table]: " + e.getMessage());
			}
			try{
				if(insertImagesStatement != null)
					insertImagesStatement.executeBatch();
			}catch(Exception e){
				if(Global.WARNING)
					System.out.println("Warning [Images Table]: " + e.getMessage());
			}
			try{
				if(insertLinksStatement != null)
					insertLinksStatement.executeBatch();
			}catch(Exception e){
				if(Global.WARNING)
					System.out.println("Warning [Links Table]: " + e.getMessage());
			}
		}
	}

	/**
	 * Find if there are any relating objects in the database
	 * @param filter Filter Object to be searched in the database
	 * @return The number of objects encounterd in the database
	 */
	//Use to find out if there are duplicates before inserting into database
	public int count(Object filter){
		int count = 0;
		try{
			//Don't make unnecessary computations
			if(filter == null || !(filter instanceof HTMLDoc)){
				return 0;
			}
			
			//Create query and append all information given by the user
			StringBuffer selectPI = new StringBuffer();
			List<Object> queryList = new LinkedList<Object>();
			selectPI.append(SELECT_PAGE_INFORMATION);
			
			//This should be used when filtering through a high level UI
//			if(((HTMLDoc)filter).getDownloadedTime() != null){
//				selectPI.append("AND Downloaded_Date = ?");
//				queryList.add(((HTMLDoc)filter).getDownloadedTime());
//			}
			
			if(((HTMLDoc)filter).getUrl() != null){
				selectPI.append(" AND Host_URL = ?");
				queryList.add(((HTMLDoc)filter).getUrl());
			}
			
			if(((HTMLDoc)filter).getHost() != null){
				selectPI.append(" AND Host_Name = ?");
				queryList.add(((HTMLDoc)filter).getHost());
			}
			
			if(((HTMLDoc)filter).getTagList(HTMLTags.TITLE) != null){
				selectPI.append(" AND Title = ?");
				queryList.add(((HTMLDoc)filter).getTagList(HTMLTags.TITLE).get(0));
			}
			
			//Avoid SQL Injection - Use a Prepared Statement
			PreparedStatement pageInfo = connection.prepareStatement(selectPI.toString());
			for(int i = 0, j = 1; i < queryList.size(); i++, j++){
				if(queryList.get(i) instanceof String){
					pageInfo.setString(j, (String) queryList.get(i));
				}else{
					throw new SQLException("Error : Invalid Object!");
				}
			}
			
			if(Global.DEBUG){
				System.out.println("Query : " + pageInfo.toString());
			}
			
			//Retrieve data
			ResultSet resultPageInfo = pageInfo.executeQuery();
			while(resultPageInfo.next()){
				count++;
			}
		}catch(Exception e){
			count = 0;
		}
		return count;
	}
	
	/**
	 * Puts an Object into the database. It's the HTML Document object with all its data.
	 */
	//Add HTMLDoc objects into database
	public void put(Object obj) throws SQLException{  
		if(obj == null || !(obj instanceof HTMLDoc)){
			//throw new SQLException("HTMLDoc could not be added to database!");
			if(Global.WARNING) System.out.println("Warning : HTMLDoc could not be added to database!");
			return;
		}
		
		HTMLDoc htmlDoc = (HTMLDoc)obj; //Cast Object
		
		//If there's a duplicate then do not add it
		if(count(obj) == 0){
			//Create the statement
			insertStatement.setDate(1, htmlDoc.getDownloadedTime() != null ? Date.valueOf(htmlDoc.getDownloadedTime()) : null);
			insertStatement.setString(2, htmlDoc.getUrl());
			insertStatement.setString(3, htmlDoc.getHost());
			insertStatement.setString(4, htmlDoc.getTagList(HTMLTags.TITLE) != null ? (String) htmlDoc.getTagList(HTMLTags.TITLE).get(0) : null );
			insertStatement.setString(5, htmlDoc.getTagList(HTMLTags.BODY) != null ? (String)htmlDoc.getTagList(HTMLTags.BODY).get(0) : null);
			
			if(Global.DEBUG){
				System.out.println("Query : " + insertStatement.toString());
			}
	
			//Execute Query to store Web page information
			if(batchUpdatesSupported){
				insertStatement.addBatch();
			}else{
				insertStatement.executeUpdate();
			}
			
			//Store all the images
			List<Object> images = htmlDoc.getTagList(HTMLTags.IMG);
			for(Object img : images){
				//Make sure that it is the ImageHTML class
				if(!(img instanceof HTMLImage)) break;	
				insertImagesStatement.setDate(1, htmlDoc.getDownloadedTime() != null ? Date.valueOf(htmlDoc.getDownloadedTime()) : null);
				insertImagesStatement.setString(2, htmlDoc.getUrl());	 	//Host URL
				insertImagesStatement.setString(3, ((HTMLImage)img).src); 	//URL of image
				insertImagesStatement.setString(4, ((HTMLImage)img).alt);
				insertImagesStatement.setBinaryStream(5, ((HTMLImage)img).image, ((HTMLImage)img).imageLength);
				
				if(Global.DEBUG){
					System.out.println("Query IMG: " + insertImagesStatement.toString());
				}
				
				//Execute Query to store Web page information
				if(batchUpdatesSupported){
					insertImagesStatement.addBatch();
				}else{
					insertImagesStatement.executeUpdate();
				}
			}
			
			//Store all the links
			List<Object> links = htmlDoc.getTagList(HTMLTags.A);
			for(Object link : links){
				insertLinksStatement.setDate(1, htmlDoc.getDownloadedTime() != null ? Date.valueOf(htmlDoc.getDownloadedTime()) : null);
				insertLinksStatement.setString(2, htmlDoc.getUrl()); 	//Host URL
				insertLinksStatement.setString(3, (String)link);	 	//Target URL
				
				if(Global.DEBUG){
					System.out.println("Query Link: " + insertLinksStatement.toString());
				}
				
				//Execute Query to store Web page information
				if(batchUpdatesSupported){
					insertLinksStatement.addBatch();
				}else{
					insertLinksStatement.executeUpdate();
				}
			}
		}
		else{
			if(Global.WARNING)
				System.out.println("Warning : There's a duplicate of [ " + ((HTMLDoc)obj).getUrl() + " ]");
			
			//Store all the tags
			List<String> tags = htmlDoc.getWordsTags();
			for(String tag : tags){
				if(tag.trim().isEmpty()) continue;						//Avoid blank tags
				
				insertTagsStatement.setString(1,tag);					//Tag
				insertTagsStatement.setString(2, htmlDoc.getUrl()); 	//Host URL
				
				if(Global.DEBUG){
					System.out.println("Query Tag: " + insertTagsStatement.toString());
				}
				//Execute Query to store Web page tags - This is non-deterministic : depends on the user's input
				//Use try catch because the tag and Host URL are used as primary key, there might be repeated tags for a site
				try{
					insertTagsStatement.executeUpdate();
				}catch(Exception e){
					if(Global.WARNING) System.out.println("Error [Tag table] : " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Gets all the Objects from the database if any, null otherwise
	 */
	//Retrieve data from database depending on the filter data
	public List<HTMLDoc> get(Object filter) throws SQLException{
		//Don't make unnecessary computations
		if(filter == null || (!(filter instanceof String) && !(filter instanceof HTMLDoc))){
			return null;
		}
		
		//It's looking by tag
		if(filter instanceof String){
			return getByTag((String)filter);
		}
		
		//Create query and append all information given by the user
		StringBuffer selectPI = new StringBuffer();
		List<Object> queryList = new LinkedList<Object>();
		selectPI.append(SELECT_PAGE_INFORMATION);
		
		//This should be used when filtering through a high level UI
//		if(((HTMLDoc)filter).getDownloadedTime() != null){
//			selectPI.append("AND Downloaded_Date = ?");
//			queryList.add(((HTMLDoc)filter).getDownloadedTime());
//		}
		
		if(((HTMLDoc)filter).getUrl() != null){
			selectPI.append(" AND Host_URL = ?");
			queryList.add(((HTMLDoc)filter).getUrl());
		}
		
		if(((HTMLDoc)filter).getHost() != null){
			selectPI.append(" AND Host_Name = ?");
			queryList.add(((HTMLDoc)filter).getHost());
		}
		
		if(((HTMLDoc)filter).getTagList(HTMLTags.TITLE) != null){
			selectPI.append(" AND Title = ?");
			queryList.add(((HTMLDoc)filter).getTagList(HTMLTags.TITLE).get(0));
		}
		
		//Avoid SQL Injection - escape characters before inserting into database
		PreparedStatement pageInfo = connection.prepareStatement(selectPI.toString());
		for(int i = 0, j = 1; i < queryList.size(); i++, j++){
			if(queryList.get(i) instanceof String){
				pageInfo.setString(j, (String) queryList.get(i));
			}else{
				throw new SQLException("Error : Invalid Object!");
			}
		}
		if(Global.DEBUG){
			System.out.println("Query : " + selectPI.toString());
		}
		//Retrieve data
		List<HTMLDoc> documents = new LinkedList<HTMLDoc>();
		ResultSet resultPageInfo = pageInfo.executeQuery();
		while(resultPageInfo.next()){
			//For every match create an object
			HTMLDoc htmlDoc = new HTMLDoc();
			htmlDoc.setDownloadedTime(resultPageInfo.getString(1));
			htmlDoc.setUrl(resultPageInfo.getString(2));
			htmlDoc.setHost(resultPageInfo.getString(3));
			
			List<Object> title = new LinkedList<Object>();
			title.add(resultPageInfo.getString(4));
			htmlDoc.setTags(HTMLTags.TITLE, title);
			
			List<Object> body = new LinkedList<Object>();
			title.add(resultPageInfo.getString(5));
			htmlDoc.setTags(HTMLTags.BODY, body);
			
			documents.add(htmlDoc);
		}
		
		//If there are not documents to retrieve, then do not attempt to retrieve images and links
		if(documents.isEmpty()) return null;

		//For every page retrieve the images and links
		List<Object> images = null;
		List<Object> links = null;		//Objects because they'll go into a Map with Object value
		List<String> tags = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		for(HTMLDoc page : documents){
			images = new LinkedList<Object>();
			links = new LinkedList<Object>();
			tags = new LinkedList<String>();
			
			//Add the list of Images to its corresponding page
			pst = connection.prepareStatement(SELECT_IMAGE + " AND Host_URL = ? AND Downloaded_Date = ?");
			pst.setString(1, page.getUrl());
			pst.setString(2, page.getDownloadedTime());
			rs = pst.executeQuery();
			while(rs.next()){
				HTMLImage img = new HTMLImage();
				img.src = rs.getString(3);
				img.alt = rs.getString(4);
				Blob b = rs.getBlob(5);
				img.image = b.getBinaryStream();
				img.imageLength = (int)b.length();
				images.add(img);
			}
			page.setTags(HTMLTags.IMG, images);
			
			//Add the list of links to its corresponding page
			pst = connection.prepareStatement(SELECT_LINKS + " AND Host_URL = ? AND Downloaded_Date = ?");
			pst.setString(1, page.getUrl());
			pst.setString(2, page.getDownloadedTime());
			rs = pst.executeQuery();
			while(rs.next()){
				links.add(rs.getString(3));
			}
			page.setTags(HTMLTags.A, links);
			
			//Get all the tags associated with this site
			pst = connection.prepareStatement(SELECT_TAGS + " AND Host_URL = ?");
			pst.setString(1, page.getUrl());
			rs = pst.executeQuery();
			while(rs.next()){
				tags.add(rs.getString(1)); //Tag position in the Select Query
			}
			page.setWordTags(tags);
		}
		
		if(pst != null) pst.close();
		if(rs != null) rs.close();
		
		return documents;
	}
	
	/**
	 * Retrieve all the information from the database given the tags
	 * @param filter Tag to be searched in the database
	 * @return List of HTMLDocs that contains the tag
	 * @throws SQLException Throws an exception, if any
	 */
	//Retrieve sites by their tag
	public List<HTMLDoc> getByTag(String filter) throws SQLException{
		//Don't make unnecessary computations
		if(filter == null){
			return null;
		}
		//Make a list and insert the word into the list
		List<String> word = new LinkedList<String>();
		word.add(filter);
		return getByTag(word);
	}
	
	/**
	 * Retrieve all the information from the database given the tags
	 * @param filters Tags to be searched in the database
	 * @return List of HTMLDocs that contains the tag
	 * @throws SQLException Throws an exception, if any
	 */
	//Retrieve sites by a list of tags
	public List<HTMLDoc> getByTag(List<String> filters) throws SQLException{
		//Create query and append all information given by the user
		StringBuffer query = new StringBuffer();
		query.append(SELECT_PAGE_INFORMATION);
		query.append(" AND Host_URL in ( select Host_URL from Tags where Tag in ( ");
		
		//Create a query for the Prepared Statement
		for(int f = 0; f < filters.size(); f++){
			if(f+1 == filters.size()){
				query.append("? ))");
			}
			else{
				query.append("?, ");
			}
		}
		
		//Avoid SQL Injection - escape characters before inserting into database
		PreparedStatement pageInfo = connection.prepareStatement(query.toString());
		for(int i = 0, j = 1; i < filters.size(); i++, j++){
			pageInfo.setString(j, filters.get(i));
		}
		
		if(Global.DEBUG){
			System.out.println("Query : " + pageInfo.toString());
		}
		//Retrieve data
		List<HTMLDoc> pages = new LinkedList<HTMLDoc>();
		ResultSet resultPageInfo = pageInfo.executeQuery();
		while(resultPageInfo.next()){
			//For every match create an object
			HTMLDoc htmlDoc = new HTMLDoc();
			htmlDoc.setDownloadedTime(resultPageInfo.getString(1));
			htmlDoc.setUrl(resultPageInfo.getString(2));
			htmlDoc.setHost(resultPageInfo.getString(3));
			
			List<Object> title = new LinkedList<Object>();
			title.add(resultPageInfo.getString(4));
			htmlDoc.setTags(HTMLTags.TITLE, title);
			
			List<Object> body = new LinkedList<Object>();
			title.add(resultPageInfo.getString(5));
			htmlDoc.setTags(HTMLTags.BODY, body);
			
			pages.add(htmlDoc);
		}
		
		//If there are not documents to retrieve, then do not attempt to retrieve images and links
		if(pages.isEmpty()) return null;

		//For every page retrieve the images and links
		List<Object> images = null;
		List<Object> links = null;		//Objects because they'll go into a Map with Object value
		List<String> tags = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		for(HTMLDoc page : pages){
			images = new LinkedList<Object>();
			links = new LinkedList<Object>();
			tags = new LinkedList<String>();
			
			//Add the list of Images to its corresponding page
			pst = connection.prepareStatement(SELECT_IMAGE + " AND Host_URL = ? AND Downloaded_Date = ?");
			pst.setString(1, page.getUrl());
			pst.setString(2, page.getDownloadedTime());
			rs = pst.executeQuery();
			while(rs.next()){
				HTMLImage img = new HTMLImage();
				img.src = rs.getString(3);
				img.alt = rs.getString(4);
				Blob b = rs.getBlob(5);
				img.image = b.getBinaryStream();
				img.imageLength = (int)b.length();
				images.add(img);
			}
			page.setTags(HTMLTags.IMG, images);
			
			//Add the list of links to its corresponding page
			pst = connection.prepareStatement(SELECT_LINKS + " AND Host_URL = ? AND Downloaded_Date = ?");
			pst.setString(1, page.getUrl());
			pst.setString(2, page.getDownloadedTime());
			rs = pst.executeQuery();
			while(rs.next()){
				links.add(rs.getString(3));
			}
			page.setTags(HTMLTags.A, links);
			
			//Get all the tags associated with this site
			pst = connection.prepareStatement(SELECT_TAGS + " AND Host_URL = ?");
			pst.setString(1, page.getUrl());
			rs = pst.executeQuery();
			while(rs.next()){
				tags.add(rs.getString(1)); //Tag position in the Select Query
			}
			page.setWordTags(tags);
		}
		
		if(pst != null) pst.close();
		if(rs != null) rs.close();
		
		return pages;
	}
	
	//Main class to test queries, this assumes that you already have tables filled with data
//	public static void main(String[] args){
//		try{
//			HTMLDocJdbc doc = new HTMLDocJdbc(DatabaseConstants.MYSQL_URL, DatabaseConstants.USERNAME, DatabaseConstants.PASSWORD);
//			doc.createUsePrimaryDatabase(DatabaseConstants.WEB_DATABASE);
//			Scanner scanner = new Scanner(System.in);
//			while(true){
//				try{
//					System.out.println("-------------------------------");
//					System.out.print("\nTag : " );
//					String line = scanner.nextLine();
//					if(line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;
//					List<String> filters = new LinkedList<String>();
//					String[] tags = line.split("\\s+");
//					for(String tag : tags){
//						filters.add(tag);
//					}
//					List<HTMLDoc> docs = doc.getByTag(filters);
//					for(HTMLDoc html : docs){
//						System.out.println("URL : " + html.getUrl());
//					}
//				}catch(Exception e){
//					System.out.println("Error : " + e.getMessage());
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
}