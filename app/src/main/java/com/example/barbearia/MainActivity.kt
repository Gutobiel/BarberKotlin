package com.example.barbearia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class Cliente(val nome: String, val valor: Double, val horario: String)

val clientesMarcadosPredefinidos = listOf(
    Cliente("João", 30.0, "09:00"),
    Cliente("Maria", 30.0, "10:00"),
    Cliente("Pedro", 30.0, "11:00"),
    Cliente("Ana", 30.0, "14:00"),
    Cliente("Carlos", 30.0, "16:00")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarberShopApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberShopApp() {
    val navController = rememberNavController()
    val darkTheme = isSystemInDarkTheme()

    val typography = Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    )

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        typography = typography
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.barbearia),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(60.dp)
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                NavHost(navController = navController, startDestination = "main", modifier = Modifier.padding(paddingValues)) {
                    composable("main") {
                        MainScreen(navController)
                    }
                }
            }
        )
    }
}

@Composable
fun MainScreen(navController: NavController, viewModel: BarberShopViewModel = viewModel()) {
    val clientesMarcados by viewModel.clientesMarcados.collectAsState()
    val clientesNaoMarcados by viewModel.clientesNaoMarcados.collectAsState()
    val totalHoje by viewModel.totalHoje.collectAsState()
    val totalMes by viewModel.totalMes.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE63946),
                                Color(0xFF1D3557)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(17.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoCard(title = "Hoje", value = totalHoje, count = clientesMarcados.size + clientesNaoMarcados.size, modifier = Modifier.weight(1f))

                    // Adicionar um Divider vertical
                    Divider(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 8.dp) // Opcional: adicionar padding para não tocar nas bordas superiores e inferiores
                    )

                    InfoCard(title = "Este Mês", value = totalMes, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Clientes Marcados de Hoje", style = MaterialTheme.typography.titleMedium)
        }
        items(clientesMarcados) { cliente ->
            ClienteItem(cliente)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Clientes Não Marcados de Hoje", style = MaterialTheme.typography.titleMedium)
        }
        items(clientesNaoMarcados) { cliente ->
            ClienteItem(cliente)
        }
        item {
            Spacer(modifier = Modifier
                .height(8.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF6200EA), Color.DarkGray)
                    )
                ))

            Button(
                onClick = { viewModel.adicionarClienteNaoMarcado() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE63946),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = "Adicionar Cliente",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClienteItem(cliente: Cliente) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(initialAlpha = 0.3f, animationSpec = tween(durationMillis = 300)),
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(cliente.nome, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black) // Aqui ajustamos a cor para preto
                        if (cliente.horario.isNotEmpty()) {
                            Text(cliente.horario, fontSize = 16.sp, fontWeight = FontWeight.Light, color = Color.Black) // Também ajustamos a cor aqui
                        }
                    }
                    Text("R$ ${cliente.valor}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black) // E aqui
                }
            }
        }
    )
}


@Composable
fun InfoCard(title: String, value: Double, count: Int? = null, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                Text("R$ $value", style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                count?.let {
                    Text("$it cortes", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

class BarberShopViewModel : ViewModel() {
    private val _clientesNaoMarcados = MutableStateFlow<List<Cliente>>(emptyList())
    val clientesNaoMarcados: StateFlow<List<Cliente>> = _clientesNaoMarcados

    private val _clientesMarcados = MutableStateFlow(clientesMarcadosPredefinidos)
    val clientesMarcados: StateFlow<List<Cliente>> = _clientesMarcados

    private val _totalHoje = MutableStateFlow(0.0)
    val totalHoje: StateFlow<Double> = _totalHoje

    private val _totalMes = MutableStateFlow(0.0)
    val totalMes: StateFlow<Double> = _totalMes

    init {
        calcularTotalHoje()
        calcularTotalMes()
    }

    fun adicionarClienteNaoMarcado() {
        val novoCliente = Cliente("Cliente não marcado", 30.0, "")
        _clientesNaoMarcados.value = _clientesNaoMarcados.value + novoCliente
        calcularTotalHoje()
        calcularTotalMes()
    }

    private fun calcularTotalHoje() {
        _totalHoje.value = (_clientesMarcados.value + _clientesNaoMarcados.value).sumOf { it.valor }
    }

    private fun calcularTotalMes() {
        _totalMes.value = (_clientesMarcados.value + _clientesNaoMarcados.value).sumOf { it.valor }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(rememberNavController())
}
