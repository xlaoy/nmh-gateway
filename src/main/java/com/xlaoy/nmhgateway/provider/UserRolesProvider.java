package com.xlaoy.nmhgateway.provider;

import com.xlaoy.nmhgateway.support.LoginUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by Administrator on 2018/12/19 0019.
 */
public interface UserRolesProvider {

    Collection<GrantedAuthority> getUserGrantedAuthority(LoginUser loginUser);
}
