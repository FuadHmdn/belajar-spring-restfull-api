# User API spec

## Register User

Endpoint : POST /api/users

Request Body :

```json
{
  "username" : "fuad",
  "password" : "rahasia",
  "name": "Fuad Hamidan"
}
```

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors": "Username must not blank, ???"
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username" : "fuad",
  "password" : "rahasia"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "token": "TOKEN",
    "expiredAt": 23123123145 // milliseconds
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Username or password wrong"
}
```

## Get User

Endpoint : GET /api/users/current


Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "username": "fuad",
    "name": "Fuad Hamidan"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Unauthorized"
}
```

## Update User

Endpoint : PATCH /api/users/current

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
  "name": "Hamidan", //put if only want to update name
  "password": "new password" //put if only want to update name
}
```

Response Body (Success) :

```json
{
  "data" : {
    "username": "fuad",
    "name": "Fuad Hamidan"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Unauthorized"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout


Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```