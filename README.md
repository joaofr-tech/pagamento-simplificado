# Pagamento Simplificado

API REST desenvolvida em Java com Spring Boot para simular um sistema de pagamentos simplificado entre usuários.

O projeto implementa regras básicas de transferência, cadastro de usuários, validação de saldo e autorização externa de transações.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## Funcionalidades

- Cadastro de usuários
- Listagem de usuários
- Criação de transações
- Validação de saldo antes da transferência
- Bloqueio de transferência para usuários do tipo lojista
- Autorização externa antes de concluir a transação
- Persistência em banco de dados H2 em memória

## Regras de Negócio

- Usuários comuns podem enviar dinheiro.
- Usuários lojistas não podem enviar dinheiro, apenas receber.
- Uma transação só pode ser realizada se o usuário pagador possuir saldo suficiente.
- Antes de finalizar a transação, o sistema consulta um serviço externo de autorização.
- Após uma transação autorizada, o saldo do pagador é debitado e o saldo do recebedor é creditado.

## Estrutura Principal

```txt
src/main/java/com/pagamento/simplificado
├── controller
│   └── UserController.java
├── domain
│   ├── Transaction.java
│   ├── User.java
│   └── UserType.java
├── dtos
│   ├── NotificationDTO.java
│   ├── TransactionDTO.java
│   └── UserDTO.java
├── infra
│   └── AppConfig.java
├── repositories
│   ├── TransactionRepository.java
│   └── UserRepository.java
└── service
    ├── NotificationService.java
    ├── TransactionService.java
    └── UserService.java
