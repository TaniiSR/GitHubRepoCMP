package com.task.githubrepo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.task.githubrepo.shared.resources.Res
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
internal fun ErrorView(message: String?, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("errorView"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ErrorAnimation(Modifier.fillMaxWidth().height(300.dp))
        Text(
            "Something went wrong",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp),
        )
        Text(
            "An alien is probably blocking your signal",
            fontSize = 18.sp,
            color = DescriptionGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )
        if (!message.isNullOrBlank()) {
            Text(
                message,
                fontSize = 12.sp,
                color = DescriptionGrey,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Spacer(Modifier.height(60.dp))
        OutlinedButton(
            onClick = onRetry,
            border = BorderStroke(1.dp, HoloGreenLight),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = HoloGreenLight),
            modifier = Modifier.fillMaxWidth().testTag("retryButton"),
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun ErrorAnimation(modifier: Modifier) {
    var json by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        json = runCatching { Res.readBytes("files/error_anime.json").decodeToString() }.getOrNull()
    }
    val source = json
    if (source == null) {
        Box(modifier)
        return
    }
    val composition by rememberLottieComposition(source) { LottieCompositionSpec.JsonString(source) }
    val progress by animateLottieCompositionAsState(composition, iterations = Compottie.IterateForever)
    Image(
        painter = rememberLottiePainter(composition = composition, progress = { progress }),
        contentDescription = null,
        modifier = modifier,
    )
}
