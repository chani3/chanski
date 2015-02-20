#!/bin/bash
#Generates a self-signed ssl key.

sslDir='keys'
certDest='../app/src/main/res/raw/cert.pem'

key=$sslDir/key.pem #private key
request=$sslDir/request.csr #signing request
cert=$sslDir/cert.pem #public cert

mkdir -p $sslDir
openssl genrsa -out $key 1024 
openssl req -new -key $key -out $request
openssl x509 -req -in $request -signkey $key -out $cert
cp -i $cert $certDest #expose public cert to android and tests

