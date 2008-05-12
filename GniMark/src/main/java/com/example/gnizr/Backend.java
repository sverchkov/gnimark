package com.example.gnizr;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.example.gnizr.Utils.VectorUtils;

import com.gnizr.core.link.LinkManager;
import com.gnizr.core.tag.TagManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;

public class Backend implements IBackend
{
	private GnizrDao db;
	
	public Backend(GnizrDao db)
	{
		this.db = db;
	}
	
	public Vector<String> quickRecommend(String url, int offset, int count) 
	{
		//db = new GnizrDao();
		Vector<String> answer = new Vector<String>();
		LinkManager lm = new LinkManager(db);
		Bookmark bookmarkIn = lm.getFirstMatchedBookmark(url);
		Vector<Bookmark> potentialResults = new Vector<Bookmark>();
		if(bookmarkIn != null)
		{
			Vector<String> tags = new Vector<String>();
			tags.addAll(bookmarkIn.getTagList());
			BookmarkDao bookmarkDB = db.getBookmarkDao();
			TagManager tm = new TagManager(db);
			
			for(int tagCount =0; tagCount < tags.size(); tagCount++)
			{
				Tag tmpTag = tm.getTag(tags.get(tagCount));
				if(tmpTag != null)
				{
					potentialResults.addAll(bookmarkDB.pageBookmarks(tmpTag, 0, 10).getResult());
				}
			}
			
			int throwaways = offset;
			for(int t = 0; t < potentialResults.size(); t++)
			{
				String urlOut = potentialResults.get(t).getLink().getUrl();
				if(!urlOut.equalsIgnoreCase(url)) // If this is useful
				{
					if(! VectorUtils.containsIgnoreCase(answer, urlOut))
					{
						if( answer.size() >= count) break; //Get out if we're done
						if( throwaways > 0) throwaways--; // Count down throwaways
						else answer.add(urlOut); // Or add result
					}
				}
			}
		}
		return answer;
	}
	
	public Vector<String> quickRecommend(String url, String username, int offset, int count) 
	{
		Vector<String> answer = new Vector<String>();
		//db = new GnizrDao();
		LinkManager lm = new LinkManager(db);
		Link theLink = lm.getFirstMatchedBookmark(url).getLink();
		
		BookmarkDao bookmarkDB = db.getBookmarkDao();
		List<User> user = db.getUserDao().findUser(username);
		
		if(theLink == null) //Error checking
		{
			//System.out.println("A");
			return quickRecommend(url, offset, count);
		}
		
		if(user == null || user.size() <= 0) //Error checking
		{
			//System.out.println("b");
			return quickRecommend(url, offset, count);
		}
		List<Bookmark> bookmarks = bookmarkDB.findBookmark(user.get(0), theLink);
			
		Bookmark bookmarkIn = null;
		if(bookmarks == null || bookmarks.size() <= 0) //Error checking
		{
			//System.out.println("c");
			bookmarkIn = lm.getFirstMatchedBookmark(url);
		}
		else
		{
			//System.out.println("d");
			bookmarkIn = bookmarks.get(0);
		}
		
		Vector<Bookmark> potentialResults = new Vector<Bookmark>();
		if(bookmarkIn != null)
		{
			Vector<String> tags = new Vector<String>();
			tags.addAll(bookmarkIn.getTagList());
			TagManager tm = new TagManager(db);
			
			for(int tagCount =0; tagCount < tags.size(); tagCount++)
			{
				Tag tmpTag = tm.getTag(tags.get(tagCount));
				if(tmpTag != null)
				{
					potentialResults.addAll(bookmarkDB.pageBookmarks(tmpTag, 0, 10).getResult());
				}
			}
			
			int throwaways = offset;
			for(int t = 0; t < potentialResults.size(); t++)
			{
				String urlOut = potentialResults.get(t).getLink().getUrl();
				if(!urlOut.equalsIgnoreCase(url)) // If this is useful
				{
					//If it hasn't been bookmarked by the provided user
					List<Bookmark> currUserBookmarkedForRes = bookmarkDB.findBookmark(user.get(0), potentialResults.get(t).getLink());
					//System.out.println(urlOut);
					if((currUserBookmarkedForRes == null) || (currUserBookmarkedForRes.size() <= 0))
					{
						if(! VectorUtils.containsIgnoreCase(answer, urlOut))
						{
							//System.out.println("Z:"+urlOut);
							if( answer.size() >= count) break; //Get out if we're done
							if( throwaways > 0) throwaways--; // Count down throwaways
							else answer.add(urlOut); // Or add result
						}
					}
					//else System.out.println(currUserBookmarkedForRes.size());
				}
			}
		}
		return answer;
	}

