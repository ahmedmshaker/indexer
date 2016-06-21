import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainClass {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, Exception {
		// TODO Auto-generated method stub
		
		
		/*ArrayList< Doc_Data> all_content=DBConnection.getcontentfromcrawler();
		System.out.println(all_content.get(0).getContent());
		/*ArrayList<Doc_Data> d=new ArrayList<Doc_Data>();
		d.add(new Doc_Data(5,"ahmed,and mo2 nady?ahmed mo2 nady"));
		Indexing in=new Indexing();
		HashMap<Integer,HashMap<String, ArrayList<Integer>>> oo=in.tokenizer(all_content);
		/*for (Integer key : oo.keySet()) {
			HashMap<String, ArrayList<Integer>> temp =new HashMap<String, ArrayList<Integer>>();
			temp=oo.get(key);
			for (String smallkey : temp.keySet()) {
				int frq=temp.get(smallkey).size();
				DBConnection.insertSteamWord(key,smallkey, temp.get(smallkey)+"",frq);
			}
			
		}
		System.out.println(oo.toString());*/

		Thread t = new Thread(new Indexing());
		t.start();

	}

}
