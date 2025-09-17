package org.interlis2.validator.refmapping;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.interlis2.validator.models.IliVRefData.Mapping.RefData;

import ch.ehi.basics.logging.EhiLogger;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.parser.Ili24Parser;
import ch.interlis.iox.IoxException;

public class RefMapping {
    static class MappingKey {
       public MappingKey(String scope, String topic) {
            super();
            this.scope = scope;
            this.topic = topic;
        }
    String scope;
       String topic;
    public String getScope() {
        return scope;
    }
    public String getTopic() {
        return topic;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MappingKey other = (MappingKey) obj;
        if (scope == null) {
            if (other.scope != null)
                return false;
        } else if (!scope.equals(other.scope))
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        return true;
    }
    }

    private TransferDescription td = null;
    private List<RefData> readFile(File file) 
    throws IoxException
    {
        if(td==null) {
            td=new TransferDescription();
            java.io.Reader stream=null;
            try {
                stream = new java.io.InputStreamReader(getClass().getResourceAsStream("/IliVRefData.ili"));
            } catch (Exception ex) {
                throw new IoxException(ex);
            }
            if (!Ili24Parser.parseIliFile(td, "IliVRefData.ili", stream,
                    false, 0,null)) {
                throw new IoxException("failed to read ili file");
            }
        }
        List<RefData> datav=new ArrayList<RefData>();
        ch.interlis.iom_j.xtf.Xtf24Reader reader=null;
        try {
            reader=new ch.interlis.iom_j.xtf.Xtf24Reader(file);
            reader.setModel(td);
            reader.getFactory().registerFactory(org.interlis2.validator.models.ILIVREFDATA.getIoxFactory());
            ch.interlis.iox.IoxEvent event=null;
            do{
                 event=reader.read();
                 if(event instanceof ch.interlis.iox.ObjectEvent){
                     ch.interlis.iom.IomObject iomObj=((ch.interlis.iox.ObjectEvent)event).getIomObject();
                     if(iomObj instanceof RefData){
                         RefData model=(RefData)iomObj;
                         datav.add(model);
                     }else{
                         EhiLogger.logAdaption("TID="+iomObj.getobjectoid()+": ignored; unknown class <"+iomObj.getobjecttag()+">");
                     }
                 }
            }while(!(event instanceof ch.interlis.iox.EndTransferEvent));
        } catch (IoxException e) {
            throw new IoxException("failed to read "+file.getName(),e);
        }finally{
            if(reader!=null){
                reader.close();
                reader=null;
            }
        }
        return datav;
    }
    private java.util.Map<MappingKey,List<String>> pool=new java.util.HashMap<MappingKey,List<String>>();
    public void addFile(File file) throws IoxException {
        if(file==null) {
            return;
        }
        List<RefData> datav=readFile(file);
        for(RefData data:datav) {
            if(data.getignore()==null || !data.getignore()) {
                MappingKey key=new MappingKey(data.getscope(),data.gettopic());
                List<String> values=null;
                if(pool.containsKey(key)) {
                    // merge
                    values=pool.get(key);
                }else {
                    // add
                    values=new ArrayList<String>();
                    pool.put(key,values);
                }
                mergeValues(values,java.util.Arrays.asList(data.getrefdata()));
            }
        }
    }


    private void mergeValues(List<String> values, List<String> newValues) {
        if(newValues!=null) {
            for(String value:newValues) {
                if(!values.contains(value)) {
                    values.add(value);
                }
            }
        }
    }


    public String[] getRefData(String scope, String[] topics) {
        List<String> ret=new ArrayList<String>();
        for(String topic:topics) {
            mergeRefData(ret,scope,topic);
        }
        Collections.sort(ret);
        return ret.toArray(new String[ret.size()]);
    }


    private void mergeRefData(List<String> ret, String scope, String topic) {
        MappingKey key=null;
        List<String> values=null;
        key=new MappingKey(scope,topic);
        values=pool.get(key);
        mergeValues(ret,values);
        key=new MappingKey(scope,null);
        values=pool.get(key);
        mergeValues(ret,values);
        key=new MappingKey(null,topic);
        values=pool.get(key);
        mergeValues(ret,values);
    }

}