	// a slow recomend when we don't know the user.
	public Vector<String> slowRecommend(String url, int offset, int count) 
	{
		Vector<String> answer = new Vector<String>();
		//count the number of tags used in all bookmarks for this url
		Hashtable<String,Double> tags = new Hashtable<String,Double>();

		//db = new GnizrDao();
		LinkManager lm = new LinkManager(db);
		Bookmark bookmarkIn = lm.getFirstMatchedBookmark(url);
		
		try
		{
			if(bookmarkIn != null)
			{
				Link start = bookmarkIn.getLink();
				List<Bookmark> bookmarksForURL = lm.getHistory(start);
				TagManager tm = new TagManager(db);
				
				if(bookmarksForURL != null)
				{
					for(int i=0; i < bookmarksForURL.size();i++)
					{
						Bookmark curr = bookmarksForURL.get(i);
						List<String> currTags = curr.getTagList();
						for(int t=0; t < currTags.size(); t++)
						{
							//if the tag exists increment its count
							if(tags.containsKey(currTags.get(t)))
							{
								double counter = 0;
								counter = tags.get(currTags.get(t));
								tags.remove(currTags.get(t));
								tags.put(currTags.get(t), counter + 1);
							}
							else //add the tag with a count of 1
							{
								tags.put(currTags.get(t), (double) 1);
							}
						}
					}
		
					//Normalize the tag counts to create a set of weights
					Vector<String> keys = new Vector<String>();
					Vector<Double> values = new Vector<Double>();
		
					for(Enumeration<String> e = tags.keys(); e.hasMoreElements();)
					{
						String tmpTag = e.nextElement();
						keys.add(tmpTag);
						values.add((double) tags.get(tmpTag));
					}
					VectorUtils.normalize(values);
					//Replace the raw tag counts with their normalized versions
					tags = new Hashtable<String,Double>();
					for(int i=0; i < keys.size(); i++)
					{
						tags.put(keys.get(i), values.get(i));
					}
		
					//a list of all the unique links with any of the given tags
					//which will hold their scores
					Hashtable<String,Double> bookmarks = new Hashtable<String,Double>();
		
					BookmarkDao bookmarkDB = db.getBookmarkDao();
					for(int t=0; t < keys.size(); t++)
					{
						
						
						int numResults = bookmarkDB.getBookmarkCount(tm.getTag(keys.get(t)));
						List<Bookmark> potentialResults= bookmarkDB.pageBookmarks(tm.getTag(keys.get(t)), 0, numResults).getResult();
						if(potentialResults != null)
						{
							for(int r=0; r < potentialResults.size(); r++)
							{
								String currLink = potentialResults.get(r).getLink().getUrl();
								List<String> tagsForLink = potentialResults.get(r).getTagList();
								if(tagsForLink != null)
								{
									double score = 0;
									for(int l=0; l < tagsForLink.size(); l++)
									{
										double valForTag = 0;
										//System.out.println(tagsForLink.get(l));
										try{valForTag = tags.get(tagsForLink.get(l));} catch (NullPointerException npe){}
										score += valForTag;
									}
									bookmarks.put(currLink, score);
								}
								//else System.out.println("No tags for "+currLink);
							}
						}
					}
		
					Vector<Tuple> pairs = new Vector<Tuple>();
		
					for(Enumeration<String> e = bookmarks.keys(); e.hasMoreElements();)
					{
						String tmpLink = e.nextElement();
						double tmpScore = (double) bookmarks.get(tmpLink);
						pairs.add(new Tuple(tmpLink, tmpScore));
					}
		
					Collections.sort( pairs );
		
					int throwaways = offset;
					for(int t = 0; t < pairs.size(); t++)
					{
						String urlOut = pairs.get(t).str;
						if(!urlOut.equalsIgnoreCase(url)) // If this is useful
						{
							if(! VectorUtils.containsIgnoreCase(answer, urlOut))
							{
								if( answer.size() >= count) break; //Get out if we're done
								if( throwaways > 0) throwaways--; // Count down throwaways
								else answer.add(urlOut); // Or add result
							}
						}
					}
				}
			}
			else
			{
				//if something broke up there we should make the same call to quick recommend.
				answer = quickRecommend(url, offset, count);
			}
		}
		catch(Exception err)
		{
			//basically, if ANYTHING goes wrong up above, call the quickRecommend.
			answer = answer = quickRecommend(url, offset, count);
		}
		catch(Throwable thr)
		{
			//overkill absolutely uber errorchecking.
			answer = answer = quickRecommend(url, offset, count);
		}
		return answer;
	}

