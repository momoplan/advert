// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.advert.controller.resp;

import com.ruyicai.advert.controller.resp.QqResponseData;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect QqResponseData_Roo_Json {
    
    public String QqResponseData.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static QqResponseData QqResponseData.fromJsonToQqResponseData(String json) {
        return new JSONDeserializer<QqResponseData>().use(null, QqResponseData.class).deserialize(json);
    }
    
    public static String QqResponseData.toJsonArray(Collection<QqResponseData> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<QqResponseData> QqResponseData.fromJsonArrayToQqResponseDatas(String json) {
        return new JSONDeserializer<List<QqResponseData>>().use(null, ArrayList.class).use("values", QqResponseData.class).deserialize(json);
    }
    
}
