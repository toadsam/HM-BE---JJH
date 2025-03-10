package com.example.HM.Global.Security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends User implements OAuth2User {

    private final String email;
    private final String name;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(OAuth2User oAuth2User, String email, String name) {
        super(email, "", oAuth2User.getAuthorities());
        this.email = email;
        this.name = name;
        this.attributes = oAuth2User.getAttributes();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return name;
    }
}
