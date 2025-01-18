<h1>Estetify</h1>
<i>By Natan Nunes Mendes (NatanNMendes)</i>

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/NatanNMendes)
[![Perfil DIO](https://img.shields.io/badge/-Meu%20Perfil%20na%20DIO-3333FF?style=for-the-badge&logo=gitbook&logoColor=white)](https://www.dio.me/users/natan_nunes_mendes_95684)
[![LinkedIn](https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/natan-nunes-mendes-progamador/)
[![WhatsApp](https://img.shields.io/badge/WhatsApp-25D366?style=for-the-badge&logo=whatsapp&logoColor=white)](https://wa.me/5575988055119)

<h3><strong>Tecnologias Utilizadas</strong></h3>

<div style="display: flex; align-items: center;">
  <a href="https://www.java.com" target="_blank" rel="noreferrer">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/>
  </a>

  <a href="https://www.java.com" target="_blank" rel="noreferrer">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/firebase/firebase-original.svg" alt="firebase" width="40" height="40"/>
  </a>

  <a href="https://developer.android.com/" target="_blank" rel="noreferrer">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/android/android-original.svg" alt="android" width="40" height="40"/>
  </a>

  <a href="https://developers.google.com/" target="_blank" rel="noreferrer">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/google/google-original.svg" alt="google" width="40" height="40"/>
  </a>
</div>

<h3><strong>Descrição Geral</strong></h3>

<p align="justify">
O <strong>Estetify</strong> é um aplicativo multiplataforma desenvolvido para facilitar o agendamento e o gerenciamento de serviços estéticos. Ele oferece uma experiência completa para usuários que buscam praticidade e acessibilidade nos cuidados com a beleza. Entre as principais funcionalidades, destacam-se o cadastro de usuários, a visualização de serviços disponíveis, a localização de profissionais próximos, e a integração com notificações e sistemas de pagamento. A proposta do Estetify é centralizar e otimizar o processo de contratação de serviços estéticos, promovendo comodidade tanto para clientes quanto para profissionais da área.
</p>
<p align="justify">
A estrutura do projeto foi cuidadosamente organizada para garantir uma manutenção eficiente e facilitar futuras expansões. O diretório principal <code>app/</code> contém todos os arquivos essenciais para o funcionamento do aplicativo. Dentro dele, o diretório <code>manifests/</code> abriga o arquivo <code>AndroidManifest.xml</code> , que é responsável por definir configurações fundamentais, como permissões, atividades e serviços utilizados pelo app. Esse arquivo é essencial para o correto funcionamento e integração do aplicativo com o sistema operacional.
</p>
<p align="justify">
No diretório <code>java/</code>, encontra-se o pacote principal <code>com.example.bancodedados</code> , que organiza o código-fonte em diferentes subdiretórios conforme suas responsabilidades. A pasta <code>models/</code> contém classes de modelos de dados, como  <code>Product.java</code>, que representa a estrutura de produtos cadastrados no sistema. A pasta <code>services/</code> concentra serviços de autenticação e integração, como o <code>FirebaseUserService.java</code>, responsável pela conexão com o Firebase, o <code>SignInGoogleService.java</code>, que permite login com Google, e os serviços de login e cadastro padrão (<code>SignInService.java</code> e <code>SignUpService.java</code>). Já a pasta <code>utils/</code> reúne classes utilitárias, como adaptadores para exibição de dados (<code>AdapterCard.java</code>, <code>AdapterTable.java</code>) e funcionalidades auxiliares de navegação (<code>Navigation.java</code>) e visibilidade de senha (<code>PasswordVisibility.java</code>).
</p>
<p align="justify">
Além disso, diversas atividades essenciais compõem a lógica de navegação do app, como <code>LoginActivity.java</code> para login, <code>SignUpActivity.java</code> para cadastro de novos usuários, <code>MainActivity.java</code> como tela principal, e <code>PerfilActivity.java</code> para gerenciar o perfil do usuário. Também há telas específicas para visualização de salões (<code>BusinessActivity.java</code> e <code>SalonPage.java</code>) e para o histórico de serviços (<code>HistoryActivity.java</code>).
</p>
<p align="justify">
A parte visual do aplicativo é organizada no diretório <code>res/</code>, que contém recursos gráficos e de layout. A pasta <code>drawable/</code> guarda layouts personalizados, como fundos e botões (<code>custom_background.xml</code>, <code>custom_background_button.xml</code>), além de ícones personalizados (<code>ic_calendar.xml</code>, <code>ic_google_logo.xml</code>). A pasta <code>layout/</code> centraliza os arquivos de interface de usuário, incluindo os layouts das telas principais (<code>activity_login.xml</code>, <code>activity_main.xml</code>, <code>activity_perfil.xml</code>) e componentes reutilizáveis, como <code>item_card.xml</code> para exibição de serviços e <code>item_table_row.xml</code> para organização de dados em tabelas. A pasta <code>menu/</code> contém a configuração do menu de navegação inferior (<code>bottom_navigation_menu.xml</code>), enquanto a pasta <code>values/</code> armazena definições de cores (<code>colors.xml</code>) e textos (<code>strings.xml</code>), permitindo fácil personalização e internacionalização.
</p>

<h3><strong>Estrutura de Diretórios</strong></h3>

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