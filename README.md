# SecureHub

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%23336791.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/docker%20compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
[![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)

SecureHub é um sistema de gerenciamento de usuários e autenticação distribuído, construído com arquitetura de microserviços usando Spring Boot, Kafka e PostgreSQL. O projeto implementa funcionalidades completas de autenticação, autorização, gestão de usuários e envio de emails.

## Resumo do Projeto

O SecureHub é composto por dois microserviços principais:

1. **Auth Service**: Centro de autenticação responsável por gerenciar usuários, autenticação JWT, ativação de contas e recuperação de senhas.
2. **Mail Sender Service**: Serviço responsável pelo envio de emails transacionais via templates Thymeleaf.

O sistema utiliza comunicação assíncrona entre serviços através do Apache Kafka para garantir alta disponibilidade e escalabilidade.

## Arquitetura

### Visão Geral
```
┌─────────────────┐    Kafka    ┌─────────────────┐
│   Auth Service  │ ──────────► │ Mail Sender     │
│                 │             │ Service         │
│ - Autenticação  │             │ - Envio Emails  │
│ - Usuários      │             │ - Templates     │
│ - JWT Tokens    │             │                 │
└─────────────────┘             └─────────────────┘
         │                               │
         ▼                               ▼
┌─────────────────┐             ┌─────────────────┐
│   PostgreSQL    │             │   SMTP Server   │
│   Database      │             │   (Mailtrap)    │
└─────────────────┘             └─────────────────┘
```

### Padrões Arquiteturais

- **Hexagonal Architecture (Ports & Adapters)**: Separação clara entre domínio, aplicação e infraestrutura
- **Domain-Driven Design (DDD)**: Organização do código baseada no domínio de negócio
- **CQRS Pattern**: Separação de comandos e consultas
- **Event-Driven Architecture**: Comunicação assíncrona via eventos Kafka
- **Clean Architecture**: Independência de frameworks e tecnologias externas

## Principais Funcionalidades

### Auth Service
- ✅ **Cadastro de Usuários**: Criação de novos usuários com validação
- ✅ **Autenticação JWT**: Login seguro com tokens JWT
- ✅ **Ativação de Conta**: Sistema de códigos de ativação por email
- ✅ **Recuperação de Senha**: Reset de senha via token temporal
- ✅ **Autorização**: Controle de acesso baseado em roles
- ✅ **Segurança**: Criptografia de senhas com BCrypt
- ✅ **Correlação de Logs**: Rastreamento de requisições via correlation ID

### Mail Sender Service
- ✅ **Envio de Emails**: Processamento assíncrono de emails
- ✅ **Templates HTML**: Suporte a templates Thymeleaf responsivos
- ✅ **Retry Logic**: Reprocessamento automático em caso de falha
- ✅ **Dead Letter Queue**: Tratamento de mensagens não processadas
- ✅ **Monitoramento**: Logs detalhados para auditoria

## Tecnologias Usadas

### Backend
- **Java 25**: Linguagem principal
- **Spring Boot 3.5.7+**: Framework principal
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **Spring Kafka**: Mensageria assíncrona
- **JWT (Auth0)**: Tokens de autenticação

### Banco de Dados
- **PostgreSQL 16**: Banco principal
- **Flyway**: Migrations (implícito via Spring Boot)

### Mensageria
- **Apache Kafka 7.4.4**: Message broker
- **Zookeeper**: Coordenação do Kafka

### Email
- **JavaMailSender**: Envio de emails
- **Thymeleaf**: Engine de templates
- **Mailtrap**: SMTP para desenvolvimento

### Ferramentas
- **Maven**: Gerenciamento de dependências
- **Docker Compose**: Orquestração de containers
- **Lombok**: Redução de boilerplate
- **SLF4J + Logback**: Sistema de logs

## Pré-requisitos

- **Java 25** ou superior
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git**

## Como Rodar

### 1. Clonar o Repositório
```bash
git clone git@github.com:julioceno/securehub.git
cd securehub
```

### 2. Subir Infraestrutura
```bash
# Inicia PostgreSQL, Kafka e Zookeeper
docker-compose up -d
```

### 3. Configurar Variáveis de Ambiente
Crie um arquivo `.env` ou configure as seguintes variáveis:

```bash
# Mail Sender Service
export MAIL_HOST=sandbox.smtp.mailtrap.io
export MAIL_PORT=2525
export MAIL_USERNAME=your_username
export MAIL_PASSWORD=your_password
```

### 4. Executar os Serviços

**Auth Service:**
```bash
cd auth
./mvnw spring-boot:run
```

**Mail Sender Service:**
```bash
cd mailsender
./mvnw spring-boot:run
```

### 5. Verificar Funcionamento
```bash
# Health check do Auth Service
curl http://localhost:8080/v1/ping

# Os logs devem mostrar as aplicações iniciadas
```

### Testes Manuais via API

**1. Criar Usuário:**
```bash
curl -X POST http://localhost:8080/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**2. Ativar Usuário:**
```bash
curl -X POST http://localhost:8080/v1/users/enable \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "code": "123456"
  }'
