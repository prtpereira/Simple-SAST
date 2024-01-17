# Simple-SAST

Basic console application that performs simple security static analysis.


## Run the application

#### Locally

- Run Main class (org.checkmarx.codescanner.Main), present in the directory`: ``src/main/java/org/checkmarx/codescanner/Main.java``

#### With docker

```
 docker-compose build
 docker-compose up app
```

### Instructions for the input in the console

1) You must introduce thr source code path  in the first input of the console.
2) You must introduce one or multiple options for the scan configuration (1, 2 and/or 3). The options can be separated by a newline. To finish the selection, please type CTRL+D.

(Please follow the instructions on the console output)

## Brief documentation on some adopted strategies

- [Please check this file](./notes.md)