
## Anotações e orientações para o desenvolvimento do backend.

### Parâmetros de paginação com a annotation `@RequestParam`:
```java
@RequestParam(value = "page", defaultValue = "0") Integer page,
@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
@RequestParam(value = "orderBy", defaultValue = "moment") String orderBy,
@RequestParam(value = "direction", defaultValue = "DESC") String direction
```
---

## ☑️ Config provisória para liberar todos endpoints e h2-console. (Sprint Boot 3.1+):
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
        // LIBERA iframes para vizualização de h2-console no navegador.
        .headers(headers -> headers.frameOptions(frame -> frame.disable())); 

		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}
```

---

### 🔒 Criptografar senha inserida por usuário:
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//Classe base para criptografar senhas
@Configuration
public class AppConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
```java
// --- No service do User ---
@Autowired
    private BCryptPasswordEncoder passwordEncoder;

@Transactional
    public UserDTO insert(UserIsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);

        //Criptografando password com BCryptPasswordEncoder da classe AppConfig
        entity.setPassword(passwordEncoder.encode(dto.getPassword())); 
        
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }
```

---

## ⚠️ Contraint Validator Customizado

- Criar a própria regra de validação, quando as anotações padrão como `@NotBlank`, `@Size`, `@Email`, etc., não são suficientes. Quando precisar validar regras específicas de negócio, como:

    - Um CPF/CNPJ válido

    - Um campo confirmPassword igual ao password

    - Verificar se um valor está dentro de uma lista do banco

    - Se uma data é futura em relação a outra

    - Verificar se um email já existe no banco (em DTO de cadastro)

**Código base:**
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented //incluir annotation no Javadoc
@Constraint(validatedBy = UserInsertValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserInsertValid {
	String message() default "Validation error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
```
**UserInsertValidator:**
```java
import java.util.ArrayList;
import java.util.List;

import com.truelanz.catalog.controllers.handlers.FieldMessage;
import com.truelanz.catalog.dto.UserInsertDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		
        //Importa a classe FieldMessage que trata as Validations Exceptions.
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getField())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
```
---

## ⚙️ .properties configs:
`application.properties`
```properties
# Configurações gerais, funciona em todos os perfis

spring.jpa.open-in-view=false

spring.profiles.active=test
```
`application-test.properties`
```properties
# Configurações específica, perfil de teste

# H2 Connection
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# H2 Client
spring.h2.console.enabled=true
# localhost:8080/h2-console
spring.h2.console.path=/h2-console

# Show SQL on console
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
`application-dev.properties`
```properties
# --- Configs para desenvolvimento com SGDBs ---
#spring.jpa.properties.jakarta.persistence.schema-generation.create-source=metadata
#spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
#spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=create.sql
#spring.jpa.properties.hibernate.hbm2ddl.delimiter=;

spring.datasource.url=jdbc:postgresql://localhost:5432/dscatalog
spring.datasource.username=postgres
spring.datasource.password=1234567

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.hibernate.ddl-auto=none
```
`application-prod.properties`
```properties
# Config para ambiente de produção
spring.datasource.url=${DATABASE_URL}

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```
---

## 🗝️ Adicionar Segurança com Spring security e OAuth2 

### 1. Modelo de domínio User-Role
![alt text](image.png)

### 2. Dependências
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-test</artifactId>
	<scope>test</scope>
</dependency>

<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-authorization-server</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### 3. Checklist Spring security
- GrantedAuthority
- UserDetails
- UserDetailsService
- UsernameNotFoundException

### 4. Configs de properties
```properties
security.client-id=${CLIENT_ID:myclientid}
security.client-secret=${CLIENT_SECRET:myclientsecret}

security.jwt.duration=${JWT_DURATION:86400}

cors.origins=${CORS_ORIGINS:http://localhost:3000,http://localhost:5173}
```

### 5. Checklist OAuth2 JWT password grant
- Base de referencia: [password-grant](https://github.com/truelanz/spring-boot-oauth2-jwt-demo/tree/main/password-grant)
- Implementação customizada do password grant
Authorization server
- Resource server

---

>## Consultas com `JOIN FETCH` (_evitando consultas lentas n:1_)

### Join Fetch n:1 (todos)
```java
//JPQL
@Query(value = "SELECT obj FROM Employee obj JOIN FETCH obj.department")
	List<Employee> searchAll();
```

### Join Fetch n:n (todos)
```java
//JPQL
//Seleciona todos os produtos (obj) e as categorias pelo atributo n:n categories, com obj.categories
@Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories")
	public List<Product> searchAll();
```

### Join Fetch n:1 (quando paginado usar countQuery)
```java
//Native Query
@Query(nativeQuery = true, value = """
        SELECT DISTINCT p.id, p.name 
        FROM tb_product p
        INNER JOIN tb_product_category pc ON p.id = pc.product_id
        WHERE (:categoryIds IS NULL OR pc.category_id IN :categoryIds)
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
        ORDER BY p.name
        """, countQuery = """
        SELECT COUNT(*) FROM (
            SELECT DISTINCT p.id, p.name 
            FROM tb_product p
            INNER JOIN tb_product_category pc ON p.id = pc.product_id
            WHERE (:categoryIds IS NULL OR pc.category_id IN :categoryIds)
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
        ) AS tb_result
    """)
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);
```
---

>## Casos de uso da aplicação (Consulta paginada de produtos)
1. [OUT] O **sistema** informa id e nome de **todas** categorias de produto
2. [IN] O **usuário** informa:
	- trecho do nome do produto (opcional)
	- categorias de produto desejadas (opcional)
	- número da página desejada
	- quantidade de itens por página
3. [OUT] O **sistema** informa uma listagem paginada dos produtos com suas respectivas categorias, conforme os critérios de consulta, ordenados por nome.

---

>## Criar perfil `application-dev.properties`
```properties
# -- Descomentar para gerar um script SQL com a base de dados --
#spring.jpa.properties.jakarta.persistence.schema-generation.create-source=metadata
#spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
#spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=create.sql
#spring.jpa.properties.hibernate.hbm2ddl.delimiter=;

