package com.example.gnizr;

import com.gnizr.web.action.AbstractAction;
import com.gnizr.db.dao.*;
import com.example.gnizr.Backend;
import java.util.Vector;


public class GnimarkAction extends AbstractAction{
  
  private static final long serialVersionUID = 4604179423988890376L;

  private String message;
  private String url;
  private GnizrDao db;
  
  
  public GnimarkAction(GnizrDao db)
  {
	  this.db = db;
  }
  public String getMessage() {
    return message;
  }
  
  public String getUrl(){
	  return url;
  }
  
  public void setUrl(String url){
	  this.url = url;
  }

  public void setMessage(String message) {
	if(this.message == null)
    {
	  this.message = message;
    }
  }

  @Override
  protected String go() throws Exception {
    
	Backend b = new Backend(db);

	//strip off the " ' " at the beggining and end of hte message first.
	this.message = this.message.substring(1, this.message.length() - 1);
	
	//now i have to get the call number
	String calltype = this.message.substring(0,1);
	
	//the number of responses.
	String responses = this.message.substring(1,2);
	
	//get the full message.
	this.message = this.message.substring(2, this.message.length());
	
	Vector<String> urls = new Vector<String>();
	
	if(calltype.equals("c"))
	{
		urls = b.quickRecommend(this.message , 0, Integer.parseInt(responses));
	}
	else if(calltype.equals("x"))
	{
		urls = b.slowRecommend(this.message, 0, Integer.parseInt(responses));
	}

	String str = "";
	for(int i=0; i < urls.size(); i++)
	{
		str = str + urls.get(i) + ",";
	}

	this.message = str;

    return SUCCESS;
  }
}