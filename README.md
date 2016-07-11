Build
=======
Build jar without dependencies

 ```./gradlew clean build```

 Build jar with all dependencies inside

 ```./gradlew clean fatJar```

 Build docker image based on fatJar

 ```./gradlew clean buildDocker```

 Run
 =======
 Run application in gradle

 ```./gradlew run```

 Run stand alone java application

 ```java -jar clour-crawler-all-NNN.jar```

Docker
=======
Run docker image build using buildDocker

```sudo docker run lzenczuk/cloud-crawler:NNN```

Configuration
=======
By default application creates file _iconomi_prices.csv_, containing fetched data, in application folder. To change location of this file set environment variable iconomi_storage_folder to folder in which file should be stored.
