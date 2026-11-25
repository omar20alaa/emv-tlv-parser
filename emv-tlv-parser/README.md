# EMV TLV Parser

A lightweight Kotlin library for parsing EMV TLV (Tag-Length-Value) data.

The library is designed to parse EMV Contact and Contactless transaction data, extract tags and values, handle constructed tags, and decode commonly used EMV data elements.

## Features

- Parse EMV TLV hexadecimal data.
- Support single-byte and multi-byte tags.
- Support short-form and long-form lengths.
- Parse constructed tags and nested TLV structures.
- Retrieve tag names and descriptions from an internal EMV tag dictionary.
- Decode commonly used EMV data elements.
- Preserve unknown tags even when they are not included in the dictionary.
- Configurable input size, nesting depth, and maximum number of tags.
- Pure Kotlin/JVM implementation with no Android dependencies.
- Unit tested.

## Supported EMV Tags

The library currently includes dictionary information and decoding support for selected EMV tags, including:

| Tag | Name |
|---|---|
| `9F02` | Amount, Authorised |
| `9F03` | Amount, Other |
| `5F2A` | Transaction Currency Code |
| `9A` | Transaction Date |
| `9C` | Transaction Type |
| `9F26` | Application Cryptogram |
| `9F27` | Cryptogram Information Data |
| `9F36` | Application Transaction Counter |
| `9F37` | Unpredictable Number |
| `5A` | Application Primary Account Number |
| `5F24` | Application Expiration Date |
| `5F34` | Application PAN Sequence Number |

Additional tags are recognized and parsed even if they are not included in the dictionary.

## Usage

### Initialize the parser

```kotlin
val parser = TLVParser()
```

### Parse TLV data

```kotlin
val tlvData = "9F0206000000010000"

when (val result = parser.parse(tlvData)) {
    is TLVParseResult.Success -> {
        result.tags.forEach { tag ->
            println("Tag: ${tag.tag}")
            println("Length: ${tag.length}")
            println("Name: ${tag.name}")
            println("Decoded Value: ${tag.decodedValue}")
        }
    }

    is TLVParseResult.Error -> {
        println("Parsing error: ${result.message}")
    }
}
```

### Configure parser limits

```kotlin
val parser = TLVParser(
    config = TLVParserConfig(
        maxInputLength = 65_536,
        maxNestingDepth = 16,
        maxTags = 1_000
    )
)
```

### Access nested tags

Constructed tags expose their nested elements through the `children` property.

```kotlin
when (val result = parser.parse(tlvData)) {
    is TLVParseResult.Success -> {
        result.tags.forEach { tag ->
            tag.children.forEach { child ->
                println(child.tag)
            }
        }
    }

    is TLVParseResult.Error -> {
        println(result.message)
    }
}
```

## Error Handling

The parser returns a `TLVParseResult`:

- `Success`: contains the parsed list of `EMVTag`.
- `Error`: contains an error message describing why parsing failed.

## Security Note

The original hexadecimal value is preserved in `EMVTag.value`.

Some EMV tags may contain sensitive payment data, such as the Primary Account Number (PAN). Avoid logging or displaying raw tag values in production applications.

The PAN decoder masks the PAN in its decoded output, but the original value remains available in `value`.

## Requirements

- Kotlin
- JVM 11 or higher

The library has no Android framework dependency and can be used in Android or JVM projects.

## License

To be added.