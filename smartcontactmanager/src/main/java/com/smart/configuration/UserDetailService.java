package com.smart.configuration;

import com.smart.entity.User;
import com.smart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // fetching user from database
        User user = userRepository.getUserByUserName(username);

        if (user == null){
            throw new UsernameNotFoundException("Could not found user !!");
        }

        CustomUserDetail customUserDetail = new CustomUserDetail(user);

        return customUserDetail;
    }
}
