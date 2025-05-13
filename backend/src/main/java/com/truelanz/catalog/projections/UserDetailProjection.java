package com.truelanz.catalog.projections;

public interface UserDetailProjection {

    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
    
}
