package com.umc.pediupatrao.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;

    private String clienteId;
    private List<ItemPedido> itens;
    private StatusPedido status;

    private BigDecimal subtotal;
    private BigDecimal percentualDesconto;
    private BigDecimal valorDesconto;
    private BigDecimal valorTotal;

    private String responsavelEntrada;
    private LocalDateTime dataHoraEntrada;

    private String responsavelSaida;
    private LocalDateTime dataHoraSaida;

    private String justificativaCancelamento;
    private String responsavelCancelamento;
    private LocalDateTime dataHoraCancelamento;
}