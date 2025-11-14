package org.interlis2.validator.models;
public class ILIVREFDATA_V1_0{
  private ILIVREFDATA_V1_0() {}
  public final static String MODEL= "IliVRefData_V1_0";
  public final static String Mapping= "IliVRefData_V1_0.Mapping";
  public static ch.interlis.iom_j.xtf.XtfModel getXtfModel(){ return new ch.interlis.iom_j.xtf.XtfModel("IliVRefData_V1_0","mailto:ceis@localhost","2025-10-01"); }
  static public ch.interlis.iox.IoxFactory getIoxFactory()
  {
    return new ch.interlis.iox.IoxFactory(){
      public ch.interlis.iom.IomObject createIomObject(String type,String oid) throws ch.interlis.iox.IoxException {
      if(type.equals("IliVRefData_V1_0.Mapping.RefData"))return new org.interlis2.validator.models.IliVRefData_V1_0.Mapping.RefData(oid);
      return null;
      }
    };
  }
  static public ch.interlis.iom_j.ViewableProperties getIoxMapping()
  {
    ch.interlis.iom_j.ViewableProperties mapping=new ch.interlis.iom_j.ViewableProperties();
    java.util.HashMap<String,String> nameMap=new java.util.HashMap<String,String>();
    nameMap.put("IliVRefData_V1_0.Mapping","Mapping");
    nameMap.put("IliVRefData_V1_0.Mapping.RefData", "RefData");
    mapping.defineClass("IliVRefData_V1_0.Mapping.RefData", new String[]{   "ignore"
      ,"topic"
      ,"scope"
      ,"refdata"
      ,"comment"
      });
    mapping.setXtf24nameMapping(nameMap);
    return mapping;
  }
}
