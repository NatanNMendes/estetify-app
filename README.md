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
[//]: # (custom layouts)
│   │   ├── custom_background.xml
│   │   ├── custom_background_button.xml
│   │   ├── custom_background_button_black.xml
│   │   ├── custom_background_card.xml   
│   │   ├── custom_background_card_image.xml
│   │   ├── custom_background_category.xml
│   │   ├── custom_background_search_bar.xml
│   │   ├── custom_background_table_row.xml   
│   │   ├── custom_container_user.xml
│   │   ├── custom_rounded_button.xml
[//]: # (custom icon)       
│   │   ├── ic_arrow_left.xml
│   │   ├── ic_book.xml
│   │   ├── ic_buy.xml
│   │   ├── ic_calendar.xml   
│   │   ├── ic_check.xml
│   │   ├── ic_clipboard.xml
│   │   ├── ic_email.xml
│   │   ├── ic_google_logo.xml   
│   │   ├── ic_house.xml
│   │   ├── ic_launcher_background.xml
│   │   ├── ic_launcher_foreground.xml
│   │   ├── ic_location.xml
│   │   ├── ic_logo.xml
│   │   ├── ic_person.xml   
│   │   ├── ic_shop.xml
│   │   ├── ic_user.xml
│   │   ├── ic_visibility_off.xml
│   │   ├── ic_visibility_on.xml   
[//]: # (custom image)
│   │   └── image_start_screen.xml
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