package org.interlis2.validator.models.IliVErrors.ErrorLog;
public class Error_Type{
  static private java.util.HashMap valuev=new java.util.HashMap();
  private String value=null;
  private Error_Type(String value) {
    this.value=value;
    valuev.put(value,this);
  }
  static public String toXmlCode(Error_Type value) {
     return value.value;
  }
  static public Error_Type parseXmlCode(String value) {
     return (Error_Type)valuev.get(value);
  }
  static public Error_Type Error=new Error_Type("Error");
  public final static String tag_Error="Error";
  static public Error_Type Warning=new Error_Type("Warning");
  public final static String tag_Warning="Warning";
  static public Error_Type Info=new Error_Type("Info");
  public final static String tag_Info="Info";
  static public Error_Type DetailInfo=new Error_Type("DetailInfo");
  public final static String tag_DetailInfo="DetailInfo";
}
