// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.advert.controller.resp;

import com.ruyicai.advert.controller.resp.MopanResponseData;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect MopanResponseData_Roo_Json {
    
    public String MopanResponseData.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static MopanResponseData MopanResponseData.fromJsonToMopanResponseData(String json) {
        return new JSONDeserializer<MopanResponseData>().use(null, MopanResponseData.class).deserialize(json);
    }
    
    public static String MopanResponseData.toJsonArray(Collection<MopanResponseData> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<MopanResponseData> MopanResponseData.fromJsonArrayToMopanResponseDatas(String json) {
        return new JSONDeserializer<List<MopanResponseData>>().use(null, ArrayList.class).use("values", MopanResponseData.class).deserialize(json);
    }
    
}
