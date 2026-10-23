package com.unifan.produto;

import com.unifan.produto.model.Categoria;
import com.unifan.produto.model.Produto;
import com.unifan.produto.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoServiceTest {

    private ProdutoService service;

    @BeforeEach
    void setUp() {
        service = new ProdutoService();
    }

    @Test
    void testCadastroEValidacao() {
        Categoria cat = new Categoria(10, "Padaria", "Pães e bolos", "Alimentos");
        Produto prod = new Produto("TEST0001", "Pão Francês 1kg", "Pão quentinho",
                LocalDate.now(), LocalDate.now().plusDays(2),
                new BigDecimal("8.00"), new BigDecimal("14.00"), 20, cat);

        // Se o produto já existir de um teste anterior, removemos
        service.excluir("TEST0001");

        service.cadastrar(prod);

        Produto consultado = service.consultarPorCodigo("TEST0001");
        assertNotNull(consultado);
        assertEquals("Pão Francês 1kg", consultado.getNome());
        assertEquals("TEST0001", consultado.getCodigo());

        // Limpeza
        service.excluir("TEST0001");
    }

    @Test
    void testValidacaoCodigoInvalido() {
        Categoria cat = new Categoria(1, "Teste", "Desc", "Setor");
        // Código com menos de 8 caracteres
        Produto prodInvalido = new Produto("CURTO", "Produto Inválido", "Desc",
                LocalDate.now(), LocalDate.now().plusDays(10),
                new BigDecimal("5.00"), new BigDecimal("10.00"), 5, cat);

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(prodInvalido));
    }

    @Test
    void testValidacaoPrecoInvalido() {
        Categoria cat = new Categoria(1, "Teste", "Desc", "Setor");
        // Preço de venda menor que preço de compra
        Produto prodInvalido = new Produto("PROD9999", "Produto Preço Errado", "Desc",
                LocalDate.now(), LocalDate.now().plusDays(10),
                new BigDecimal("20.00"), new BigDecimal("15.00"), 5, cat);

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(prodInvalido));
    }

    @Test
    void testRelatoriosStream() {
        List<Produto> vencimento60 = service.filtrarProximosVencimento(60);
        assertNotNull(vencimento60);

        List<Produto> estoqueBaixo = service.filtrarEstoqueBaixo(10);
        assertNotNull(estoqueBaixo);
        for (Produto p : estoqueBaixo) {
            assertTrue(p.getQuantidadeEstoque() < 10);
        }

        Map<String, Double> margens = service.calcularMargemLucroMediaPorCategoria();
        assertNotNull(margens);

        Map<String, List<Produto>> setores = service.listarProdutosAgrupadosPorSetor();
        assertNotNull(setores);

        BigDecimal lucroTotal = service.calcularLucroTotalPotencial();
        assertNotNull(lucroTotal);
        assertTrue(lucroTotal.compareTo(BigDecimal.ZERO) >= 0);
    }
}
