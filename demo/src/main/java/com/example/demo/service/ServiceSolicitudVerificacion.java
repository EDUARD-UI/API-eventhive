package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.SolicitudVerificacionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.SolicitudVerificacion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.SolicitudVerificacionRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.utils.Utilidades;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceSolicitudVerificacion {

    private final SolicitudVerificacionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${upload.path.verificacion:uploads/verificacion}")
    private String uploadPath;

    // crear la solicitud
    public void crearSolicitud(String mensaje, MultipartFile archivo) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario organizador = usuarioRepository.findByCorreo(correo);

        if (organizador == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        // Validar que sea organizador
        if (!"ORGANIZADOR".equalsIgnoreCase(organizador.getRol().getNombre())) {
            throw new BusinessException("Solo organizadores pueden solicitar verificación");
        }

        // Validar que no tenga una solicitud pendiente
        SolicitudVerificacion pendiente = solicitudRepository.findByOrganizadorId(organizador.getId());
        if (pendiente != null && "PENDIENTE".equals(pendiente.getEstado())) {
            throw new BusinessException("Ya tiene una solicitud de verificación pendiente");
        }

        // Validar archivo
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Debe adjuntar un archivo de confirmación");
        }

        Utilidades.validarFoto(archivo);
        
        String nombreArchivo;
        try {
            nombreArchivo = Utilidades.guardarFoto(archivo, uploadPath);
        } catch (IOException e) {
            throw new BusinessException("Error al guardar el archivo: " + e.getMessage());
        }

        SolicitudVerificacion solicitud = new SolicitudVerificacion();
        solicitud.setOrganizador(organizador);
        solicitud.setMensaje(mensaje);
        solicitud.setArchivoConfirmacion(nombreArchivo);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(LocalDateTime.now());

        solicitudRepository.save(solicitud);
    }

    // ver estado de solicitud
    public SolicitudVerificacionDTO miSolicitud() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario organizador = usuarioRepository.findByCorreo(correo);

        if (organizador == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        SolicitudVerificacion solicitud = solicitudRepository.findByOrganizadorId(organizador.getId());

        if (solicitud == null) {
            throw new BusinessException("No tiene solicitudes de verificación");
        }

        return convertirADTO(solicitud);
    }

    // listar todas la solicitudes pendientes
    public Page<SolicitudVerificacionDTO> obtenerSolicitudesPendientes(Pageable pageable) {
        return solicitudRepository.findByEstado("PENDIENTE", pageable)
                .map(this::convertirADTO);
    }

    // obtener solicitud por id
    public SolicitudVerificacionDTO obtenerSolicitud(String solicitudId) {
        SolicitudVerificacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new BusinessException("Solicitud no encontrada"));
        return convertirADTO(solicitud);
    }

    // aprobar solicitud
    public void aprobarSolicitud(String solicitudId) {
        SolicitudVerificacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new BusinessException("Solicitud no encontrada"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new BusinessException("Solo se pueden aprobar solicitudes pendientes");
        }

        // Marcar al organizador como verificado
        Usuario organizador = solicitud.getOrganizador();
        organizador.setEsVerificado(true);
        usuarioRepository.save(organizador);

        // Actualizar solicitud
        String correoAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioRepository.findByCorreo(correoAdmin);

        solicitud.setEstado("APROBADA");
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitud.setAdministradorQueResolvi(admin);

        solicitudRepository.save(solicitud);
    }

    // rechazar solicitud
    public void rechazarSolicitud(String solicitudId, String motivo) {
        SolicitudVerificacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new BusinessException("Solicitud no encontrada"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new BusinessException("Solo se pueden rechazar solicitudes pendientes");
        }

        String correoAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioRepository.findByCorreo(correoAdmin);

        solicitud.setEstado("RECHAZADA");
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitud.setAdministradorQueResolvi(admin);
        solicitud.setMotivoRechazo(motivo);

        solicitudRepository.save(solicitud);
    }

    // helper
    private SolicitudVerificacionDTO convertirADTO(SolicitudVerificacion solicitud) {
        SolicitudVerificacionDTO dto = new SolicitudVerificacionDTO();

        dto.setId(solicitud.getId());
        dto.setEstado(solicitud.getEstado());
        dto.setMensaje(solicitud.getMensaje());
        dto.setArchivoConfirmacion(solicitud.getArchivoConfirmacion());
        dto.setFechaSolicitud(solicitud.getFechaSolicitud());
        dto.setFechaResolucion(solicitud.getFechaResolucion());
        dto.setMotivoRechazo(solicitud.getMotivoRechazo());

        if (solicitud.getOrganizador() != null) {
            dto.setOrganizadorId(solicitud.getOrganizador().getId());
            dto.setOrganizadorNombre(solicitud.getOrganizador().getNombre() + " " + solicitud.getOrganizador().getApellido());
            dto.setOrganizadorCorreo(solicitud.getOrganizador().getCorreo());
        }

        if (solicitud.getAdministradorQueResolvi() != null) {
            dto.setAdministradorNombre(solicitud.getAdministradorQueResolvi().getNombre());
        }

        return dto;
    }
}