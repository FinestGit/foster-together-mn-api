package org.fostertogethermn.api.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class CognitoGroupConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        if (groups == null) {
            return List.of();
        }
        List<GrantedAuthority> result = new ArrayList<>();
        for (String group : groups) {
            if ("ftmn-directory-admin".equals(group)) {
                result.add(new SimpleGrantedAuthority("agency:write"));
                result.add(new SimpleGrantedAuthority("agency:delete"));
            }
        }
        return result;
    }

}
