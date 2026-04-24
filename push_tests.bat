@echo off

set /p ="%DOCKERHUB_TOKEN%" < nul | docker login -u %DOCKERHUB_LOGIN% --password-stdin

docker tag nbank_tests:latest %DOCKERHUB_LOGIN%/nbank_tests_repo:latest

docker push %DOCKERHUB_LOGIN%/nbank_tests_repo:latest

pause