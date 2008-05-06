import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import Utils.VectorUtils;

import com.gnizr.core.link.LinkManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;

public class Backend implements IBackend
{

	public Vector<String> quickRecommend(String url, int offset, int count) 
	{
		GnizrDao db = new GnizrDao();
		LinkManager lm = new LinkManager(db);
		Bookmark bookmarkIn = lm.getFirstMatchedBookmark(url);
		String tags = bookmarkIn.getTags();
		BookmarkDao bookmarkDB = db.getBookmarkDao();
		
		List<Bookmark> potentialResults = bookmarkDB.searchCommunityBookmarks(tags, 0, 10).getResult();

		Vector<String> answer = new Vector<String>();
		int throwaways = offset;
		for(int t = 0; t < potentialResults.size(); t++)
		{
			String urlOut = potentialResults.get(t).getLink().getUrl();
			if(!urlOut.equalsIgnoreCase(url)) // If this is useful
			{
				if( answer.size() >= count) break; //Get out if we're done
				if( throwaways > 0) throwaways--; // Count down throwaways
				else answer.add(urlOut); // Or add result
			}
		}
		
		return answer;
	}
	
	public Vector<String> quickRecommend(String url, String username, int offset, int count) 
	{
		GnizrDao db = new GnizrDao();
		Link theLink = new Link(url);
		BookmarkDao bookmarkDB = db.getBookmarkDao();
		List<User> user = db.getUserDao().findUser(username);
		
		if(user == null || user.size() == 0) //Error checking
		{
			return quickRecommend(url, offset, count);
		}
		List<Bookmark> bookmarks = bookmarkDB.findBookmark(user.get(0), theLink);
			
		if(bookmarks == null || bookmarks.size() == 0) //Error checking
		{
			return quickRecommend(url, offset, count);
		}
		
		Bookmark bookmarkIn = bookmarks.get(0);
		String tags = bookmarkIn.getTags();
		
		List<Bookmark> potentialResults = bookmarkDB.searchCommunityBookmarks(tags, 0, 10).getResult();
		
		Vector<String> answer = new Vector<String>();
		int throwaways = offset;
		for(int t = 0; t < potentialResults.size(); t++)
		{
			String urlOut = potentialResults.get(t).getLink().getUrl();
			if(!urlOut.equalsIgnoreCase(url)) // If this is useful
			{
				if( answer.size() >= count) break; //Get out if we're done
				if( throwaways > 0) throwaways--; // Count down throwaways
				else answer.add(urlOut); // Or add result
			}
		}
		
		return answer;
	}

	// a slow recomend when we don't know the user.
	public Vector<String> slowRecommend(String url, int offset, int count) 
	{
		//count the number of tags used in all bookmarks for this url
		Hashtable<String,Double> tags = new Hashtable<String,Double>();
		
		GnizrDao db = new GnizrDao();
		LinkManager lm = new LinkManager(db);
		Link start = new Link(url);
		List<Bookmark> bookmarksForURL = lm.getHistory(start);
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
		
		BookmarkDao bookmarkDB = db.getBookmarkDao();
		for(int t=0; t < keys.size(); t++)
		{
			int numResults = bookmarkDB.getBookmarkCount(new Tag(keys.get(t)) );
			List<Bookmark> potentialResults= bookmarkDB.pageBookmarks(new Tag(keys.get(t)), 0, numResults).getResult();
			for(int r=0; r < potentialResults.size(); r++)
			{
				String currLink = potentialResults.get(r).getLink().getUrl();
				List<String> tagsForLink = potentialResults.get(r).getTagList();
				double score = 0;
				for(int l=0; l < tagsForLink.size(); l++)
				{
					score += tags.get(tagsForLink.get(l));
				}
				bookmarks.put(currLink, score);
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
		
		Vector<String> answer = new Vector<String>();
		int throwaways = offset;
		for(int t = 0; t < pairs.size(); t++)
		{
			String urlOut = pairs.get(t).str;
			if(!urlOut.equalsIgnoreCase(url)) // If this is useful
			{
				if( answer.size() >= count) break; //Get out if we're done
				if( throwaways > 0) throwaways--; // Count down throwaways
				else answer.add(urlOut); // Or add result
			}
		}

		return answer;
	}

	public Vector<String> slowRecommend(String url, String username, int offset, int count)
	{
		//find how the user bookmarked this url
		Hashtable<String,Double> tags = new Hashtable<String,Double>();
		
		GnizrDao db = new GnizrDao();
		
		LinkManager lm = new LinkManager(db);
		Link start = new Link(url);
		
		List<User> user = db.getUserDao().findUser(username);
		if(user == null || user.size() == 0) //Error checking
		{
			return slowRecommend(url, offset, count);
		}
		BookmarkDao bookmarkDB = db.getBookmarkDao();
		List<Bookmark> bookmarksForURL = bookmarkDB.findBookmark(user.get(0), start);
		if(bookmarksForURL == null || bookmarksForURL.size() == 0) //Error checking
		{
			bookmarksForURL = lm.getHistory(start);
		}
		
//		count the number of tags used in the bookmarks for this url
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
			int numResults = bookmarkDB.getBookmarkCount(new Tag(keys.get(t)) );
			List<Bookmark> potentialResults= bookmarkDB.pageBookmarks(new Tag(keys.get(t)), 0, numResults).getResult();
			for(int r=0; r < potentialResults.size(); r++)
			{
				String currLink = potentialResults.get(r).getLink().getUrl();
				List<String> tagsForLink = potentialResults.get(r).getTagList();
				double score = 0;
				for(int l=0; l < tagsForLink.size(); l++)
				{
					score += tags.get(tagsForLink.get(l));
				}
				bookmarks.put(currLink, score);
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
		
		Vector<String> answer = new Vector<String>();
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
				//if the user has not bookmarked this url
				if(! VectorUtils.containsIgnoreCase(userUrls, urlOut))
				{
					if( answer.size() >= count) break; //Get out if we're done
					if( throwaways > 0) throwaways--; // Count down throwaways
					else answer.add(urlOut); // Or add result
				}
			}
		}

		if((answer == null) || (answer.size() == 0))
			return slowRecommend(url, offset, count);
		
		return answer;
	}
	
}
