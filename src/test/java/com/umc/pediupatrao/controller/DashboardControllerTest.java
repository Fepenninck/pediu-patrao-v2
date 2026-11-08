package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.config.SecurityConfig;
import com.umc.pediupatrao.dto.*;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({DashboardController.class, HomeController.class})
@Import(SecurityConfig.class)
class DashboardControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean DashboardService service;
    @MockitoBean UsuarioDetailsService usuarioDetailsService;
    @MockitoBean PedidoService pedidoService;
    @MockitoBean ClienteService clienteService;
    @MockitoBean ProdutoService produtoService;
    @MockitoBean UsuarioService usuarioService;
    private final LocalDate inicio = LocalDate.of(2026, 11, 1);
    private final LocalDate fim = LocalDate.of(2026, 11, 7);

    private String url(String recurso) {
        return "/api/dashboard/" + recurso + "?dataInicial=2026-11-01&dataFinal=2026-11-07";
    }

    @Test @WithMockUser(roles = "GERENTE")
    void gerenteConsultaResumo() throws Exception {
        when(service.buscarResumo(inicio, fim)).thenReturn(new ResumoDashboardDTO(new BigDecimal("150.00"), 3, new BigDecimal("50.00"), 1));
        mvc.perform(get(url("resumo"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.faturamento").value(150))
                .andExpect(jsonPath("$.pedidosFinalizados").value(3));
    }

    @Test @WithMockUser(roles = "ADMIN")
    void adminConsultaAnalises() throws Exception {
        when(service.buscarVendasDiarias(inicio, fim)).thenReturn(List.of(new VendaDiariaDTO(inicio, new BigDecimal("30.00"))));
        when(service.buscarStatus(inicio, fim)).thenReturn(List.of(new StatusDashboardDTO(StatusPedido.FINALIZADO, 1)));
        when(service.buscarProdutos(inicio, fim, 5)).thenReturn(List.of(new ProdutoRankingDTO("p1", "Pizza", 2)));
        mvc.perform(get(url("vendas-diarias"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].data").value("2026-11-01"));
        mvc.perform(get(url("status"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].quantidade").value(1));
        mvc.perform(get(url("produtos-mais-vendidos"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].quantidade").value(2));
    }

    @Test @WithMockUser(roles = "GERENTE")
    void retorna400ParaParametrosAusentesOuInvalidos() throws Exception {
        mvc.perform(get("/api/dashboard/resumo")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/dashboard/resumo?dataInicial=errada&dataFinal=2026-11-07")).andExpect(status().isBadRequest());
        mvc.perform(get(url("produtos-mais-vendidos") + "&limite=abc")).andExpect(status().isBadRequest());
        when(service.buscarResumo(inicio, fim)).thenThrow(new IllegalArgumentException("Período inválido"));
        mvc.perform(get(url("resumo"))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").value("Período inválido"));
    }

    @Test @WithMockUser(roles = "GERENTE")
    void falhaInternaNaoExpoeDetalhesDoBanco() throws Exception {
        when(service.buscarResumo(inicio, fim)).thenThrow(new IllegalStateException("detalhe interno"));
        mvc.perform(get(url("resumo"))).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value("Não foi possível consultar os dados. Tente novamente mais tarde."));
    }

    @Test @WithMockUser(roles = "ATENDENTE")
    void atendenteNaoAcessaPaginaNemApi() throws Exception {
        mvc.perform(get("/dashboard")).andExpect(status().isForbidden());
        for (String recurso : List.of("resumo", "vendas-diarias", "status", "produtos-mais-vendidos")) {
            mvc.perform(get(url(recurso))).andExpect(status().isForbidden());
        }
        verifyNoInteractions(service);
    }

    @Test void visitanteVaiParaLogin() throws Exception {
        mvc.perform(get(url("resumo"))).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));
    }

    @Test @WithMockUser(roles = "GERENTE")
    void paginaRenderizaTemplateERecursos() throws Exception {
        mvc.perform(get("/dashboard")).andExpect(status().isOk()).andExpect(view().name("layout"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"grafico-vendas\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/js/dashboard.js")));
    }
}
