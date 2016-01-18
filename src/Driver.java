import com.qualityworks.Nightwatcher;

public class Driver {

	public static void main(String[] args) {

		Nightwatcher nightwatcher = new Nightwatcher();
		// Add 'sample.txt' with testNG XML test results to root folder to test
		nightwatcher.sendToNightwatcher("sample.xml");

	}
}