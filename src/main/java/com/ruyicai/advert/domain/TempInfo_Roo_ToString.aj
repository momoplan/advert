// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.advert.domain;

import java.lang.String;

privileged aspect TempInfo_Roo_ToString {
    
    public String TempInfo.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Mac: ").append(getMac()).append(", ");
        sb.append("Type: ").append(getType()).append(", ");
        sb.append("Userno: ").append(getUserno());
        return sb.toString();
    }
    
}
