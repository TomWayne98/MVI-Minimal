package tom.wayne.mvi.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tom.wayne.mvi.NumbersIntent
import tom.wayne.mvi.NumbersViewModel
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity(), CoroutineScope {

    val viewModel by lazy { NumbersViewModel() }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            render()
        }

    }

    private suspend fun render() {
        viewModel.state.collect {
            Log.d("HEY", "Rendering this amount of numbers ${it.numbers.size}")
            setContent {
                ListOfNumber(
                    numbers = it.numbers,
                    onAddClicked = { viewModel.emitIntent(NumbersIntent.Add) },
                    onRemoveClicked = { viewModel.emitIntent(NumbersIntent.Remove) })
            }
        }
    }
}


@Composable
fun ListOfNumber(numbers: List<Int>, onAddClicked: () -> Unit, onRemoveClicked: () -> Unit) {
    Row() {
        LazyColumn(modifier = Modifier.width(100.dp)) {
            items(numbers) { number ->
                NumberView(number = number)
            }

        }
        Column() {
            Image(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    .clickable(onClick = { onAddClicked() })
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Image(
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    .clickable(onClick = { onRemoveClicked() })
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
        }
    }

}

@Composable
fun NumberView(number: Int) {
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = number.toString(),
        modifier = Modifier.padding(all = 4.dp),
        style = MaterialTheme.typography.subtitle2
    )
    Spacer(modifier = Modifier.width(8.dp))
}

@Preview
@Composable
fun ComposablePreview() {
    ListOfNumber(listOf(4, 4, 2, 13, 6, 7), {}, {})
}

