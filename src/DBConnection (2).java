import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DBConnection {

	public static Connection createConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(Constants.dbClass);
			con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
		} catch (Exception e) {
			throw e;
		} finally {
			return con;
		}
	}
	
	public static boolean insertSteamWord(int id,String word, String postions,int frequncy) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Statement stmt = dbConn.createStatement();
			String query = "INSERT into steamer(DocID,word,position,frequency) values( ?,?,? , ?)";
			PreparedStatement statment = dbConn.prepareStatement( query );
			statment.setInt(1, id);
			statment.setString(2, word);
			statment.setString(3, postions);
			statment.setInt(4, frequncy);

			
			//System.out.println(query);
			int records = statment.executeUpdate();//stmt.executeUpdate(statment);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static boolean insertOriginalWord(int id,String word) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Statement stmt = dbConn.createStatement();
			String query = "INSERT into origenalword(docID,word) values( ?,?)";
			PreparedStatement statment = dbConn.prepareStatement( query );
			statment.setInt(1, id);
			statment.setString(2, word);
			
			
			//System.out.println(query);
			int records = statment.executeUpdate();//stmt.executeUpdate(statment);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static ArrayList<Doc_Data>  getcontentfromcrawler(){
		ArrayList<Doc_Data> arraylist = new ArrayList<Doc_Data>();
		Connection dbConn = null;
		Statement stmt;
		
		try {
			dbConn = DBConnection.createConnection();
			stmt = dbConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
		              java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		
		String sql = "select id,content from task_crawler where id<3";  
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			
			arraylist.add(new Doc_Data(rs.getInt("id"),getTextFromHtml(rs.getString("content"))));
		}
		
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arraylist;
		
	}
	
	public static String getTextFromHtml(String content){
		org.jsoup.nodes.Document doc = Jsoup.parse(content);
		String text =  doc.body().text();
		
		
		return text;
	}
	
	
}
