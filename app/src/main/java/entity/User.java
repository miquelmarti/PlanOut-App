/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author mmartira
 */
public class User implements Serializable {
    private String name;
    private String mail;
    private String url;
    private static final long serialVersionUID = 1L;
    private String googleid;
    private Collection<Subscription> subscriptionCollection;
    private Collection<Plan> planCollection;

    public User() {
    }

    public User(String googleid) {
        this.googleid = googleid;
    }

    public String getGoogleid() {
        return googleid;
    }

    public void setGoogleid(String googleid) {
        this.googleid = googleid;
    }

    public Collection<Subscription> getSubscriptionCollection() {
        return subscriptionCollection;
    }

    public void setSubscriptionCollection(Collection<Subscription> subscriptionCollection) {
        this.subscriptionCollection = subscriptionCollection;
    }

    public Collection<Plan> getPlanCollection() {
        return planCollection;
    }

    public void setPlanCollection(Collection<Plan> planCollection) {
        this.planCollection = planCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (googleid != null ? googleid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.googleid == null && other.googleid != null) || (this.googleid != null && !this.googleid.equals(other.googleid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.User[ googleid=" + googleid + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
