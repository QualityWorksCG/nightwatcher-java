package com.qualityworks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

public class Git {
	
	private static String currentDirectory = System.getProperty("user.dir");
	private static String REGEX_BRANCH = "^ref: refs\\/heads\\/(\\w+)$";
	private static String REGEX_LINE = "\n(.*)";
	private static String REGEX_AUTHOR = "(author) (.*) <(.*)>";
	private static String REGEX_COMMITTER = "committer (.*) <(.*)>";
	private Pattern regex_branch = Pattern.compile(REGEX_BRANCH);
	private Pattern regex_line = Pattern.compile(REGEX_LINE);
	private Pattern regex_author = Pattern.compile(REGEX_AUTHOR);
	private Pattern regex_committer = Pattern.compile(REGEX_COMMITTER);
	private String git_commit = "{git_commit : '' , git_branch: ''}";
	private String git_data = "{head: {id: '', author_name: 'Unknown Author', author_email: '', committer_name: 'Unknown Committer', committer_email: '', message: 'Unknown Commit Message'}, branch: '', remotes: []}";
	private JSONObject get_data = null;
	
	public JSONObject getGitData(){
		String knownCommit="", knownBranch="", localGitRawJSON="";
		
		localGitRawJSON = localGit(knownCommit,knownBranch);
		
		JSONObject jsonObj = null;
		try{
			
			jsonObj = new JSONObject(localGitRawJSON);
			get_data = new JSONObject(git_data);
			
			if(jsonObj.get("git_commit") != "")
				get_data.getJSONObject("head").put("id",jsonObj.get("git_commit"));
			
			if(jsonObj.get("git_branch") != "")
				get_data.put("branch",jsonObj.get("git_branch"));
			
			try {
				
				 String line,info = "";
				 
				  Process p = Runtime.getRuntime().exec("git cat-file -p " + get_data.getJSONObject("head").get("id"));
				  BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				  while ((line = input.readLine()) != null) {
					  info = info + line + "\n";
				  }
				  
				  Matcher regex_line_matcher = this.regex_line.matcher(info);
				  int count = 0;
				  while (regex_line_matcher.find()){
					  count++;
					  String details = "";
					  String[] words = null;
					  
					  details = regex_line_matcher.group(1).toString().trim();
					  
					  // Get author details
					  if(this.regex_author.matcher(details).find()){
						  details = details.replaceAll("<", "");
						  details = details.replaceAll(">", "");
						  
						  words = details.split(" ");
						  
						  get_data.getJSONObject("head").put("author_name",words[1].toString().trim() + " " + words[2].toString().trim());
						  get_data.getJSONObject("head").put("author_email",words[3].toString().trim());
					  }  
					  
					  // Get committer details
					  if(this.regex_committer.matcher(details).find()){
						  details = details.replaceAll("<", "");
						  details = details.replaceAll(">", "");
						  
						  words = details.split(" ");
						  
						  get_data.getJSONObject("head").put("committer_name",words[1].toString().trim() + " " + words[2].toString().trim());
						  get_data.getJSONObject("head").put("committer_email",words[3].toString().trim());
						  
					  }
					  
					  // Get message details
					  if(count == 4){
						  get_data.getJSONObject("head").put("message",details.toString().trim());
					  }
				  }
				  input.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}catch (JSONException ex) {
			System.err.println(ex.getMessage());
		}

		return get_data;
	}
	
	public void remotes(){
		try{
			List<String> command = new ArrayList<String>();
			command.add("git");
			command.add("remote");
			command.add("-v");
			command.add("show");
			command.add("-n");
			command.add("origin");
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		while ((line = reader.readLine()) != null){
		   System.out.println(line);

		}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves local git data from .git
	 * @param  
	 * @param  
	 * @return       The JSON string
	 */
	public String localGit(String knownCommit, String knownBranch){
		String filePath, commitPath, commit = null, head = null, branch = null, git;
		
		git = Git.currentDirectory + "/" + ".git";
		filePath = Git.currentDirectory + "/" + ".git" + "/" + "HEAD";
		
		File gitDir = new File(git);
		if (gitDir.isDirectory()) {
				
				try {
				    BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("ISO-8859-1"));
				    while ((head = br.readLine()) != null) {
				        head = new String(head.getBytes("UTF-8")).trim();
				        
				        Matcher regex_branch_matcher = this.regex_branch.matcher(head);
						if(regex_branch_matcher.find())
							branch = regex_branch_matcher.group(1).trim();
						
						if(branch==null)
							return this.git_commit = "{git_commit: '" + head + "'";
						
						commitPath = Git.currentDirectory + "/.git/refs/heads/" + branch; 
						
						try {
						    BufferedReader br2 = Files.newBufferedReader(Paths.get(commitPath), Charset.forName("ISO-8859-1"));
						    while ((commit = br2.readLine()) != null) {
						    	commit = new String(commit.getBytes("UTF-8")).trim();
						 
						    	return this.git_commit = "{git_commit : '" + commit + "' , git_branch: '" + branch + "'}";
						    }
						} catch (IOException ex) {
						   ex.printStackTrace();
						}
				    }
				} catch (IOException ex) {
				    ex.printStackTrace();
				}
			} 
		return this.git_commit;
	}
}
