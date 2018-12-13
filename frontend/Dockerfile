FROM node:dubnium-slim

ADD .

RUN npm install && \
    npm test && \
    npm build-prod
