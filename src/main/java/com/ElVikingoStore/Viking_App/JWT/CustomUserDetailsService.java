package com.ElVikingoStore.Viking_App.JWT;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.ElVikingoStore.Viking_App.Models.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Repositories.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo repository;

    public CustomUserDetailsService(UserRepo repository) {
        this.repository = repository;
    }
    @Operation(summary = "Carga de usuario por email",
            description = "Carga un usuario por su email y devuelve un objeto UserDetails")
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user = repository.findByEmail(email)
               .orElseThrow(() ->
                       new UsernameNotFoundException("User not found with email:" + email));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }
    @Operation(summary = "Mapeo de roles a autoridades",
            description = "Mapea los roles de un usuario a sus respectivas autoridades")
    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getPermission())).collect(Collectors.toList());
    }
}