```

**3. Fazer Login:**
```bash
curl -X POST http://localhost:8080/v1/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## Estrutura de Pastas

```
securehub/
├── docker-compose.yml          # Orquestração de containers
├── README.md                   # Documentação
│
├── auth/                       # Microserviço de Autenticação
│   ├── pom.xml                 # Dependências Maven
│   └── src/
│       ├── main/java/com/securehub/auth/
│       │   ├── AuthApplication.java                    # Main class
│       │   ├── adapters/                               # Camada de adaptadores
│       │   │   ├── in/                                 # Adaptadores de entrada
│       │   │   │   ├── controller/                     # REST Controllers
│       │   │   │   │   ├── AuthController.java        # Login/Logout
│       │   │   │   │   ├── UsersController.java       # CRUD Usuários  
│       │   │   │   │   └── PingController.java        # Health check
│       │   │   │   ├── dto/                           # DTOs de entrada
│       │   │   │   └── filter/                        # Filtros HTTP
│       │   │   │       └── JwtAuthenticationFilter.java # Filtro JWT
│       │   │   └── out/                                # Adaptadores de saída
│       │   │       ├── entities/                       # Entidades JPA
│       │   │       ├── repositories/                   # Implementação repositórios
│       │   │       └── kafka/                         # Produtores Kafka
│       │   ├── application/                            # Camada de aplicação
│       │   │   ├── dto/                               # DTOs internos
│       │   │   ├── mapper/                            # Mapeadores
│       │   │   ├── port/                              # Interfaces/Ports
│       │   │   │   ├── in/                            # Use cases
│       │   │   │   └── out/                           # Portas de saída
│       │   │   ├── service/                           # Implementação use cases
│       │   │   │   ├── auth/                          # Serviços de autenticação
│       │   │   │   └── user/                          # Serviços de usuário
│       │   │   ├── usecases/                          # Interfaces de casos de uso
│       │   │   └── util/                              # Utilitários
│       │   ├── domain/                                 # Camada de domínio
│       │   │   ├── activationCode/                    # Domínio códigos ativação
│       │   │   ├── email/                             # Domínio emails
│       │   │   ├── passwordResetToken/                # Domínio reset senha
│       │   │   └── user/                              # Domínio usuários
│       │   └── infrastructure/                         # Camada de infraestrutura
│       │       ├── config/                            # Configurações
│       │       └── security/                          # Configuração segurança
│       ├── resources/
│       │   ├── application.yaml            # Configurações da aplicação
│       │   └── db/migration/              # Scripts de migração
│       └── test/                          # Testes unitários e integração
│
└── mailsender/                    # Microserviço de Email
    ├── pom.xml                    # Dependências Maven
    └── src/
        ├── main/java/com/securehub/mailsender/
        │   ├── MailsenderApplication.java              # Main class
        │   ├── adapter/in/                             # Adaptadores de entrada
        │   │   └── EmailConsumer.java                  # Consumer Kafka
        │   ├── application/                            # Camada de aplicação
        │   │   ├── port/out/                          # Portas de saída
        │   │   ├── service/                           # Implementação serviços
        │   │   └── usecases/                          # Casos de uso
        │   ├── domain/                                 # Camada de domínio
        │   │   └── EmailMessage.java                  # Entidade email
        │   └── infrastructure/                         # Infraestrutura
        │       ├── config/                            # Configurações
        │       └── mail/                              # Implementação email
        ├── resources/
        │   ├── application.yaml               # Configurações
        │   └── templates/                     # Templates Thymeleaf
        │       └── account-activation.html    # Template ativação
        └── test/                             # Testes
```

## Endpoints da API

### Auth Service (Port 8080)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/ping` | Health check |
| POST | `/v1/users` | Criar usuário |
| POST | `/v1/users/enable` | Ativar conta |
| POST | `/v1/users/password/forgot` | Esqueci senha |
| POST | `/v1/users/password/reset` | Reset senha |
| POST | `/v1/auth` | Login |
| GET | `/v1/auth/me` | Perfil usuário |

### Configurações

#### Auth Service (`auth/src/main/resources/application.yaml`)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth
  kafka:
    bootstrap-servers: localhost:29092

api:
  jwt:
    secret: jwt-secret
    expirationInSeconds: 10
    issuer: secure-hub
```

#### Mail Sender Service (`mailsender/src/main/resources/application.yaml`)
```yaml
spring:
  mail:
    host: sandbox.smtp.mailtrap.io
    
kafka:
  consumer:
    bootstrap-servers: localhost:29092
    group-id: mail-sender-group
    email:
      topic: email-topic
```

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
