// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.advert.domain;

import java.lang.String;

privileged aspect QqTaskProgress_Roo_ToString {
    
    public String QqTaskProgress.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Createtime: ").append(getCreatetime()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Step: ").append(getStep()).append(", ");
        sb.append("Userno: ").append(getUserno());
        return sb.toString();
    }
    
}