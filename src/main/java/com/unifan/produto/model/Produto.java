package com.unifan.produto.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe de modelo para Produto.
 * Representa um item comercializado na loja/supermercado.
 * Possui relação de composição com a classe Categoria.
 */
public class Produto {

    private String codigo;
    private String nome;
    private String descricao;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private int quantidadeEstoque;
    private Categoria categoria;

    // Formatador auxiliar de data para exibições e relatórios
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor padrão
    public Produto() {
    }

    // Construtor completo
    public Produto(String codigo, String nome, String descricao, LocalDate dataFabricacao,
                   LocalDate dataValidade, BigDecimal precoCompra, BigDecimal precoVenda,
                   int quantidadeEstoque, Categoria categoria) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
    }

    // Getters e Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(LocalDate dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    // Métodos auxiliares para exibição fácil no JavaFX TableView
    public String getCategoriaNome() {
        return (categoria != null) ? categoria.getNome() : "";
    }

    public String getCategoriaSetor() {
        return (categoria != null) ? categoria.getSetor() : "";
    }

    public String getDataFabricacaoFormatada() {
        return (dataFabricacao != null) ? dataFabricacao.format(FORMATADOR_DATA) : "";
    }

    public String getDataValidadeFormatada() {
        return (dataValidade != null) ? dataValidade.format(FORMATADOR_DATA) : "";
    }

    // Cálculo da margem de lucro unitária em R$ (precoVenda - precoCompra)
    public BigDecimal getMargemLucro() {
        if (precoVenda != null && precoCompra != null) {
            return precoVenda.subtract(precoCompra);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                ", precoVenda=" + precoVenda +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", categoria=" + (categoria != null ? categoria.getNome() : "Nenhuma") +
                '}';
    }
}