	public Vector<String> slowRecommend(String url, String username, int offset, int count)
	{
		Vector<String> answer = new Vector<String>();
		//find how the user bookmarked this url
		Hashtable<String,Double> tags = new Hashtable<String,Double>();
		
		//db = new GnizrDao();
		
		LinkManager lm = new LinkManager(db);
		TagManager tm = new TagManager(db);
		Link start = lm.getFirstMatchedBookmark(url).getLink();
		
		List<User> user = db.getUserDao().findUser(username);
		if(user == null || user.size() == 0) //Error checking
		{
			//System.out.println("A");
			return slowRecommend(url, offset, count);
		}
		BookmarkDao bookmarkDB = db.getBookmarkDao();
		List<Bookmark> bookmarksForURL = bookmarkDB.findBookmark(user.get(0), start);
		if(bookmarksForURL == null || bookmarksForURL.size() == 0) //Error checking
		{
			//System.out.println("b");
			bookmarksForURL = lm.getHistory(start);
		}
		
		if(bookmarksForURL != null)
		{
//			count the number of tags used in the bookmarks for this url
			for(int i=0; i < bookmarksForURL.size();i++)
			{
				Bookmark curr = bookmarksForURL.get(i);
				List<String> currTags = curr.getTagList();
				for(int t=0; t < currTags.size(); t++)
				{
					//if the tag exists increment its count
					if(tags.containsKey(currTags.get(t)))
					{
						double counter = 0;
						counter = tags.get(currTags.get(t));
						tags.remove(currTags.get(t));
						tags.put(currTags.get(t), counter + 1);
					}
					else //add the tag with a count of 1
					{
						tags.put(currTags.get(t), (double) 1);
					}
				}
			}

			//Normalize the tag counts to create a set of weights
			Vector<String> keys = new Vector<String>();
			Vector<Double> values = new Vector<Double>();

			for(Enumeration<String> e = tags.keys(); e.hasMoreElements();)
			{
				String tmpTag = e.nextElement();
				keys.add(tmpTag);
				values.add((double) tags.get(tmpTag));
			}
			VectorUtils.normalize(values);
			//Replace the raw tag counts with their normalized versions
			tags.clear();
			for(int i=0; i < keys.size(); i++)
			{
				tags.put(keys.get(i), values.get(i));
			}

			//a list of all the unique links with any of the given tags
			//which will hold their scores
			Hashtable<String,Double> bookmarks = new Hashtable<String,Double>();

			for(int t=0; t < keys.size(); t++)
			{
				int numResults = bookmarkDB.getBookmarkCount(tm.getTag(keys.get(t)));
				List<Bookmark> potentialResults= bookmarkDB.pageBookmarks(tm.getTag(keys.get(t)), 0, numResults).getResult();
				if(potentialResults != null)
				{
					for(int r=0; r < potentialResults.size(); r++)
					{
						String currLink = potentialResults.get(r).getLink().getUrl();
						List<String> tagsForLink = potentialResults.get(r).getTagList();
						if(tagsForLink != null)
						{
							double score = 0;
							for(int l=0; l < tagsForLink.size(); l++)
							{
								double valForTag = 0;
								//System.out.println(tagsForLink.get(l));
								try{valForTag = tags.get(tagsForLink.get(l));} catch (NullPointerException npe){}
								score += valForTag;
							}
							bookmarks.put(currLink, score);
						}
						//else System.out.println("No tags for "+currLink);
					}
				}
			}

			Vector<Tuple> pairs = new Vector<Tuple>();

			for(Enumeration<String> e = bookmarks.keys(); e.hasMoreElements();)
			{
				String tmpLink = e.nextElement();
				double tmpScore = (double) bookmarks.get(tmpLink);
				pairs.add(new Tuple(tmpLink, tmpScore));
			}

			Collections.sort( pairs );

			int throwaways = offset;
			Vector<String> userUrls = new Vector<String>();

			List<Bookmark> userBookmarks = bookmarkDB.pageBookmarks(user.get(0), 0, bookmarkDB.getBookmarkCount(user.get(0))).getResult();

			for(int b=0; b<userBookmarks.size(); b++){
				userUrls.add(userBookmarks.get(b).getLink().getUrl());
			}
			VectorUtils.removeDuplicates(userUrls);

			for(int t = 0; t < pairs.size(); t++)
			{
				String urlOut = pairs.get(t).str;
				if(!urlOut.equalsIgnoreCase(url)) // If this is useful
				{
					//If it hasn't been bookmarked by the provided user
					List<Bookmark> currUserBookmarkedForRes = bookmarkDB.findBookmark(user.get(0), lm.getFirstMatchedBookmark(urlOut).getLink());
					//System.out.println(urlOut);
					if((currUserBookmarkedForRes == null) || (currUserBookmarkedForRes.size() <= 0))
					{
						if(! VectorUtils.containsIgnoreCase(answer, urlOut))
						{
							//System.out.println("Z:"+urlOut);
							if( answer.size() >= count) break; //Get out if we're done
							if( throwaways > 0) throwaways--; // Count down throwaways
							else answer.add(urlOut); // Or add result
						}
					}
					//else System.out.println(currUserBookmarkedForRes.size());
				}
			}

			if((answer == null) || (answer.size() == 0))
			{
				//System.out.println("Z");
				return slowRecommend(url, offset, count);
			}
		}
		return answer;
	}

}
