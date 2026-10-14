package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Auditoria;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.AuditoriaRepository;
import com.umc.pediupatrao.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository,
            UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrar(
            String operacao,
            String entidade,
            String entidadeId,
            String campo,
            String valorAnterior,
            String valorNovo,
            String justificativa) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario.getUsername());
        auditoria.setPerfil(usuario.getRole());
        auditoria.setDataHora(LocalDateTime.now());
        auditoria.setOperacao(operacao);
        auditoria.setEntidade(entidade);
        auditoria.setEntidadeId(entidadeId);
        auditoria.setCampo(campo);
        auditoria.setValorAnterior(valorAnterior);
        auditoria.setValorNovo(valorNovo);
        auditoria.setJustificativa(justificativa);

        auditoriaRepository.save(auditoria);
    }
}