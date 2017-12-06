package com.spaceships.security;

import com.spaceships.domains.User;
import com.spaceships.services.reposervices.user.UserRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
@Service("userDetailsService")
public class SpringSecUserDetailsServiceImpl implements UserDetailsService {

    private UserRepoService userRepoService;

    @Autowired
    public void setUserRepoService(UserRepoService userRepoService) {
        this.userRepoService = userRepoService;
    }

    private Converter<User, UserDetails> userUserDetailsConverter;

    @Autowired
    public void setUserUserDetailsConverter(Converter<User, UserDetails> userUserDetailsConverter) {
        this.userUserDetailsConverter = userUserDetailsConverter;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userUserDetailsConverter.convert(userRepoService.findUser(s));
    }
}
