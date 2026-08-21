# Browser Automation (Windows, Kotlin, Playwright)

Локальное консольное приложение для **явно запускаемых** воспроизводимых UI-сценариев в отдельном Chromium-профиле и управления только allowlisted-процессами Windows. Оно не использует `Robot`, не работает как служба и не реагирует на простой/блокировку компьютера.

## Структура

```text
browser-automation/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/automation/
    │   │   ├── Main.kt
    │   │   ├── action/AutomationAction.kt
    │   │   ├── browser/BrowserManager.kt
    │   │   ├── config/{AutomationConfig,ConfigLoader}.kt
    │   │   ├── engine/{ActionExecutor,AutomationContext,AutomationEngine,StopController}.kt
    │   │   ├── model/ScenarioResult.kt
    │   │   ├── process/WindowsProcessManager.kt
    │   │   ├── report/ReportGenerator.kt
    │   │   ├── scenario/{BrowserScenario,ExampleScenarios}.kt
    │   │   └── validation/AllowedUrlValidator.kt
    │   └── resources/{application.yml,logback.xml}
    └── test/kotlin/automation/validation/AllowedUrlValidatorTest.kt
```

## Требования и сборка

* Windows 10/11 x64;
* JDK 21 (`java -version`);
* Maven 3.9+ (либо Maven Wrapper родительского репозитория).

```powershell
cd browser-automation
mvn clean verify
```

Получится автономный `target/browser-automation.jar`. Перед первым UI-запуском установите Chromium той же версии Playwright, которая зафиксирована в `pom.xml`:

```powershell
mvn dependency:build-classpath "-Dmdep.outputFile=target\classpath.txt"
$cp = Get-Content target\classpath.txt
java -cp $cp com.microsoft.playwright.CLI install chromium
```

Это официальный Java CLI, включённый в выбранную зависимость Playwright. После изменения версии повторите установку.

## Явный запуск

```powershell
mvn exec:java
java -jar target/browser-automation.jar
java -jar target/browser-automation.jar --scenario=open-demo
java -jar target/browser-automation.jar --scenario=open-demo,multipage-demo
java -jar target/browser-automation.jar --config=C:\tests\automation.yml
```

Рабочий каталог должен быть каталогом проекта: относительно него создаются `build/browser-profile`, stop-файл и артефакты. `--scenario` принимает YAML-ID, неизвестный ID является ошибкой. Выключенные сценарии не запускаются. Для безопасной проверки плана поставьте `dry-run: true`: конфигурация, URL и ссылки на process-ID валидируются, но Chromium и процессы не запускаются.

## Модель безопасности

* `AllowedUrlValidator` разбирает URI, разрешает только `http`/`https`, запрещает user-info и сравнивает нормализованный host по границе DNS-метки. Поэтому `example.com.attacker.test` и `example.com@attacker.test` не проходят.
* Для каждой document-навигации Playwright route проверяется до загрузки. Явные переходы, ссылки, back, новая вкладка и конечные URL проверяются дополнительно; неожиданный popup/`window.open` и redirect отслеживаются на уровне context/page и запрещённая страница закрывается.
* Процесс запускается только по ID из `processes.allowed`; YAML action не имеет поля произвольной команды. Менеджер хранит полученный `ProcessHandle` и останавливает именно созданный PID, а не процессы по имени.
* Профиль `build/browser-profile` отдельный; путь к обычному Chrome/Edge не указывайте. Не храните в YAML пароли, cookies, токены и реальные данные. `${ENV_NAME}` разворачивается только из окружения и при отсутствии переменной приводит к ошибке.

## Что заменить для моего сайта

Все настройки находятся в `src/main/resources/application.yml` (или во внешнем файле `--config`).

### URL и allowlist

Добавьте каждый разрешённый домен и только затем меняйте `url` в actions:

```yaml
allowed-hosts:
  - domain: test.my-company.example
    include-subdomains: false
    ports: [443]
```

