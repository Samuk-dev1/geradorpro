# FinanceApp - Sistema Financeiro Completo

Aplicativo Android completo para gerenciamento financeiro pessoal, desenvolvido com Kotlin e Jetpack Compose.

## Funcionalidades

### 💰 Gerenciamento Financeiro
- **Transações**: Registre receitas e despesas com categorias personalizadas
- **Saldo em Tempo Real**: Acompanhe seu saldo total atualizado automaticamente
- **Relatórios Mensais**: Visualize gráficos de despesas por categoria
- **Categorias**: 13 categorias pré-definidas para receitas e despesas

### 🔔 Sistema de Notificações Avançado
- **Lembretes Diários**: Notificação personalizada para registrar gastos
- **Alertas de Orçamento**: Aviso quando atingir 80% do limite mensal
- **Serviço em Segundo Plano**: Funciona mesmo com app fechado
- **Tela Desligada**: Alarmes garantem entrega mesmo com a tela desligada
- **6 Sons Personalizados**: Escolha entre diferentes sons de notificação
- **Persistência após Reboot**: Alarmes restaurados automaticamente

### 🎨 Interface Moderna
- **Jetpack Compose**: UI moderna e reativa
- **Material Design 3**: Design atualizado e intuitivo
- **Navegação Inferior**: 4 telas principais (Início, Transações, Relatórios, Configurações)
- **Tema Dinâmico**: Suporte a modo claro e escuro

### 🏗️ Arquitetura
- **Room Database**: Persistência local robusta
- **Coroutines + Flow**: Programação assíncrona
- **ViewModel**: Separação de responsabilidades
- **Repository Pattern**: Arquitetura limpa
- **DataStore**: Preferências do usuário
- **WorkManager**: Tarefas em segundo plano

## Requisitos
- Android 7.0 (API 24) ou superior
- Android Studio Hedgehog | 2023.1.1 ou superior
- JDK 17

## Como Compilar

### 1. Clonar o repositório
```bash
git clone https://github.com/seu-usuario/FinanceApp.git
cd FinanceApp
```

### 2. Compilar via linha de comando
```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

### 3. APK gerado
O APK estará em: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions

O projeto já está configurado para compilar automaticamente no GitHub Actions.

### Como usar:
1. Faça upload deste projeto para o GitHub
2. Vá em **Actions** no seu repositório
3. Execute o workflow **"Build Android APK"**
4. Baixe o APK gerado nos **Artifacts**

### O workflow faz:
- ✅ Checkout do código
- ✅ Configura JDK 17
- ✅ Compila APK Debug
- ✅ Tenta compilar APK Release
- ✅ Faz upload dos APKs como artifacts

## Permissões Necessárias

O aplicativo solicita as seguintes permissões:

| Permissão | Motivo |
|-----------|--------|
| `POST_NOTIFICATIONS` | Exibir notificações (Android 13+) |
| `FOREGROUND_SERVICE` | Manter serviço ativo em segundo plano |
| `WAKE_LOCK` | Garantir entrega de notificações |
| `RECEIVE_BOOT_COMPLETED` | Restaurar alarmes após reinício |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Desativar otimização de bateria |
| `SCHEDULE_EXACT_ALARM` | Alarmes precisos para lembretes |

## Configuração Recomendada

Para melhor funcionamento das notificações:

1. Abra o app → **Configurações**
2. Clique em **"Desativar Otimização de Bateria"**
3. Confirme a permissão
4. Ative o **"Serviço Persistente"**
5. Ajuste o horário do **"Lembrete Diário"**

## Estrutura do Projeto

```
FinanceApp/
├── app/
│   └── src/main/
│       ├── java/com/financeapp/
│       │   ├── data/
│       │   │   ├── local/          # Room Database
│       │   │   ├── preferences/    # DataStore
│       │   │   └── repository/     # Repositórios
│       │   ├── notification/       # Sistema de notificações
│       │   ├── service/            # Foreground Service
│       │   ├── worker/             # WorkManager
│       │   ├── ui/
│       │   │   ├── screens/        # Telas Compose
│       │   │   ├── theme/          # Tema e estilos
│       │   │   └── viewmodel/      # ViewModels
│       │   └── utils/              # Utilitários
│       └── res/
│           ├── drawable/           # Ícones vetoriais
│           ├── raw/                # Sons de notificação (.wav)
│           └── values/             # Strings, cores, temas
├── .github/workflows/              # GitHub Actions
└── gradle/wrapper/                 # Gradle Wrapper
```

## Sons de Notificação Inclusos

1. **Padrão** - Tom limpo e profissional
2. **Sucesso** - Tom alto e positivo
3. **Aviso** - Tom de atenção
4. **Alerta** - Tom de urgência
5. **Moeda** - Som de caixa registradora
6. **Conta** - Tom para lembretes de pagamento

## Tecnologias Utilizadas

- **Kotlin** - Linguagem principal
- **Jetpack Compose** - UI moderna
- **Room** - Banco de dados local
- **Coroutines + Flow** - Assincronismo
- **ViewModel** - Camada de apresentação
- **DataStore** - Preferências
- **WorkManager** - Tarefas agendadas
- **AlarmManager** - Alarmes precisos
- **Foreground Service** - Serviços persistentes
- **Material Design 3** - Design system

## Licença

Este projeto está disponível para uso pessoal e comercial.
