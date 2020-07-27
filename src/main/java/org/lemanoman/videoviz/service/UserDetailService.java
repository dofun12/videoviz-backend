package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.model.UsuarioModel;
import org.lemanoman.videoviz.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService  implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    /*Here we are using dummy data, you need to load user data from
     database or other third party application*/
        UsuarioModel user = findUserbyName(username);

        User.UserBuilder builder = null;
        if (user != null) {
            //String fromDB = new BCryptPasswordEncoder().encode("senhamestre");
            builder = User.withUsername(username);
            builder.password(user.getPassword());
            builder.roles(user.getRole());
        } else {
            throw new UsernameNotFoundException("UserModel not found.");
        }

        return builder.build();
    }

    private UsuarioModel findUserbyName(String username) {
        return usuarioRepository.findByLogin(username);
    }
}
