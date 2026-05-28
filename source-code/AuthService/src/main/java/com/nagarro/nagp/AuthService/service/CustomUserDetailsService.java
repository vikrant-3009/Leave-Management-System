package com.nagarro.nagp.AuthService.service;

import com.nagarro.nagp.AuthService.entity.CustomUserDetails;
import com.nagarro.nagp.AuthService.entity.User;
import com.nagarro.nagp.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);

//        List<SimpleGrantedAuthority> authorities =
//                user.getRoles()
//                        .stream()
//                        .map(role ->
//                                new SimpleGrantedAuthority("ROLE_" + role.getName()))
//                        .toList();

//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                user.isActive(),
//                true,
//                true,
//                true,
//                authorities
//        );
    }
}
