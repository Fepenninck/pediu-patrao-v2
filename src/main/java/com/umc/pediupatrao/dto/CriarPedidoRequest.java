package com.umc.pediupatrao.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarPedidoRequest {

    private String clienteId;
    private List<ItemPedidoRequest> itens;
}