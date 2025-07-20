# 构建适用于 Render 的镜像，并保存到本地
HTTP_PROXY=http://127.0.0.1:7890/ HTTPS_PROXY=http://127.0.0.1:7890/ \
docker buildx build --platform linux/amd64 -t akimuz/storyseek:latest --load .

docker push akimuz/storyseek:latest
