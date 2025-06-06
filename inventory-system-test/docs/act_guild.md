# act

### act download
https://nektosact.com/installation/index.html

### act install with brew
```
brew install act
```    

### act run
```
act -P ubuntu-22.04=catthehacker/ubuntu:act-22.04 pull_request
```

### act 명령어
```
act -h | grep '\-l'
act -l 
```

### act run fail
**log**
```
INFO[0000] Using docker host 'unix:///Users/nicejongwoo/.docker/run/docker.sock', and daemon socket 'unix:///Users/nicejongwoo/.docker/run/docker.sock' 
WARN  ⚠ You are using Apple M-series chip and you have not specified container architecture, you might encounter issues while running act. If so, try running it with '--container-architecture linux/amd64'. ⚠  
[Pull request/Test and Analyze] ⭐ Run Set up job
[Pull request/Test and Analyze] 🚀  Start image=catthehacker/ubuntu:act-22.04
[Pull request/Test and Analyze]   🐳  docker pull image=catthehacker/ubuntu:act-22.04 platform= username= forcePull=true
[Pull request/Test and Analyze]   🐳  docker create image=catthehacker/ubuntu:act-22.04 platform= entrypoint=["tail" "-f" "/dev/null"] cmd=[] network="host"
[Pull request/Test and Analyze]   🐳  docker run image=catthehacker/ubuntu:act-22.04 platform= entrypoint=["tail" "-f" "/dev/null"] cmd=[] network="host"
[Pull request/Test and Analyze] failed to start container: Error response from daemon: error while creating mount source path '/host_mnt/Users/nicejongwoo/.docker/run/docker.sock': mkdir /host_mnt/Users/nicejongwoo/.docker/run/docker.sock: operation not supported
[Pull request/Test and Analyze]   ❌  Failure - Set up job
[Pull request/Test and Analyze] 🏁  Job failed
Error: failed to start container: Error response from daemon: error while creating mount source path '/host_mnt/Users/nicejongwoo/.docker/run/docker.sock': mkdir /host_mnt/Users/nicejongwoo/.docker/run/docker.sock: operation not supported
```
**act run fail 해결**
```
ls -l /Users/nicejongwoo/.docker/run/docker.sock
srwxr-xr-x@ 1 nicejongwoo  staff  0 May 31 10:45 /Users/nicejongwoo/.docker/run/docker.sock

sudo chmod 666 /Users/nicejongwoo/.docker/run/docker.sock
Password:

ls -l /Users/nicejongwoo/.docker/run/docker.sock
srw-rw-rw-@ 1 nicejongwoo  staff  0 May 31 10:45 /Users/nicejongwoo/.docker/run/docker.sock
```

### docker 재설치

**1. Docker 관련 프로세스 중지**
```bash
# Docker 데스크탑 앱 종료
osascript -e 'quit app "Docker"'
```

**2. brew 로 설치된 Docker 관련 패키지 제거**
```bash
brew uninstall --cask docker
brew uninstall docker docker-compose docker-machine
```

**3. Docker Desktop 잔여 파일 수동 삭제**
```bash
# 사용자 라이브러리 내 도커 관련 파일 삭제
rm -rf ~/Library/Containers/com.docker.docker
rm -rf ~/.docker
rm -rf ~/Library/Group\ Containers/group.com.docker
rm -rf ~/Library/Application\ Support/Docker\ Desktop
rm -rf ~/Library/Caches/com.docker.docker
rm -rf ~/Library/Preferences/com.docker.docker.plist
rm -rf /usr/local/bin/docker
rm -rf /usr/local/bin/docker-compose
rm -rf /usr/local/bin/docker-credential-desktop
rm -rf /usr/local/bin/docker-credential-osxkeychain
rm -rf /usr/local/bin/docker-machine
rm -rf /usr/local/bin/com.docker.cli
rm -f /usr/local/bin/hub-tool
```

> 참고: Apple Silicon(M1/M2/M3)의 경우 `/opt/homebrew/bin` 아래도 확인할 필요가 있습니다.

**4. Homebrew 캐시 정리**
```bash
brew cleanup
```

**5. 재설치**
```bash
brew install --cask docker
```

설치 후에는 **Docker Desktop 앱을 수동으로 실행**해야 초기 설정이 완료

```bash
open -a Docker
```
