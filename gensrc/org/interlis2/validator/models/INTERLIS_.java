package org.interlis2.validator.models;
public class INTERLIS_{
  private INTERLIS_() {}
  public final static String MODEL= "INTERLIS";
  public final static String TIMESYSTEMS= "INTERLIS.TIMESYSTEMS";
  public static ch.interlis.iom_j.xtf.XtfModel getXtfModel(){ return new ch.interlis.iom_j.xtf.XtfModel("INTERLIS","http://www.interlis.ch","20060126"); }
  static public ch.interlis.iox.IoxFactory getIoxFactory()
  {
    return new ch.interlis.iox.IoxFactory(){
      public ch.interlis.iom.IomObject createIomObject(String type,String oid) throws ch.interlis.iox.IoxException {
      return null;
      }
    };
  }
  static public ch.interlis.iom_j.ViewableProperties getIoxMapping()
  {
    ch.interlis.iom_j.ViewableProperties mapping=new ch.interlis.iom_j.ViewableProperties();
    java.util.HashMap<String,String> nameMap=new java.util.HashMap<String,String>();
    nameMap.put("INTERLIS.TIMESYSTEMS","TIMESYSTEMS");
    nameMap.put("INTERLIS.ArcSegment", "ArcSegment");
    mapping.defineClass("INTERLIS.ArcSegment", new String[]{   "SegmentEndPoint"
      });
    nameMap.put("INTERLIS.TimeOfDay", "TimeOfDay");
    mapping.defineClass("INTERLIS.TimeOfDay", new String[]{   "Hours"
      ,"Minutes"
      ,"Seconds"
      });
    nameMap.put("INTERLIS.StraightSegment", "StraightSegment");
    mapping.defineClass("INTERLIS.StraightSegment", new String[]{   "SegmentEndPoint"
      });
    nameMap.put("INTERLIS.LineGeometry", "LineGeometry");
    mapping.defineClass("INTERLIS.LineGeometry", new String[]{   "Segments"
      });
    nameMap.put("INTERLIS.StartSegment", "StartSegment");
    mapping.defineClass("INTERLIS.StartSegment", new String[]{   "SegmentEndPoint"
      });
    nameMap.put("INTERLIS.SCALSYSTEM", "SCALSYSTEM");
    mapping.defineClass("INTERLIS.SCALSYSTEM", new String[]{   "Name"
      });
    nameMap.put("INTERLIS.UTC", "UTC");
    mapping.defineClass("INTERLIS.UTC", new String[]{   "Hours"
      ,"Minutes"
      ,"Seconds"
      });
    nameMap.put("INTERLIS.LineSegment", "LineSegment");
    mapping.defineClass("INTERLIS.LineSegment", new String[]{   "SegmentEndPoint"
      });
    nameMap.put("INTERLIS.SurfaceEdge", "SurfaceEdge");
    mapping.defineClass("INTERLIS.SurfaceEdge", new String[]{   "Geometry"
      ,"LineAttrs"
      });
    nameMap.put("INTERLIS.METAOBJECT", "METAOBJECT");
    mapping.defineClass("INTERLIS.METAOBJECT", new String[]{   "Name"
      });
    nameMap.put("INTERLIS.ANYCLASS", "ANYCLASS");
    mapping.defineClass("INTERLIS.ANYCLASS", new String[]{  });
    nameMap.put("INTERLIS.REFSYSTEM", "REFSYSTEM");
    mapping.defineClass("INTERLIS.REFSYSTEM", new String[]{   "Name"
      });
    nameMap.put("INTERLIS.GregorianDateTime", "GregorianDateTime");
    mapping.defineClass("INTERLIS.GregorianDateTime", new String[]{   "Year"
      ,"Month"
      ,"Day"
      ,"Hours"
      ,"Minutes"
      ,"Seconds"
      });
    nameMap.put("INTERLIS.GregorianDate", "GregorianDate");
    mapping.defineClass("INTERLIS.GregorianDate", new String[]{   "Year"
      ,"Month"
      ,"Day"
      });
    nameMap.put("INTERLIS.METAOBJECT_TRANSLATION", "METAOBJECT_TRANSLATION");
    mapping.defineClass("INTERLIS.METAOBJECT_TRANSLATION", new String[]{   "Name"
      ,"NameInBaseLanguage"
      });
    nameMap.put("INTERLIS.COORDSYSTEM", "COORDSYSTEM");
    mapping.defineClass("INTERLIS.COORDSYSTEM", new String[]{   "Name"
      ,"Axis"
      });
    nameMap.put("INTERLIS.TIMESYSTEMS.CALENDAR", "CALENDAR");
    mapping.defineClass("INTERLIS.TIMESYSTEMS.CALENDAR", new String[]{   "Name"
      });
    nameMap.put("INTERLIS.TIMESYSTEMS.TIMEOFDAYSYS", "TIMEOFDAYSYS");
    mapping.defineClass("INTERLIS.TIMESYSTEMS.TIMEOFDAYSYS", new String[]{   "Name"
      });
    nameMap.put("INTERLIS.AXIS", "AXIS");
    mapping.defineClass("INTERLIS.AXIS", new String[]{  });
    nameMap.put("INTERLIS.SIGN", "SIGN");
    mapping.defineClass("INTERLIS.SIGN", new String[]{   "Name"
      });
    nameMap.put("INTERLIS.SurfaceBoundary", "SurfaceBoundary");
    mapping.defineClass("INTERLIS.SurfaceBoundary", new String[]{   "Lines"
      });
    nameMap.put("INTERLIS.ANYSTRUCTURE", "ANYSTRUCTURE");
    mapping.defineClass("INTERLIS.ANYSTRUCTURE", new String[]{  });
    mapping.setXtf24nameMapping(nameMap);
    return mapping;
  }
}
