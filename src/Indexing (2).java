import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

public class Indexing implements Runnable{

	public static HashMap<String, ArrayList<Integer>> wordsInDoc;
	public static ArrayList<Integer> positions;
	public static Stemmer stemmer = new Stemmer();
	public static HashMap<Integer, HashMap<String, ArrayList<Integer>>> ret;
	public static ArrayList<String> original ;

	public static HashMap<Integer, HashMap<String, ArrayList<Integer>>> tokenizer(
			ArrayList<Doc_Data> data) throws SQLException, Exception {

		for (Doc_Data element : data) {
			
			wordsInDoc = new HashMap<>();
			original = new ArrayList<String>();

			StringTokenizer toke = new StringTokenizer(element.getContent()
					.toLowerCase(), ", ?!#$%&*-_+=/+\"'».:<>()[]{}@^©");
			// System.out.println("/////////////////////"+toke.countTokens());
			int count = -1;
			while (toke.hasMoreElements()) {
				positions = new ArrayList<Integer>();
				ret = new HashMap<>();
				String word = toke.nextToken();
				//System.out.println("/////////"+word+"  "+StringUtils.isAlpha(word));
				count++;
				if (stopword(word)) {
					//System.out.println("/////////"+word);
					String steam = stemmer.stem(word);
					if (!wordsInDoc.containsKey(steam)) {
						// System.out.println("///////");
						positions.add(count);
						wordsInDoc.put(steam, positions);
						original.add(word);

					} else {
						positions.removeAll(positions);
						positions.addAll(wordsInDoc.get(steam));
						positions.add(count);
						wordsInDoc.replace(steam, positions);
						
					}

					ret.put(element.getId(), wordsInDoc);
				}
			}
			System.out.println("/////////////////////" + count);
			
			int i = 0;
            for (String smallkey: wordsInDoc.keySet()) {
            	int frq=wordsInDoc.get(smallkey).size();
				boolean insert  =DBConnection.insertSteamWord(element.getId(),smallkey, wordsInDoc.get(smallkey)+"",frq);
				if (insert) {
					System.out.println("inserted");
				}
				if(i<original.size()){
				DBConnection.insertOriginalWord(element.getId(), original.get(i));
				i++;}
			}    

		}
		return ret;
	}

	public static boolean stopword(String word) {

		String sword = " a about above after again against all aman and any are aren't as at be because been before "
				+ "being below between both but by can't cannotcould couldn't did didn't do does doesn't doing"
				+ "don't down during each few for from further had hadn't has hasn't have haven't having he"
				+ "he'll he's her here here's hersherself him himself his how how's i i'd i'll i'm i've if in "
				+ "intois isn't it it's its itself let's me more most mustn't my myself no nor not of off "
				+ "he'd on once only or other ought our ours"
				+ "ourselves outover own shan'tsheshe'd she'll she's should shouldn't so some such "
				+ "than that that's the their theirs them themselves then there there's these they they'd they'll "
				+ "they're they've this those through to too under until up very was wasn't we we'd we'll we're we've"
				+ "were weren't what what's when when's where where's which while who who's whom why why's with won't would "
				+ "wouldn't you you'd you'll you're you've your yours yourself yourselves";
		if (!sword.contains(word) && word.length()>1 &&StringUtils.isAlpha(word)) {
			return true;
		}

		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		ArrayList< Doc_Data> all_content=DBConnection.getcontentfromcrawler();
		
		try {
			tokenizer(all_content);
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
