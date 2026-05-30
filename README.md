# 📱 Dashboard Mobile — IoT MQTT Monitoramento Industrial

> Aplicativo Android nativo em **Kotlin** para monitoramento em tempo real de um tanque industrial via protocolo MQTT.
> Desenvolvido como parte do **Projeto Unidade II — SENAI CIMATEC**.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Requisitos Atendidos](#requisitos-atendidos)
- [Arquitetura do Sistema](#arquitetura-do-sistema)
- [Estrutura de Arquivos](#estrutura-de-arquivos)
- [Tecnologias e Dependências](#tecnologias-e-dependências)
- [Sensores Monitorados](#sensores-monitorados)
- [Tópicos MQTT](#tópicos-mqtt)
- [Alarmes e Alertas](#alarmes-e-alertas)
- [Telas do Aplicativo](#telas-do-aplicativo)
- [Configuração e Instalação](#configuração-e-instalação)
- [Como Rodar](#como-rodar)
- [Payload MQTT](#payload-mqtt)
- [Fluxo de Dados](#fluxo-de-dados)

---

## Sobre o Projeto

O **Dashboard Mobile** é o componente de monitoramento em smartphone do sistema IoT industrial. Ele se conecta diretamente ao broker **Eclipse Mosquitto** (rodando via Docker no servidor) e recebe dados dos 4 sensores simulados em tempo real, exibindo valores, gráficos históricos e alertas visuais de alarme.

O aplicativo funciona em conjunto com o restante da infraestrutura do projeto:

```
[Publisher Python] → [Broker Mosquitto] → [App Android Kotlin]
                           ↓
                    [Subscriber Python]
                           ↓
                      [SQLite DB]
                           ↓
                  [Dashboard PC — Dash/Plotly]
```

---

## Requisitos Atendidos

### Requisitos do Projeto (Parte 4 — Dashboard Mobile)

| Requisito | Status | Implementação |
|---|---|---|
| Layout responsivo | ✅ | `GridLayout` 2×2 + `ScrollView` adapta a qualquer tela |
| Funcionamento no celular | ✅ | App nativo Android (APK) |
| Atualização em tempo real | ✅ | Callback MQTT → `LiveData` → UI automática |
| Pelo menos 2 gráficos | ✅ | Temperatura e Nível (MPAndroidChart `LineChart`) |
| Alarmes visuais | ✅ | Cards com fundo vermelho + painel de alarmes |
| Atualização automática | ✅ | 1 atualização por segundo (frequência do publisher) |

### Requisitos da Parte 3 (Dashboard Supervisório) implementados no Mobile

| Requisito | Status | Implementação |
|---|---|---|
| Indicadores em tempo real | ✅ | 4 cards com valor instantâneo de cada sensor |
| Gráficos históricos | ✅ | 2 gráficos de linha (últimos 30 pontos) |
| Alarme Temperatura > 80 °C | ✅ | Card vermelho + texto de alarme |
| Aviso Nível < 20% | ✅ | Card com destaque laranja + aviso no painel |
| Alerta Pressão > 8 bar | ✅ | Alerta visual no painel de alarmes |
| Status do broker (conectado/desconectado) | ✅ | Indicador com bolinha colorida no topo |
| Último pacote recebido | ✅ | Timestamp exibido ao lado do status |
| Nome da variável | ✅ | Exibido em cada card |
| Unidade de medida | ✅ | Exibida em cada card |
| Tópico MQTT | ✅ | Exibido em cada card |

---

## Arquitetura do Sistema

### Arquitetura Geral do Projeto

```
Sensores Simulados (Python)
         │
         ▼
  Publisher MQTT ──────────────────────────────────────────────┐
         │                                                      │
         ▼                                                      │
 Broker Mosquitto (Docker)                                      │
    porta 1883 (TCP)                                            │
    porta 9001 (WebSocket)                                      │
         │                                                      │
         ├──────────────────────────────┐                       │
         ▼                             ▼                        │
  Subscriber Python           App Android (Kotlin)             │
         │                     (este repositório)              │
         ▼                                                      │
     SQLite DB                                                  │
         │                                                      │
         ▼                                                      │
 Dashboard PC (Dash/Plotly)                                     │
    http://localhost:8050                                       │
```

### Arquitetura Interna do App (MVVM)

```
┌─────────────────────────────────────────────────────────┐
│                     App Android                         │
│                                                         │
│  ┌─────────────┐    ┌──────────────┐    ┌───────────┐  │
│  │ MqttService │───▶│SensorViewModel│───▶│   UI /    │  │
│  │  (Paho)     │    │  (LiveData)   │    │ Activities│  │
│  └─────────────┘    └──────────────┘    └───────────┘  │
│         │                  │                  │         │
│   Broker MQTT        SensorState        MainActivity   │
│   TCP :1883          (imutável)         ChartsActivity  │
└─────────────────────────────────────────────────────────┘
```

O app segue o padrão **MVVM (Model-View-ViewModel)**:

- **Model** — `SensorData` e `SensorState` representam os dados dos sensores
- **ViewModel** — `SensorViewModel` gerencia o estado e a conexão MQTT via `LiveData`
- **View** — `MainActivity` e `ChartsActivity` observam o `LiveData` e atualizam a UI automaticamente

---

## Estrutura de Arquivos

```
app/
└── src/
    └── main/
        ├── AndroidManifest.xml               # Permissões e declaração de serviços
        │
        ├── java/com/seuprojeto/iot/
        │   │
        │   ├── MainActivity.kt               # Tela principal: cards + alarmes + status
        │   ├── ChartsActivity.kt             # Tela de gráficos históricos
        │   │
        │   ├── model/
        │   │   ├── SensorData.kt             # Modelo do payload JSON recebido via MQTT
        │   │   └── SensorState.kt            # Estado global dos 4 sensores + alarmes
        │   │
        │   ├── mqtt/
        │   │   └── MqttService.kt            # Conexão, subscrição e callbacks do broker
        │   │
        │   └── viewmodel/
        │       └── SensorViewModel.kt        # Lógica de negócio + LiveData
        │
        └── res/
            ├── layout/
            │   ├── activity_main.xml         # Layout da tela principal
            │   ├── activity_charts.xml       # Layout da tela de gráficos
            │   └── card_sensor.xml           # Componente reutilizável de card de sensor
            │
            └── drawable/
                ├── circle_green.xml          # Ícone de status: broker conectado
                └── circle_red.xml            # Ícone de status: broker desconectado

build.gradle.kts                              # Dependências do projeto
settings.gradle.kts                           # Repositórios (JitPack para MPAndroidChart)
```

### Descrição de cada arquivo

#### `model/SensorData.kt`
Data class que representa exatamente o payload JSON que o publisher envia via MQTT. Contém: `sensor`, `valor`, `unidade`, `timestamp` e `topico`.

#### `model/SensorState.kt`
Data class imutável que representa o estado completo da aplicação em um dado momento. Armazena os valores atuais dos 4 sensores, os históricos para os gráficos (lista de até 30 pontos cada), o status do broker e o horário do último pacote. Também contém as **computed properties de alarme** (`alarmeTemperatura`, `alarmeNivel`, `alarmePressao`).

#### `mqtt/MqttService.kt`
Responsável por toda a comunicação com o broker Mosquitto usando a biblioteca **Eclipse Paho Android**. Gerencia a conexão TCP, a subscrição ao tópico wildcard `industria/tanque/+`, o parser do JSON recebido e os callbacks de status (conectado/desconectado). Suporta **reconexão automática**.

#### `viewmodel/SensorViewModel.kt`
Componente central do MVVM. Instancia o `MqttService`, processa cada `SensorData` recebido e atualiza o `MutableLiveData<SensorState>`. A UI observa esse LiveData e é redesenhada automaticamente a cada nova leitura de sensor.

#### `MainActivity.kt`
Tela principal do app. Observa o `SensorViewModel` e atualiza: os 4 cards de sensor, o painel de alarmes e o indicador de status do broker. Possui um botão para navegar até a `ChartsActivity`.

#### `ChartsActivity.kt`
Tela de gráficos históricos. Exibe dois `LineChart` do MPAndroidChart: um para **Temperatura** e outro para **Nível**, ambos atualizados em tempo real conforme chegam novas leituras.

---

## Tecnologias e Dependências

| Biblioteca | Versão | Finalidade |
|---|---|---|
| `org.eclipse.paho:mqtt.client` | 1.2.5 | Cliente MQTT — comunicação com o broker |
| `org.eclipse.paho:mqtt.android.service` | 1.1.1 | Serviço Android do Paho (obrigatório) |
| `com.google.code.gson:gson` | 2.10.1 | Deserialização do payload JSON |
| `com.github.PhilJay:MPAndroidChart` | v3.1.0 | Gráficos históricos (`LineChart`) |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | 2.7.0 | ViewModel com suporte a Kotlin coroutines |
| `androidx.lifecycle:lifecycle-livedata-ktx` | 2.7.0 | LiveData para atualização reativa da UI |
| `com.google.android.material` | 1.11.0 | Componentes de UI Material Design |

**Repositório adicional necessário** no `settings.gradle.kts`:
```kotlin
maven { url = uri("https://jitpack.io") }
```

---

## Sensores Monitorados

| Sensor | Faixa | Unidade | Visualização |
|---|---|---|---|
| Temperatura | 20 a 90 | °C | Card com valor numérico |
| Nível | 0 a 100 | % | Card com valor numérico |
| Pressão | 0 a 10 | bar | Card com valor numérico |
| Vazão | 0 a 200 | L/min | Card com valor numérico |

Os valores são atualizados **1 vez por segundo**, na mesma frequência em que o publisher Python envia dados ao broker.

---

## Tópicos MQTT

O app assina todos os tópicos de uma vez usando o **wildcard** `+`:

```
industria/tanque/+
```

O que equivale a assinar simultaneamente:

```
industria/tanque/temperatura
industria/tanque/nivel
industria/tanque/pressao
industria/tanque/vazao
```

O campo `sensor` dentro do payload JSON é usado para identificar qual sensor enviou a leitura e atualizar o estado correto.

---

## Alarmes e Alertas

| Condição | Ação Visual |
|---|---|
| Temperatura > 80 °C | Card com fundo vermelho + texto "🔴 ALARME! > 80°C" |
| Nível < 20% | Card com fundo laranja + aviso "🟡 Nível < 20%" |
| Pressão > 8 bar | Texto "🔔 Pressão > 8 bar" no painel de alarmes |
| Qualquer alarme ativo | Painel de alarmes aparece no topo da tela |
| Sem alarmes | Painel de alarmes oculto, fundo dos cards branco |

A lógica dos alarmes fica em `SensorState.kt` como propriedades computadas:

```kotlin
val alarmeTemperatura: Boolean get() = temperatura > 80.0
val alarmeNivel: Boolean       get() = nivel < 20.0
val alarmePressao: Boolean     get() = pressao > 8.0
```

---

## Telas do Aplicativo

### Tela Principal (`MainActivity`)

```
┌─────────────────────────────────┐
│   🏭 Tanque Industrial          │
├─────────────────────────────────┤
│ 🟢 Broker: Conectado  Últ: 12:34│
├─────────────────────────────────┤
│ ⚠ ALARMES ATIVOS               │  ← visível só quando há alarme
│   🔴 Temperatura > 80°C        │
├────────────────┬────────────────┤
│  Temperatura   │     Nível      │
│    83.2 °C     │    17.5 %      │  ← card vermelho quando em alarme
│industria/tanque│industria/tanque│
├────────────────┼────────────────┤
│    Pressão     │     Vazão      │
│    5.1 bar     │   102.3 L/min  │
│industria/tanque│industria/tanque│
├─────────────────────────────────┤
│  [ 📈 Ver Gráficos Históricos ] │
└─────────────────────────────────┘
```

### Tela de Gráficos (`ChartsActivity`)

```
┌─────────────────────────────────┐
│   📈 Gráficos Históricos        │
├─────────────────────────────────┤
│ 🌡 Temperatura (°C)             │
│  90 ┤                           │
│     │    ╭──╮    ╭─             │
│  55 ┤───╯  ╰────╯              │
│  20 ┤                           │
│     └──────────── tempo         │
├─────────────────────────────────┤
│ 💧 Nível do Tanque (%)          │
│ 100 ┤                           │
│     │ ╭──╮                      │
│  50 ┤╯  ╰──────╮               │
│   0 ┤           ╰───            │
│     └──────────── tempo         │
└─────────────────────────────────┘
```

Cada gráfico exibe os **últimos 30 pontos** recebidos e é atualizado automaticamente via LiveData.

---

## Configuração e Instalação

### Pré-requisitos

- Android Studio **Hedgehog** ou superior
- Android SDK **API 26** (Android 8.0) ou superior
- Dispositivo físico ou emulador Android
- Broker Mosquitto rodando (via Docker no PC do projeto)
- Celular e PC na **mesma rede Wi-Fi**

### Passo 1 — Clonar ou criar o projeto

No Android Studio:
1. `File → New → New Project`
2. Selecione **Empty Views Activity**
3. Language: **Kotlin**
4. Minimum SDK: **API 26**

### Passo 2 — Adicionar dependências

No `settings.gradle.kts`, adicione o repositório JitPack:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

No `build.gradle.kts` do módulo `app`, adicione as dependências listadas na seção [Tecnologias e Dependências](#tecnologias-e-dependências).

### Passo 3 — Configurar o IP do broker

Em `SensorViewModel.kt`, altere o IP para o endereço local do PC que roda o Docker:

```kotlin
private val mqttService = MqttService(
    context   = application,
    brokerIp  = "192.168.X.X",   // ← coloque o IP real aqui
    port      = 1883,
    ...
)
```

Para descobrir o IP:
- **Windows:** execute `ipconfig` no prompt → campo "Endereço IPv4"
- **Linux/macOS:** execute `ip addr` ou `ifconfig`

### Passo 4 — Verificar o Mosquitto

Certifique-se de que o broker está aceitando conexões externas. O `mosquitto.conf` do projeto já está configurado corretamente:

```
listener 1883
allow_anonymous true
```

E o `docker-compose.yml` já mapeia a porta:
```yaml
ports:
  - "1883:1883"
```

### Passo 5 — Permissões no AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>

<!-- Serviço obrigatório do Paho MQTT -->
<service android:name="org.eclipse.paho.android.service.MqttService"/>
```

---

## Como Rodar

### 1. Subir a infraestrutura do projeto (no PC)

```bash
cd iot-system-via-mqtt-main/
docker compose up -d
```

Verifique que o publisher está enviando dados:
```bash
docker compose logs -f publisher
```

### 2. Verificar conectividade do broker

Em um terminal, assine os tópicos para confirmar que os dados chegam:
```bash
docker exec -it iot_mosquitto mosquitto_sub -h localhost -t "industria/tanque/+" -v
```

### 3. Instalar o app no celular

No Android Studio, com o celular conectado via USB (ou emulador aberto):
```
Run → Run 'app'   (ou Shift + F10)
```

### 4. Alternativa para demonstração sem IP fixo

Se quiser evitar dependência de IP local durante a apresentação, aponte o publisher e o app para o broker público gratuito **HiveMQ**:

No `docker-compose.yml`:
```yaml
environment:
  - MQTT_HOST=broker.hivemq.com
```

No `SensorViewModel.kt`:
```kotlin
brokerIp = "broker.hivemq.com",
port     = 1883,
```

---

## Payload MQTT

Formato JSON enviado pelo publisher Python a cada segundo:

```json
{
  "valor": 67.3,
  "unidade": "°C",
  "timestamp": "2026-05-29T12:34:56.789Z",
  "topico": "industria/tanque/temperatura",
  "sensor": "temperatura"
}
```

| Campo | Tipo | Descrição |
|---|---|---|
| `valor` | `Double` | Leitura numérica do sensor |
| `unidade` | `String` | Unidade de medida (°C, %, bar, L/min) |
| `timestamp` | `String` | ISO 8601 — momento da medição |
| `topico` | `String` | Tópico MQTT completo da mensagem |
| `sensor` | `String` | Nome do sensor (temperatura, nivel, pressao, vazao) |

---

## Fluxo de Dados

```
1. Docker sobe o Mosquitto (broker) na porta 1883
2. Docker sobe o Publisher Python
3. Publisher gera leituras simuladas (1x por segundo por sensor)
4. Publisher publica JSON em industria/tanque/{sensor}
5. App Android conecta ao broker via MqttService (Paho)
6. App assina industria/tanque/+ (wildcard = todos os 4 sensores)
7. Broker entrega as mensagens ao App
8. MqttService chama onData() com SensorData parseado do JSON
9. SensorViewModel atualiza o SensorState no MutableLiveData
10. MainActivity e ChartsActivity observam o LiveData
11. UI é redesenhada automaticamente com os novos valores
12. Alarmes são verificados a cada atualização de SensorState
```

---

## Projeto — SENAI CIMATEC

**Disciplina:** Sistemas IoT  
**Projeto:** Unidade II — Sistema IoT com MQTT para Monitoramento Industrial  
**Componente:** Dashboard Mobile (Parte 4)  
**Tecnologia:** Android Studio + Kotlin  
**Protocolo:** MQTT (Eclipse Paho Android)  
**Broker:** Eclipse Mosquitto 2.0 (Docker)
