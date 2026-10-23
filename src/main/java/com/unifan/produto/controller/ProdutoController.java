package com.unifan.produto.controller;

import com.unifan.produto.model.Categoria;
import com.unifan.produto.model.Produto;
import com.unifan.produto.service.ProdutoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Controlador JavaFX para a tela de gerenciamento de produtos.
 * Trata os eventos dos botões, preenchimento de campos, tabela e relatórios.
 */
public class ProdutoController implements Initializable {

    // Componentes da Busca Rápida
    @FXML
    private TextField txtConsultaCodigo;

    @FXML
    private Button btnConsultar;

    @FXML
    private Button btnListarTodos;

    @FXML
    private Label lblStatusPesquisa;

    // Componentes do Formulário de Produto
    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtDescricao;

    @FXML
    private DatePicker dpFabricacao;

    @FXML
    private DatePicker dpValidade;

    @FXML
    private TextField txtPrecoCompra;

    @FXML
    private TextField txtPrecoVenda;

    @FXML
    private TextField txtEstoque;

    // Componentes da Categoria
    @FXML
    private TextField txtCategoriaId;

    @FXML
    private TextField txtCategoriaNome;

    @FXML
    private TextField txtCategoriaSetor;

    @FXML
    private TextField txtCategoriaDescricao;

    // Botões do Formulário
    @FXML
    private Button btnCadastrar;

    @FXML
    private Button btnAtualizar;

    @FXML
    private Button btnExcluir;

    @FXML
    private Button btnLimpar;

    // Tabela e Colunas
    @FXML
    private TableView<Produto> tableViewProdutos;

    @FXML
    private TableColumn<Produto, String> colCodigo;

    @FXML
    private TableColumn<Produto, String> colNome;

    @FXML
    private TableColumn<Produto, String> colCategoria;

    @FXML
    private TableColumn<Produto, String> colSetor;

    @FXML
    private TableColumn<Produto, BigDecimal> colPrecoCompra;

    @FXML
    private TableColumn<Produto, BigDecimal> colPrecoVenda;

    @FXML
    private TableColumn<Produto, Integer> colEstoque;

    @FXML
    private TableColumn<Produto, String> colFabricacao;

    @FXML
    private TableColumn<Produto, String> colValidade;

    // Aba de Relatórios
    @FXML
    private Button btnRelatorioVencimento;

    @FXML
    private Button btnRelatorioEstoqueBaixo;

    @FXML
    private Button btnRelatorioMargem;

    @FXML
    private Button btnRelatorioSetor;

    @FXML
    private Button btnRelatorioLucroTotal;

    @FXML
    private Button btnLimparRelatorio;

    @FXML
    private TextArea txtAreaRelatorio;

    // Serviços e Lista Observável
    private final ProdutoService produtoService = new ProdutoService();
    private final ObservableList<Produto> listaObservable = FXCollections.observableArrayList();

