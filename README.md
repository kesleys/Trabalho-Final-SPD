# Agendamento de vacinas

Trabalho final — Software para Persistência de Dados — 2026/2.
Kesley Soares da Silva. 

## Executar a base

Pré-requisitos: JDK 17+, Maven 3.9+ e Tomcat 10.1.

Siga primeiro [COMECE-AQUI.md](COMECE-AQUI.md), que detalha configuração do banco,
compilação e atualização do Tomcat.

1. Copiar `config/vacinacao.properties.example` para a pasta conf do Tomcat como
   `vacinacao.properties` e preencher suas credenciais localmente.
2. Na pasta deste README, executar `mvn clean package` (inclui todos os 39 testes).
3. Com o Tomcat parado, copiar `target/vacinacao.war` para sua pasta webapps.
4. Iniciar/reiniciar o Tomcat e abrir `http://localhost:8080/vacinacao/`.
5. Acessar os cadastros de vacinas, alergias e usuários e seguir os testes manuais do guia.

O modo de desenvolvimento `hibernate.hbm2ddl.auto=update` cria/atualiza as tabelas
sem recriá-las ao reiniciar. O pool simples do Hibernate é apropriado para
a demonstração local; uma implantação de produção exigiria configuração própria.

## Requisitos e critérios de aceite

| ID | Implementação | Critério de aceite |
|---|---|---|
| R01 | Vacinas | Incluir, excluir e consultar todos os registros; doses inteiras obrigatórias e maiores que zero. |
| R02 | Alergias | Incluir, excluir e consultar todos os registros. |
| R03 | Usuários | Incluir, excluir e consultar; permitir zero ou várias alergias; sexo em seleção e UF em seleção com as 27 siglas. |
| R04 | Agendas | Incluir, excluir e consultar com usuário, vacina, dose, data, hora, situação, data da situação e observações, respeitando o MER. |
| R05 | Parcelamento em doses | Ao agendar, criar exatamente a quantidade de doses da vacina em uma transação. Se alguma gravação falhar, reverter todas. |
| R06 | Calendário | Dose n na data inicial mais (n-1) vezes o intervalo, na unidade configurada; ajustar para o último dia válido em meses/anos quando necessário. |
| R07 | Situação | Usar ENUM Java: AGENDADO, REALIZADO, CANCELADO. Agendado tem data_situacao nula. |
| R08 | Baixa | Marcar uma agenda como realizada ou cancelada e registrar a data corrente da aplicação. |
| R09 | Filtros | Consultar agendas realizadas e canceladas. |
| R10 | Hoje | Filtrar pela data corrente e ordenar: agendadas, realizadas, canceladas. |
| R11 | Histórico individual | Exibir agendamentos de um usuário com data, situação, vacina e dose. |
| R12 | Extra proposta | Exportar os agendamentos de um usuário em CSV. Documentar em PDF separado; sem alteração de estrutura do banco prevista. |
