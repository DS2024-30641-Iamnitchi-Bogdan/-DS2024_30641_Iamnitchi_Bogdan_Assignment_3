FROM node:20.18.0 as build

WORKDIR /app

COPY package*.json ./

RUN npm install

RUN npm install -g @angular/cli

COPY . .

RUN ng build --configuration=production

FROM nginx:latest

COPY ./nginx.conf /etc/nginx/templates/nginx.conf.template

CMD ["/bin/sh", "-c", "envsubst '${USER_HOST} ${USER_PORT} ${DEVICE_HOST} ${DEVICE_PORT} ${MONITOR_HOST} ${MONITOR_PORT}' < /etc/nginx/templates/nginx.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]

COPY --from=build /app/dist/energy-app-ui/browser /usr/share/nginx/html

EXPOSE 4200

