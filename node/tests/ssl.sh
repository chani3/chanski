#!/bin/bash
#basic test that ssl is working / the server is currently up.
#TODO: a testing framework would be nice.

url="https://chani.ca:3000"

echo testing insecure https...
curl -k $url
