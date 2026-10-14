package com.umc.pediupatrao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemPedidoRequest {

    private String produtoId;
    private Integer quantidade;
}