# WebAuthn MFA Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** To integrate WebAuthn as a second-factor authentication method within the existing MFA framework.

**Architecture:** We will implement a `WebAuthnProvider` that plugs into the existing `MfaProvider` interface. This provider will use the `webauthn4j` library to handle the WebAuthn ceremony. Credential data will be stored in the `details` JSONB column of the `multifactors` table, with a clean separation between the domain and persistence layers.

**Tech Stack:** Java, Spring Boot, Gradle, `webauthn4j`.

---

### Task 1: Add Dependencies

**Files:**
- Modify: `core/build.gradle.kts`

- [ ] **Step 1: Add webauthn4j dependencies**

Add the following lines to the `dependencies` block in `core/build.gradle.kts`:

```kotlin
implementation("com.webauthn4j:webauthn4j-core:0.23.0.RELEASE")
implementation("com.webauthn4j:webauthn4j-device-reputation:0.23.0.RELEASE")
```

- [ ] **Step 2: Reload Gradle project**

Reload the Gradle project in your IDE to ensure the new dependencies are downloaded and available.

- [ ] **Step 3: Commit**

```bash
git add core/build.gradle.kts
git commit -m "feat(mfa): add webauthn4j dependencies"
```

### Task 2: (Revised) Domain and Persistence Layer

**Files:**
- Modify: `core/src/main/java/com/uit/se356/core/domain/vo/authentication/mfa/WebAuthMfaConfig.java`
- Modify: `core/src/main/java/com/uit/se356/core/infrastructure/persistence/mappers/authentication/MfaPersistenceMapper.java`

- [ ] **Step 1: Update `WebAuthMfaConfig` to be library-agnostic**

Replace the content of `core/src/main/java/com/uit/se356/core/domain/vo/authentication/mfa/WebAuthMfaConfig.java` to remove any `webauthn4j` imports. It will only contain data as primitive types.

```java
package com.uit.se356.core.domain.vo.authentication.mfa;

import lombok.Data;

@Data
public class WebAuthMfaConfig implements MfaConfig {
    private byte[] credentialId;
    private byte[] publicKeyCose;
    private long signCount;
}
```

- [ ] **Step 2: Update `MfaPersistenceMapper` to handle the new `WebAuthMfaConfig`**

Modify the `toDomain` method in `core/src/main/java/com/uit/se356/core/infrastructure/persistence/mappers/authentication/MfaPersistenceMapper.java` to correctly deserialize the `WebAuthMfaConfig`.

```java
// in MfaPersistenceMapper
// ...
          case WEBAUTHN -> objectMapper.readValue(entity.getDetails(), WebAuthMfaConfig.class);
// ...
```

The `toEntity` method should already be correctly serializing the `MfaConfig` object to a string.

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "refactor(mfa): separate webauthn domain from infrastructure"
```

### Task 3: (Revised) Implement the WebAuthnProvider

**Files:**
- Create: `core/src/main/java/com/uit/se356/core/infrastructure/provider/mfa/WebAuthnProvider.java`
- Test: `core/src/test/java/com/uit/se356/core/infrastructure/provider/mfa/WebAuthnProviderTest.java`

- [ ] **Step 1: Create the `WebAuthnProvider` class structure**

Create the `WebAuthnProvider` class, implementing the `MfaProvider` interface. Inject the necessary `webauthn4j` beans.

```java
package com.uit.se356.core.infrastructure.provider.mfa;

import com.uit.se356.core.application.authentication.port.out.MfaProvider;
import com.uit.se356.core.domain.vo.authentication.mfa.WebAuthMfaConfig;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.data.*;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.server.ServerProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebAuthnProvider implements MfaProvider {

    private final WebAuthnManager webAuthnManager;

    @Override
    public boolean supports(String method) {
        return "WEBAUTHN".equalsIgnoreCase(method);
    }

    // ... other methods to be implemented
}
```

- [ ] **Step 2: Implement `initiateMfaSetup`**

This method will generate the `PublicKeyCredentialCreationOptions` for the frontend.

```java
// Inside WebAuthnProvider

@Override
public MfaSetupResult initiateMfaSetup(UserId userId) {
    RelyingParty rp = new RelyingParty("your-relying-party-id", "Your App Name", new Origin("http://localhost:3000"));
    Challenge challenge = new DefaultChallenge();
    PublicKeyCredentialUserEntity userEntity = new PublicKeyCredentialUserEntity(userId.getValue().toString().getBytes(), userId.getValue().toString(), "display-name");

    PublicKeyCredentialParameters... pubKeyCredParams = // ... define your parameters

    PublicKeyCredentialCreationOptions creationOptions = new PublicKeyCredentialCreationOptions(
        rp,
        userEntity,
        challenge,
        pubKeyCredParams
        // ... other options
    );

    // This needs to be stored temporarily to be verified in the completeMfaSetup step
    // A cache (like Redis) or the session can be used for this.

    return new MfaSetupResult(null, creationOptions.toPropertyMap());
}
```

- [ ] **Step 3: Implement a method to complete the setup**

This method will validate the attestation response from the client and return a domain `WebAuthMfaConfig`.

```java
// Inside WebAuthnProvider

