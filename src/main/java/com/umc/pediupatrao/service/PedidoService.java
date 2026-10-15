package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.CriarPedidoRequest;
import com.umc.pediupatrao.dto.ItemPedidoRequest;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final AuditoriaService auditoriaService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            AuditoriaService auditoriaService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.auditoriaService = auditoriaService;

    }

    private void auditarCampo(
        String operacao,
        String pedidoId,
        String campo,
        Object anterior,
        Object novo) {

    if (!Objects.equals(anterior, novo)) {
        auditoriaService.registrar(
                operacao,
                "PEDIDO",
                pedidoId,
                campo,
                anterior == null ? null : anterior.toString(),
                novo == null ? null : novo.toString(),
                null
        );
    }
}

    public Pedido criarPedido(CriarPedidoRequest request) {
        if (request.getClienteId() == null || request.getClienteId().isBlank()) {
            throw new IllegalArgumentException("O cliente é obrigatório.");
        }

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve possuir ao menos um item.");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.getItens()) {
            if (itemRequest.getProdutoId() == null || itemRequest.getProdutoId().isBlank()) {
                throw new IllegalArgumentException("Todo item deve possuir um produto.");
            }

            if (itemRequest.getQuantidade() == null || itemRequest.getQuantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
            }

            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

            if (!Boolean.TRUE.equals(produto.getAtivo())) {
                throw new IllegalArgumentException(
                        "O produto " + produto.getNome() + " está inativo."
                );
            }

            BigDecimal precoUnitario = BigDecimal.valueOf(produto.getPreco())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal subtotalItem = precoUnitario
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantidade()))
                    .setScale(2, RoundingMode.HALF_UP);

            ItemPedido item = new ItemPedido();
            item.setProdutoId(produto.getId());
            item.setNomeProduto(produto.getNome());
            item.setQuantidade(itemRequest.getQuantidade());
            item.setPrecoUnitario(precoUnitario);
            item.setSubtotal(subtotalItem);

            itens.add(item);
            subtotal = subtotal.add(subtotalItem);
        }

        String usuarioAutenticado = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Pedido pedido = new Pedido();
        pedido.setClienteId(cliente.getId());
        pedido.setItens(itens);
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setSubtotal(subtotal);
        pedido.setPercentualDesconto(BigDecimal.ZERO);
        pedido.setValorDesconto(BigDecimal.ZERO);
        pedido.setValorTotal(subtotal);
        pedido.setResponsavelEntrada(usuarioAutenticado);
        pedido.setDataHoraEntrada(LocalDateTime.now());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        auditarCampo("CRIACAO", pedidoSalvo.getId(), "clienteId", null, pedidoSalvo.getClienteId());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "status", null, pedidoSalvo.getStatus());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "subtotal", null, pedidoSalvo.getSubtotal());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "percentualDesconto", null, pedidoSalvo.getPercentualDesconto());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "valorDesconto", null, pedidoSalvo.getValorDesconto());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "valorTotal", null, pedidoSalvo.getValorTotal());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "responsavelEntrada", null, pedidoSalvo.getResponsavelEntrada());
        auditarCampo("CRIACAO", pedidoSalvo.getId(), "dataHoraEntrada", null, pedidoSalvo.getDataHoraEntrada());

        for (int i = 0; i < pedidoSalvo.getItens().size(); i++) {
            ItemPedido item = pedidoSalvo.getItens().get(i);
            String campo = "itens[" + i + "].";

            auditarCampo("CRIACAO", pedidoSalvo.getId(), campo + "produtoId", null, item.getProdutoId());
            auditarCampo("CRIACAO", pedidoSalvo.getId(), campo + "nomeProduto", null, item.getNomeProduto());
            auditarCampo("CRIACAO", pedidoSalvo.getId(), campo + "quantidade", null, item.getQuantidade());
            auditarCampo("CRIACAO", pedidoSalvo.getId(), campo + "precoUnitario", null, item.getPrecoUnitario());
            auditarCampo("CRIACAO", pedidoSalvo.getId(), campo + "subtotal", null, item.getSubtotal());
        }

        return pedidoSalvo;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido atualizarStatus(String id, StatusPedido novoStatus) {
    if (novoStatus == null) {
        throw new IllegalArgumentException("O novo status é obrigatório.");
    }

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

    StatusPedido statusAtual = pedido.getStatus();
    LocalDateTime dataHoraSaidaAnterior = pedido.getDataHoraSaida();
    String responsavelSaidaAnterior = pedido.getResponsavelSaida();

    if (statusAtual == StatusPedido.CANCELADO) {
        throw new IllegalStateException(
                "Um pedido cancelado não pode sofrer novas alterações."
        );
    }

    if (statusAtual == StatusPedido.FINALIZADO) {
        throw new IllegalStateException(
                "Um pedido finalizado não pode sofrer novas alterações."
        );
    }

    if (!transicaoPermitida(statusAtual, novoStatus)) {
        throw new IllegalStateException(
                "Transição inválida: " + statusAtual + " para " + novoStatus
        );
    }

    if (novoStatus == StatusPedido.SAIU_PARA_ENTREGA
            || novoStatus == StatusPedido.RETIRADO) {

        String usuarioAutenticado = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        pedido.setDataHoraSaida(LocalDateTime.now());
        pedido.setResponsavelSaida(usuarioAutenticado);
    }

    pedido.setStatus(novoStatus);

    Pedido pedidoAtualizado = pedidoRepository.save(pedido);

    auditoriaService.registrar(
            "ALTERACAO_STATUS",
            "PEDIDO",
            pedidoAtualizado.getId(),
            "status",
            statusAtual.name(),
            novoStatus.name(),
            null
    );

    auditarCampo(
            "SAIDA", pedidoAtualizado.getId(), "dataHoraSaida",
            dataHoraSaidaAnterior, pedidoAtualizado.getDataHoraSaida()
    );
    auditarCampo(
            "SAIDA", pedidoAtualizado.getId(), "responsavelSaida",
            responsavelSaidaAnterior, pedidoAtualizado.getResponsavelSaida()
    );

    return pedidoAtualizado;
}

