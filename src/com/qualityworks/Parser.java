package com.qualityworks;

// Java imports
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// Vender imports
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class Parser {

	Git git = new Git();
	private String xmlResults = "";
	private BufferedReader br = null;
	private String currentDirectory = System.getProperty("user.dir");
	public final static String URL = "http://nightwatcher.nodeqa.io/tests";
	private static String rawJson = "{'url' : '', type : 'JAVA', 'results': '', 'currentOptions' : {repo_token: '', git : ''}}";

	/**
	 * Creates a JSON object that Nightwatcher will understand
	 * 
	 * @param testResults
	 *            test results in JSON format
	 * @param path
	 *            project path to the test results
	 * @return The JSON string
	 */
	public String constructJSON(String path) {
		JSONObject jsonObj = null;
		try {

			jsonObj = new JSONObject(rawJson);

			jsonObj.put("url", URL);
			jsonObj.put("results", this.toJSON(path));
			jsonObj.getJSONObject("currentOptions").put("repo_token",
					this.getOptions());
			jsonObj.getJSONObject("currentOptions").put("git",
					this.git.getGitData());

		} catch (JSONException ex) {
			System.err.println(ex.getMessage());
		}

		return jsonObj.toString();
	}

	/**
	 * Retrieves XML test results and returns the converted JSON
	 * 
	 * @param path
	 *            project path to the test results
	 * @return The converted JSON object
	 */
	private JSONObject toJSON(String path) {

		String filePath = "";
		String sCurrentLine = "";
		JSONObject xmlJSONObj = null;

		try {
			filePath = currentDirectory + "/" + path;
			br = new BufferedReader(new FileReader(filePath));

			while ((sCurrentLine = br.readLine()) != null)
				xmlResults += sCurrentLine.trim();

			xmlJSONObj = XML.toJSONObject(xmlResults);

		} catch (JSONException ex) {
			System.err.println(ex.getMessage());

		} catch (FileNotFoundException ex) {
			System.err.println("Unable to find file in: " + filePath);

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}

		return xmlJSONObj;
	}

	/**
	 * Retrieves repo token from nightwatcher.yml
	 * 
	 * @return The repo token string
	 */
	private String getOptions() {

		String filePath = "";
		String repoToken = "";
		String sCurrentLine = "";

		try {
			filePath = currentDirectory + "/.nightwatcher.yml";
			br = new BufferedReader(new FileReader(filePath));

			while ((sCurrentLine = br.readLine()) != null)
				repoToken += sCurrentLine.trim();

		} catch (FileNotFoundException ex) {
			System.err.println("Unable to find file in: " + filePath);

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}

		return repoToken.replace("repo_token:", "").trim();
	}
}
