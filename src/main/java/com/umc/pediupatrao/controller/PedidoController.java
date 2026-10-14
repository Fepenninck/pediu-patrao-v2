package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.CriarPedidoRequest;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.service.PedidoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.umc.pediupatrao.dto.CancelamentoRequest;
import com.umc.pediupatrao.dto.DescontoRequest;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody CriarPedidoRequest request) {
        Pedido pedido = pedidoService.criarPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @PutMapping("/{id}/status")
    public Pedido atualizarStatus(
            @PathVariable String id,
            @RequestParam StatusPedido status) {

        return pedidoService.atualizarStatus(id, status);
    }

    @PutMapping("/{id}/desconto")
public Pedido aplicarDesconto(
        @PathVariable String id,
        @RequestBody DescontoRequest request) {

    return pedidoService.aplicarDesconto(id, request.getPercentual());
}

@PutMapping("/{id}/cancelar")
public Pedido cancelarPedido(
        @PathVariable String id,
        @RequestBody CancelamentoRequest request) {

    return pedidoService.cancelarPedido(id, request.getJustificativa());
}

}