public Pedido aplicarDesconto(String id, BigDecimal percentual) {
    if (percentual == null) {
        throw new IllegalArgumentException("O percentual de desconto é obrigatório.");
    }

    if (percentual.compareTo(BigDecimal.ZERO) < 0
            || percentual.compareTo(new BigDecimal("20")) > 0) {
        throw new IllegalArgumentException(
                "O desconto deve ser entre 0% e 20%."
        );
    }

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

    if (!pedidoPodeSerAlteradoComercialmente(pedido)) {
        throw new IllegalStateException(
                "O desconto só pode ser aplicado antes da saída ou retirada do pedido."
        );
    }

    BigDecimal percentualAnterior = pedido.getPercentualDesconto();
    BigDecimal totalAnterior = pedido.getValorTotal();

    BigDecimal valorDesconto = pedido.getSubtotal()
            .multiply(percentual)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

    BigDecimal valorTotal = pedido.getSubtotal()
            .subtract(valorDesconto)
            .setScale(2, RoundingMode.HALF_UP);

    pedido.setPercentualDesconto(percentual);
    pedido.setValorDesconto(valorDesconto);
    pedido.setValorTotal(valorTotal);

    Pedido pedidoAtualizado = pedidoRepository.save(pedido);

    auditoriaService.registrar(
            "DESCONTO",
            "PEDIDO",
            pedidoAtualizado.getId(),
            "desconto e valorTotal",
            "Percentual: " + percentualAnterior + "% | Total: R$ " + totalAnterior,
            "Percentual: " + percentual + "% | Total: R$ " + valorTotal,
            null
    );

    return pedidoAtualizado;
}

public Pedido cancelarPedido(String id, String justificativa) {
    if (justificativa == null || justificativa.isBlank()) {
        throw new IllegalArgumentException(
                "A justificativa do cancelamento é obrigatória."
        );
    }

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

    if (!pedidoPodeSerAlteradoComercialmente(pedido)) {
        throw new IllegalStateException(
                "Não é possível cancelar um pedido que já saiu ou foi retirado."
        );
    }

    String usuarioAutenticado = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    StatusPedido statusAnterior = pedido.getStatus();
    String justificativaAnterior = pedido.getJustificativaCancelamento();
    String responsavelAnterior = pedido.getResponsavelCancelamento();
    LocalDateTime dataHoraAnterior = pedido.getDataHoraCancelamento();

    pedido.setStatus(StatusPedido.CANCELADO);
    pedido.setJustificativaCancelamento(justificativa.trim());
    pedido.setResponsavelCancelamento(usuarioAutenticado);
    pedido.setDataHoraCancelamento(LocalDateTime.now());

    Pedido pedidoCancelado = pedidoRepository.save(pedido);

    auditoriaService.registrar(
            "CANCELAMENTO",
            "PEDIDO",
            pedidoCancelado.getId(),
            "status",
            statusAnterior.name(),
            StatusPedido.CANCELADO.name(),
            justificativa.trim()
    );

    auditoriaService.registrar(
            "CANCELAMENTO",
            "PEDIDO",
            pedidoCancelado.getId(),
            "justificativaCancelamento",
            justificativaAnterior,
            pedidoCancelado.getJustificativaCancelamento(),
            justificativa.trim()
    );

    auditoriaService.registrar(
            "CANCELAMENTO",
            "PEDIDO",
            pedidoCancelado.getId(),
            "responsavelCancelamento",
            responsavelAnterior,
            pedidoCancelado.getResponsavelCancelamento(),
            justificativa.trim()
    );

    auditoriaService.registrar(
            "CANCELAMENTO",
            "PEDIDO",
            pedidoCancelado.getId(),
            "dataHoraCancelamento",
            dataHoraAnterior == null ? null : dataHoraAnterior.toString(),
            pedidoCancelado.getDataHoraCancelamento().toString(),
            justificativa.trim()
    );

    return pedidoCancelado;
}

private boolean pedidoPodeSerAlteradoComercialmente(Pedido pedido) {
    return pedido.getStatus() != StatusPedido.SAIU_PARA_ENTREGA
            && pedido.getStatus() != StatusPedido.RETIRADO
            && pedido.getStatus() != StatusPedido.FINALIZADO
            && pedido.getStatus() != StatusPedido.CANCELADO;
}

private boolean transicaoPermitida(
        StatusPedido statusAtual,
        StatusPedido novoStatus) {

    return switch (statusAtual) {
        case RECEBIDO ->
            novoStatus == StatusPedido.EM_PREPARACAO;

        case EM_PREPARACAO ->
            novoStatus == StatusPedido.PRONTO;

        case PRONTO ->
            novoStatus == StatusPedido.SAIU_PARA_ENTREGA
            || novoStatus == StatusPedido.RETIRADO;

        case SAIU_PARA_ENTREGA, RETIRADO ->
            novoStatus == StatusPedido.FINALIZADO;

        case FINALIZADO, CANCELADO ->
            false;
    };
}
}
