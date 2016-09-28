#!/bin/sh

# Did someone say proper build pipeline?

ng build --prod && \
rm -rf dist/assets/css && \
rm dist/*.gz && \
sed -i s/http\\:\\/\\/localhost:8080/https\\:\\/\\/api.prod.leedsskydivers.com/g dist/*.js
