Для использования переменных среды используите %ENV_VAR_NAME%
Пример запуска всех тестов:

./run_tests.sh --profile ui \
--base-ui-url http://nginx:80 \
--base-api-url http://backend:4111 \
--remote-url http://selenoid:4444/wd/hub \
--network nbank-network