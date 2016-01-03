package com.qualityworks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class Nightwatcher {

	private String url = Parser.URL;
	private static DataOutputStream wr = null;
	private HttpURLConnection con = null;
	private int responseCode = 500;
	private JSONObject res =null;

	/**
	 * Retrieves XML test results and sends build data to nightwatcher.io
	 * 
	 * @param path
	 *            project path to the test results
	 */
	public void sendToNightwatcher(String path) {

		String nightwatcherJSON = new Parser().constructJSON(path);
		System.out.println("\n\n" + nightwatcherJSON);

		try {

			// Establish a connection to URL
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			// Configure post request
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

			// Send post request
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(nightwatcherJSON);

			// Close stream
			wr.flush();
			wr.close();

			// Get response code
			responseCode = con.getResponseCode();

		} catch (ProtocolException ex) {
			System.err.println(ex.getMessage());

		} catch (MalformedURLException ex) {
			System.err.println(ex.getMessage());

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}

		System.out.println("Sending Test Results...");

		// Format response
		BufferedReader input = null;
		try {

			input = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer response = new StringBuffer();

			String inputLine = "";
			while ((inputLine = input.readLine()) != null) {
				response.append(inputLine);
			}

			input.close();
			res = new JSONObject(response.toString());
			System.out.println("Response Code : " + responseCode);
			if(res.get("err").toString() == "null"){
				System.out.println("Response Details : " + res.getString("msg"));
			}else{
				System.out.println("Response Details : " + res.getString("err"));
			}
				

		} catch (IOException | JSONException ex) {
			System.err.println(ex.getMessage());
		}

	}
}
