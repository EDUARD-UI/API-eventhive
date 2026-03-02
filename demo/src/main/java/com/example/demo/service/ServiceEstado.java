package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Estado;
import com.example.demo.repository.EstadoRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEstado {
    private final EstadoRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    public Estado findById(Long id){
        return estadoRepository.findById(id).orElse(null);
    }

    public Estado crearEstado(Estado estado){
        return estadoRepository.save(estado);
    }

    public List<Estado> obtenerEstados(){
        return estadoRepository.findAll();
    }

    public Estado findByNombre(String nombre) {
        return estadoRepository.findByNombre(nombre);
    }

    public void actualizarEstado(Estado estadoExistente) {
        estadoRepository.save(estadoExistente);
    }

    public boolean tieneEntidadesAsociadas(Long estadoId){
        long usuariosConEstado = usuarioRepository.countByEstadoId(estadoId);
        long eventosConEstado = eventoRepository.countByEstadoId(estadoId);
        return usuariosConEstado > 0 || eventosConEstado > 0;
    }

    public void eliminarEstado(Long id) {
        estadoRepository.deleteById(id);
    }

    public Estado obtenerEstadoPorId(Long estadoId) {
        return estadoRepository.findById(estadoId).orElse(null);
    }


}
