package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.config.SecurityConfig;
import com.umc.pediupatrao.service.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest({ClienteController.class, PedidoController.class, UsuarioController.class, HomeController.class})
@Import(SecurityConfig.class)
class PermissoesControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean UsuarioDetailsService details;
    @MockitoBean UsuarioService usuarios;
    @MockitoBean ClienteService clientes;
    @MockitoBean PedidoService pedidos;
    @MockitoBean ProdutoService produtos;

    @ParameterizedTest @ValueSource(strings = {"GERENTE", "ATENDENTE"})
    void equipeOperaClientesEPedidos(String perfil) throws Exception {
        mvc.perform(post("/api/clientes").with(user("operador").roles(perfil)).with(csrf())
                .contentType("application/json").content("{\"nome\":\"Maria\"}"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/clientes/c1").with(user("operador").roles(perfil)).with(csrf())
                .contentType("application/json").content("{\"nome\":\"Maria\"}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/clientes/c1").with(user("operador").roles(perfil)).with(csrf()))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/pedidos").with(user("operador").roles(perfil)).with(csrf())
                .contentType("application/json").content("{\"clienteId\":\"c1\",\"itens\":[]}"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/pedidos/p1/status?status=EM_PREPARACAO").with(user("operador").roles(perfil)).with(csrf()))
                .andExpect(status().isOk());
    }

    @ParameterizedTest @ValueSource(strings = {"ADMIN", "ATENDENTE"})
    void descontosECancelamentosContinuamExclusivosDoGerente(String perfil) throws Exception {
        mvc.perform(put("/api/pedidos/p1/desconto").with(user("operador").roles(perfil)).with(csrf())
                .contentType("application/json").content("{\"percentual\":10}"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/pedidos/p1/cancelar").with(user("operador").roles(perfil)).with(csrf())
                .contentType("application/json").content("{\"justificativa\":\"Solicitado\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(pedidos);
    }

    @ParameterizedTest @ValueSource(strings = {"GERENTE", "ATENDENTE"})
    void somenteAdminGerenciaUsuarios(String perfil) throws Exception {
        mvc.perform(delete("/api/usuarios/u1").with(user("operador").roles(perfil)).with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/usuarios/u1").with(user("operador").roles(perfil)).with(csrf())
                .contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(usuarios);
    }

    @Test void adminConsultaClientesSemPoderExcluir() throws Exception {
        when(clientes.listarClientes()).thenReturn(List.of());
        mvc.perform(get("/api/clientes").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
        mvc.perform(delete("/api/clientes/c1").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test void escritaContinuaExigindoCsrf() throws Exception {
        mvc.perform(delete("/api/clientes/c1").with(user("gerente").roles("GERENTE")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(clientes);
    }

    @Test void conflitoDeUsuarioRetorna409NaApiEFeedbackNoFormulario() throws Exception {
        doThrow(new IllegalStateException("Não é permitido remover ou rebaixar o último administrador."))
                .when(usuarios).deletarUsuario("a");
        mvc.perform(delete("/api/usuarios/a").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.erro").value(containsString("último administrador")));
        mvc.perform(post("/usuarios/deletar/a").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/usuarios")).andExpect(flash().attribute("erro", containsString("último administrador")));
    }

    @Test void formularioDoProprioUsuarioRenderizaPerfilBloqueado() throws Exception {
        var usuario = new com.umc.pediupatrao.entity.Usuario();
        usuario.setId("a"); usuario.setUsername("admin"); usuario.setRole(com.umc.pediupatrao.entity.Perfil.ADMIN);
        when(usuarios.buscarPorId("a")).thenReturn(java.util.Optional.of(usuario));
        mvc.perform(get("/usuarios/editar/a").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Você não pode alterar o próprio perfil")))
                .andExpect(content().string(containsString("disabled=\"disabled\"")));
    }
}
