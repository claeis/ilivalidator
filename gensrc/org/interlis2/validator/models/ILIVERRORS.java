package org.interlis2.validator.models;
public class ILIVERRORS{
  private ILIVERRORS() {}
  public final static String MODEL= "IliVErrors";
  public final static String ErrorLog= "IliVErrors.ErrorLog";
  public static ch.interlis.iom_j.xtf.XtfModel getXtfModel(){ return new ch.interlis.iom_j.xtf.XtfModel("IliVErrors","mailto:ceis@localhost","2016-06-10"); }
  static public ch.interlis.iox.IoxFactory getIoxFactory()
  {
    return new ch.interlis.iox.IoxFactory(){
      public ch.interlis.iom.IomObject createIomObject(String type,String oid) throws ch.interlis.iox.IoxException {
      if(type.equals("IliVErrors.ErrorLog.Error"))return new org.interlis2.validator.models.IliVErrors.ErrorLog.Error(oid);
      return null;
      }
    };
  }
  static public ch.interlis.iom_j.ViewableProperties getIoxMapping()
  {
    ch.interlis.iom_j.ViewableProperties mapping=new ch.interlis.iom_j.ViewableProperties();
    mapping.defineClass("IliVErrors.ErrorLog.Error", new String[]{   "Message"
      ,"Type"
      ,"ObjTag"
      ,"Tid"
      ,"TechtId"
      ,"UserId"
      ,"IliQName"
      ,"DataSource"
      ,"Line"
      ,"Geometry"
      ,"TechDetails"
      });
    return mapping;
  }
}
