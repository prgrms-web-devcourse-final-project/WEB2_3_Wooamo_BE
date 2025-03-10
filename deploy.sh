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

# 서버가 제대로 실행될 때까지 최대 10번 시도
MAX_RETRIES=10
COUNT=0
HEALTHY="false"
while [ $COUNT -lt $MAX_RETRIES ]; do
  COUNT=$((COUNT + 1))
  LOG_OUTPUT=$(sudo docker logs stuv-app-${AFTER_COLOR} 2>&1)

  if echo "$LOG_OUTPUT" | grep -q "Started Application"; then
    HEALTHY="true"
    break
  fi

  echo "Retrying... Attempt #${COUNT}"
  sleep 5
done

if [ "$HEALTHY" != "true" ]; then
  echo "==== ${AFTER_COLOR} 서버 실행 실패 (헬스 체크 실패) ===="
  exit 1
fi

# 3
echo "===== Nginx 설정 변경 ====="
sudo docker exec -it nginx /bin/bash -c "sed -i 's/:${BEFORE_PORT}/:${AFTER_PORT}/g' /etc/nginx/conf.d/default.conf && nginx -s reload"

# 4
echo "===== ${BEFORE_COLOR} server down(port:${BEFORE_PORT}) ====="
sudo docker compose down springboot-${BEFORE_COLOR}

# 불필요한 Docker 볼륨 정리
echo "===== 사용하지 않는 Docker 볼륨 정리 ====="
sudo docker volume prune -f
