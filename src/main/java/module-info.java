module com.unifan.produto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    opens com.unifan.produto to javafx.fxml, javafx.graphics;
    opens com.unifan.produto.controller to javafx.fxml;
    opens com.unifan.produto.model to javafx.base, javafx.fxml;
    opens com.unifan.produto.view to javafx.fxml;

    exports com.unifan.produto;
    exports com.unifan.produto.controller;
    exports com.unifan.produto.model;
    exports com.unifan.produto.service;
}
