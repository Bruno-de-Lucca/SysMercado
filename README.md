# SysMercado - Sistema de Gestão de Produtos e Estoque

Aplicação desktop em **Java** com interface gráfica **JavaFX**, arquitetura em camadas (MVC) e processamento analítico declarativo utilizando a **Java Stream API**. O sistema gerencia o cadastro, consulta, atualização, exclusão e controle de validade e reposição de produtos com categorização mercadológica e persistência contínua em arquivo **CSV** (UTF-8).

---

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 21+ (Java SE LTS)
* **Interface Gráfica:** JavaFX 21 (javafx-controls, javafx-fxml, javafx-base)
* **Design Visual:** Scene Builder (compatibilidade FXML total)
* **Gerenciador de Dependências e Build:** Apache Maven (com Maven Wrapper `mvnw` incluso)
* **Testes Automatizados:** JUnit 5 (JUnit Jupiter)
* **Persistência de Dados:** Arquivo semiestruturado textual CSV (`produtos.csv`) com delimitador ponto-e-vírgula (`;`) e codificação `UTF-8`

---

## 📂 Estrutura do Repositório

```
SysMercado/
├── .gitignore                       # Configuração para ignorar binários e arquivos de IDE
├── pom.xml                          # Configuração de build e dependências do Maven
├── mvnw / mvnw.cmd / .mvn/          # Maven Wrapper (execução sem requerer Maven pré-instalado)
├── produtos.csv                     # Base de persistência de dados do sistema
├── README.md                        # Documentação do projeto
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java     # Configuração modular do JavaFX
    │   │   └── com/unifan/produto/
    │   │       ├── Main.java        # Ponto de entrada (Entry Point) puro para execução direta
    │   │       ├── ProdutoApp.java  # Classe Application do ciclo de vida JavaFX
    │   │       ├── model/
    │   │       │   ├── Categoria.java    # Entidade de Categoria (id, nome, desc, setor)
    │   │       │   └── Produto.java      # Entidade de Produto (composição com Categoria)
    │   │       ├── service/
    │   │       │   └── ProdutoService.java # Regras de negócio, validações, CSV e Streams
    │   │       └── controller/
    │   │           └── ProdutoController.java # Controlador da UI, eventos e bindings
    │   └── resources/
    │       └── com/unifan/produto/view/
    │           └── ProdutoView.fxml # Interface declarativa editável no Scene Builder
    └── test/
        └── java/
            └── com/unifan/produto/
                ├── ProdutoServiceTest.java # Testes unitários das regras de negócio e Streams
                └── ProdutoViewTest.java    # Teste de integridade de carga do FXML
```

---

## ⚡ Como Executar o Projeto

O projeto foi configurado com uma classe `Main.java` desacoplada de `Application`, permitindo que seja executado diretamente pelo método `main` em qualquer IDE, dispensando arquivos de script `.bat` ou executáveis `.exe`.

### Opção 1: Executar Diretamente pela IDE (Recomendado)
1. Abra a pasta `SysMercado` na sua IDE de preferência (**VS Code**, **IntelliJ IDEA**, **Eclipse** ou **NetBeans**).
2. Aguarde a IDE indexar o projeto Maven.
3. Navegue até o arquivo:  
   `src/main/java/com/unifan/produto/Main.java`
4. Execute o arquivo:
   * **No VS Code:** Clique em **Run** acima do método `main` ou pressione `F5`.
   * **No IntelliJ IDEA:** Clique no ícone verde de **Play** ao lado do método `main` e selecione *"Run 'Main.main()'"*.
   * **No Eclipse:** Clique com o botão direito no arquivo `Main.java` > **Run As** > **Java Application**.
   * **No NetBeans:** Clique com o botão direito no arquivo `Main.java` > **Run File**.

---

### Opção 2: Executar via Terminal (Maven Wrapper)

Caso prefira executar via linha de comando a partir da raiz do projeto:

* **No Windows (PowerShell / Prompt de Comando):**
  ```powershell
  .\mvnw.cmd javafx:run
  ```

* **No Linux / macOS:**
  ```bash
  ./mvnw javafx:run
  ```

---

## 🧪 Como Rodar os Testes Automatizados

O sistema conta com suíte de testes unitários desenvolvida em **JUnit 5**, validando invariantes de dados (código, datas, margem positiva), operações da Stream API e carga do FXML:

```powershell
.\mvnw.cmd test
```

---

## 🎨 Compatibilidade com Scene Builder

O arquivo de interface visual está localizado em:  
`src/main/resources/com/unifan/produto/view/ProdutoView.fxml`

Para abri-lo:
1. Abra o software **Scene Builder**.
2. Clique em **File** > **Open** e selecione o arquivo `ProdutoView.fxml`.
3. Todos os componentes (`TabPane`, `TableView`, botões e campos) serão exibidos visualmente com seus respectivos `fx:id` e eventos vinculados ao `ProdutoController`.

---

## 📋 Funcionalidades Principais

1. **Gestão Completa de Produtos (CRUD):**
   * Cadastro com validação estrita (código alfanumérico de 8 caracteres, validade posterior à fabricação, preço de venda maior que compra, estoque não-negativo).
   * Consulta pontual por código com seleção visual instantânea na tabela.
   * Edição com recarregamento dinâmico ao clicar em qualquer registro da tabela.
   * Exclusão protegida por diálogo de confirmação (`Confirmation Alert`).
2. **Relatórios Analíticos com Java Stream API:**
   * **Vencimento Crítico (60 dias):** Identifica produtos com vencimento próximo (`filter`, `sorted`).
   * **Estoque Baixo (< 10 unidades):** Alerta produtos com risco de ruptura de gôndola.
   * **Margem Média por Categoria:** Agrupamento e cálculo estatístico (`groupingBy`, `averagingDouble`).
   * **Produtos por Setor:** Classificação física da loja organizada via `TreeMap`.
   * **Resumo Financeiro:** Projeção patrimonial e lucro bruto potencial com `map` e `reduce`.
3. **Persistência Confiável:**
   * Gravação e leitura atômica em formato CSV delimitado por ponto-e-vírgula (`;`).
   * Codificação `UTF-8` explícita, mantendo caracteres da língua portuguesa preservados.
