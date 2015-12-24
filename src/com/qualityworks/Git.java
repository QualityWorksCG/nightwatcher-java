package com.qualityworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Git {
	
	private String currentDirectory = System.getProperty("user.dir");
	static String REGEX_BRANCH = "^ref: refs\\/heads\\/(\\w+)$";
	Pattern regex_branch = Pattern.compile(REGEX_BRANCH);
	String git_commit;
	
	public String localGit(String knownCommit, String knownBranch){
		String filePath, commitPath, commit = null, head = null, branch = null;
		
		filePath = this.currentDirectory + "/" + ".git" + "/" + "HEAD";
		
		try {
		    BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("ISO-8859-1"));
		    while ((head = br.readLine()) != null) {
		        head = new String(head.getBytes("UTF-8")).trim();
		        
		        Matcher regex_branch_matcher = this.regex_branch.matcher(head);
				if(regex_branch_matcher.find())
					branch = regex_branch_matcher.group(1).trim();
				
				if(branch==null)
					return this.git_commit = "{git_commit: '" + head + "'";
				
				commitPath = this.currentDirectory + "/.git/refs/heads/" + branch; 
				try {
				    BufferedReader br2 = Files.newBufferedReader(Paths.get(commitPath), Charset.forName("ISO-8859-1"));
				    while ((commit = br2.readLine()) != null) {
				    	commit = new String(commit.getBytes("UTF-8")).trim();
				    	
				    	return this.git_commit = "{git_commit : '" + commit + "' , git_branch: '" + branch + "'";
				    }
				} catch (IOException ex) {
				    //...
				}
		    }
		} catch (IOException ex) {
		    //...
		}
		return this.git_commit;
	}
}
