package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.repository.ClienteRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(
            ClienteRepository clienteRepository,
            AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    public Cliente novoCliente(Cliente cliente) {
        return salvar(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public void excluir(String id) {
        clienteRepository.deleteById(id);
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Os dados do cliente são obrigatórios.");
        }

        if (cliente.getId() == null || cliente.getId().isBlank()) {
            Cliente clienteNovo = clienteRepository.save(cliente);

            auditoriaService.registrar(
                    "CRIACAO",
                    "CLIENTE",
                    clienteNovo.getId(),
                    "cliente",
                    null,
                    "Cliente criado: " + clienteNovo.getNome(),
                    null
            );

            return clienteNovo;
        }

        Cliente clienteAnterior = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente não encontrado para atualização."
                ));

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        registrarAlteracoes(clienteAnterior, clienteAtualizado);

        return clienteAtualizado;
    }

    private void registrarAlteracoes(
            Cliente anterior,
            Cliente atual) {

        registrarCampoSeAlterado(
                anterior.getId(),
                "nome",
                anterior.getNome(),
                atual.getNome()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "telefone",
                anterior.getTelefone(),
                atual.getTelefone()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "cep",
                anterior.getCep(),
                atual.getCep()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "logradouro",
                anterior.getLogradouro(),
                atual.getLogradouro()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "numero",
                anterior.getNumero(),
                atual.getNumero()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "complemento",
                anterior.getComplemento(),
                atual.getComplemento()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "bairro",
                anterior.getBairro(),
                atual.getBairro()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "cidade",
                anterior.getCidade(),
                atual.getCidade()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "estado",
                anterior.getEstado(),
                atual.getEstado()
        );

        registrarCampoSeAlterado(
                anterior.getId(),
                "endereco",
                anterior.getEndereco(),
                atual.getEndereco()
        );
    }

    private void registrarCampoSeAlterado(
            String clienteId,
            String campo,
            String valorAnterior,
            String valorNovo) {

        if (!Objects.equals(valorAnterior, valorNovo)) {
            auditoriaService.registrar(
                    "ALTERACAO",
                    "CLIENTE",
                    clienteId,
                    campo,
                    valorAnterior,
                    valorNovo,
                    null
            );
        }
    }
}