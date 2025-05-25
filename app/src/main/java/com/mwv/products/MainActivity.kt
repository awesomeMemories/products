package com.mwv.products

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mwv.products.ui.theme.ProductsTheme
import com.mwv.products.ui.theme.ComposeSqlliteTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.database.Cursor
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Color // Import Color for link styling
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.style.TextAlign
import java.net.URLEncoder // For robust URL encoding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo edge-to-edge

        setContent {
            ProductsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        setContent {
            ComposeSqlliteTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    topBar = { /* Your TopAppBar */ },
                    bottomBar = { /* Your BottomNavigationBar */ },
                    content = { paddingValues ->
                        // Apply paddingValues to the *root* of your content.
                        // SQLiteApp() will now fill the space *within* these paddings.
                        SQLiteApp(modifier = Modifier.padding(paddingValues)) // Pass padding to SQLiteApp
                    }
                )
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProductsTheme {
        Greeting("Android")
    }
}


@Composable
fun SQLiteApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper(context) }
    var product by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var showAddressDetails by remember { mutableStateOf(false) }
    var dataList by remember { mutableStateOf(listOf<UserData>()) }
    var id by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize() // <--- Apply the modifier here
        //modifier = Modifier
        //    .fillMaxSize()
        //    .padding(16.dp),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Products's price",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = product,
            onValueChange = { product = it },
            label = { Text("Product") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(1.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(1.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Distributes buttons evenly
        ) {
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal // Suggests a decimal keyboard
                ),
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(1.dp)) // Optional: Add some space between the texts
            TextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("Id") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(1.dp))
        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Distributes buttons evenly
        ) {
            Button(onClick = {
                if (product.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty() && address.isNotEmpty()) {
                    val rowId = databaseHelper.insertData(product, description, price.toFloat(), address)
                    if (rowId > 0) {
                        Toast.makeText(context, "Data inserted successfully", Toast.LENGTH_SHORT)
                            .show()
                        product = ""
                        description = ""
                        price = ""
                        address = ""
                        loadData(databaseHelper) { newData ->
                            dataList = newData
                        }
                    } else {
                        Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter product, description, price, address", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Add Data")
            }
            Spacer(modifier = Modifier.height(2.dp))
            Button(onClick = {
                loadData(databaseHelper) { newData ->
                    dataList = newData
                }
            }) {
                Text("View Data")
            }
        }
        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Distributes buttons evenly
        ) {
            Text(
                text = "Delete by:",
                //modifier = Modifier.padding(bottom = 8.dp) // Add bottom padding for spacing
                modifier = Modifier.padding(end = 1.dp) // Add bottom padding for spacing
                    .padding(top = 10.dp)
            )

            Button(onClick = {
                if(id.isNotEmpty()){
                    val rowId = databaseHelper.deleteDataById(id.toInt())
                    if (rowId > 0) {
                        Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                        id = ""
                        loadData(databaseHelper) { newData ->
                            dataList = newData
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter id", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Id")
            }

            Button(onClick = {
                if(product.isNotEmpty()){
                    val rowId = databaseHelper.deleteDataByProduct(product)
                    if (rowId > 0) {
                        Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                        product = ""
                        loadData(databaseHelper) { newData ->
                            dataList = newData
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter product", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Product")
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically, // Vertically centers all items in this row
            horizontalArrangement = Arrangement.SpaceAround // Distributes buttons evenly
        ) {
            Text(
                text = "Search by:",
                //modifier = Modifier.padding(bottom = 8.dp) // Add bottom padding for spacing
                modifier = Modifier.padding(end = 0.dp) // Add bottom padding for spacing
                                   .padding(top = 10.dp)
            )

            Button(onClick = {
                if (product.isNotEmpty()) {
                    loadDataByPortionProduct(databaseHelper, product) { newData ->
                        dataList = newData
                    }
                    product = ""
                } else {
                    Toast.makeText(context, "Please enter portion product name", Toast.LENGTH_SHORT).show()
                }
            }
            ) {
                Text("LikeProduct",
                    //modifier = Modifier.fillMaxWidth(), // Make the text fill the button's width
                    textAlign = TextAlign.Center // Center the text within that filled width
                    )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Button(onClick = {
                if(id.isNotEmpty()){
                    loadDataById(databaseHelper, id){ newData ->
                        dataList = newData
                    }
                    id = ""
                    showAddressDetails = true  //Is able to show address
                } else{
                    Toast.makeText(context, "Please enter Id", Toast.LENGTH_SHORT).show()
                }
            }
            ) {
                Text("Id")
            }

            Button(onClick = {
                if(product.isNotEmpty()){
                    loadDataByProduct(databaseHelper, product) { newData ->
                        dataList = newData
                    }
                    product = ""
                } else {
                    Toast.makeText(context, "Please enter product name", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Product")
            }

        }

        Spacer(modifier = Modifier.height(4.dp))

        if (dataList.isEmpty()) {
            Text("No data found")
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "ID",
                        modifier = Modifier
                            .weight(1f) // This Text will take 1/4 of the available space
                            .padding(horizontal = 1.dp, vertical = 1.dp) // Reduced vertical padding
                    )

                    Text(
                        text = "Product",
                        modifier = Modifier
                            .weight(4f) // This Text will take 1/4 of the available space
                            .padding(horizontal = 1.dp, vertical = 1.dp) // Reduced vertical padding
                    )

                    Text(
                        text = "Description",
                        modifier = Modifier
                            .weight(6f) // This Text will take 1/4 of the available space
                            .padding(horizontal = 1.dp, vertical = 1.dp) // Reduced vertical padding
                    )

                    Text(
                        text = "Price",
                        modifier = Modifier
                            .weight(3f) // This Text will take 1/4 of the available space
                            .padding(horizontal = 1.dp, vertical = 1.dp) // Reduced vertical padding
                    )
                }

                // Data Rows
                dataList.forEach { user ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = user.id.toString(),
                            modifier = Modifier
                                .weight(1f) // This Text will take 1/4 of the available space
                                .padding(horizontal = 0.dp, vertical = 0.dp) // Reduced vertical padding
                        )

                        Text(
                            text = user.product.toString(),
                            modifier = Modifier
                                .weight(4f) // This Text will take 1/4 of the available space
                                .padding(horizontal = 0.dp, vertical = 0.dp) // Reduced vertical padding
                        )

                        Text(
                            text = user.description.toString(),
                            modifier = Modifier
                                .weight(6f) // This Text will take 1/4 of the available space
                                .padding(horizontal = 0.dp, vertical = 0.dp) // Reduced vertical padding
                        )

                        Text(
                            text = user.price.toString(),
                            modifier = Modifier
                                .weight(3f) // This Text will take 1/4 of the available space
                                .padding(horizontal = 0.dp, vertical = 0.dp) // Reduced vertical padding
                        )
                    }

                    if(showAddressDetails && dataList.size == 1 ){
                        Spacer(modifier = Modifier.height(8.dp))
                        // Make the address text selectable (copyable)
                        SelectionContainer {
                            val addressText = user.address.toString()

                            // Robustly URL-encode the address for a Google Maps search
                            val encodedAddress = URLEncoder.encode(addressText, "UTF-8")
                            //val googleMapsUrl = "$encodedAddress"
                            val googleMapsUrl = addressText
                            //val googleMapsUrl = "https://maps.google.com/?q=$encodedAddress"

                            val annotatedLinkString = buildAnnotatedString {
                                // Apply the default text style first
                                withStyle(style = LocalTextStyle.current.toSpanStyle().copy(color = LocalContentColor.current)) {
                                    // Define the link annotation
                                    withLink(
                                        LinkAnnotation.Url(url = googleMapsUrl)
                                    ) {
                                        // Apply the link-specific style (blue color and underline) to the appended text
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Blue, // Or your theme's link color
                                                textDecoration = TextDecoration.Underline
                                            )
                                        ) {
                                            append(addressText)
                                        }
                                    }
                                }
                            }

                            Text(
                                text = annotatedLinkString,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}

data class UserData(val id: Int, val product: String, val description: String, val price: Float, val address: String)

fun loadData(databaseHelper: DatabaseHelper, onDataLoaded: (List<UserData>) -> Unit) {
    val cursor = databaseHelper.getAllData()
    loadDataFromCursor(cursor, onDataLoaded)
}

fun loadDataByProduct(databaseHelper: DatabaseHelper, product: String, onDataLoaded: (List<UserData>) -> Unit) {
    val cursor = databaseHelper.getDataByProduct(product)
    loadDataFromCursor(cursor, onDataLoaded)
}

fun loadDataByPortionProduct(databaseHelper: DatabaseHelper, product: String, onDataLoaded: (List<UserData>) -> Unit) {
    val cursor = databaseHelper.getDataByPortionProduct(product)
    loadDataFromCursor(cursor, onDataLoaded)
}

fun loadDataById(databaseHelper: DatabaseHelper, id: String, onDataLoaded: (List<UserData>) -> Unit) {
    val cursor = databaseHelper.getDataById(id.toInt())
    loadDataFromCursor(cursor, onDataLoaded)
}

fun loadDataFromCursor(cursor: Cursor, onDataLoaded: (List<UserData>) -> Unit) {
    val users = mutableListOf<UserData>()
    cursor.use { // Ensures the cursor is closed automatically
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val product = cursor.getString(1)
            val description = cursor.getString(2)
            val price = cursor.getFloat(3)
            val address = cursor.getString(4)
            users.add(UserData(id, product, description, price, address))
        }
    }
    onDataLoaded(users)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeSqlliteTheme {
        SQLiteApp()
    }
}




