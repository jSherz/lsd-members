#!/bin/sh

yarn build -- --prod && \
rm -rv dist/assets/css && \
sed -i s/http\\:\\/\\/localhost:8080/https\\:\\/\\/api.prod.leedsskydivers.com/g dist/*.js
