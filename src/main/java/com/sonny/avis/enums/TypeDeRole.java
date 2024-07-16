package com.sonny.avis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum TypeDeRole {
    UTILISATEUR(
            Set.of(TypePermission.UTILISATEUR_CREATE_AVIS)
    ),

    MANAGER(
            Set.of(
                    TypePermission.MANAGER_CREATE,
                    TypePermission.MANAGER_READ,
                    TypePermission.MANAGER_UPDATE,
                    TypePermission.MANAGER_DELETE_AVIS
            )
    ),

    ADMIN(
            Set.of(
                    TypePermission.ADMIN_CREATE,
                    TypePermission.ADMIN_READ,
                    TypePermission.ADMIN_UPDATE,
                    TypePermission.ADMIN_DELETE,

                    TypePermission.MANAGER_CREATE,
                    TypePermission.MANAGER_READ,
                    TypePermission.MANAGER_UPDATE,
                    TypePermission.MANAGER_DELETE_AVIS
            )
    );


   Set<TypePermission> permissions;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<SimpleGrantedAuthority> grantedAuthorities = this.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name())).collect(Collectors.toList());

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return grantedAuthorities;
    }
}
