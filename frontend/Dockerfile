FROM node:dubnium

RUN mkdir /usr/app /home/build && \
    useradd build && \
    chown build /usr/app /home/build

WORKDIR /usr/app

RUN apt-get update && \
    apt-get install libx11-xcb1 \
                    libxtst6 \
                    libnss3 \
                    libxss1 \
                    libasound2 \
                    libatk-bridge2.0-0 \
                    libgtk-3-0 --yes

USER build
