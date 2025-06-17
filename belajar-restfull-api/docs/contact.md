# Contact API Spec

## Create Contact

Endpoin : POST /api/contacts

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "firstName" : "Fuad",
  "lastName" : "Hamidan",
  "email" : "fuad@example.com",
  "phone" : "08708977567"
}
```

Response Body (Success) :

```json
{
  "data": {
    "id": "random string",
    "firstName": "Fuad",
    "lastName": "Hamidan",
    "email": "fuad@example.com",
    "phone": "08708977567"
  }
}
```

Response Body (Failed)

```json
{
  "errors" : "Phone number format invalid..."
}
```

## Update Contact

Endpoin : PUT /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "fisrtName" : "Fuad",
  "lastName" : "Hamidan",
  "email" : "fuad@example.com",
  "phone" : "08708977567"
}
```

Response Body (Success) : 

```json
{
  "data": {
    "id": "random string",
    "firstName": "Fuad",
    "lastName": "Hamidan",
    "email": "fuad@example.com",
    "phone": "08708977567"
  }
}
```

Response Body (Failed)

```json
{
  "errors" : "Phone number format invalid..."
}
```

## Get Contact

Endpoin : GET /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data": {
    "id": "random string",
    "firstName": "Fuad",
    "lastName": "Hamidan",
    "email": "fuad@example.com",
    "phone": "08708977567"
  }
}
```

Response Body (Failed, 404)

```json
{
  "errors" : "Contact is not found"
}
```

## Search Contact

Endpoint : GET /api/contacts

Query Param :

- name : String, contact first name or last name, using like query, optional
- phone : String, contact phone, using like query, optional
- email : String, contact email, using like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : [
    {
      "id": "random string",
      "firstName": "Fuad",
      "lastName": "Hamidan",
      "email": "fuad@example.com",
      "phone": "08708977567"
    }
  ],
  "paging" : {
    "currentPage": 0,
    "totalPage" : 10,
    "size" : 10
  }
}
```

Response Body (Failed)

```json
{
  "errors" : "Unauthorized"
}
```

## Remove Contact

Endpoin : DELETE /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token (Mandatory)
- 
Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed)

```json
{
  "errors" : "Contact not found"
}
```