package org.interlis2.validator.models;
public class ILIVREFDATA{
  private ILIVREFDATA() {}
  public final static String MODEL= "IliVRefData";
  public final static String Mapping= "IliVRefData.Mapping";
  public static ch.interlis.iom_j.xtf.XtfModel getXtfModel(){ return new ch.interlis.iom_j.xtf.XtfModel("IliVRefData","mailto:ceis@localhost","2025-09-16"); }
  static public ch.interlis.iox.IoxFactory getIoxFactory()
  {
    return new ch.interlis.iox.IoxFactory(){
      public ch.interlis.iom.IomObject createIomObject(String type,String oid) throws ch.interlis.iox.IoxException {
      if(type.equals("IliVRefData.Mapping.RefData"))return new org.interlis2.validator.models.IliVRefData.Mapping.RefData(oid);
      return null;
      }
    };
  }
  static public ch.interlis.iom_j.ViewableProperties getIoxMapping()
  {
    ch.interlis.iom_j.ViewableProperties mapping=new ch.interlis.iom_j.ViewableProperties();
    java.util.HashMap<String,String> nameMap=new java.util.HashMap<String,String>();
    nameMap.put("IliVRefData.Mapping","Mapping");
    nameMap.put("IliVRefData.Mapping.RefData", "RefData");
    mapping.defineClass("IliVRefData.Mapping.RefData", new String[]{   "ignore"
      ,"topic"
      ,"scope"
      ,"refdata"
      ,"comment"
      });
    mapping.setXtf24nameMapping(nameMap);
    return mapping;
  }
}
