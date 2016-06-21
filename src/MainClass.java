import java.sql.SQLException;

public class MainClass {

	/**
	 * @param args
	 * @throws Exception
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException, Exception {
		// TODO Auto-generated method stub

		Thread t = new Thread(new Indexing());
		t.start();

	}

}
