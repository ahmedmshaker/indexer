import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class Indexing implements Runnable {
    //hash map to store words after steaming
	public static HashMap<String, HashMap<Integer, ArrayList<Integer>>> steamWord = new HashMap<>();;
	//ArrayList To store positions of words in documents
	public static ArrayList<Integer> positions;
	//class of porter steamer to steam words before saving
	public static Stemmer stemmer = new Stemmer();
	//ArrayList to store documents id of words
	public static ArrayList<Integer> ids;
	//ArrayList to Store id and positions of words
	public static HashMap<Integer, ArrayList<Integer>> PositionAndID;
	//Hash map to store original words and documents id of it 
	public static HashMap<String, ArrayList<Integer>> wordAndIds = new HashMap<>();
	
    //
	public static HashMap<String, HashMap<Integer, ArrayList<Integer>>> tokenizer(
			ArrayList<Doc_Data> data) throws SQLException, Exception {

		for (Doc_Data element : data) {

			StringTokenizer toke = new StringTokenizer(element.getContent()
					.toLowerCase(), ", ?!#$%&*-_+=/+\"'».:<>()[]{}@^©");
			int count = -1;
			while (toke.hasMoreElements()) {
				positions = new ArrayList<Integer>();
				ids = new ArrayList<>();
				PositionAndID = new HashMap<>();
				String word = toke.nextToken();
				count++;
				if (stopword(word)) {
					// System.out.println("/////////"+word);
					String steam = stemmer.stem(word);
					if (!steamWord.containsKey(steam)) {
						// System.out.println("///////");
						positions.add(count);
						PositionAndID.put(element.getId(), positions);
						steamWord.put(steam, PositionAndID);

					} else {
						PositionAndID = steamWord.get(steam);
						positions.removeAll(positions);
						if (PositionAndID.get(element.getId()) != null)
							positions
									.addAll(PositionAndID.get(element.getId()));
						positions.add(count);
						PositionAndID.put(element.getId(), positions);
						steamWord.put(steam, PositionAndID);

					}
					if (!wordAndIds.containsKey(word)) {
						ids.add(element.getId());
						wordAndIds.put(word, ids);
					} else {
						ids.removeAll(ids);
						ids.addAll(wordAndIds.get(word));
						ids.add(element.getId());
						wordAndIds.put(word, ids);
					}

					System.out.println(PositionAndID.toString());
				}
			}
			System.out.println("/////////////////////" + count);

		}
		return steamWord;
	}



	@Override
	public void run() {

		ArrayList<Doc_Data> all_content = DBConnection.getcontentfromcrawler();

		try {
			System.out.println(wordAndIds.size());
			tokenizer(all_content);

			System.out.println(wordAndIds.size() + "             "+steamWord.size());
			Iterator<Entry<String, HashMap<Integer, ArrayList<Integer>>>> it = steamWord
					.entrySet().iterator();
			for (String originalWord : wordAndIds.keySet()) {
				DBConnection.insertOriginalWord(wordAndIds.get(originalWord).toString(), originalWord);
				if (it.hasNext()) {
					Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> pair = (Map.Entry<String, HashMap<Integer, ArrayList<Integer>>>) it
							.next();
					PositionAndID = steamWord.get(pair.getKey().toString());
					StringBuffer frequency = new StringBuffer();
					int i = 0;
					for (Integer id : PositionAndID.keySet()) {
						frequency.append(id + ":" + PositionAndID.get(id).size());
						if (i < PositionAndID.size() - 1) {
							frequency.append(",");
						}
						i++;
					}
					DBConnection.insertSteamWord(pair.getKey().toString(), PositionAndID.toString(),
							frequency.toString());
					it.remove(); // avoids a ConcurrentModificationException
				}
				
			}
			/*Iterator<Entry<String, ArrayList<Integer>>> it = wordAndIds
					.entrySet().iterator();
			for (String word : steamWord.keySet()) {
				PositionAndID = steamWord.get(word);
				StringBuffer frequency = new StringBuffer();
				int i = 0;
				StringBuffer ids = new StringBuffer();
				for (Integer id : PositionAndID.keySet()) {
					frequency.append(id + ":" + PositionAndID.get(id).size());
					ids.append(id + "");
					if (i < PositionAndID.size() - 1) {
						frequency.append(",");
						ids.append(",");
					}
					i++;
				}
				DBConnection.insertSteamWord(word, PositionAndID.toString(),
						frequency.toString());

				if (it.hasNext()) {
					Map.Entry<String, ArrayList<Integer>> pair = (Map.Entry<String, ArrayList<Integer>>) it
							.next();
					DBConnection.insertOriginalWord(pair.getValue().toString(),
							pair.getKey().toString());
					it.remove(); // avoids a ConcurrentModificationException
				}

			}*/

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
		if (!sword.contains(word) && word.length() > 1
				&& StringUtils.isAlpha(word)) {
			return true;
		}

		return false;
	}

}
