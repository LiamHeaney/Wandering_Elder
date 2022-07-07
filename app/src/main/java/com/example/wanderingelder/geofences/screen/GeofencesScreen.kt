package com.example.wanderingelder.geofences.screen


import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderingelder.database.Marker
import com.example.wanderingelder.model.GeofenceRepo.dataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@Composable
fun LaunchGeofencesScreen()
{

   LocalViewModelStoreOwner.current?.let { val viewModel:GeofencesScreenViewModel =
       viewModel(
            it,
           "GeofencesScreenViewModel",
            GeofencesScreenViewModelFactory(
                LocalContext.current.applicationContext as Application)
       )
       GeofencesList(viewModel = viewModel)
   }



}
@Composable
fun GeofencesList(viewModel: GeofencesScreenViewModel)
{
    val allGeofences by viewModel.allMarkers.observeAsState(listOf())
//    val shownGeofences by viewModel.displayedMarkers.observeAsState(listOf())
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
        Button(
            onClick = {
                CoroutineScope(Dispatchers.Default).launch {dataSource.dao.clearDatabase()  }
            },
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxHeight(.1f))
        {
            Text("  Click here to Clear Database  ")
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        GeofencesColumn(geofences = allGeofences, viewModel = viewModel)
    }

}
@Composable
fun GeofencesColumn(geofences:List<Marker>, viewModel: GeofencesScreenViewModel)
{
        LazyColumn(contentPadding = PaddingValues(4.dp)){
            items(geofences){
                    item->
                GeofenceItem(marker = item, viewModel)
            }

        }

}
@Composable
fun GeofenceItem(marker: Marker, viewModel: GeofencesScreenViewModel)
{
    Surface(border = BorderStroke(2.dp, Color.LightGray), modifier = Modifier.background(Color.Blue, RectangleShape)){
        Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
            Surface(modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(color = Color.Black)) {
                Column {
                    Spacer(modifier = Modifier.size(5.dp))
                    Text("${marker.name}\n" +
                            "Latitude: ${String.format("%.2f",marker.latitude)}\t\t " +
                            "Longitude: ${String.format("%.2f",marker.longitude)}")
                    }
                }
               
            Surface(modifier = Modifier
                .fillMaxWidth(0.3f)
                .background(color = Color.Black)) {
                Button(
                    onClick = {
                        viewModel.repo.deleteMarker(marker.name)
                    },
                    content = {

                            Text(
                                text = "X",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 30.sp
                            )
                    },
                    modifier = Modifier.fillMaxWidth(),

                    )
            }

        }
    }

}