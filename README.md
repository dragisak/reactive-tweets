# Reactive Project Tweets De-Luxe

## Building

1. Install sbt: `brew install sbt`
1. Build assembly jar: `sbt assembly`
1. Run jar: `java -Dtwitter.key=<key> -Dtwitter.secret=<secret> -jar target/scala-2.11/reactive-tweets-assembly-1.0.jar`

## Configuration

Instead of passing `-Dtwitter.key` and `-Dtwitter.secret`, you can pass a reference to external config file via `-Dconfig.file=<file>`

Example of config file:
```
twitter {
  key = <twitter_app_key>
  secret = <secret_kety>
}
```
