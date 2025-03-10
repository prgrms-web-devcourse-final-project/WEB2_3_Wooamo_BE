# 파일 위치 : /home/ubuntu/deploy.sh

# 색상 확인 후, 반대 되는 색상으로 실행
EXIST_BLUE=$(sudo doker compose ps | grep "springboot-blue" | grep Up)

if [-z "$EXIST_BLUE"]; then
  sudo docker compose up -d springboot-blue
  BEFORE_COLOR="green"
  AFTER_COLOR="blue"
  BEFORE_PORT=8081
  AFTER_PORT=8080
else
  sudo docker compose up -d springboot-green
  BEFORE_COLOR-"blue"
  AFTER_COLOR="green"
  BEFORE_PORT=8080
  AFTER_PORT=8081
fi

echo "===== ${AFTER_COLOR} server up(port:${AFTER_PORT}) ====="

# 릴리즈 응답 확인 (10초간 10번 대기)
for cnt in {1..10}
do
  echo "==== 서버 응답 확인 중 (${cnt}/10) ===="
  UP=$(curl -s http://localhost:8080/actuator/health) #ip, https 중에 어떤걸 넣어야 할까 ~
  if [-z "${UP}"]
    then
      sleep 10
      continue
    else
      break
  fi
done

if [$cnt -eq 10]
then
  echo "==== 서버 실행 실패 ===="
  exit 1
fi

# 3
# 여기 설정은 시스템 내부 nginx 를 설정하는 것 같은데 docker 에서 설정은 어떻게 다른지 보기 // 도커 맞음(앞에 도커 명령어 있음)
echo "===== Nginx 설정 변경 ====="
sudo docker exec -it nginx /bin/bash -c "sed -i 's/:${BEFORE_PORT}/:${AFTER_PORT}/' /etc/nginx/conf.d/default.conf && nginx -s reload"

# 4
echo "$BEFORE_COLOR server down(port:${BEFORE_PORT})"
sudo docker compose stop springboot-${BEFORE_COLOR}

## 도커 볼륨 정리 +++
## 이 내용을 github actions 에서 설정하도록 해야 함 // 사실 nginx reload 도 actions에 들어가야할 것 같긴 함
## 해당 deploy를 실행하고 도커 컴포즈 stop , volume 삭제 에 대한 내용을 넣으면 좋을듯?