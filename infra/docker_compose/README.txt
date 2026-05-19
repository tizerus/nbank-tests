Иногда пишет ошибку, что порт 4111 занят или недоступен, помгает
PowerShell:
net stop winnat
# Wait a few seconds, then try starting your Docker container
net start winnat


# Запуск API-тестов (по умолчанию)
./run_tests.sh api

# Запуск UI-тестов
./run_tests.sh ui