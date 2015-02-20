#!/bin/bash
#Generates a self-signed ssl key.

sslDir='keys'

key=$sslDir/key.pem #private key
request=$sslDir/request.csr #signing request
cert=$sslDir/cert.pem #public cert

mkdir -p $sslDir
openssl genrsa -out $key 1024 
openssl req -new -key $key -out $request
openssl x509 -req -in $request -signkey $key -out $cert
cp -i $cert curl-ca-bundle.crt #expose public cert to tests

