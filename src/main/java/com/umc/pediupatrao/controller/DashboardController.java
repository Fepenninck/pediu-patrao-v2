package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.ResumoDashboardDTO;
import com.umc.pediupatrao.service.DashboardService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import com.umc.pediupatrao.dto.VendaDiariaDTO;
import com.umc.pediupatrao.dto.StatusDashboardDTO;
import com.umc.pediupatrao.dto.ProdutoRankingDTO;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/vendas-diarias")
    public List<VendaDiariaDTO> vendasDiarias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        return dashboardService.buscarVendasDiarias(dataInicial, dataFinal);
    }

    @GetMapping("/status")
    public List<StatusDashboardDTO> status(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        return dashboardService.buscarStatus(dataInicial, dataFinal);
    }

    @GetMapping("/produtos-mais-vendidos")
    public List<ProdutoRankingDTO> produtos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(defaultValue = "5") int limite) {
        return dashboardService.buscarProdutos(dataInicial, dataFinal, limite);
    }

    @GetMapping("/resumo")
    public ResumoDashboardDTO buscarResumo(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal
    ) {
        return dashboardService.buscarResumo(dataInicial, dataFinal);
    }
}
