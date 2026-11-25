# EMV TLV Parser

A lightweight Kotlin library for parsing and decoding EMV TLV (Tag-Length-Value) data. Designed to be reusable, easy to integrate, and suitable for EMV learning, analysis, and development tools.

## Features

- Parse EMV TLV data from hexadecimal strings.
- Support multi-byte tags.
- Support short-form and long-form lengths.
- Parse constructed tags and nested TLV structures.
- Decode common EMV tag values into human-readable formats.
- Built-in EMV tag dictionary.
- Configurable parsing limits.
- Structured success and error results.
- Pure Kotlin/JVM library — no Android dependency required.

## Supported Decoding

The library includes decoding support for common EMV tags, including:

| Tag | Description |
|---|---|
| `9F02` | Amount, Authorised |
| `9F03` | Amount, Other |
| `5F2A` | Transaction Currency Code |
| `9C` | Transaction Type |
| `9A` | Transaction Date |
| `9F36` | Application Transaction Counter |
| `9F37` | Unpredictable Number |
| `9F26` | Application Cryptogram |
| `9F27` | Cryptogram Information Data |
| `5A` | Application PAN |
| `5F34` | PAN Sequence Number |
| `5F24` | Application Expiration Date |

## Example

### Input

```text
9F02060000000100005F2A020840
```

### Decoded Result

| Tag | Decoded Value |
|---|---|
| `9F02` | 100.00 |
| `5F2A` | USD - US Dollar |

The parser can also process constructed tags containing nested TLV data.

## Requirements

- Kotlin/JVM
- Java 11 compatible target

## Installation

The library will be available through JitPack.

```kotlin
dependencies {
    implementation("com.github.omar20alaa:emv-tlv-parser:1.0.0")
}
```

JitPack repository configuration may be required depending on your project's Gradle setup.

## Security Note

This library is intended for EMV TLV parsing, educational purposes, development, and data analysis.

Sensitive payment data, including cardholder PANs, should be handled carefully. Avoid logging, sharing, or exposing raw card data. The parser does not replace payment security controls, cryptographic validation, or the security requirements of a certified payment application.

## License

MIT License