    private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunasTabela();
        configurarSelecaoTabela();
        atualizarTabela();
    }

    /**
     * Configura as colunas da TableView com PropertyValueFactory.
     */
    private void configurarColunasTabela() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNome"));
        colSetor.setCellValueFactory(new PropertyValueFactory<>("categoriaSetor"));
        colPrecoCompra.setCellValueFactory(new PropertyValueFactory<>("precoCompra"));
        colPrecoVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        colFabricacao.setCellValueFactory(new PropertyValueFactory<>("dataFabricacaoFormatada"));
        colValidade.setCellValueFactory(new PropertyValueFactory<>("dataValidadeFormatada"));

        // Formatação amigável para preços (R$)
        colPrecoCompra.setCellFactory(col -> new TableCell<Produto, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatadorMoeda.format(item));
                }
            }
        });

        colPrecoVenda.setCellFactory(col -> new TableCell<Produto, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatadorMoeda.format(item));
                }
            }
        });

        tableViewProdutos.setItems(listaObservable);
    }

    /**
     * Ao clicar em uma linha da tabela, preenche os campos do formulário para facilitar edição/exclusão.
     */
    private void configurarSelecaoTabela() {
        tableViewProdutos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });
    }

    /**
     * Recarrega a lista observável com todos os produtos persistidos.
     */
    private void atualizarTabela() {
        listaObservable.clear();
        listaObservable.addAll(produtoService.listarTodos());
    }

    /**
     * Preenche os campos de texto e seletores com os dados de um Produto.
     */
    private void preencherFormulario(Produto p) {
        txtCodigo.setText(p.getCodigo());
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao() != null ? p.getDescricao() : "");
        dpFabricacao.setValue(p.getDataFabricacao());
        dpValidade.setValue(p.getDataValidade());
        txtPrecoCompra.setText(p.getPrecoCompra() != null ? p.getPrecoCompra().toString() : "");
        txtPrecoVenda.setText(p.getPrecoVenda() != null ? p.getPrecoVenda().toString() : "");
        txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));

        if (p.getCategoria() != null) {
            txtCategoriaId.setText(String.valueOf(p.getCategoria().getId()));
            txtCategoriaNome.setText(p.getCategoria().getNome());
            txtCategoriaSetor.setText(p.getCategoria().getSetor());
            txtCategoriaDescricao.setText(p.getCategoria().getDescricao() != null ? p.getCategoria().getDescricao() : "");
        }
    }

    /**
     * Constrói um objeto Produto a partir dos valores digitados no formulário.
     */
    private Produto extrairProdutoDoFormulario() {
        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String descricao = txtDescricao.getText() != null ? txtDescricao.getText().trim() : "";
        LocalDate fabricacao = dpFabricacao.getValue();
        LocalDate validade = dpValidade.getValue();

        BigDecimal precoCompra;
        try {
            String strPrecoCompra = txtPrecoCompra.getText() != null ? txtPrecoCompra.getText().trim().replace(",", ".") : "";
            precoCompra = new BigDecimal(strPrecoCompra);
        } catch (Exception e) {
            throw new IllegalArgumentException("Preço de compra inválido! Digite um valor numérico válido (ex: 15.50).");
        }

        BigDecimal precoVenda;
        try {
            String strPrecoVenda = txtPrecoVenda.getText() != null ? txtPrecoVenda.getText().trim().replace(",", ".") : "";
            precoVenda = new BigDecimal(strPrecoVenda);
        } catch (Exception e) {
            throw new IllegalArgumentException("Preço de venda inválido! Digite um valor numérico válido (ex: 22.90).");
        }

        int estoque;
        try {
            String strEstoque = txtEstoque.getText() != null ? txtEstoque.getText().trim() : "";
            estoque = Integer.parseInt(strEstoque);
        } catch (Exception e) {
            throw new IllegalArgumentException("Quantidade em estoque inválida! Digite um número inteiro.");
        }

        // Categoria
        int catId;
        try {
            String strCatId = txtCategoriaId.getText() != null ? txtCategoriaId.getText().trim() : "";
            catId = Integer.parseInt(strCatId);
        } catch (Exception e) {
            throw new IllegalArgumentException("ID da Categoria inválido! Digite um número inteiro positivo.");
        }

        String catNome = txtCategoriaNome.getText() != null ? txtCategoriaNome.getText().trim() : "";
        String catSetor = txtCategoriaSetor.getText() != null ? txtCategoriaSetor.getText().trim() : "";
        String catDescricao = txtCategoriaDescricao.getText() != null ? txtCategoriaDescricao.getText().trim() : "";

        Categoria categoria = new Categoria(catId, catNome, catDescricao, catSetor);
        return new Produto(codigo, nome, descricao, fabricacao, validade, precoCompra, precoVenda, estoque, categoria);
    }

    // =========================================================================
    // EVENTOS DO FORMULÁRIO (CADASTRAR, ATUALIZAR, EXCLUIR, CONSULTAR, LIMPAR)
    // =========================================================================

    @FXML
    void onBtnCadastrarAction(ActionEvent event) {
        try {
            Produto p = extrairProdutoDoFormulario();
            produtoService.cadastrar(p);
            atualizarTabela();
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!",
                    "O produto " + p.getNome() + " (" + p.getCodigo() + ") foi salvo com sucesso.");
            onBtnLimparAction(null);
        } catch (IllegalArgumentException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação de Dados", "Dados inválidos", e.getMessage());
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao cadastrar produto", e.getMessage());
        }
    }

    @FXML
    void onBtnAtualizarAction(ActionEvent event) {
        try {
            Produto p = extrairProdutoDoFormulario();
            produtoService.atualizar(p);
            atualizarTabela();
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!",
                    "As alterações do produto " + p.getNome() + " foram salvas com sucesso.");
        } catch (IllegalArgumentException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação de Dados", "Dados inválidos", e.getMessage());
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar produto", e.getMessage());
        }
    }

    @FXML
    void onBtnExcluirAction(ActionEvent event) {
        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        if (codigo.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Nenhum produto selecionado",
                    "Por favor, selecione um produto na tabela ou informe o código para exclusão.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir este produto?");
        confirmacao.setContentText("Código do produto: " + codigo);

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean excluido = produtoService.excluir(codigo);
            if (excluido) {
                atualizarTabela();
                onBtnLimparAction(null);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído",
                        "O produto de código " + codigo + " foi removido com sucesso.");
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Erro", "Falha na exclusão",
                        "Produto não encontrado para o código: " + codigo);
            }
        }
    }

    @FXML
    void onBtnConsultarAction(ActionEvent event) {
        String codigo = txtConsultaCodigo.getText() != null ? txtConsultaCodigo.getText().trim() : "";
        if (codigo.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Consulta", "Campo vazio", "Digite o código do produto para consultar.");
            return;
        }

        Produto encontrado = produtoService.consultarPorCodigo(codigo);
        if (encontrado != null) {
            preencherFormulario(encontrado);
            tableViewProdutos.getSelectionModel().select(encontrado);
            tableViewProdutos.scrollTo(encontrado);
            lblStatusPesquisa.setText("Produto encontrado: " + encontrado.getNome());
        } else {
            lblStatusPesquisa.setText("");
            exibirAlerta(Alert.AlertType.INFORMATION, "Consulta de Produto", "Não encontrado",
                    "Nenhum produto encontrado com o código informado: " + codigo);
        }
    }

    @FXML
    void onBtnListarTodosAction(ActionEvent event) {
        txtConsultaCodigo.clear();
        lblStatusPesquisa.setText("");
        atualizarTabela();
    }

    @FXML
    void onBtnLimparAction(ActionEvent event) {
        txtCodigo.clear();
        txtNome.clear();
        txtDescricao.clear();
        dpFabricacao.setValue(null);
        dpValidade.setValue(null);
        txtPrecoCompra.clear();
        txtPrecoVenda.clear();
        txtEstoque.clear();

        txtCategoriaId.clear();
        txtCategoriaNome.clear();
        txtCategoriaSetor.clear();
        txtCategoriaDescricao.clear();

        tableViewProdutos.getSelectionModel().clearSelection();
    }

    // =========================================================================
    // EVENTOS DE RELATÓRIOS (STREAM API: filter, map, reduce, groupingBy)
    // =========================================================================

    @FXML
    void onBtnRelatorioVencimentoAction(ActionEvent event) {
        List<Produto> lista = produtoService.filtrarProximosVencimento(60);
        LocalDate hoje = LocalDate.now();

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================================\n");
        sb.append(" RELATÓRIO: PRODUTOS PRÓXIMOS AO VENCIMENTO (60 DIAS)\n");
        sb.append(" Data base: ").append(hoje.format(formatadorData)).append("\n");
        sb.append(" Total de produtos: ").append(lista.size()).append("\n");
        sb.append("=========================================================================================\n\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum produto com vencimento previsto para os próximos 60 dias.\n");
        } else {
            sb.append(String.format("%-10s | %-24s | %-16s | %-12s | %-10s | %s\n",
                    "CÓDIGO", "NOME", "CATEGORIA", "VALIDADE", "DIAS REST.", "ESTOQUE"));
            sb.append("-----------------------------------------------------------------------------------------\n");
            for (Produto p : lista) {
                long diasRestantes = ChronoUnit.DAYS.between(hoje, p.getDataValidade());
                sb.append(String.format("%-10s | %-24s | %-16s | %-12s | %-10d | %d un.\n",
                        p.getCodigo(),
                        resumirTexto(p.getNome(), 24),
                        resumirTexto(p.getCategoriaNome(), 16),
                        p.getDataValidadeFormatada(),
                        diasRestantes,
                        p.getQuantidadeEstoque()));
            }
        }

        txtAreaRelatorio.setText(sb.toString());
    }

    @FXML
    void onBtnRelatorioEstoqueBaixoAction(ActionEvent event) {
        List<Produto> lista = produtoService.filtrarEstoqueBaixo(10);

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================================\n");
        sb.append(" RELATÓRIO: PRODUTOS COM ESTOQUE BAIXO (MENOS DE 10 UNIDADES)\n");
        sb.append(" Total de itens para reposição: ").append(lista.size()).append("\n");
        sb.append("=========================================================================================\n\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum produto com estoque abaixo de 10 unidades. O estoque está normal!\n");
        } else {
            sb.append(String.format("%-10s | %-24s | %-16s | %-12s | %-14s | %s\n",
                    "CÓDIGO", "NOME", "CATEGORIA", "SETOR", "PREÇO VENDA", "ESTOQUE"));
            sb.append("-----------------------------------------------------------------------------------------\n");
            for (Produto p : lista) {
                sb.append(String.format("%-10s | %-24s | %-16s | %-12s | %-14s | %d un.\n",
                        p.getCodigo(),
                        resumirTexto(p.getNome(), 24),
                        resumirTexto(p.getCategoriaNome(), 16),
                        resumirTexto(p.getCategoriaSetor(), 12),
                        formatadorMoeda.format(p.getPrecoVenda()),
                        p.getQuantidadeEstoque()));
            }
        }

        txtAreaRelatorio.setText(sb.toString());
    }

    @FXML
    void onBtnRelatorioMargemAction(ActionEvent event) {
        Map<String, Double> margens = produtoService.calcularMargemLucroMediaPorCategoria();

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================================\n");
        sb.append(" RELATÓRIO: MARGEM DE LUCRO MÉDIA POR CATEGORIA\n");
        sb.append(" Total de categorias: ").append(margens.size()).append("\n");
        sb.append("=========================================================================================\n\n");

        if (margens.isEmpty()) {
            sb.append("Não há produtos cadastrados para calcular a margem.\n");
        } else {
            sb.append(String.format("%-30s | %-20s\n", "CATEGORIA", "MARGEM MÉDIA UNITÁRIA"));
            sb.append("-----------------------------------------------------------------------------------------\n");
            for (Map.Entry<String, Double> entry : margens.entrySet()) {
                sb.append(String.format("%-30s | %s\n",
                        entry.getKey(),
                        formatadorMoeda.format(entry.getValue())));
            }
        }

        txtAreaRelatorio.setText(sb.toString());
    }

    @FXML
    void onBtnRelatorioSetorAction(ActionEvent event) {
        Map<String, List<Produto>> produtosPorSetor = produtoService.listarProdutosAgrupadosPorSetor();

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================================\n");
        sb.append(" RELATÓRIO: PRODUTOS POR SETOR\n");
        sb.append(" Total de setores: ").append(produtosPorSetor.size()).append("\n");
        sb.append("=========================================================================================\n\n");

        if (produtosPorSetor.isEmpty()) {
            sb.append("Nenhum produto cadastrado no sistema.\n");
        } else {
            for (Map.Entry<String, List<Produto>> entry : produtosPorSetor.entrySet()) {
                String setor = entry.getKey().isEmpty() ? "Setor não informado" : entry.getKey();
                List<Produto> prods = entry.getValue();

                sb.append(">> SETOR: ").append(setor.toUpperCase())
                        .append(" (").append(prods.size()).append(" produto(s))\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
                for (Produto p : prods) {
                    sb.append(String.format("   [%s] %-25s | Cat: %-15s | Preço: %-10s | Estoque: %d un.\n",
                            p.getCodigo(),
                            resumirTexto(p.getNome(), 25),
                            resumirTexto(p.getCategoriaNome(), 15),
                            formatadorMoeda.format(p.getPrecoVenda()),
                            p.getQuantidadeEstoque()));
                }
                sb.append("\n");
            }
        }

        txtAreaRelatorio.setText(sb.toString());
    }

    @FXML
    void onBtnRelatorioLucroTotalAction(ActionEvent event) {
        BigDecimal valorTotal = produtoService.calcularValorTotalEstoque();
        BigDecimal lucroPotencial = produtoService.calcularLucroTotalPotencial();
        int totalProdutos = produtoService.listarTodos().size();

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================================\n");
        sb.append(" RELATÓRIO: RESUMO FINANCEIRO DO ESTOQUE\n");
        sb.append("=========================================================================================\n\n");

        sb.append("• Quantidade de produtos cadastrados:       ").append(totalProdutos).append("\n");
        sb.append("• Valor total em estoque (preço de venda):  ").append(formatadorMoeda.format(valorTotal)).append("\n");
        sb.append("• Lucro bruto potencial:                    ").append(formatadorMoeda.format(lucroPotencial)).append("\n");

        txtAreaRelatorio.setText(sb.toString());
    }

    @FXML
    void onBtnLimparRelatorioAction(ActionEvent event) {
        txtAreaRelatorio.clear();
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String cabecalho, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }

    private String resumirTexto(String texto, int tamanhoMaximo) {
        if (texto == null) return "";
        if (texto.length() <= tamanhoMaximo) return texto;
        return texto.substring(0, tamanhoMaximo - 3) + "...";
    }
}
