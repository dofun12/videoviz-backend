docker stop videoviz || true
docker rm videoviz || true
docker build -t videoviz/videoviz:$BUILD_ID . -f docker/java/Dockerfile || true
docker run -d --name videoviz  -p 9011:8080 --env spring_profiles_active=prod  -v /var/www/html/gui:/var/www/html/gui -v /var/www/html/v1/:/var/www/html/v1/ -v /var/www/html/v2/:/var/www/html/v2/ videoviz/videoviz:$BUILD_ID || true