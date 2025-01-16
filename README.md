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
│   │   │   └── Product.java
│   │   ├── services/
│   │   │   ├── FirebaseUserService.java
│   │   │   ├── SignInGoogleService.java
│   │   │   ├── SignInService.java
│   │   │   └── SignUpService.java
│   │   ├── utils/
[//]: # (adapters)
│   │   │   ├── AdapterCard.java
│   │   │   ├── AdapterCardSalon.java
│   │   │   ├── AdapterTable.java
│   │   │   ├── AdapterTableHistory.java
[//]: # (utilities)
│   │   │   ├── Navigation.java
│   │   │   └── PasswordVisibility.java
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
[//]: # (activitys)
│   │   ├── activity_base.xml
│   │   ├── activity_business.xml
│   │   ├── activity_history.xml
│   │   ├── activity_login.xml
│   │   ├── activity_main.xml
│   │   ├── activity_perfil.xml
│   │   ├── activity_salon_page.xml
│   │   ├── activity_sign_up.xml
│   │   ├── activity_start_screen.xml
[//]: # (items)
│   │   ├── item_card.xml
│   │   ├── item_card_salon.xml
│   │   ├── item_header.xml
│   │   ├── item_row.xml
│   │   ├── item_table_row.xml
│   │   └── item_table_row_buy.xml     
│   ├── menu/
│   │   └── bottom_navigation_menu.xml
│   ├── values/
│   │   ├── colors.xml
│   │   └── strings.xml   
```

### Descrição dos Diretórios e Arquivos

#### manifests/:
  - `AndroidManifest.xml`: Arquivo fundamental que define as configurações básicas do app, como permissões, atividades, e serviços.

#### java/:
- **com.example.bancodedados**
  - **models/**:
      - `Product.java`:  Modelo de dados representando produtos.
  - **services/**:
      - `FirebaseUserService.java`: Gerencia autenticação e interação com o Firebase.
      - `SignInGoogleService.java`: Implementa login com Google.
      - `SignInService.java`: Lida com o processo de login padrão.
      - `SignUpService.java`: Gerencia o cadastro de novos usuários.
  - **utils/**:
      - `AdapterCard.java`: Adapta dados para exibição em cartões.
      - `AdapterCardSalon.java`: Adapta dados de salões em cartões.
      - `AdapterTable.java`: Adapta dados para tabelas.
      - `AdapterTableHistory.java`: Adapta o histórico de ações em tabelas.
      - `Navigation.java`: Auxilia na navegação entre telas.
      - `PasswordVisibility.java`: Alterna a visibilidade da senha.
  - `BaseActivity.java`: Classe base da bottom navigation com funcionalidades comuns às demais atividades.
  - `BusinessActivity.java`: Tela para exibir e pesquisar salões.
  - `HistoryActivity.java`: Tela para exibir o histórico de compras do usuário separado por mês.
  - `LoginActivity.java`: Tela de login.
  - `MainActivity.java`: Tela principal do aplicativo.
  - `PerfilActivity.java`: Tela de perfil do usuário.
  - `SalonPage.java`: Tela de detalhes de salões.
  - `SignInActivity.java`: Tela de login.
  - `SignUpActivity.java`:Tela de cadastro de novos usuários.
  - `StartScreen.java`: Tela inicial de boas-vindas.
  - `CredentialManagerHelper.java`: Gerencia credenciais do usuário.
  - `UserPreferences.java`: Gerencia preferências e configurações do usuário.

#### res/:
  - **drawable/**:
    - `custom_background.xml`:  Modelo de dados representando produtos.
  - **layout/**:
    - `custom_background.xml`:  Modelo de dados representando produtos.
  - **menu/**:
    - `bottom_navigation_menu.xml`: Menu inferior de navegação
  - **values/**:
    - `colors.xml`: Paleta de cores do app.
    - `strings.xml`: Textos e mensagens do app.
    
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