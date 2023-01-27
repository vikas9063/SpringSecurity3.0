package com.vk.proj.config;

import com.vk.proj.modal.Users;
import com.vk.proj.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
