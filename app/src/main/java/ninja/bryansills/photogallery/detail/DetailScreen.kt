package ninja.bryansills.photogallery.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import ninja.bryansills.photogallery.R
import ninja.bryansills.photogallery.network.PhotoDetails
import ninja.bryansills.photogallery.resolve

@Serializable
data class Detail(val id: String, val title: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    title: String?,
    onBack: () -> Unit
) {
    val uiState by hiltViewModel<DetailViewModel>().uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title ?: stringResource(R.string.unknown_photo),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(48.dp))
                }
            }
            is DetailUiState.Loaded -> {
                DetailView(state.photoDetails, modifier = Modifier.padding(innerPadding))
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    Text(
                        text = state.message.resolve(),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailView(photoDetails: PhotoDetails, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth().zoomable(rememberZoomableState()),
            model = photoDetails.url,
            contentDescription = photoDetails.description
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = photoDetails.title.takeIf { it?.isNotBlank() == true } ?: stringResource(R.string.unknown_photo),
                style = MaterialTheme.typography.titleLarge
            )
            photoDetails.dateTaken?.let { taken ->
                val localDateTime = taken.toLocalDateTime(TimeZone.currentSystemDefault())
                val formattedTaken = LocalDateTimeFormatter.format(localDateTime)

                Text(
                    text = stringResource(R.string.date_taken, formattedTaken),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            photoDetails.datePosted?.let { posted ->
                val localDateTime = posted.toLocalDateTime(TimeZone.currentSystemDefault())
                val formattedPosted = LocalDateTimeFormatter.format(localDateTime)

                Text(
                    text = stringResource(R.string.date_posted, formattedPosted),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            photoDetails.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private val LocalDateTimeFormatter = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day()
    char(' ')
    year()
    char(' ')
    amPmHour()
    char(':')
    minute()
    amPmMarker(am = "AM", pm = "PM")
}