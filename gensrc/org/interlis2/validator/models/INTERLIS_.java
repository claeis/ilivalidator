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
    mapping.defineClass("INTERLIS.ArcSegment", new String[]{   "SegmentEndPoint"
      });
    mapping.defineClass("INTERLIS.TimeOfDay", new String[]{   "Hours"
      ,"Minutes"
      ,"Seconds"
      });
    mapping.defineClass("INTERLIS.StraightSegment", new String[]{   "SegmentEndPoint"
      });
    mapping.defineClass("INTERLIS.LineGeometry", new String[]{   "Segments"
      });
    mapping.defineClass("INTERLIS.StartSegment", new String[]{   "SegmentEndPoint"
      });
    mapping.defineClass("INTERLIS.SCALSYSTEM", new String[]{   "Name"
      });
    mapping.defineClass("INTERLIS.UTC", new String[]{   "Hours"
      ,"Minutes"
      ,"Seconds"
      });
    mapping.defineClass("INTERLIS.LineSegment", new String[]{   "SegmentEndPoint"
      });
    mapping.defineClass("INTERLIS.SurfaceEdge", new String[]{   "Geometry"
      ,"LineAttrs"
      });
    mapping.defineClass("INTERLIS.METAOBJECT", new String[]{   "Name"
      });
    mapping.defineClass("INTERLIS.ANYCLASS", new String[]{  });
    mapping.defineClass("INTERLIS.REFSYSTEM", new String[]{   "Name"
      });
    mapping.defineClass("INTERLIS.GregorianDateTime", new String[]{   "Year"
      ,"Month"
      ,"Day"
      ,"Hours"
      ,"Minutes"
      ,"Seconds"
      });
    mapping.defineClass("INTERLIS.GregorianDate", new String[]{   "Year"
      ,"Month"
      ,"Day"
      });
    mapping.defineClass("INTERLIS.METAOBJECT_TRANSLATION", new String[]{   "Name"
      ,"NameInBaseLanguage"
      });
    mapping.defineClass("INTERLIS.COORDSYSTEM", new String[]{   "Name"
      ,"Axis"
      });
    mapping.defineClass("INTERLIS.TIMESYSTEMS.CALENDAR", new String[]{   "Name"
      });
    mapping.defineClass("INTERLIS.TIMESYSTEMS.TIMEOFDAYSYS", new String[]{   "Name"
      });
    mapping.defineClass("INTERLIS.AXIS", new String[]{  });
    mapping.defineClass("INTERLIS.SIGN", new String[]{   "Name"
      });
    mapping.defineClass("INTERLIS.SurfaceBoundary", new String[]{   "Lines"
      });
    mapping.defineClass("INTERLIS.ANYSTRUCTURE", new String[]{  });
    return mapping;
  }
}
