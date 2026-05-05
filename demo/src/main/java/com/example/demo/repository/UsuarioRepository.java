package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.UsuariosRolDto;
import com.example.demo.model.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Usuario findByCorreo(String correo);

    @Override
    Optional<Usuario> findById(String id);

    boolean existsByCorreo(String correo);

    long countByRolId(String rolId);

    @Aggregation(pipeline = {
    "{ $group: { _id: '$rol._id', cantidad: { $sum: 1 } } }",  // Cambiar $rol.nombre por $rol._id
    "{ $project: { _id: 0, rol: '$_id', cantidad: 1 } }",
    "{ $sort: { rol: 1 } }"
})
List<UsuariosRolDto> obtenerUsuariosPorRol();
}
