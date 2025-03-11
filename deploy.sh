#!/bin/bash

# 색상 확인 후, 반대되는 색상으로 실행
EXIST_BLUE=$(sudo docker compose ps | grep "springboot-blue" | grep Up)

if [ -z "$EXIST_BLUE" ]; then
  sudo docker compose up -d springboot-blue
  BEFORE_COLOR="green"
  AFTER_COLOR="blue"
  BEFORE_PORT=8081
  AFTER_PORT=8080
else
  sudo docker compose up -d springboot-green
  BEFORE_COLOR="blue"
  AFTER_COLOR="green"
  BEFORE_PORT=8080
  AFTER_PORT=8081
fi

echo "===== ${AFTER_COLOR} server up(port:${AFTER_PORT}) ====="

# 컨테이너 상태 확인
CONTAINER_STATUS=$(sudo docker inspect -f '{{.State.Status}}' stuv-app-${AFTER_COLOR})

if [ "$CONTAINER_STATUS" != "running" ]; then
  echo "==== ${AFTER_COLOR} 서버가 실행 중이지 않습니다. 배포 실패 ===="
  exit 1
fi

echo "===== Nginx 설정 변경 ====="
sudo docker exec -i nginx /bin/sh -c "sed -i 's/:${BEFORE_PORT}/:${AFTER_PORT}/g' /etc/nginx/conf.d/default.conf && nginx -s reload"

echo "===== ${BEFORE_COLOR} server down(port:${BEFORE_PORT}) ====="
sudo docker compose down springboot-${BEFORE_COLOR}

echo "===== 사용하지 않는 Docker 볼륨 정리 ====="
sudo docker volume prune -f
