#!/bin/bash
#basic test that ssl is working / the server is currently up.
#TODO: a testing framework would be nice.

url="https://chani.ca:3000"
ssl="--cacert curl-ca-bundle.crt"
sslUrl="$ssl $url"

echo testing insecure https...
curl -k $url

echo testing certificate...
curl $sslUrl

echo testing post...
curl -H "Content-Type: application/json" -d'{"a":"b"}' $sslUrl

