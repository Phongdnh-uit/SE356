# WebAuthn MFA Integration Design Document

## 1. Introduction

This document outlines the design for integrating WebAuthn as a new Multi-Factor Authentication (MFA) method into the existing backend system. The primary goal is to allow users to register and utilize WebAuthn devices (such as Face ID or fingerprint on PWA) as a second factor for authentication, enhancing security and user experience.

## 2. Requirements

*   **Registration**: Users will manually register WebAuthn devices via a security settings page in the frontend PWA.
*   **Authentication**: WebAuthn will serve as a second factor, prompted after successful primary (username/password) authentication.
*   **Recovery**: Account recovery will be handled using existing backup codes.
*   **Platform Authenticators**: The implementation should support platform authenticators (e.g., Face ID, Touch ID).

## 3. Architectural Overview

WebAuthn will be integrated as a new MFA method by extending the existing MFA framework.

*   A new `WebAuthnProvider` will be created, implementing the `com.uit.se356.core.application.authentication.port.out.MfaProvider` interface.
*   This `WebAuthnProvider` will encapsulate all interactions with the `webauthn4j` library, handling the generation of WebAuthn challenges and the validation of client responses.
*   The existing `Mfa` entity and its corresponding `multifactors` database table will be utilized.
*   The `details` JSONB column in the `multifactors` table will store WebAuthn-specific credential data.
*   The existing command/handler patterns (`InitiateMfaSetupCommand`, `CompleteSetupMfaCommand`, `MfaChallengeCommand`, `VerifyMfaCommand`) will be extended or reused to orchestrate the WebAuthn flows.

## 4. Registration Flow

The registration process will follow the standard WebAuthn ceremony, integrated into the existing `InitiateMfaSetup` and `CompleteSetupMfa` command/handler flow.

1.  **Initiate Registration**:
    *   The frontend (PWA) sends an `InitiateMfaSetupCommand` with `method = "WEBAUTHN"` to the backend.
    *   The `InitiateMfaSetupHandler` delegates to the new `WebAuthnProvider`.
    *   The `WebAuthnProvider` uses `webauthn4j` to generate `PublicKeyCredentialCreationOptions` (WebAuthn "challenge" for registration), including a unique `challenge`, `rp` (relying party) information, `user` information, and desired `pubKeyCredParams`.
    *   These options are returned to the frontend. No `Mfa` record is persisted or it's marked as unverified and temporary.

2.  **Frontend Interaction**:
    *   The frontend receives the `PublicKeyCredentialCreationOptions` and passes them to the browser's WebAuthn API (`navigator.credentials.create()`).
    *   The browser prompts the user to register their platform authenticator (Face ID, fingerprint).
    *   Upon successful user interaction, the browser returns a `PublicKeyCredential` object containing the `attestationObject` and `clientDataJSON`.

3.  **Complete Registration**:
    *   The frontend sends a `CompleteSetupMfaCommand` (or similar) containing the `PublicKeyCredential` object (specifically `attestationObject`, `clientDataJSON`, `rawId`, and `type`) to the backend.
    *   A new `CompleteSetupWebAuthnHandler` (or an extended `CompleteSetupMfaHandler` with WebAuthn-specific logic) delegates to the `WebAuthnProvider`.
    *   The `WebAuthnProvider` uses `webauthn4j` to validate the `PublicKeyCredential`.
    *   Upon successful validation, a new `Mfa` record is created in the `multifactors` table with `method = "WEBAUTHN"`, `is_verified = TRUE`, and the necessary WebAuthn credential data stored in the `details` column as an `MfaConfig` object.

## 5. Authentication Flow

This flow describes how a user will use their registered WebAuthn device as a second factor during login.

1.  **Primary Authentication**:
    *   The user first submits their username and password.
    *   If valid, the backend checks for a verified `WEBAUTHN` method in the `multifactors` table.
    *   If found, the second-factor authentication process is initiated.

2.  **Initiate WebAuthn Challenge**:
    *   The backend's `MfaChallengeCommand` handler delegates to the `WebAuthnProvider`.
    *   The `WebAuthnProvider` uses `webauthn4j` to generate `PublicKeyCredentialRequestOptions` (WebAuthn "challenge" for authentication), including the `challenge` and the `allowCredentials` list of the user's registered `credentialId`s.
    *   These options are sent to the frontend.

3.  **Frontend Interaction**:
    *   The frontend receives the `PublicKeyCredentialRequestOptions` and passes them to the browser's WebAuthn API (`navigator.credentials.get()`).
    *   The browser prompts the user to authenticate with their registered device.
    *   Upon successful user interaction, the browser returns a `PublicKeyCredential` object containing the `authenticatorData`, `clientDataJSON`, and `signature`.

4.  **Verify Authentication**:
    *   The frontend sends a `VerifyMfaCommand` containing the `PublicKeyCredential` object to the backend.
    *   The `VerifyMfaHandler` delegates to the `WebAuthnProvider`.
    *   The `WebAuthnProvider` uses `webauthn4j` to validate the response (signature, challenge, rpIdHash, signCount).
    *   If validation is successful, MFA is complete, and the user is granted access.

## 6. Data Storage (Multifactors Table `details` column)

The `details` JSONB column of the `multifactors` table will store the `MfaConfig` object, which will be a `WebAuthnConfig` for WebAuthn methods. This configuration will contain the following key-value pairs:

```json
{
  "credentialId": "...",        // Base64 encoded credential ID
  "publicKeyCose": "...",       // Base64 encoded COSE public key
  "signCount": 12345,           // The signature counter
  "transports": ["internal", "hybrid"], // The transports supported by the authenticator
  // Additional metadata can be stored if necessary
}
```

## 7. Dependencies

The following dependencies will be added to the `core` module's `build.gradle.kts` file:

```kotlin
implementation("com.webauthn4j:webauthn4j-core:0.23.0.RELEASE")
implementation("com.webauthn4j:webauthn4j-device-reputation:0.23.0.RELEASE")
```

The exact versions will be updated to the latest stable release at the time of implementation.
