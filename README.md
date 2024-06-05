# Billion Rows Challenge

## Overview
This project is a solution to the challenge of reading and processing 1 billion rows from a csv-like text file using Java. The objective is to efficiently handle and process a large dataset within reasonable time and resource constraints.

## Features
- Efficiently reads and processes 1 billion rows from a file.
- Utilizes multi-threading for improved performance.
- Uses Executors to handle file reading process with multiple threads.
- Utilizes the Java RandomAccessFile together with BufferedReader to read file from different positions (different chunks) without wasting time to skip lines.
- Has custom implementation to handle the linebreaks to not miss a line from the dataset.
- Provides custom data structure implementation for storing the data to be more memory efficient and faster to calculate the result.
- Has custom parsing method to be more efficient not only in IO operations, but in processing operations as well.
- Uses combination of HashMap and TreeMap to be able to handle data processing in threads safely and without locking.

## Entry File
The main entry point for the application is the [`CalculateAverage_azim.java`](CalculateAverage_azim.java) file.

## Results
- **Runtime**: The processing of 1 billion rows was completed in around `30 seconds`.
- **Environment**: 
  - **Operating System**: `Mac OS 14`
  - **CPU**: `Apple Silicon M1 Pro`
  - **RAM**: `16 Gib`
  - **Available RAM at the test moment**: `4 Gib`
  - **Java Version**: `Openjdk 22`

## References
This project was created as a solution to a challenge that involves reading and processing 1 billion rows from a file using Java. You can find more details about the challenge [here](https://github.com/gunnarmorling/1brc).

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
