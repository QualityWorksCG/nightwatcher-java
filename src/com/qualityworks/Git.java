package com.qualityworks;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.json.JSONException;
import org.json.JSONObject;

public class Git {

	private static String currentDirectory = System.getProperty("user.dir");
	private String git_data = "{head: {id: '', author_name: 'Unknown Author', author_email: '', committer_name: 'Unknown Committer', committer_email: '', message: 'Unknown Commit Message'}, branch: '', remotes: []},server:''";
	private JSONObject get_data = null;

	/**
	 * Retrieves local git data from .git
	 * 
	 * @return The JSON object
	 * @throws JSONException 
	 */

	@SuppressWarnings({ "resource", "static-access" })
	public JSONObject getGitData(){
		try {
			this.get_data = new JSONObject(git_data);
			File gitDir = new File(Git.currentDirectory + "/.git");
			if (gitDir.isDirectory()) {
				Repository repository = null;
				try {
					repository = new RepositoryBuilder().findGitDir(
							new File(this.currentDirectory + "/")).build();
					ObjectId revision = repository.resolve(Constants.HEAD);
					RevCommit commit = new RevWalk(repository)
							.parseCommit(revision);

					// Get id
					this.get_data.getJSONObject("head").put("id",
							revision.getName().toString().trim());

					// Get author details
					this.get_data.getJSONObject("head").put("author_name",
							commit.getAuthorIdent().getName().toString().trim());
					this.get_data.getJSONObject("head").put(
							"author_email",
							commit.getAuthorIdent().getEmailAddress().toString()
									.trim());

					// Get committer details
					this.get_data.getJSONObject("head").put("committer_name",
							commit.getCommitterIdent().getName().toString().trim());
					this.get_data.getJSONObject("head").put(
							"committer_email",
							commit.getCommitterIdent().getEmailAddress().toString()
									.trim());

					// Get message details
					this.get_data.getJSONObject("head").put("message",
							commit.getFullMessage().toString().trim());

					// Get Branch
					this.get_data.put("branch", repository.getBranch());

					// Get Remotes
					Config config = repository.getConfig();
					List<Git.Remote> remotes = new ArrayList<>();
					for (String remote : config.getSubsections("remote")) {
						String url = config.getString("remote", remote, "url");
						remotes.add(new Git.Remote(remote, url));
					}

					this.get_data.put("remotes", remotes);
					
					//Get server for project
					String git;
					git = this.get_data.toString().trim();
					if (git.contains("https://github.com"))
						this.get_data.put("server", "github");
					if (git.contains("https://bitbucket.org"))
						this.get_data.put("server", "bitbucket");

				} catch (IOException e) {
					System.err.println("File exception" + e.getMessage());
				} 
				return this.get_data;
			}
			
		} catch (JSONException e) {
			System.err.println("JSON exception" + e.getMessage());
		}
		return this.get_data;
	}

	public static class Remote implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final String name;

		private final String url;

		public Remote(final String name, final String url) {
			this.name = name;
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}
	}
}