public WebAuthMfaConfig completeMfaSetup(byte[] attestationObject, byte[] clientDataJSON, UserId userId) {
    // Retrieve the server property from the temporary store (cache/session)

    RegistrationData registrationData = webAuthnManager.parse(new RegistrationRequest(attestationObject, clientDataJSON));
    RegistrationParameters registrationParameters = new RegistrationParameters(
        // server property from temporary store
        // ...
    );
    RegistrationResult registrationResult = webAuthnManager.validate(registrationData, registrationParameters);

    // Map from webauthn4j's objects to our domain object
    WebAuthMfaConfig config = new WebAuthMfaConfig();
    config.setCredentialId(registrationResult.getAttestedCredentialData().getCredentialId());
    config.setPublicKeyCose(registrationResult.getAttestedCredentialData().getCOSEKey().getValue());
    config.setSignCount(registrationResult.getAttestationObject().getAuthenticatorData().getSignCount());
    return config;
}
```

- [ ] **Step 4: Implement `challenge`**

This method generates the `PublicKeyCredentialRequestOptions` for the login challenge.

```java
// Inside WebAuthnProvider

public Map<String, String> challenge(Mfa mfa) {
     WebAuthMfaConfig config = (WebAuthMfaConfig) mfa.getConfig();
     Challenge challenge = new DefaultChallenge();

     PublicKeyCredentialRequestOptions requestOptions = new PublicKeyCredentialRequestOptions(
        challenge,
        60000L,
        "your-relying-party-id",
        List.of(new PublicKeyCredentialDescriptor(PublicKeyCredentialType.PUBLIC_KEY, config.getCredentialId(), null)),
        null
     );

    // Store the challenge temporarily for verification

    return requestOptions.toPropertyMap();
}
```

- [ ] **Step 5: Implement `verify`**

This method validates the assertion response from the client.

```java
// Inside WebAuthnProvider

public void verify(Mfa mfa, byte[] credentialId, byte[] authenticatorData, byte[] clientDataJSON, byte[] signature) {
    WebAuthMfaConfig config = (WebAuthMfaConfig) mfa.getConfig();

    AuthenticationData authenticationData = webAuthnManager.parse(new AuthenticationRequest(credentialId, authenticatorData, clientDataJSON, signature));
    AuthenticationParameters authenticationParameters = new AuthenticationParameters(
        // server property with challenge from temp store
        // ...
    );

    AuthenticationResult authenticationResult = webAuthnManager.validate(authenticationData, authenticationParameters);

    // Update the sign count in the MfaConfig and persist it
    config.setSignCount(authenticationResult.getAuthenticatorData().getSignCount());
}
```

- [ ] **Step 6: Write unit and integration tests**

Write tests for the `WebAuthnProvider`, mocking the `WebAuthnManager` for unit tests and using a real `WebAuthnManager` for integration tests.

- [ ] **Step 7: Commit**

```bash
git add .
git commit -m "feat(mfa): implement WebAuthnProvider"
```

### Task 4: Update Command Handlers

**Files:**
- Modify: `core/src/main/java/com/uit/se356/core/application/authentication/handler/mfa/InitiateMfaSetupHandler.java`
- Modify: `core/src/main/java/com/uit/se356/core/application/authentication/handler/mfa/CompleteSetupMfaHandler.java`
- Modify: `core/src/main/java/com/uit/se356/core/application/authentication/handler/mfa/ChallengeMfaHandler.java`
- Modify: `core/src/main/java/com/uit/se356/core/application/authentication/handler/mfa/VerifyMfaHandler.java`
- Test: Update corresponding test files.

- [ ] **Step 1: Update `InitiateMfaSetupHandler`**

This handler should already be delegating to the correct provider. No significant changes should be needed here, but review it to ensure it works correctly with the new `WebAuthnProvider`.

- [ ] **Step 2: Update `CompleteSetupMfaHandler`**

This handler will need to be updated to handle the `PublicKeyCredential` response from the client. It will call the `WebAuthnProvider.completeMfaSetup` method and persist the new `Mfa` entity with the `WebAuthMfaConfig`.

- [ ] **Step 3: Update `ChallengeMfaHandler`**

This handler will call the `WebAuthnProvider.challenge` method to generate the authentication challenge and return it to the client.

- [ ] **Step 4: Update `VerifyMfaHandler`**

This handler will take the `PublicKeyCredential` assertion from the client and call the `WebAuthnProvider.verify` method. If successful, it will complete the login process.

- [ ] **Step 5: Update tests**

Update the unit and integration tests for each of the modified handlers to cover the new WebAuthn flow.

- [ ] **Step 6: Commit**

```bash
git add .
git commit -m "feat(mfa): update command handlers for webauthn"
```

### Task 5: Presentation Layer (DTOs)

**Files:**
- Create: `core/src/main/java/com/uit/se356/core/presentation/request/mfa/CompleteWebAuthnSetupRequest.java`
- Create: `core/src/main/java/com/uit/se356/core/presentation/request/mfa/VerifyWebAuthnRequest.java`

- [ ] **Step 1: Create DTO for completing WebAuthn setup**

Create a request DTO that the frontend will send to complete the WebAuthn registration. This will be mapped to the `CompleteSetupMfaCommand`.

```java
package com.uit.se356.core.presentation.request.mfa;

import lombok.Data;

@Data
public class CompleteWebAuthnSetupRequest {
    private byte[] attestationObject;
    private byte[] clientDataJSON;
    // other fields from PublicKeyCredential
}
```

- [ ] **Step 2: Create DTO for verifying WebAuthn**

Create a request DTO that the frontend will send to verify the WebAuthn assertion during login. This will be mapped to the `VerifyMfaCommand`.

```java
package com.uit.se356.core.presentation.request.mfa;

import lombok.Data;

@Data
public class VerifyWebAuthnRequest {
    private byte[] credentialId;
    private byte[] authenticatorData;
    private byte[] clientDataJSON;
    private byte[] signature;
}
```

- [ ] **Step 3: Update Controller**

Update the relevant controller(s) to accept these new DTOs and map them to the corresponding commands.

- [ ] **Step 4: Commit**

```bash
git add .
git commit -m "feat(mfa): add presentation layer for webauthn"
```
