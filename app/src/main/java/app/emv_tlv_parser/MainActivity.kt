
package app.emv_tlv_parser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.emv_tlv_parser.model.EMVTag
import app.emv_tlv_parser.model.TLVParseResult
import app.emv_tlv_parser.parser.TLVParser
import app.emvtlvparser.ui.theme.EMVTLVParserTheme

private val BackgroundTop = Color(0xFF101827)
private val BackgroundBottom = Color(0xFF172438)
private val CardColor = Color(0xFF1D2B3E)
private val AccentBlue = Color(0xFF55A9F5)
private val MutedText = Color(0xFF91A4BC)
private val SuccessGreen = Color(0xFF67DDAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EMVTLVParserTheme {
                ParserScreen()
            }
        }
    }
}

@Composable
fun ParserScreen() {
    var tlvInput by remember {
        mutableStateOf("9F02060000000100009C0100")
    }

    var result by remember {
        mutableStateOf<TLVParseResult?>(null)
    }

    var showResults by remember {
        mutableStateOf(false)
    }

    val parser = remember { TLVParser() }

    BackHandler(enabled = showResults) {
        showResults = false
    }

    Scaffold(
        containerColor = BackgroundTop,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundTop,
                            BackgroundBottom
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            if (showResults) {
                ResultsScreen(
                    result = result,
                    onBack = { showResults = false }
                )
            } else {
                InputScreen(
                    input = tlvInput,
                    onInputChange = {
                        tlvInput = it
                        result = null
                    },
                    error = result as? TLVParseResult.Error,
                    onParse = {
                        val parseResult = parser.parse(tlvInput)
                        result = parseResult

                        if (parseResult is TLVParseResult.Success) {
                            showResults = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InputScreen(
    input: String,
    onInputChange: (String) -> Unit,
    error: TLVParseResult.Error?,
    onParse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF203C5B),
            modifier = Modifier.size(76.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Code,
                    contentDescription = "TLV Input",
                    tint = AccentBlue,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "EMV TLV Parser",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "PAYMENT DATA ANALYZER",
            style = MaterialTheme.typography.labelMedium,
            color = MutedText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enter your EMV TLV data and decode its structure.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = AccentBlue
                    )

                    Text(
                        text = "TLV HEX DATA",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }

                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter hexadecimal data",
                            color = MutedText
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color(0xFF43546A),
                        cursorColor = AccentBlue,
                        focusedContainerColor = Color(0xFF111D2D),
                        unfocusedContainerColor = Color(0xFF111D2D)
                    )
                )

                Button(
                    onClick = onParse,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2476C5)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Parse TLV",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                error?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFFF8995)
                        )

                        Text(
                            text = it.message,
                            color = Color(0xFFFF8995),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MutedText,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = "Supports EMV TLV hexadecimal data",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ResultsScreen(
    result: TLVParseResult?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Analysis Results",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Text(
                    text = "EMV TRANSACTION DATA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )
            }
        }

        when (result) {
            is TLVParseResult.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen
                        )

                        Text(
                            text = "Parsing Successful",
                            color = SuccessGreen,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = "${result.tags.size} TAGS",
                        color = AccentBlue,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                result.tags.forEach { tag ->
                    TagItem(tag)
                }
            }

            is TLVParseResult.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF42252E)
                    )
                ) {
                    Text(
                        text = result.message,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFFF8995)
                    )
                }
            }

            null -> Unit
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AccentBlue
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Back to Input")
        }
    }
}

@Composable
fun TagItem(tag: EMVTag) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tag.name ?: "Unknown Tag",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF203C5B)
                ) {
                    Text(
                        text = tag.tag,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = AccentBlue,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = SuccessGreen
                )

                Text(
                    text = if (tag.tag == "5A") {
                        tag.decodedValue ?: "Hidden"
                    } else {
                        tag.decodedValue ?: tag.value
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = SuccessGreen
                )
            }

            Text(
                text = "Length: ${tag.length} Bytes",
                color = MutedText,
                style = MaterialTheme.typography.bodyMedium
            )

            if (tag.tag != "5A") {
                HorizontalDivider(
                    color = Color(0xFF344256)
                )

                Text(
                    text = "RAW VALUE",
                    color = MutedText,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = tag.value,
                    color = Color(0xFFC5D2E2),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            tag.description?.let {
                Text(
                    text = it,
                    color = MutedText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (tag.children.isNotEmpty()) {
                Text(
                    text = "NESTED TAGS",
                    color = AccentBlue,
                    style = MaterialTheme.typography.titleSmall
                )

                tag.children.forEach { child ->
                    TagItem(child)
                }
            }
        }
    }
}
