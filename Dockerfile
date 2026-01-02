FROM nginx:1.29-alpine

RUN rm -rf /usr/share/nginx/html/*

COPY index.html styles.css /usr/share/nginx/html/

