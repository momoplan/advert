// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.advert.domain;

import com.ruyicai.advert.domain.TregisterInfo;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect TregisterInfo_Roo_Json {
    
    public String TregisterInfo.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static TregisterInfo TregisterInfo.fromJsonToTregisterInfo(String json) {
        return new JSONDeserializer<TregisterInfo>().use(null, TregisterInfo.class).deserialize(json);
    }
    
    public static String TregisterInfo.toJsonArray(Collection<TregisterInfo> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<TregisterInfo> TregisterInfo.fromJsonArrayToTregisterInfoes(String json) {
        return new JSONDeserializer<List<TregisterInfo>>().use(null, ArrayList.class).use("values", TregisterInfo.class).deserialize(json);
    }
    
}