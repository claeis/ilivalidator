package org.interlis2.validator.models.IliVErrors.ErrorLog;
public class Error extends ch.interlis.iom_j.Iom_jObject
{
  public final static String tag= "IliVErrors.ErrorLog.Error";
  public Error(String oid) {
    super(tag,oid);
  }
  public String getobjecttag() {
    return tag;
  }
  public final static String tag_Message="Message";
  public String getMessage() {
    String value=getattrvalue("Message");
    return value;
  }
  public void setMessage(String value) {
    setattrvalue("Message", value);
  }
  public final static String tag_Type="Type";
  public Error_Type getType() {
    String value=getattrvalue("Type");
    return Error_Type.parseXmlCode(value);
  }
  public void setType(Error_Type value) {
    setattrvalue("Type", Error_Type.toXmlCode(value));
  }
  public final static String tag_ObjTag="ObjTag";
  public String getObjTag() {
    String value=getattrvalue("ObjTag");
    return value;
  }
  public void setObjTag(String value) {
    setattrvalue("ObjTag", value);
  }
  public final static String tag_Tid="Tid";
  public String getTid() {
    String value=getattrvalue("Tid");
    return value;
  }
  public void setTid(String value) {
    setattrvalue("Tid", value);
  }
  public final static String tag_TechId="TechId";
  public String getTechId() {
    String value=getattrvalue("TechId");
    return value;
  }
  public void setTechId(String value) {
    setattrvalue("TechId", value);
  }
  public final static String tag_UserId="UserId";
  public String getUserId() {
    String value=getattrvalue("UserId");
    return value;
  }
  public void setUserId(String value) {
    setattrvalue("UserId", value);
  }
  public final static String tag_IliQName="IliQName";
  public String getIliQName() {
    String value=getattrvalue("IliQName");
    return value;
  }
  public void setIliQName(String value) {
    setattrvalue("IliQName", value);
  }
  public final static String tag_DataSource="DataSource";
  public String getDataSource() {
    String value=getattrvalue("DataSource");
    return value;
  }
  public void setDataSource(String value) {
    setattrvalue("DataSource", value);
  }
  public final static String tag_Line="Line";
  public int getLine() {
    String value=getattrvalue("Line");
    return Integer.parseInt(value);
  }
  public void setLine(int value) {
    setattrvalue("Line", Integer.toString(value));
  }
  public final static String tag_Geometry="Geometry";
  public String getGeometry() {
    String value=getattrvalue("Geometry");
    return value;
  }
  public void setGeometry(String value) {
    setattrvalue("Geometry", value);
  }
  public final static String tag_TechDetails="TechDetails";
  public String getTechDetails() {
    String value=getattrvalue("TechDetails");
    return value;
  }
  public void setTechDetails(String value) {
    setattrvalue("TechDetails", value);
  }
}
