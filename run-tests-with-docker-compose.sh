#!/bin/bash

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Определение корневой директории проекта
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="${PROJECT_ROOT}/infra/docker_compose"
RESTART_SCRIPT="${COMPOSE_DIR}/restart_docker_compose.sh"
RUN_TESTS_SCRIPT="${PROJECT_ROOT}/run_tests.sh"

# Параметры окружения
NETWORK_NAME="nbank-network"
# ВНИМАНИЕ: Эти URL работают ВНУТРИ контейнера с тестами!
BASE_UI_URL="http://nginx:80"
BASE_API_URL="http://backend:4111"
REMOTE_URL="http://selenoid:4444/wd/hub"

# Функция для вывода сообщений
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Функция для отображения меню
show_menu() {
    echo ""
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}   Выберите тип тестов для запуска${NC}"
    echo -e "${BLUE}================================${NC}"
    echo -e "  ${GREEN}1)${NC} API тесты"
    echo -e "  ${GREEN}2)${NC} UI тесты"
    echo -e "  ${GREEN}3)${NC} Все тесты (API + UI)"
    echo -e "  ${RED}0)${NC} Выход"
    echo ""
    echo -n "Ваш выбор: "
}

# Функция для запуска тестов
run_tests() {
    local profile=$1

    print_info "Запуск тестов с профилем: ${profile}"

    # Переходим в корневую директорию для запуска тестов
    cd "${PROJECT_ROOT}"

    local cmd="${RUN_TESTS_SCRIPT} --profile ${profile} \
        --base-ui-url ${BASE_UI_URL} \
        --base-api-url ${BASE_API_URL} \
        --remote-url ${REMOTE_URL} \
        --network ${NETWORK_NAME}"

    print_info "Выполняется команда: ${cmd}"

    # Важно: run_tests.sh должен запускать тесты в контейнере с той же сетью
    if eval ${cmd}; then
        print_success "Тесты с профилем '${profile}' успешно завершены"
        return 0
    else
        print_error "Ошибка при выполнении тестов с профилем '${profile}'"
        return 1
    fi
}

# Функция для поднятия окружения
start_environment() {
    print_info "Поднятие тестового окружения..."

    # Проверяем существование скрипта
    if [ ! -f "${RESTART_SCRIPT}" ]; then
        print_error "Скрипт ${RESTART_SCRIPT} не найден!"
        exit 1
    fi

    # Проверяем существование docker-compose.yml
    if [ ! -f "${COMPOSE_DIR}/docker-compose.yml" ]; then
        print_error "Файл ${COMPOSE_DIR}/docker-compose.yml не найден!"
        exit 1
    fi

    # Переходим в директорию с docker-compose.yml
    cd "${COMPOSE_DIR}"

    print_info "Рабочая директория: $(pwd)"
    print_info "Запуск скрипта: ./$(basename ${RESTART_SCRIPT})"

    # Запускаем скрипт restart_docker_compose.sh
    if bash "$(basename ${RESTART_SCRIPT})"; then
        print_success "Тестовое окружение успешно поднято"

        # Возвращаемся в корневую директорию
        cd "${PROJECT_ROOT}"

        # Даем время на инициализацию сервисов
        print_info "Ожидание инициализации сервисов (15 секунд)..."
        sleep 15

        # Показываем статус контейнеров для информации
        print_info "Статус контейнеров:"
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "nginx|backend|selenoid|postgres" || docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

        return 0
    else
        cd "${PROJECT_ROOT}"
        print_error "Не удалось поднять тестовое окружение"
        exit 1
    fi
}

# Функция для остановки окружения
stop_environment() {
    print_info "Остановка тестового окружения..."

    # Сохраняем текущую директорию
    local current_dir=$(pwd)

    # Переходим в директорию с docker-compose.yml
    cd "${COMPOSE_DIR}"

    # Проверяем наличие файла docker-compose.yml
    if [ -f "docker-compose.yml" ]; then
        print_info "Остановка контейнеров в $(pwd)"
        docker-compose down

        if [ $? -eq 0 ]; then
            print_success "Тестовое окружение остановлено"
        else
            print_error "Ошибка при остановке окружения"
        fi
    else
        print_error "Файл docker-compose.yml не найден в $(pwd)"
    fi

    # Возвращаемся в исходную директорию
    cd "${current_dir}"
}

# Функция обработки прерывания (Ctrl+C)
cleanup() {
    echo ""
    print_warning "Получен сигнал прерывания. Останавливаем окружение..."
    stop_environment
    exit 1
}

# Основная логика
main() {
    print_info "=== Запуск тестов с Docker Compose окружением ==="
    echo ""

    # Проверка наличия необходимых скриптов
    if [ ! -f "${RUN_TESTS_SCRIPT}" ]; then
        print_error "Скрипт ${RUN_TESTS_SCRIPT} не найден!"
        exit 1
    fi

    # Проверка Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker не установлен!"
        exit 1
    fi

    # Проверка docker-compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "docker-compose не установлен!"
        exit 1
    fi

    # Установка обработчика сигналов
    trap cleanup SIGINT SIGTERM

    # Поднятие окружения
    start_environment

    # Меню выбора тестов
    local exit_script=false
    local test_exit_code=0

    while [ "${exit_script}" = false ]; do
        show_menu
        read -r choice

        case ${choice} in
            1)
                run_tests "api"
                test_exit_code=$?
                exit_script=true
                ;;
            2)
                run_tests "ui"
                test_exit_code=$?
                exit_script=true
                ;;
            3)
                print_info "Запуск API тестов..."
                run_tests "api"
                if [ $? -ne 0 ]; then
                    test_exit_code=1
                    print_warning "API тесты завершились с ошибкой, но продолжаем с UI тестами..."
                fi

                echo ""
                print_info "Запуск UI тестов..."
                run_tests "ui"
                if [ $? -ne 0 ]; then
                    test_exit_code=1
                fi

                if [ ${test_exit_code} -eq 0 ]; then
                    print_success "Все тесты успешно завершены"
                else
                    print_error "Некоторые тесты завершились с ошибками"
                fi
                exit_script=true
                ;;
            0)
                print_info "Выход без запуска тестов"
                test_exit_code=0
                exit_script=true
                ;;
            *)
                print_error "Неверный выбор. Пожалуйста, выберите 0, 1, 2 или 3"
                ;;
        esac
    done

    # Остановка окружения
    stop_environment

    if [ ${test_exit_code} -eq 0 ]; then
        print_success "Скрипт успешно завершен"
    else
        print_error "Скрипт завершен с ошибками"
    fi

    exit ${test_exit_code}
}

# Запуск основной функции
main