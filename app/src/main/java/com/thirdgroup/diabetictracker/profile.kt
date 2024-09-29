package com.thirdgroup.diabetictracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.thirdgroup.diabetictracker.theme.StoreAppTheme

class Profile: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
        }
    }
}

@Composable
fun ProfilePage(navController: NavHostController) {
    StoreAppTheme {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "Name: Joseph Andrew Ancit",
                style = MaterialTheme.typography.h4,
            )

            Text(
                text = "Age: 30",
                style = MaterialTheme.typography.h5,
            )

            Text(
                text = "Gender: Male",
                style = MaterialTheme.typography.h5,
            )

            Text(
                text = "Height: 175 cm",
                style = MaterialTheme.typography.h5,
            )

            Text(
                text = "Weight: 70 kg",
                style = MaterialTheme.typography.h5,
            )

            Text(
                text = "Type of diabetes: Type 1",
                style = MaterialTheme.typography.h5,
            )
        }
    }
}


