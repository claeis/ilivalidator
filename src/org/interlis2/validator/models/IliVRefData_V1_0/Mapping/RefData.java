package org.interlis2.validator.models.IliVRefData_V1_0.Mapping;
public class RefData extends ch.interlis.iom_j.Iom_jObject
{
  public final static String tag= "IliVRefData_V1_0.Mapping.RefData";
  public RefData(String oid) {
    super(tag,oid);
  }
  public String getobjecttag() {
    return tag;
  }
  public final static String tag_ignore="ignore";
  public Boolean getignore() {
    if(getattrvaluecount("ignore")==0)return null;
    String value=getattrvalue("ignore");
    return value!=null && value.equals("true");
  }
  public void setignore(Boolean value) {
    if(value==null){setattrundefined("ignore");return;}
    setattrvalue("ignore", value?"true":"false");
  }
  public final static String tag_topic="topic";
  public String gettopic() {
    if(getattrvaluecount("topic")==0)return null;
    String value=getattrvalue("topic");
    return value;
  }
  public void settopic(String value) {
    if(value==null){setattrundefined("topic");return;}
    setattrvalue("topic", value);
  }
  public final static String tag_scope="scope";
  public String getscope() {
    if(getattrvaluecount("scope")==0)return null;
    String value=getattrvalue("scope");
    return value;
  }
  public void setscope(String value) {
    if(value==null){setattrundefined("scope");return;}
    setattrvalue("scope", value);
  }
  public final static String tag_refdata="refdata";
  public String[] getrefdata() {
      int valuec=getattrvaluecount("refdata");
      String ret[]=new String[valuec];
      for(int idx=0;idx<valuec;idx++) {
          ret[idx]=getattrprim("refdata",idx);
      }
    return ret;
  }
  public void setrefdata(String values[]) {
      for(String value:values) {
          addattrvalue("refdata", value);
      }
  }
  public final static String tag_comment="comment";
  public String getcomment() {
    if(getattrvaluecount("comment")==0)return null;
    String value=getattrvalue("comment");
    return value;
  }
  public void setcomment(String value) {
    if(value==null){setattrundefined("comment");return;}
    setattrvalue("comment", value);
  }
}
