package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer>, JpaSpecificationExecutor<UsuarioModel> {

    public UsuarioModel findByLogin(String login);
}