# Choosing file reader

https://www.baeldung.com/java-read-lines-large-file

"BufferedReader reduces the number of I/O operations by reading the file chunk by chunk and caching the chunks in an internal buffer."

https://stackoverflow.com/questions/19486077/java-fastest-way-to-read-through-text-file-with-2-million-lines

"BufferedReader is the fastest, Files.readAllLines() is also acceptable, Scanner is slow due to regex, RandomAccessFile is inacceptable"

https://www.digitalocean.com/community/tutorials/java-read-text-file

"BufferedReader is good if you want to read file line by line and process on them. It’s good for processing the large file and it supports encoding also. BufferedReader is synchronized, so read operations on a BufferedReader can safely be done from multiple threads."

# Writing vulnerabilities to file

## Strategy

### Write line by line VS. store and print all information

There's two possibilities to generate the files with the vulnerabilities found:

1) Read the source code file, process line by line, trying to find some vulnerability, and immediately write to the file when some vulnerability is found.
2) Read the source code file, process line by line, and store in-memory the vulnerabilities found. After the file is processed, the information had been stored in memory, this data is exported to the file collectively.

#### Pro vs cons

- Storing all lines in memory and then writing collectively: (1)

Pros:

Potentially faster I/O operations: Writing data collectively can reduce the number of I/O operations, which may improve performance.
Simplicity: The code might be simpler as you don't have to manage file I/O operations for each line.

Cons:

Memory usage: If the number of lines is very large, storing all of them in memory can lead to increased memory usage.
Increased risk of losing data: If the program crashes or encounters an error before writing all lines, you might lose the unsaved data.

- Writing line by line: (2)

Pros:

Lower memory usage: Writing line by line can reduce the overall memory footprint, which is essential for large datasets.
Immediate persistence: Each line is immediately persisted to the file, reducing the risk of data loss in case of a failure.

Cons:

Potentially slower I/O operations: Writing data line by line may result in more I/O operations, which could be less efficient.

### Veredict

Given this application is intended to parse source code files and find vulnerabilities, the file size is not expected to be large (MB, GB), hence the number of vulnerabilities found is not expected to be large.
Strategy addopted: store vulnerabilites in-memory and then wrie all data collectively.

It's not critical if the program crashes and the information is lost, because all the process can be executed again in short time.

Performance will be better with this approach since I/O bottlenecks will be lowered by only opening and reading the file once.

# Threadpool

### 1) concurrency strategy

The threadpool will spawn multiples threads. Each one will be responsible for scanning one file and detect eventual vulnerabilities present into it.

Each thread will return the detected vulnerabilities to the 'thread father' (threadpool manager).

When the main thread receives all the detected vulnerabilities, these will be printed into a plaintext file and a json file.

### 2) number of threads

