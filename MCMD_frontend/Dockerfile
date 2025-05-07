# 使用官方的 Nginx 镜像作为基础镜像
FROM nginx:alpine

# 删除默认的 Nginx 配置文件
RUN rm /etc/nginx/conf.d/default.conf

# 将自定义的 Nginx 配置文件复制到容器中
COPY nginx.conf /etc/nginx/conf.d

# 将构建好的前端静态文件复制到 Nginx 的默认静态文件目录
COPY dist/ /usr/share/nginx/html

# 暴露 80 端口（Nginx 默认监听 80 端口）
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]