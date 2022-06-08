package com.example.wanderingelder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wanderingelder.ui.theme.WanderingElderTheme
class MainScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        }

    }


}
@Composable
fun launchMainScreen()
{

        WanderingElderTheme {
            // A surface container using the 'background' color from the theme
            Column(
                modifier = Modifier.fillMaxSize()

            ) {
                Greeting("Android")

                Text("Blank spot")
            }
        }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WanderingElderTheme {
        Greeting("Android")
    }
}