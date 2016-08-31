package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * Created by yejun on 8/30/16.
 */
public class Loclist implements Serializable {
    private String locid;
    private String locdesc;
    private String arrivetime;

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getArrivetime() {
        return arrivetime;
    }

    public void setArrivetime(String arrivetime) {
        this.arrivetime = arrivetime;
    }

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }
}
