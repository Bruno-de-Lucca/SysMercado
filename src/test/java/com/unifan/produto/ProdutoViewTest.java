package com.unifan.produto;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoViewTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit already initialized
        }
    }

    @Test
    void testCarregamentoFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifan/produto/view/ProdutoView.fxml"));
        Parent root = loader.load();
        assertNotNull(root, "O arquivo FXML deve carregar sem erros!");
        assertNotNull(loader.getController(), "O controlador ProdutoController deve ser instanciado pelo FXML!");
    }
}
