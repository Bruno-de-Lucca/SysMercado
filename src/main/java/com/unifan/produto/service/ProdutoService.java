package com.unifan.produto.service;

import com.unifan.produto.model.Categoria;
import com.unifan.produto.model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe de serviço responsável pelas regras de negócio, validações,
 * persistência em arquivo CSV e geração de relatórios com Stream API.
 */
public class ProdutoService {

    private static final String NOME_ARQUIVO_CSV = "produtos.csv";
    private static final String SEPARADOR_CSV = ";";

    private final List<Produto> listaProdutos = new ArrayList<>();

    public ProdutoService() {
        carregarArquivo();
    }

    // =========================================================================
    // OPERAÇÕES BÁSICAS
    // =========================================================================

    /**
     * Retorna a lista completa de produtos em memória.
     */
    public List<Produto> listarTodos() {
        return new ArrayList<>(listaProdutos);
    }

    /**
     * Consulta um produto pelo seu código único.
     */
    public Produto consultarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        String codigoLimpo = codigo.trim();
        return listaProdutos.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigoLimpo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Cadastra um novo produto após validação e salva no CSV.
     */
    public void cadastrar(Produto produto) {
        validar(produto, true);

        // Verifica se o código já está em uso
        if (consultarPorCodigo(produto.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe um produto cadastrado com o código: " + produto.getCodigo());
        }

        listaProdutos.add(produto);
        salvarArquivo();
    }

    /**
     * Atualiza um produto existente e salva no CSV.
     */
    public void atualizar(Produto produto) {
        validar(produto, false);

        int index = -1;
        for (int i = 0; i < listaProdutos.size(); i++) {
            if (listaProdutos.get(i).getCodigo().equalsIgnoreCase(produto.getCodigo())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Produto com código " + produto.getCodigo() + " não foi encontrado para atualização.");
        }

        listaProdutos.set(index, produto);
        salvarArquivo();
    }

    /**
     * Exclui um produto pelo código e atualiza o arquivo CSV.
     */
    public boolean excluir(String codigo) {
        Produto produto = consultarPorCodigo(codigo);
        if (produto != null) {
            listaProdutos.remove(produto);
            salvarArquivo();
            return true;
        }
        return false;
    }

    // =========================================================================
    // VALIDAÇÕES DE DADOS (Requisitos Técnicos)
    // =========================================================================

    public void validar(Produto p, boolean isNovo) {
        if (p == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }

        // 1. Código: formato definido (8 caracteres alfanuméricos)
        if (p.getCodigo() == null || !p.getCodigo().trim().matches("^[a-zA-Z0-9]{8}$")) {
            throw new IllegalArgumentException("Código inválido! Deve conter exatamente 8 caracteres alfanuméricos (ex: PROD0001).");
        }

        // 2. Nome: não vazio, tamanho mínimo (pelo menos 3 caracteres)
        if (p.getNome() == null || p.getNome().trim().length() < 3) {
            throw new IllegalArgumentException("Nome inválido! O nome deve ter no mínimo 3 caracteres.");
        }

        // 3. Data de fabricação: não pode ser posterior à data atual
        if (p.getDataFabricacao() == null) {
            throw new IllegalArgumentException("A data de fabricação é obrigatória.");
        }
        if (p.getDataFabricacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de fabricação inválida! Não pode ser posterior à data atual.");
        }

        // 4. Data de validade: não pode ser anterior à data de fabricação
        if (p.getDataValidade() == null) {
            throw new IllegalArgumentException("A data de validade é obrigatória.");
        }
        if (p.getDataValidade().isBefore(p.getDataFabricacao())) {
            throw new IllegalArgumentException("Data de validade inválida! Não pode ser anterior à data de fabricação.");
        }

        // 5. Preços: valores positivos e preço de venda maior que preço de compra
        if (p.getPrecoCompra() == null || p.getPrecoCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de compra inválido! Deve ser um valor positivo maior que zero.");
        }
        if (p.getPrecoVenda() == null || p.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de venda inválido! Deve ser um valor positivo maior que zero.");
        }
        if (p.getPrecoVenda().compareTo(p.getPrecoCompra()) <= 0) {
            throw new IllegalArgumentException("Preço de venda inválido! O preço de venda deve ser maior que o preço de compra.");
        }

        // 6. Quantidade em estoque: não negativa
        if (p.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Quantidade em estoque inválida! Não pode ser negativa.");
        }

        // Validação da Categoria composta
        if (p.getCategoria() == null) {
            throw new IllegalArgumentException("A categoria do produto é obrigatória.");
        }
        if (p.getCategoria().getId() <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser um número positivo maior que zero.");
        }
        if (p.getCategoria().getNome() == null || p.getCategoria().getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }
        if (p.getCategoria().getSetor() == null || p.getCategoria().getSetor().trim().isEmpty()) {
            throw new IllegalArgumentException("O setor da categoria é obrigatório.");
        }
    }

    // =========================================================================
    // PERSISTÊNCIA EM ARQUIVO CSV
    // =========================================================================

    /**
     * Carrega os produtos do arquivo CSV para a memória.
     */
    public void carregarArquivo() {
        listaProdutos.clear();
        File arquivo = new File(NOME_ARQUIVO_CSV);

        if (!arquivo.exists()) {
            criarArquivoInicialComExemplos();
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha = br.readLine(); // Lê cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] dados = linha.split(SEPARADOR_CSV);
                if (dados.length >= 12) {
                    try {
                        String codigo = dados[0].trim();
                        String nome = dados[1].trim();
                        String descricao = dados[2].trim();
                        LocalDate dataFabricacao = LocalDate.parse(dados[3].trim());
                        LocalDate dataValidade = LocalDate.parse(dados[4].trim());
                        BigDecimal precoCompra = new BigDecimal(dados[5].trim());
                        BigDecimal precoVenda = new BigDecimal(dados[6].trim());
                        int quantidadeEstoque = Integer.parseInt(dados[7].trim());

                        int catId = Integer.parseInt(dados[8].trim());
                        String catNome = dados[9].trim();
                        String catDescricao = dados[10].trim();
                        String catSetor = dados[11].trim();

                        Categoria categoria = new Categoria(catId, catNome, catDescricao, catSetor);
                        Produto produto = new Produto(codigo, nome, descricao, dataFabricacao, dataValidade,
                                precoCompra, precoVenda, quantidadeEstoque, categoria);

                        listaProdutos.add(produto);
                    } catch (Exception e) {
                        System.err.println("Erro ao processar linha do CSV: " + linha + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo CSV: " + e.getMessage());
        }
    }

    /**
     * Salva a lista de produtos atual no arquivo CSV.
     */
    public void salvarArquivo() {
        File arquivo = new File(NOME_ARQUIVO_CSV);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {
            // Cabeçalho CSV
            bw.write("codigo;nome;descricao;dataFabricacao;dataValidade;precoCompra;precoVenda;quantidadeEstoque;categoriaId;categoriaNome;categoriaDescricao;categoriaSetor");
            bw.newLine();

            for (Produto p : listaProdutos) {
                Categoria c = p.getCategoria();
                String linha = String.join(SEPARADOR_CSV,
                        p.getCodigo(),
                        p.getNome(),
                        p.getDescricao() != null ? p.getDescricao() : "",
                        p.getDataFabricacao().toString(),
                        p.getDataValidade().toString(),
                        p.getPrecoCompra().toString(),
                        p.getPrecoVenda().toString(),
                        String.valueOf(p.getQuantidadeEstoque()),
                        String.valueOf(c != null ? c.getId() : 1),
                        c != null && c.getNome() != null ? c.getNome() : "",
                        c != null && c.getDescricao() != null ? c.getDescricao() : "",
                        c != null && c.getSetor() != null ? c.getSetor() : ""
                );
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo CSV: " + e.getMessage());
        }
    }

    /**
     * Cria arquivo inicial com produtos de teste caso o CSV ainda não exista.
     */
    private void criarArquivoInicialComExemplos() {
        LocalDate hoje = LocalDate.now();

        Categoria cAlimentos = new Categoria(1, "Grãos e Cereais", "Itens não perecíveis", "Mercearia");
        Categoria cLaticinios = new Categoria(2, "Laticínios", "Derivados do leite refrigerados", "Frios e Laticínios");
        Categoria cBebidas = new Categoria(3, "Bebidas", "Sucos e refrigerantes", "Bebidas");
        Categoria cLimpeza = new Categoria(4, "Limpeza Doméstica", "Produtos de limpeza geral", "Limpeza");

        listaProdutos.add(new Produto("PROD0001", "Arroz Branco 5kg", "Arroz agulhinha tipo 1",
                hoje.minusMonths(3), hoje.plusMonths(9), new BigDecimal("21.50"), new BigDecimal("28.90"), 35, cAlimentos));

        listaProdutos.add(new Produto("PROD0002", "Feijão Carioca 1kg", "Feijão carioca selecionado",
                hoje.minusMonths(2), hoje.plusDays(40), new BigDecimal("6.20"), new BigDecimal("8.99"), 8, cAlimentos));

        listaProdutos.add(new Produto("PROD0003", "Leite Integral 1L", "Leite UHT longa vida",
                hoje.minusWeeks(2), hoje.plusDays(25), new BigDecimal("3.80"), new BigDecimal("5.49"), 50, cLaticinios));

        listaProdutos.add(new Produto("PROD0004", "Iogurte Natural 170g", "Iogurte desnatado",
                hoje.minusWeeks(1), hoje.plusDays(15), new BigDecimal("2.10"), new BigDecimal("3.50"), 4, cLaticinios));

        listaProdutos.add(new Produto("PROD0005", "Suco de Laranja 1L", "Suco integral sem açúcar",
                hoje.minusMonths(1), hoje.plusMonths(4), new BigDecimal("7.50"), new BigDecimal("11.90"), 18, cBebidas));

        listaProdutos.add(new Produto("PROD0006", "Detergente Líquido 500ml", "Detergente neutro concentrado",
                hoje.minusMonths(5), hoje.plusYears(1), new BigDecimal("1.80"), new BigDecimal("2.99"), 6, cLimpeza));

        salvarArquivo();
    }

    // =========================================================================
    // RELATÓRIOS COM STREAM API (Requisitos Técnicos)
    // =========================================================================

    /**
     * 1. Filtrar produtos próximos ao vencimento (próximos 60 dias).
     * Utiliza filter com LocalDate.
     */
    public List<Produto> filtrarProximosVencimento(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);

        return listaProdutos.stream()
                .filter(p -> p.getDataValidade() != null)
                .filter(p -> !p.getDataValidade().isBefore(hoje) && !p.getDataValidade().isAfter(limite))
                .sorted(Comparator.comparing(Produto::getDataValidade))
                .collect(Collectors.toList());
    }

    /**
     * 2. Filtrar produtos com estoque baixo (menos de 10 unidades).
     * Utiliza filter.
     */
    public List<Produto> filtrarEstoqueBaixo(int limiteEstoque) {
        return listaProdutos.stream()
                .filter(p -> p.getQuantidadeEstoque() < limiteEstoque)
                .sorted(Comparator.comparingInt(Produto::getQuantidadeEstoque))
                .collect(Collectors.toList());
    }

    /**
     * 3. Calcular margem de lucro média por categoria.
     * Utiliza Stream groupingBy e averagingDouble.
     */
    public Map<String, Double> calcularMargemLucroMediaPorCategoria() {
        return listaProdutos.stream()
                .collect(Collectors.groupingBy(
                        Produto::getCategoriaNome,
                        Collectors.averagingDouble(p -> p.getMargemLucro().doubleValue())
                ));
    }

    /**
     * 4. Listar produtos agrupados por setor de categoria.
     * Utiliza Stream groupingBy.
     */
    public Map<String, List<Produto>> listarProdutosAgrupadosPorSetor() {
        return listaProdutos.stream()
                .collect(Collectors.groupingBy(
                        Produto::getCategoriaSetor,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    /**
     * Relatório Bônus com filter, map e reduce:
     * Calcula o lucro total potencial considerando todas as unidades em estoque.
     */
    public BigDecimal calcularLucroTotalPotencial() {
        return listaProdutos.stream()
                .map(p -> p.getMargemLucro().multiply(BigDecimal.valueOf(p.getQuantidadeEstoque())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Valor total em estoque (preço de venda * quantidade):
     * Demonstração de map e reduce.
     */
    public BigDecimal calcularValorTotalEstoque() {
        return listaProdutos.stream()
                .map(p -> p.getPrecoVenda().multiply(BigDecimal.valueOf(p.getQuantidadeEstoque())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
