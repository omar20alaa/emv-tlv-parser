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

## Requirements

- Kotlin/JVM
- Java 11 compatible target

## Installation

### 1. Add JitPack Repository

In your project's `settings.gradle.kts`, add JitPack to `dependencyResolutionManagement`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add Dependency

In your app-level `build.gradle.kts`, add:

```kotlin
dependencies {
    implementation("com.github.omar20alaa:emv-tlv-parser:1.0.0")
}
```

For the latest release and build status, visit [JitPack](https://jitpack.io/#omar20alaa/emv-tlv-parser/1.0.0).

## Usage

### Parse EMV TLV Data

Create a `TLVParser` instance and pass the hexadecimal TLV data to `parse()`.

```kotlin
import app.emv_tlv_parser.parser.TLVParser
import app.emv_tlv_parser.model.TLVParseResult

val parser = TLVParser()

val result = parser.parse("9F02060000000100005F2A020840")

when (result) {
    is TLVParseResult.Success -> {
        result.tags.forEach { tag ->
            println("Tag: ${tag.tag}")
            println("Name: ${tag.name}")
            println("Length: ${tag.length}")
            println("Value: ${tag.value}")
            println("Decoded Value: ${tag.decodedValue}")
        }
    }

    is TLVParseResult.Error -> {
        println("Parsing failed: ${result.message}")
    }
}
```

### Decoded Result

For the input:

```text
9F02060000000100005F2A020840
```

The parser returns two EMV tags:

| Tag | Name | Decoded Value |
|---|---|---|
| `9F02` | Amount, Authorised | 100.00 |
| `5F2A` | Transaction Currency Code | USD - US Dollar |

### Parse Nested TLV Structures

The parser supports constructed tags containing nested TLV structures.

Nested tags are available through the `children` property:

```kotlin
when (val result = parser.parse(tlvData)) {
    is TLVParseResult.Success -> {
        result.tags.forEach { parentTag ->
            println("Parent Tag: ${parentTag.tag}")

            parentTag.children.forEach { child ->
                println("Child Tag: ${child.tag}")
                println("Decoded Value: ${child.decodedValue}")
            }
        }
    }

    is TLVParseResult.Error -> {
        println("Parsing failed: ${result.message}")
    }
}
```

### Configure Parsing Limits

You can customize the parser's input length, maximum nesting depth, and maximum number of tags:

```kotlin
import app.emv_tlv_parser.parser.TLVParser
import app.emv_tlv_parser.parser.TLVParserConfig

val config = TLVParserConfig(
    maxInputLength = 32768,
    maxNestingDepth = 8,
    maxTags = 500
)

val parser = TLVParser(config)
```

These limits help control resource usage when processing TLV input.

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

## Security Note

This library is intended for EMV TLV parsing, educational purposes, development, and data analysis.

Sensitive payment data, including cardholder PANs, should be handled carefully. Avoid logging, sharing, or exposing raw card data. The parser does not replace payment security controls, cryptographic validation, or the security requirements of a certified payment application.

## Testing

The library includes 41 unit test cases covering parsing behavior, supported TLV structures, decoding, and error handling.

All 41 tests passed in the project's test run.

## License

This project is licensed under the MIT License.

## Screenshots

<img width="633" height="1280" alt="EMV TLV Parser Screenshot 1" src="https://github.com/user-attachments/assets/6215099f-8b51-436c-bf45-190262ff0c59" />

<img width="633" height="1280" alt="EMV TLV Parser Screenshot 2" src="https://github.com/user-attachments/assets/1b857c07-d094-4cdd-9b85-00ecbbe0d0cf" />

<img width="633" height="1280" alt="EMV TLV Parser Screenshot 3" src="https://github.com/user-attachments/assets/a5b97b4c-eae2-4cc9-89cf-2923fb8a5dff" />
