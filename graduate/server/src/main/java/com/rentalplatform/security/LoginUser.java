package com.rentalplatform.security;

import com.rentalplatform.common.enums.RoleType;
import com.rentalplatform.model.entity.SysUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** UserDetails 实现：只携带鉴权所需的最小字段，敏感字段不出 security 层。 */
@Getter
public class LoginUser implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String role;
    private final Integer tokenVersion;
    private final boolean enabled;

    public LoginUser(SysUser user) {
        this.userId = user.getId();
        this.username = user.getUsername() != null ? user.getUsername() : "wx_" + user.getId();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.tokenVersion = user.getTokenVersion();
        this.enabled = "ACTIVE".equals(user.getStatus());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public boolean hasRole(RoleType type) {
        return role != null && role.equals(type.name());
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
