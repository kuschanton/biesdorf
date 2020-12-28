### Description
This project shows how events from Twilio TaskRouter can be forwarded to AWS Kinesis for futrher processing.

The project is built using Kotless (https://github.com/JetBrains/kotless) and Ktor DSL in particular (https://github.com/JetBrains/kotless/wiki/Ktor-DSL-Overview).

### TODO:
+ send event to kinesis
+ cover with tests

### Testing

To test the app you can start it with:
```
gradle local
```
Do not forget to comment out **aws_sns_topic_subscription** block from **tf/extensions.tf**

### Deployment

To deploy the app:
```
gradle deploy
```
This requires having aws credentials configured with profile name according to the configuration in **build.gradle.kts**.