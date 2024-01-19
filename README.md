# Simple-SAST

Basic console application that performs simple security static analysis.


## Run the application

#### Locally

- $ ``mvn clean install``
- Run Main class (org.checkmarx.codescanner.Main), present in the directory`: ``src/main/java/org/checkmarx/codescanner/Main.java``

#### With docker

Before execute with docker, please create a file, like "input_params.txt".

The first line should contain the input folder to the files be scanned.

The second line should contain the scan configuration options.\
Options available: {1=Cross site scripting, 2=Sensitive data exposure, 3=SQL Injection}

File example:\
input_params.txt
```
./input
1 2 3
```
Then you can run:
```
 docker-compose build
 docker run -i sast-checkmarx:latest < input_params.txt
```

### Instructions for the input in the console

1) You must introduce thr source code path  in the first input of the console.
2) You must introduce one or multiple options for the scan configuration (1, 2 and/or 3). The options can be separated by a newline. To finish the selection, please type CTRL+D.

(Please follow the instructions on the console output)

## Brief documentation on some adopted strategies

- [Please check this file](./notes.md)