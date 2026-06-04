# 0007 - Release APK Signing

## Status

Accepted

## Context

Android cannot install unsigned APKs. `assembleDebug` is signed automatically with the local debug keystore, but `assembleRelease` needs an explicit signing configuration.

The project should support an installable local release APK while keeping real signing credentials outside Git.

## Decision

Configure the release build type with a `release` signing config.

The signing config reads real release credentials from `local.properties` or environment variables:

- `signing.storeFile` or `SIGNING_STOREFILE`
- `signing.storePassword` or `SIGNING_STOREPASSWORD`
- `signing.keyAlias` or `SIGNING_KEYALIAS`
- `signing.keyPassword` or `SIGNING_KEYPASSWORD`

If those values are not present, the release build falls back to the local debug keystore. This keeps `assembleRelease` installable for local validation while avoiding committed signing credentials.

## Alternatives Considered

- Leave release unsigned: simple, but the generated APK cannot be installed directly.
- Commit a keystore: rejected because signing credentials must not be stored in source control.
- Require release credentials for every release build: secure, but inconvenient for local validation and CI build checks.

## Consequences

Local release APKs are installable. Real production signing remains external and can be provided by local machine configuration or CI secrets. APKs signed with the debug fallback are not suitable for app store distribution.