`include-subdomains: true` разрешает сам домен и `*.domain`, но не похожие домены. Пустой `ports` разрешает любой explicit/default port; непустой — только перечисленные explicit ports. Все `OPEN_PAGE`/`OPEN_TAB` URL должны пройти allowlist.

### Selectors и тестовые значения

Selector задаётся внутри action:

```yaml
selector: { type: TEST_ID, value: search-input }       # data-testid="search-input"
selector: { type: ROLE, value: TEXTBOX, name: Search } # aria role + accessible name
selector: { type: LABEL, value: First name }           # связанный <label>
selector: { type: CSS, value: "#search" }              # CSS
selector: { type: TEXT, value: "Ready" }               # видимый текст
```

Предпочитайте стабильный `data-testid`; ROLE/LABEL проверяют доступный интерфейс. Значения находятся в `test-data`, action получает их через `data-key`, поэтому данные не дублируются. `value` годится только для безобидной тестовой константы. Не коммитьте секреты; используйте `${TEST_PASSWORD}` во внешней конфигурации, если это потребуется.

`submit-enabled: false` — безопасное значение по умолчанию. Встроенный `FormScenario` добавляет нажатие Submit лишь при `true`; в YAML-потоке также не добавляйте action кнопки отправки, пока отправка не разрешена политикой теста.

### Новый процесс

```yaml
processes:
  cleanup-on-exit: true
  allowed:
    my-tool:
      executable: "C:\\Tools\\TestTool.exe"
      args: ["--test-mode"]
scenarios:
  process-demo:
    enabled: true
    actions:
      - { type: START_PROCESS, process: my-tool }
      - { type: WAIT_PROCESS, process: my-tool, timeout: 10000 }
      - { type: STOP_PROCESS, process: my-tool, timeout: 5000 }
```

Не добавляйте shell (`cmd.exe`, PowerShell) как универсальный обход allowlist. `cleanup-on-exit` останавливает только PID, созданные текущим запуском.

### Новый сценарий/action

Добавьте YAML-блок в `scenarios`; порядок actions сохраняется. Поддержаны `OPEN_PAGE`, `CLICK`, `FILL`, `CHECK`, `UNCHECK`, `SELECT`, `ASSERT_TEXT`, `ASSERT_URL`, `WAIT_FOR`, `BACK`, `OPEN_TAB`, `CLOSE_TAB`, `START_PROCESS`, `STOP_PROCESS`, `WAIT_PROCESS`. Для новой семантики добавьте enum и отдельный `ActionHandler` в registry `ActionExecutor`; для программного сценария реализуйте `BrowserScenario`. Четыре готовых программных примера показывают open/title-text, navigation/back, все виды selector/form controls и popup/tabs; готовые YAML demos выполняются движком без перекомпиляции.

### Лимиты, seed, dry-run

```yaml
randomize-scenario-order: true
random-seed: 123456
limits:
  max-run-duration: PT10M
  max-navigation-count: 30
  max-action-count: 100
dry-run: true
```

Seed записывается в оба отчёта. Shuffle влияет только на порядок сценариев. Duration имеет ISO-8601 формат. Счётчики и deadline проверяются перед каждым action/переходом.

### Аварийная остановка

Нажмите **Ctrl+C** либо в другом PowerShell создайте файл:

```powershell
New-Item -ItemType File build\automation.stop
# либо
java -jar target/browser-automation.jar stop
```

Старый stop-файл удаляется при следующем старте. Перед каждым action stop-файл проверяется. Shutdown hook устанавливает stop-флаг, закрывает context/Playwright, при настройке очищает созданные процессы и записывает отчёт.

## Артефакты и отчёт

Каждый запуск пишет `build/test-results/browser/run-<UTC timestamp>/report.txt`, `report.json`, `trace.zip`. При ошибке дополнительно создаются `<Scenario>/error.png`, `page.html`, `error.txt` с URL, action и stack trace. Trace открывается командой Playwright `show-trace`. Логи содержат scenario/action, URL/selector, PID, результат, duration и счётчики.
