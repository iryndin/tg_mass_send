# Mass Sender API for Telegram

Telegram Mass Sender service to send messages to multiple users.

## API Endpoints

* Create request for mass message send.  
  `POST http://localhost:8088/api/v1/massSend`
  
  Accepts JSON of the following format: 
  
  ```$json
  {
    "userIds": [1,2,3,4,5],
    "text": "some message"
  }
  ```
  
  Example of `curl` call:
  ```
  curl -i -H "Content-Type: application/json" \
    http://localhost:8088/api/v1/massSend \
    -d "{\"users\": [11,22,33], \"msg\": \"some message\"}"
  ```   
  
* Receive mass send status. 
  It accepts job ID of mass send.
  It returns list of user IDs and send status (WAIT/SENT/DELIVERED)
  
  `GET http://localhost:8088/api/v1/massSend/123`, where `123` is mass send job ID returned by previous API endpoint. 
  
## Build instructions

Simply run `gradle build`

## Run instructions

0. You need to replace Telegram Bot token in the file `application.yml` in property: `app.tg.api.token` 
1. You need Docker and Gradle installed
2. Run PostgreSQL with docker: 
   ```
   docker run -d -p 5433:5432 \
     -e "POSTGRES_USER=tgmsapp" \
     -e "POSTGRES_PASSWORD=test123" \
     -e "POSTGRES_DB=tgms" \
     --name tgmsdb postgres:11
   ``` 
3. Run application: `gradle bootRun`
4. Enjoy! 