# BigDataPipeline

## Overview

This project is the scala version of my previous project(https://github.com/Dukecat0613/Big-Data). In this project, I wrote my own GoogleFinanceHJavaApi as the real time data source, maybe I can do better to handle the anti-crawler, this is a pain of the Api. As for the data pipeline, I first get the data from GoogleFinanceApi then transmit the data to spark streaming computing the average price of the batch time, finally transport the processed data to front end. 

## FrameWork

I use the scalatra as the web server, it is suitable for new users like me to get started web framework in scala. However, it depends on the `sbt`, which I prefer more on maven. Therefore, for the web server part, that is `pipelineserver`, I use `sbt` to manage, for the rest, I use `maven` instead. In addition, `Kafka producer`, `Kafka consumer`, `spark streaming integration with kafka`, `scala with cassandra`, they all can be treated as templates for future work.

## For data pipeline, python vs scala?

After trying both two, I personally like scala more. The reason is scala can support type comprehension which is awesome. Besides, any java library can be easily used in scala. For those who are good at java and new to scala, it would be a great advantage to get started with scala. 
