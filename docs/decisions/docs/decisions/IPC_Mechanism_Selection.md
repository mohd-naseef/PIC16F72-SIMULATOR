# Inter-Process Communication (IPC) Mechanism Selection & Justification

## 1. Selected Mechanism
- **Selected IPC:** POSIX Sockets (TCP Loopback / Stream Sockets, Port 9999 for Logger)
- **Processes Involved:** UI Process, Core Process, Logging Process

## 2. Technical Justification
1. **Decoupling and Asynchronous Handling:**
   - Logging operates independently from the CPU execution core. Using stream sockets allows the Logger process to buffer and persist logs without blocking time-sensitive microcontroller simulation cycles in CoreProcess.
2. **Platform & POSIX Compatibility:**
   - Standard TCP loopback (`localhost`) sockets provide full POSIX IPC compliance, ensuring identical behavior across Linux, macOS, and Windows development environments.
3. **Multi-Client Concurrency:**
   - Sockets allow the Logging Process to use multithreading (`LogClientHandler`) to safely accept log streams from both the CoreProcess and UIProcess simultaneously.
4. **Clean Message Framing:**
   - Using standard newline-terminated, tagged strings (`LEVEL:MESSAGE`) makes log parsing fast, robust, and easy to monitor in real-time.
