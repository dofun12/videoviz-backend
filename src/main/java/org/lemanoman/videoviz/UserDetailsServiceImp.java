package org.lemanoman.videoviz;

import org.lemanoman.videoviz.model.UsuarioModel;
import org.lemanoman.videoviz.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class UserDetailsServiceImp implements UserDetailsService {

    private UsuarioRepository usuarioRepository;

    public UserDetailsServiceImp(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    /*Here we are using dummy data, you need to load user data from
     database or other third party application*/
        UsuarioModel user = findUserbyName(username);

        UserBuilder builder = null;
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
