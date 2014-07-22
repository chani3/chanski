#!/bin/bash
#Generates a self-signed ssl key.

sslDir='keys'

key=$sslDir/key.pem
request=$sslDir/request.csr
cert=$sslDir/cert.pem

mkdir -p $sslDir
openssl genrsa -out $key 1024 
openssl req -new -key $key -out $request
openssl x509 -req -in $request -signkey $key -out $cert

