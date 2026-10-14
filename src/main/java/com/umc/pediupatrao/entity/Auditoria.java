package com.umc.pediupatrao.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "auditorias")
public class Auditoria {

    @Id
    private String id;

    private String usuario;
    private Perfil perfil;
    private LocalDateTime dataHora;

    private String operacao;
    private String entidade;
    private String entidadeId;

    private String campo;
    private String valorAnterior;
    private String valorNovo;
    private String justificativa;
}