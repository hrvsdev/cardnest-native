# CardNest

CardNest securely stores payment card details, whether locally or synced with your account, always
keeping your data encrypted. With CardNest, you no longer need to check your physical card, a photo,
or a slow bank app when making online purchases.

## Features

- **Card Storage**: Securely store card details including number, expiry, CVV, and more.
- **Security**: Lock the app with PIN and biometrics; encrypt data with user-specific keys.
- **Google Sign-In**: Access cards on any device with Google account and a 12-character password.
- **Data Deletion**: Delete all stored data with a single tap without complicating the process.

## Cryptography

- **Key Derivation**: PBKDF2 with HMAC-SHA-512, 256-bit keys, 128-bit salt, 210,000 iterations.
- **Encryption**: AES-GCM, 256-bit keys, 128-bit IV, 128-bit authentication tag.
- **Biometric Key Storage**: Android Keystore with AES-GCM, 256-bit keys.

## Secure Data Storage Process

1. **Local Storage**: Initially, card data is stored in plaintext locally.

2. **PIN-Enabled Encryption**:

    - A Data Encryption Key (DEK) is generated to encrypt card data.
    - A Key Encryption Key (KEK) derived from the user's PIN encrypts the DEK.
    - The encrypted DEK and the encrypted card data are stored locally.

3. **Biometric Protection**:

    - Upon enabling biometrics, a key is created and stored in the Android Keystore.
    - This biometric key encrypts the DEK, which is then stored locally.

4. **Google Sign-In Integration**:

    - When signing in with Google, the user creates a password to derive a KEK.
    - This KEK encrypts the DEK, and both the encrypted DEK and card data are stored locally and on
      the server.
    - All server-stored data remains encrypted at all times.

## Tech Stack

- **UI**: Kotlin with Jetpack Compose
- **Server**: Firebase
