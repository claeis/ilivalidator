package org.interlis2.validator.models.INTERLIS;
public class HALIGNMENT{
  static private java.util.HashMap valuev=new java.util.HashMap();
  private String value=null;
  private HALIGNMENT(String value) {
    this.value=value;
    valuev.put(value,this);
  }
  static public String toXmlCode(HALIGNMENT value) {
     return value.value;
  }
  static public HALIGNMENT parseXmlCode(String value) {
     return (HALIGNMENT)valuev.get(value);
  }
  static public HALIGNMENT Left=new HALIGNMENT("Left");
  public final static String tag_Left="Left";
  static public HALIGNMENT Center=new HALIGNMENT("Center");
  public final static String tag_Center="Center";
  static public HALIGNMENT Right=new HALIGNMENT("Right");
  public final static String tag_Right="Right";
}
