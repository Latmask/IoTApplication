# -*- coding: utf-8 -*-
# !/usr/bin/env python

# This is where to insert your generated API keys (http://api.telldus.com/keys)
pubkey = "FEHUVEW84RAFR5SP22RABURUPHAFRUNU"  # Public Key
privkey = "ZUXEVEGA9USTAZEWRETHAQUBUR69U6EF"  # Private Key
token = "1212926ae3218029e19f76d4cadbb38c061bb1dec"  # Token
secret = "2bdbdc217552a27012da93e9a4cd858d"  # Token Secret

import requests, json, hashlib, uuid, time, sys

localtime = time.localtime(time.time())
timestamp = str(time.mktime(localtime))
nonce = uuid.uuid4().hex
oauthSignature = (privkey + "%26" + secret)

actuator_command = int(sys.argv[1])
actuator_id = int(sys.argv[2])
actuator_id_quotes = f'"{actuator_id}"'

# GET-request
if actuator_command == "TurnOn":
    response = requests.get(
        url="https://pa-api.telldus.com/json/device/turnOn",
        params={
            "id": actuator_id_quotes,
        },
        headers={
            "Authorization": 'OAuth oauth_consumer_key="{pubkey}", oauth_nonce="{nonce}", oauth_signature="{oauthSignature}", oauth_signature_method="PLAINTEXT", oauth_timestamp="{timestamp}", oauth_token="{token}", oauth_version="1.0"'.format(
                pubkey=pubkey, nonce=nonce, oauthSignature=oauthSignature, timestamp=timestamp, token=token),
        },
    )
else:
    response = requests.get(
        url="https://pa-api.telldus.com/json/device/turnOff",
        params={
            "id": actuator_id_quotes,
        },
        headers={
            "Authorization": 'OAuth oauth_consumer_key="{pubkey}", oauth_nonce="{nonce}", oauth_signature="{oauthSignature}", oauth_signature_method="PLAINTEXT", oauth_timestamp="{timestamp}", oauth_token="{token}", oauth_version="1.0"'.format(
                pubkey=pubkey, nonce=nonce, oauthSignature=oauthSignature, timestamp=timestamp, token=token),
        },
    )
# Output/response from GET-request
responseData = response.json()

# Uncomment to print response :)
# print(json.dumps(responseData, indent=4, sort_keys=True))