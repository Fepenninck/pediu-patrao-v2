package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.ProdutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto novoProduto(Produto produto) {
        return salvar(produto);
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public Produto salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException(
                    "Os dados do produto são obrigatórios."
            );
        }

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório."
            );
        }

        if (produto.getTipo() == null || produto.getTipo().isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo do produto é obrigatório."
            );
        }

        // Double aceita tanto um getter double quanto Double.
        Double preco = produto.getPreco();

        if (preco == null || !Double.isFinite(preco) || preco <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser um número maior que zero."
            );
        }

        if (produto.getId() != null && !produto.getId().isBlank()) {
            if (!produtoRepository.existsById(produto.getId())) {
                throw new IllegalArgumentException(
                        "Produto não encontrado para atualização."
                );
            }
        } else {
            produto.setId(null);
        }

        produto.setNome(produto.getNome().trim());
        produto.setTipo(produto.getTipo().trim());
        produto.setAtivo(Boolean.TRUE.equals(produto.getAtivo()));

        return produtoRepository.save(produto);
    }

    public void excluir(String id) {
        if (!produtoRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Produto não encontrado."
            );
        }

        produtoRepository.deleteById(id);
    }
}