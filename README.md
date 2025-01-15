# Estetify
Aplicativo multiplataforma para agendamento e gerenciamento de serviços estéticos, com funcionalidades como cadastro de usuários, visualização de serviços, localização de profissionais próximos, e integração com notificações e pagamento. Desenvolvido para oferecer praticidade e acessibilidade no cuidado com a beleza.
Aqui está um exemplo de um arquivo `README.md` para mostrar a organização de pastas do seu projeto:

```
app/
│
├── mainifests/                
│   ├── AndroidManifest.xml
├── java/                
│   ├── java\com\example\bancodedados
│   │   ├── models/
│   │   │   ├── Product.java
│   │   ├── services/
│   │   │   ├── FirebaseUserService.java
│   │   │   ├── SignInGoogleService.java
│   │   │   ├── SignInService.java
│   │   │   ├── SignUpService.java
│   │   ├── utils/
│   │   │   ├── CardAdapter.java
│   │   │   ├── CardSalonAdapter.java
│   │   │   ├── Navigation.java
│   │   │   ├── PasswordVisibility.java
│   │   │   ├── TableAdapter.java
│   │   │   ├── TableHistoryAdapter.java
│   │   ├── BaseActivity.java
│   │   ├── BusinessActivity.java
│   │   ├── CredentialManagerHelper.java              
│   │   ├── HistoryActivity.java
│   │   ├── LoginActivity.java
│   │   ├── MainActivity.java  
│   │   ├── PerfilActivity.java
│   │   ├── SalonPage.java     
│   │   ├── SignInActivity.java     
│   │   ├── SignUpActivity.java
│   │   ├── StartScreen.java                           
│   │   └── UserPreferences.java           
├── res/                
│   ├── drawable/
│   │   ├── background.xml
│   │   ├── book.xml
│   │   ├── button_background_black.xml
│   │   ├── button_background_white.xml   
│   │   ├── card_background.xml
│   │   ├── card_image_background.xml
│   │   ├── category_background.xml
│   │   ├── clipboard.xml   
│   │   ├── container_user.xml
│   │   ├── house.xml       
│   │   ├── ic_arrow_back.png
│   │   ├── ic_buy.xml
│   │   ├── ic_calendar.xml   
│   │   ├── container_user.xml
│   │   ├── house.xml 
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── activity_user.xml
│   │   └── activity_book.xml     
│   ├── menu/
│   │   └── activity_book.xml
│   ├── mipmap/
│   │   └── activity_book.xml
│   ├── values/
│   │   └── activity_book.xml
│   ├── xml/
│   │   └── activity_book.xml     
```

## Descrição dos Diretórios e Arquivos

- **src/**: Contém os arquivos de código-fonte da aplicação, organizados por funcionalidades.
  - `main.c`: Ponto de entrada da aplicação.
  - `user.c`: Funções relacionadas a usuários, como login e criação de contas.
  - `book.c`: Funções relacionadas a livros, como cadastro e reserva.
  - `gui.c`: Funções responsáveis pela interface gráfica.
  - `utils.c`: Funções auxiliares, como manipulação de arquivos e validações.

- **include/**: Contém os arquivos de cabeçalho para as funções e estruturas.
  - `user.h`: Definições relacionadas a usuários.
  - `book.h`: Definições relacionadas a livros.
  - `gui.h`: Definições para a interface gráfica.
  - `utils.h`: Funções auxiliares, como leitura/escrita de arquivos.

- **assets/**: Armazena recursos estáticos como imagens e arquivos de estilo.
  - `logo.png`: O logo da aplicação.
  - `style.css`: Arquivo CSS para estilizar as janelas GTK.

- **data/**: Contém arquivos de dados persistentes, como `users.txt` e `books.txt`, para armazenar informações dos usuários e livros.

- **build/**: Diretório criado durante o processo de build, onde os arquivos compilados são armazenados.

- **Makefile**: Arquivo de automação para compilar e gerar a aplicação a partir do código-fonte.

## Compilação e Execução

1. Para compilar o projeto, use o comando:
   ```bash
   make
   ```

2. Para executar a aplicação após a compilação:
   ```bash
   ./app
   ```

Este projeto usa o GTK para a interface gráfica e foi desenvolvido para o sistema operacional Linux. A compilação pode precisar de ajustes em sistemas Windows ou macOS.

## Contribuições

Contribuições são bem-vindas! Se você tiver sugestões ou melhorias, sinta-se à vontade para abrir uma **issue** ou **pull request**.

```

Esse exemplo de `README.md` organiza os arquivos do projeto em uma estrutura lógica e descreve brevemente cada diretório e arquivo, facilitando o entendimento do projeto e como ele está estruturado.