spring.datasource.url=jdbc:postgresql://localhost:5433/dbName
spring.datasource.username=postgres
spring.datasource.password=serverPassword

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.hibernate.ddl-auto=none
```
### comandos SQL para excluir tables:
```SQL
SELECT 'drop table if exists ' || tablename || ' cascade;' 
FROM pg_tables
WHERE schemaname = 'public';
```
### Resetar sequencia de ID's no Postgres desde o último maior ID existente:
```SQL
-- Procurar pela tabela:
SELECT pg_get_serial_sequence('tb_user', 'id');

-- Resetar IDs
SELECT setval('tb_user_id_seq', (SELECT MAX(id) FROM tb_user));
```

### Adicionar maven dependency:
```xml
<dependency>
	<groupId>org.postgresql</groupId>
	<artifactId>postgresql</artifactId>
	<scope>runtime</scope>
</dependency>
```
---

>## Signup

- [IN] O usuário informa primeiro nome, sobrenome, email e senha
- [OUT] O sistema informa os erros de validação

- Informações complementares
	- Critérios de validação de usuário
	- Nome: campo requerido
	- Email: email válido
	- Senha: mínimo 8 caracteres

- **Preparando o projeto para envio de email**:

	- [criar senha de App na account Google](https://myaccount.google.com/u/3/apppasswords?continue=https://myaccount.google.com/u/3/security?rapt%3DAEjHL4NztdiDgTz1smmHC4lZl8xBpkDebB3yPIearT8hVHeK6bJK39qXSK7orVcYytLnQpzm42oE1ak6KhxGebsKbQUSyvz9OcOdzA8Zu4imAWcfYtAJZVQ%26authuser%3D3&pli=1&rapt=AEjHL4ONMFtjj_SrIHqyW4KZTR5rH5ywhRtcukiP72_6wMst8HQQ3Kop2vXMXXZvlZQp80oqlEN7Hy7qpt4e0uAgr4siJhUljk3KymgYskWCwVZOrU3tLmU) e salvar em um lugar seguro.
	- adicionar ao `application.properties`:
	```properties
	spring.mail.host=${EMAIL_HOST:smtp.gmail.com}
	spring.mail.port=${EMAIL_PORT:587}
	spring.mail.username=${EMAIL_USERNAME:test@gmail.com}
	spring.mail.password=${EMAIL_PASSWORD:123456}
	spring.mail.properties.mail.smtp.auth=true
	spring.mail.properties.mail.smtp.starttls.enable=true
	```
	- No VSCODE adicionar as variáveis de ambiente em `launch.json`:
	```json
	"env": {
				"EMAIL_USERNAME": "yourEmail@gmail.com",
				"EMAIL_PASSWORD": "google senha App",
				"DB_URL": "jdbc:postgresql://localhost:5432/projectName",
				"DB_USERNAME": "yourDbName",
				"DB_PASSWORD": "YourPassword" 
            }
	```
	- Adcionar `.vscode/` ou `.env` ao .gitignore para não vazar as informações confidenciais do projeto. 
	- Usar [Projeto ref. para envio Email](https://github.com/devsuperior/spring-boot-gmail)
	- Adicionar dependencia para envio de email no pom.xml:
	```xml
	 	<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
	```
	- Body para testar envio com endpoint:
	```json
	"to": "destinatario@gmail.com",
    "subject": "Aviso aos clientes",
    "body": "Prezado cliente,\n\nAcesse agora:\n\nhttps://devsuperior.com.br\n\nAbraços!"
	```

- **Recuperação de senha com Gmail**

	- Caso de uso recuperação de senha
	Cenário principal:
	1. [IN] O usuário informa seu email
	2. [OUT] O sistema informa o token de recuperação e a validade do mesmo
	3. [IN] O usuário informa o token de recuperação e a nova senha

	**Exceção 1.1: Email inválido**

		1.1.1. [OUT] O sistema informa que o email é inválido
		Exceção 1.2: Email não encontrado
		1.2.1. [OUT] O sistema informa que o email não foi encontrado
		Exceção 3.1: Token inválido
		3.1.1. [OUT] O sistema informa que o token é inválido
		Exceção 3.2: Erro de validação
		3.1.2. [OUT] O sistema informa que a senha é inválida
		Informações complementares

		Critérios de validação de senha: 
		Mínimo 8 caracteres

	- **Adicionar em `application.properties` Variáveis de ambiente para recuperação de senha:**
	```properties
	email.password-recover.token.minutes=${PASSWORD_RECOVER_TOKEN_MINUTES:30}
	email.password-recover.uri=${PASSWORD_RECOVER_URI:http://localhost:5173/recover-password/}

	```
	- **JPQL para encontrar token não expirado**:
	```java
	@Query("SELECT obj FROM PasswordRecover obj WHERE obj.token = :token AND obj.expiration > :now")
	List<PasswordRecover> searchValidTokens(String token, Instant now);
	```

- **Obter usuário logado**

```java
protected User authenticated() {
  try {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
    String username = jwtPrincipal.getClaim("username");
    return userRepository.findByEmail(username);
  }
  catch (Exception e) {
    throw new UsernameNotFoundException("Invalid user");
  }
}
```